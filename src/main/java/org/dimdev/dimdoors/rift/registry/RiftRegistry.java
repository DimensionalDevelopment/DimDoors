package org.dimdev.dimdoors.rift.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.util.GraphUtils;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.util.NbtUtil;
import org.dimdev.dimdoors.world.pocket.Pocket;
import org.dimdev.dimdoors.world.pocket.PocketRegistry;
import org.dimdev.dimdoors.world.pocket.PrivatePocketData;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import static net.minecraft.world.World.OVERWORLD;
import static org.dimdev.dimdoors.DimensionalDoorsInitializer.*;

public class RiftRegistry extends PersistentState {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String DATA_NAME = "rifts";

    protected DefaultDirectedGraph<RegistryVertex, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    protected Map<Location, Rift> locationMap = new HashMap<>();
    protected Map<Pocket, PocketEntrancePointer> pocketEntranceMap = new HashMap<>();
    protected Map<UUID, RegistryVertex> uuidMap = new HashMap<>();

    protected Map<UUID, PlayerRiftPointer> lastPrivatePocketEntrances = new HashMap<>(); // Player UUID -> last rift used to exit pocket
    protected Map<UUID, PlayerRiftPointer> lastPrivatePocketExits = new HashMap<>(); // Player UUID -> last rift used to enter pocket
    protected Map<UUID, PlayerRiftPointer> overworldRifts = new HashMap<>(); // Player UUID -> rift used to exit the overworld
    private final World overworld;

    public RiftRegistry(World overworld) {
        super(DATA_NAME);
        this.overworld = overworld;
    }

    public static RiftRegistry instance() {
        return getWorld(OVERWORLD).getPersistentStateManager().getOrCreate(() -> new RiftRegistry(getWorld(OVERWORLD)), DATA_NAME);
    }

    @Override
    public void fromTag(CompoundTag nbt) {
        // Read rifts in this dimension

        ListTag riftsNBT = (ListTag) nbt.get("rifts");
        for (Tag riftNBT : riftsNBT) {
            Rift rift = NbtUtil.deserialize(riftNBT, Rift.CODEC);
            graph.addVertex(rift);
            uuidMap.put(rift.id, rift);
            locationMap.put(rift.location, rift);
        }

        ListTag pocketsNBT = (ListTag) nbt.get("pockets");
        for (Tag pocketNBT : pocketsNBT) {
            PocketEntrancePointer pocket = NbtUtil.deserialize(pocketNBT, PocketEntrancePointer.CODEC);
            graph.addVertex(pocket);
            uuidMap.put(pocket.id, pocket);
            pocketEntranceMap.put(PocketRegistry.instance(pocket.world).getPocket(pocket.pocketId), pocket);
        }

        // Read the connections between links that have a source or destination in this dimension
        ListTag linksNBT = (ListTag) nbt.get("links");
        for (Tag linkNBT : linksNBT) {
            RegistryVertex from = uuidMap.get(((CompoundTag) linkNBT).getUuid("from"));
            RegistryVertex to = uuidMap.get(((CompoundTag) linkNBT).getUuid("to"));
            if (from != null && to != null) {
                graph.addEdge(from, to);
                // We need a system for detecting links that are incomplete after processing them in the other subregistry too
            }
        }

        lastPrivatePocketEntrances = readPlayerRiftPointers((ListTag) nbt.get("lastPrivatePocketEntrances"));
        lastPrivatePocketExits = readPlayerRiftPointers((ListTag) nbt.get("lastPrivatePocketExits"));
        overworldRifts = readPlayerRiftPointers((ListTag) nbt.get("overworldRifts"));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (this == null) {
        }
        // Write rifts in this dimension
        ListTag riftsNBT = new ListTag();
        ListTag pocketsNBT = new ListTag();
        for (RegistryVertex vertex : graph.vertexSet()) {
            CompoundTag vertexNBT = (CompoundTag) NbtUtil.serialize(vertex, RegistryVertex.CODEC);
            if (vertex instanceof Rift) {
                riftsNBT.add(vertexNBT);
            } else if (vertex instanceof PocketEntrancePointer) {
                pocketsNBT.add(vertexNBT);
            } else if (!(vertex instanceof PlayerRiftPointer)) {
                throw new RuntimeException("Unsupported registry vertex type " + vertex.getClass().getName());
            }
        }
        tag.put("rifts", riftsNBT);
        tag.put("pockets", pocketsNBT);

        // Write the connections between links that have a source or destination in this dimension
        ListTag linksNBT = new ListTag();
        for (DefaultEdge edge : graph.edgeSet()) {
            RegistryVertex from = graph.getEdgeSource(edge);
            RegistryVertex to = graph.getEdgeTarget(edge);
            CompoundTag linkNBT = new CompoundTag();
            linkNBT.putUuid("from", from.id);
            linkNBT.putUuid("to", to.id);
            linksNBT.add(linkNBT);
        }
        tag.put("links", linksNBT);

        // Subregistries are written automatically when the worlds are saved.
        tag.put("lastPrivatePocketEntrances", writePlayerRiftPointers(lastPrivatePocketEntrances));
        tag.put("lastPrivatePocketExits", writePlayerRiftPointers(lastPrivatePocketExits));
        tag.put("overworldRifts", writePlayerRiftPointers(overworldRifts));
        return tag;
    }

