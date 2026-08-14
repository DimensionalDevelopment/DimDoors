package org.dimdev.dimdoors.rift.registry;

import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelResource;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.Edge;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.NbtUtil;
import org.dimdev.dimdoors.util.CodecUtils;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.PocketInfo;
import org.dimdev.dimdoors.world.pocket.PrivateRegistry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class LegacyDimensionalRegistryMigrator {
    private static final String OLD_DATA_NAME = "dimensional_registry";
    private static final int SUPPORTED_RIFT_DATA_VERSION = 1;
    private static final Codec<Map<ResourceKey<Level>, PocketDirectory>> POCKET_DIRECTORY_MAP_CODEC = CodecUtils.unboundedMap(Level.RESOURCE_KEY_CODEC, PocketDirectory.CODEC);

    public static void migrateIfNeeded(MinecraftServer server) {
        Path oldFile = dataFile(server, OLD_DATA_NAME);
        if (!Files.exists(oldFile)) {
            return;
        }

        if (hasAllSplitData(server)) {
            return;
        }

        if (hasAnySplitData(server)) {
            DimensionalDoors.LOGGER.warn("Found old {}.dat but only some split registry data exists. Skipping automatic registry migration.", OLD_DATA_NAME);
            return;
        }

        try {
            CompoundTag root = readSavedDataRoot(oldFile);
            CompoundTag data = root.getCompound("data");
            var result = parse(data);
            result.set(server.overworld().getDataStorage());


            DimensionalDoors.LOGGER.info(
                    "Migrated old {}.dat into split registry data: {} rifts, {} pocket entrance pointers, {} private pocket owners, {} graph edges.",
                    OLD_DATA_NAME,
                    result.riftCount,
                    result.pocketEntrancePointerCount,
                    result.privatePocketOwnerCount,
                    result.graphEdgeCount
            );

            if (result.droppedRiftCount > 0 || result.droppedLinkCount > 0 || result.droppedPocketPointerCount > 0 || result.droppedPlayerPointerCount > 0 || result.droppedOverworldEntryCount > 0) {
                DimensionalDoors.LOGGER.warn(
                        "Registry migration skipped {} rift entries, {} unresolved links, {} pocket pointer entries, {} private pointer entries, and {} old overworld entries.",
                        result.droppedRiftCount,
                        result.droppedLinkCount,
                        result.droppedPocketPointerCount,
                        result.droppedPlayerPointerCount,
                        result.droppedOverworldEntryCount
                );
            }
        } catch (RuntimeException | IOException e) {
            DimensionalDoors.LOGGER.error("Failed to migrate old {}.dat. Leaving existing data unchanged.", OLD_DATA_NAME, e);
        }
    }

    private static boolean hasAnySplitData(MinecraftServer server) {
        return Files.exists(dataFile(server, SubsystemTypes.GRAPH.toFilename()))
                || Files.exists(dataFile(server, SubsystemTypes.RIFT.toFilename()))
                || Files.exists(dataFile(server, SubsystemTypes.PRIVATE.toFilename()))
                || Files.exists(dataFile(server, SubsystemTypes.POCKET.toFilename()));
    }

    private static boolean hasAllSplitData(MinecraftServer server) {
        return Files.exists(dataFile(server, SubsystemTypes.GRAPH.toFilename()))
                && Files.exists(dataFile(server, SubsystemTypes.RIFT.toFilename()))
                && Files.exists(dataFile(server, SubsystemTypes.PRIVATE.toFilename()))
                && Files.exists(dataFile(server, SubsystemTypes.POCKET.toFilename()));
    }

    private static Path dataFile(MinecraftServer server, String name) {
        return server.getWorldPath(LevelResource.ROOT).resolve("data").resolve(name + ".dat");
    }

    private static CompoundTag readSavedDataRoot(Path file) throws IOException {
        try {
            return NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
        } catch (IOException compressedFailure) {
            try {
            CompoundTag raw = NbtIo.read(file);
                if (raw != null) {
                    return raw;
                }
            } catch (IOException rawFailure) {
                compressedFailure.addSuppressed(rawFailure);
            }
            throw compressedFailure;
        }
    }

    private static MigrationResult parse(CompoundTag oldData) {
        requireCompound(oldData, "pocket_registry");
        requireCompound(oldData, "private_registry");
        requireCompound(oldData, "rift_registry");

        int riftDataVersion = oldData.getInt("RiftDataVersion");
        if (riftDataVersion > SUPPORTED_RIFT_DATA_VERSION) {
            throw new IllegalStateException("Cannot migrate future RiftDataVersion " + riftDataVersion);
        }

        Map<ResourceKey<Level>, PocketDirectory> directories = POCKET_DIRECTORY_MAP_CODEC
                .parse(NbtOps.INSTANCE, oldData.getCompound("pocket_registry"))
                .getOrThrow();

        HashBiMap<UUID, PocketInfo> privatePockets = readPrivatePockets(oldData.getCompound("private_registry"));
        CompoundTag oldRiftRegistry = oldData.getCompound("rift_registry");
        ParsedRifts parsedRifts = readRifts(oldRiftRegistry);
        Map<PocketInfo, PocketEntrancePointer> pocketEntrancePointers = readPocketEntrancePointers(oldRiftRegistry, parsedRifts.uuidMap);
        List<Edge> edges = readLinks(oldRiftRegistry, parsedRifts.uuidMap);
        int edgeCountBeforePlayerPointers = edges.size();

        Map<UUID, PlayerTrackingSubSystem.PlayerRiftConnection> privateLocations = readPrivatePlayerPointers(oldRiftRegistry, parsedRifts.uuidMap, edges);
        RiftRegistry riftRegistry = new RiftRegistry();
        riftRegistry.locationMap.putAll(parsedRifts.riftsByLocation);

        PocketRegistry pocketRegistry = new PocketRegistry(directories, pocketEntrancePointers);
        PrivateRegistry privateRegistry = new PrivateRegistry(privateLocations, privatePockets);
        RiftGraph riftGraph = new RiftGraph(edges);

        int overworldEntries = oldRiftRegistry.getList("overworld_rifts", Tag.TAG_COMPOUND).size()
                + oldRiftRegistry.getList("overworld_locations", Tag.TAG_COMPOUND).size();
        ListTag legacyOverworldRifts = oldRiftRegistry.getList("overworld_rifts", Tag.TAG_COMPOUND).copy();
        ListTag legacyOverworldLocations = oldRiftRegistry.getList("overworld_locations", Tag.TAG_COMPOUND).copy();

        return new MigrationResult(
                pocketRegistry,
                riftRegistry,
                privateRegistry,
                riftGraph,
                riftDataVersion,
                parsedRifts.riftsByLocation.size(),
                pocketEntrancePointers.size(),
                privatePockets.size(),
                edges.size(),
                parsedRifts.droppedRiftCount + parsedRifts.droppedPlaceholderCount,
                readDroppedPocketPointerCount(oldRiftRegistry, pocketEntrancePointers),
                countDroppedLinks(oldRiftRegistry, edgeCountBeforePlayerPointers),
                countPrivatePointerEntries(oldRiftRegistry) - privateLocations.values().stream()
                        .mapToInt(connection -> (connection.getEntrance() == null ? 0 : 1) + (connection.getExit() == null ? 0 : 1))
                        .sum(),
                overworldEntries,
                legacyOverworldRifts,
                legacyOverworldLocations
        );
    }

    private static void requireCompound(CompoundTag tag, String key) {
        if (!tag.contains(key, Tag.TAG_COMPOUND)) {
            throw new IllegalStateException("Legacy dimensional registry is missing required compound '" + key + "'");
        }
    }

    private static HashBiMap<UUID, PocketInfo> readPrivatePockets(CompoundTag privateRegistryTag) {
        HashBiMap<UUID, PocketInfo> privatePockets = HashBiMap.create();
        CompoundTag privatePocketMapNbt = privateRegistryTag.getCompound("private_pocket_map");

        for (String key : privatePocketMapNbt.getAllKeys()) {
            try {
                UUID playerId = UUID.fromString(key);
                PocketInfo pocketInfo = NbtUtil.deserialize(privatePocketMapNbt.getCompound(key), PocketInfo.CODEC);
                if (!privatePockets.containsKey(playerId) && !privatePockets.containsValue(pocketInfo)) {
                    privatePockets.put(playerId, pocketInfo);
                }
            } catch (RuntimeException e) {
                DimensionalDoors.LOGGER.warn("Skipping invalid legacy private pocket mapping for {}.", key, e);
            }
        }

        return privatePockets;
    }

    private static ParsedRifts readRifts(CompoundTag oldRiftRegistry) {
        Map<UUID, RegistryVertex> uuidMap = new HashMap<>();
        Map<Location, Rift> riftsByLocation = new LinkedHashMap<>();
        ListTag riftsNBT = oldRiftRegistry.getList("rifts", Tag.TAG_COMPOUND);
        String riftTypeId = typeId(RegistryVertex.RegistryVertexType.RIFT);
        String placeholderTypeId = typeId(RegistryVertex.RegistryVertexType.RIFT_PLACEHOLDER);
        int droppedRifts = 0;
        int droppedPlaceholders = 0;

        for (Tag tag : riftsNBT) {
            CompoundTag vertexNbt = (CompoundTag) tag;
            String type = vertexNbt.getString("type");

            if (Objects.equals(type, riftTypeId)) {
                Rift rift = Rift.fromNbt(vertexNbt);
                if (rift.getLocation() == null) {
                    droppedRifts++;
                    continue;
                }
                uuidMap.put(rift.getId(), rift);
                riftsByLocation.put(rift.getLocation(), rift);
            } else if (Objects.equals(type, placeholderTypeId)) {
                RiftPlaceholder placeholder = RiftPlaceholder.fromNbt(vertexNbt);
                if (placeholder.getLocation() == null) {
                    droppedPlaceholders++;
                    continue;
                }
                uuidMap.put(placeholder.getId(), placeholder);
                riftsByLocation.put(placeholder.getLocation(), placeholder);
            }
        }

        return new ParsedRifts(uuidMap, riftsByLocation, droppedRifts, droppedPlaceholders);
    }

    private static Map<PocketInfo, PocketEntrancePointer> readPocketEntrancePointers(CompoundTag oldRiftRegistry, Map<UUID, RegistryVertex> uuidMap) {
        Map<PocketInfo, PocketEntrancePointer> pointers = new LinkedHashMap<>();
        ListTag pocketsNBT = oldRiftRegistry.getList("pockets", Tag.TAG_COMPOUND);
        String entranceTypeId = typeId(RegistryVertex.RegistryVertexType.ENTRANCE);

        for (Tag tag : pocketsNBT) {
            CompoundTag pointerNbt = (CompoundTag) tag;
            if (!Objects.equals(pointerNbt.getString("type"), entranceTypeId)) {
                continue;
            }

            PocketEntrancePointer pointer = PocketEntrancePointer.fromNbt(pointerNbt);
            PocketInfo info = new PocketInfo(pointer.getWorld(), pointer.getPocketId());
            PocketEntrancePointer replaced = pointers.put(info, pointer);
            if (replaced != null) {
                uuidMap.remove(replaced.getId());
            }
            uuidMap.put(pointer.getId(), pointer);
        }

        return pointers;
    }

    private static int readDroppedPocketPointerCount(CompoundTag oldRiftRegistry, Map<PocketInfo, PocketEntrancePointer> importedPointers) {
        int entranceEntries = 0;
        ListTag pocketsNBT = oldRiftRegistry.getList("pockets", Tag.TAG_COMPOUND);
        String entranceTypeId = typeId(RegistryVertex.RegistryVertexType.ENTRANCE);
        for (Tag tag : pocketsNBT) {
            if (Objects.equals(((CompoundTag) tag).getString("type"), entranceTypeId)) {
                entranceEntries++;
            }
        }
        return Math.max(0, entranceEntries - importedPointers.size());
    }

    private static List<Edge> readLinks(CompoundTag oldRiftRegistry, Map<UUID, RegistryVertex> uuidMap) {
        List<Edge> edges = new ArrayList<>();
        ListTag linksNBT = oldRiftRegistry.getList("links", Tag.TAG_COMPOUND);

        for (Tag tag : linksNBT) {
            CompoundTag linkNbt = (CompoundTag) tag;
            UUID from = linkNbt.getUUID("from");
            UUID to = linkNbt.getUUID("to");
            if (uuidMap.containsKey(from) && uuidMap.containsKey(to)) {
                edges.add(new Edge(from, to));
            }
        }

        return edges;
    }

    private static int countDroppedLinks(CompoundTag oldRiftRegistry, int importedLinkCount) {
        return Math.max(0, oldRiftRegistry.getList("links", Tag.TAG_COMPOUND).size() - importedLinkCount);
    }

    private static Map<UUID, PlayerTrackingSubSystem.PlayerRiftConnection> readPrivatePlayerPointers(
            CompoundTag oldRiftRegistry,
            Map<UUID, RegistryVertex> uuidMap,
            List<Edge> edges
    ) {
        Map<UUID, UUID> entranceTargets = readPlayerPointerTargets(oldRiftRegistry.getList("last_private_pocket_entrances", Tag.TAG_COMPOUND), uuidMap);
        Map<UUID, UUID> exitTargets = readPlayerPointerTargets(oldRiftRegistry.getList("last_private_pocket_exits", Tag.TAG_COMPOUND), uuidMap);
        Map<UUID, PlayerTrackingSubSystem.PlayerRiftConnection> locations = new LinkedHashMap<>();

        for (UUID player : entranceTargets.keySet()) {
            locations.computeIfAbsent(player, ignored -> new PlayerTrackingSubSystem.PlayerRiftConnection())
                    .setEntrance(createPlayerPointer(entranceTargets.get(player), uuidMap, edges));
        }
        for (UUID player : exitTargets.keySet()) {
            locations.computeIfAbsent(player, ignored -> new PlayerTrackingSubSystem.PlayerRiftConnection())
                    .setExit(createPlayerPointer(exitTargets.get(player), uuidMap, edges));
        }

        return locations;
    }

    private static Map<UUID, UUID> readPlayerPointerTargets(ListTag list, Map<UUID, RegistryVertex> uuidMap) {
        Map<UUID, UUID> targets = new LinkedHashMap<>();
        for (Tag tag : list) {
            CompoundTag entry = (CompoundTag) tag;
            UUID targetId = entry.getUUID("rift");
            if (uuidMap.get(targetId) instanceof Rift rift && !(rift instanceof RiftPlaceholder)) {
                targets.put(entry.getUUID("player"), targetId);
            }
        }
        return targets;
    }

    private static UUID createPlayerPointer(UUID target, Map<UUID, RegistryVertex> uuidMap, List<Edge> edges) {
        if (target == null) {
            return null;
        }

        PlayerRiftPointer pointer = new PlayerRiftPointer();
        uuidMap.put(pointer.getId(), pointer);
        edges.add(new Edge(pointer.getId(), target));
        return pointer.getId();
    }

    private static int countPrivatePointerEntries(CompoundTag oldRiftRegistry) {
        return oldRiftRegistry.getList("last_private_pocket_entrances", Tag.TAG_COMPOUND).size()
                + oldRiftRegistry.getList("last_private_pocket_exits", Tag.TAG_COMPOUND).size();
    }

    private static void install(DimensionDataStorage storage, MigrationResult result) {
        result.set(storage);
    }

    private static void markDirty(SavedData data) {
        data.setDirty();
    }

    private static String typeId(RegistryVertex.RegistryVertexType<?> type) {
        return Objects.requireNonNull(RegistryVertex.REGISTRY.getKey(type), "Unregistered legacy registry vertex type").toString();
    }

    private record ParsedRifts(
            Map<UUID, RegistryVertex> uuidMap,
            Map<Location, Rift> riftsByLocation,
            int droppedRiftCount,
            int droppedPlaceholderCount
    ) {
    }

    private record MigrationResult(
            PocketRegistry pocketRegistry,
            RiftRegistry riftRegistry,
            PrivateRegistry privateRegistry,
            RiftGraph riftGraph,
            int riftDataVersion,
            int riftCount,
            int pocketEntrancePointerCount,
            int privatePocketOwnerCount,
            int graphEdgeCount,
            int droppedRiftCount,
            int droppedPocketPointerCount,
            int droppedLinkCount,
            int droppedPlayerPointerCount,
            int droppedOverworldEntryCount,
            ListTag legacyOverworldRifts,
            ListTag legacyOverworldLocations
    ) {
        public void set(DimensionDataStorage storage) {
            pocketRegistry.setDirty();
            riftRegistry.setDirty();
            privateRegistry.setDirty();
            riftGraph.setDirty();

            storage.set(SubsystemTypes.POCKET.toFilename(), this.pocketRegistry);
            storage.set(SubsystemTypes.RIFT.toFilename(), this.riftRegistry);
            storage.set(SubsystemTypes.PRIVATE.toFilename(), this.privateRegistry);
            storage.set(SubsystemTypes.GRAPH.toFilename(), this.riftGraph);
            storage.save();
        }
    }
}