    private Map<UUID, PlayerRiftPointer> readPlayerRiftPointers(ListTag tag) {
        Map<UUID, PlayerRiftPointer> pointerMap = new HashMap<>();
        for (Tag entryNBT : tag) {
            UUID player = ((CompoundTag) entryNBT).getUuid("player");
            UUID rift = ((CompoundTag) entryNBT).getUuid("rift");
            PlayerRiftPointer pointer = new PlayerRiftPointer(player);
            pointerMap.put(player, pointer);
            uuidMap.put(pointer.id, pointer);
            graph.addVertex(pointer);
            graph.addEdge(pointer, uuidMap.get(rift));
        }
        return pointerMap;
    }

    private ListTag writePlayerRiftPointers(Map<UUID, PlayerRiftPointer> playerRiftPointerMap) {
        ListTag pointers = new ListTag();
        for (Map.Entry<UUID, PlayerRiftPointer> entry : playerRiftPointerMap.entrySet()) {
            CompoundTag entryNBT = new CompoundTag();
            entryNBT.putUuid("player", entry.getKey());
            int count = 0;
            for (DefaultEdge edge : graph.outgoingEdgesOf(entry.getValue())) {
                entryNBT.putUuid("rift", graph.getEdgeTarget(edge).id);
                count++;
            }
            if (count != 1) throw new RuntimeException("PlayerRiftPointer points to more than one rift");
            pointers.add(entryNBT);
        }
        return pointers;
    }

    public boolean isRiftAt(Location location) {
        Rift possibleRift = locationMap.get(location);
        return possibleRift != null && !(possibleRift instanceof RiftPlaceholder);
    }

    public Rift getRift(Location location) {
        Rift rift = locationMap.get(location);
        if (rift == null) throw new IllegalArgumentException("There is no rift registered at " + location);
        return rift;
    }

    private Rift getRiftOrPlaceholder(Location location) {
        Rift rift = locationMap.get(location);
        if (rift == null) {
            LOGGER.debug("Creating a rift placeholder at " + location);
            rift = new RiftPlaceholder();
            rift.world = location.world;
            rift.location = location;
            locationMap.put(location, rift);
            uuidMap.put(rift.id, rift);
            graph.addVertex(rift);
        }
        return rift;
    }

    public void addRift(Location location) {
        LOGGER.debug("Adding rift at " + location);
        RegistryVertex currentRift = locationMap.get(location);
        Rift rift;
        if (currentRift instanceof RiftPlaceholder) {
            LOGGER.info("Converting a rift placeholder at " + location + " into a rift");
            rift = new Rift(location);
            rift.world = location.world;
            rift.id = currentRift.id;
            GraphUtils.replaceVertex(graph, currentRift, rift);
        } else if (currentRift == null) {
            rift = new Rift(location);
            rift.world = location.world;
            graph.addVertex(rift);
        } else {
            throw new IllegalArgumentException("There is already a rift registered at " + location);
        }
        uuidMap.put(rift.id, rift);
        locationMap.put(location, rift);
        rift.markDirty();
    }

    public void removeRift(Location location) {
        LOGGER.debug("Removing rift at " + location);

        Rift rift = getRift(location);

        Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(rift);
        Set<DefaultEdge> outgoingEdges = graph.outgoingEdgesOf(rift);

        graph.removeVertex(rift);
        locationMap.remove(location);
        uuidMap.remove(rift.id);

        // Notify the adjacent vertices of the change
        for (DefaultEdge edge : incomingEdges) graph.getEdgeSource(edge).targetGone(rift);
        for (DefaultEdge edge : outgoingEdges) graph.getEdgeTarget(edge).sourceGone(rift);
    }

    private void addEdge(RegistryVertex from, RegistryVertex to) {
        graph.addEdge(from, to);
        if (from instanceof PlayerRiftPointer) {
            markDirty();
        } else if (from instanceof Rift) {
            ((Rift) from).markDirty();
        }
        if (to instanceof Rift) {
            ((Rift) to).markDirty();
        }
    }

    private void removeEdge(RegistryVertex from, RegistryVertex to) {
        graph.removeEdge(from, to);

        if (from instanceof PlayerRiftPointer) {
            markDirty();
        }
    }

    public void addLink(Location locationFrom, Location locationTo) {
        LOGGER.debug("Adding link " + locationFrom + " -> " + locationTo);

        Rift from = getRiftOrPlaceholder(locationFrom);
        Rift to = getRiftOrPlaceholder(locationTo);

        addEdge(from, to);

        // Notify the linked vertices of the change
        if (!(from instanceof RiftPlaceholder) && !(to instanceof RiftPlaceholder)) {
            from.targetAdded(to);
            to.sourceAdded(from);
        }
    }

    public void removeLink(Location locationFrom, Location locationTo) {
        LOGGER.debug("Removing link " + locationFrom + " -> " + locationTo);

        Rift from = getRift(locationFrom);
        Rift to = getRift(locationTo);

        removeEdge(from, to);

        // Notify the linked vertices of the change
        from.targetGone(to);
        to.sourceGone(from);
    }

    public void setProperties(Location location, LinkProperties properties) {
        LOGGER.debug("Setting DungeonLinkProperties for rift at " + location + " to " + properties);
        Rift rift = getRift(location);
        rift.properties = properties;
        rift.markDirty();
    }

    public Set<Location> getPocketEntrances(Pocket pocket) {
        PocketEntrancePointer pointer = pocketEntranceMap.get(pocket);
        if (pointer == null) {
            return Collections.emptySet();
        } else {
            return graph.outgoingEdgesOf(pointer).stream()
                    .map(graph::getEdgeTarget)
                    .map(Rift.class::cast)
                    .map(rift -> rift.location)
                    .collect(Collectors.toSet());
        }
    }

    public Location getPocketEntrance(Pocket pocket) {
        return getPocketEntrances(pocket).stream().findFirst().orElse(null);
    }

    public void addPocketEntrance(Pocket pocket, Location location) {
        LOGGER.debug("Adding pocket entrance for pocket " + pocket.id + " in dimension " + pocket.world + " at " + location);
        PocketEntrancePointer pointer = pocketEntranceMap.get(pocket);
        if (pointer == null) {
            pointer = new PocketEntrancePointer(pocket.world, pocket.id);
            pointer.world = pocket.world;
            graph.addVertex(pointer);
            pocketEntranceMap.put(pocket, pointer);
            uuidMap.put(pointer.id, pointer);
        }
        Rift rift = getRift(location);
        addEdge(pointer, rift);
    }

    public Location getPrivatePocketEntrance(UUID playerUUID) {
        // Try to get the last used entrance
        PlayerRiftPointer entrancePointer = lastPrivatePocketEntrances.get(playerUUID);
        Rift entrance = (Rift) GraphUtils.followPointer(graph, entrancePointer);
        if (entrance != null) return entrance.location;

        // If there was no last used private entrance, get the first player's private pocket entrance
        return getPocketEntrance(PrivatePocketData.instance().getPrivatePocket(playerUUID));
    }

    private void setPlayerRiftPointer(UUID playerUUID, Location rift, Map<UUID, PlayerRiftPointer> map) {
        PlayerRiftPointer pointer = map.get(playerUUID);
        if (pointer != null) {
            graph.removeVertex(pointer);
            map.remove(playerUUID);
            uuidMap.remove(pointer.id);
        }
        if (rift != null) {
            pointer = new PlayerRiftPointer(playerUUID);
            graph.addVertex(pointer);
            map.put(playerUUID, pointer);
            uuidMap.put(pointer.id, pointer);
            addEdge(pointer, getRift(rift));
        }
    }

    public void setLastPrivatePocketEntrance(UUID playerUUID, Location rift) {
        LOGGER.debug("Setting last used private pocket entrance for " + playerUUID + " at " + rift);
        setPlayerRiftPointer(playerUUID, rift, lastPrivatePocketEntrances);
    }

    public Location getPrivatePocketExit(UUID playerUUID) {
        PlayerRiftPointer entrancePointer = lastPrivatePocketExits.get(playerUUID);
        Rift entrance = (Rift) GraphUtils.followPointer(graph, entrancePointer);
        return entrance != null ? entrance.location : null;
    }

    public void setLastPrivatePocketExit(UUID playerUUID, Location rift) {
        LOGGER.debug("Setting last used private pocket exit for " + playerUUID + " at " + rift);
        setPlayerRiftPointer(playerUUID, rift, lastPrivatePocketExits);
    }

    public Location getOverworldRift(UUID playerUUID) {
        PlayerRiftPointer entrancePointer = overworldRifts.get(playerUUID);
        Rift rift = (Rift) GraphUtils.followPointer(graph, entrancePointer);
        return rift != null ? rift.location : null;
    }

    public void setOverworldRift(UUID playerUUID, Location rift) {
        LOGGER.debug("Setting last used overworld rift for " + playerUUID + " at " + rift);
        setPlayerRiftPointer(playerUUID, rift, overworldRifts);
    }

    public Collection<Rift> getRifts() {
        return locationMap.values();
    }

    public Set<Location> getTargets(Location location) {
        return graph.outgoingEdgesOf(getRift(location)).stream()
                .map(graph::getEdgeTarget)
                .map(Rift.class::cast)
                .map(rift -> rift.location)
                .collect(Collectors.toSet());
    }

    public Set<Location> getSources(Location location) {
        return graph.incomingEdgesOf(getRift(location)).stream()
                .map(graph::getEdgeTarget)
                .map(Rift.class::cast)
                .map(rift -> rift.location)
                .collect(Collectors.toSet());
    }
}
