package org.dimdev.dimdoors.world.pocket;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Global pocket index.
 *
 * Requires WorldInfo.CODEC.
 */
public final class PocketRegistry {
    public record PocketRecord(UUID id, WorldInfo worldInfo) {
        public static final Codec<PocketRecord> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("id").forGetter(PocketRecord::id),
                WorldInfo.CODEC.fieldOf("world_info").forGetter(PocketRecord::worldInfo)
        ).apply(instance, PocketRecord::new));
    }

    public static final Codec<PocketRegistry> CODEC = PocketRecord.CODEC.listOf().comapFlatMap(
            PocketRegistry::fromRecords,
            registry -> new ArrayList<>(registry.byId.values())
    );

    private final Map<UUID, PocketRecord> byId = new HashMap<>();
    private final Map<ResourceKey<Level>, UUID> byWorld = new HashMap<>();

    public PocketRegistry() {
    }

    private static DataResult<PocketRegistry> fromRecords(List<PocketRecord> records) {
        PocketRegistry registry = new PocketRegistry();

        for (PocketRecord record : records) {
            if (registry.byId.containsKey(record.id())) {
                return DataResult.error(() -> "Duplicate pocket id: " + record.id());
            }
            if (registry.byWorld.containsKey(record.world())) {
                return DataResult.error(() -> "Duplicate pocket world: " + record.world().location());
            }

            registry.byId.put(record.id(), record);
            registry.byWorld.put(record.world(), record.id());
        }

        return DataResult.success(registry);
    }

    public void add(UUID id, WorldInfo worldInfo) {
        add(new PocketRecord(id, worldInfo));
    }

    public void add(PocketRecord record) {
        PocketRecord existingById = byId.get(record.id());
        if (existingById != null && !existingById.world().equals(record.world())) {
            throw new IllegalStateException("Pocket id " + record.id() + " already mapped to " + existingById.world().location());
        }

        UUID existingByWorld = byWorld.get(record.world());
        if (existingByWorld != null && !existingByWorld.equals(record.id())) {
            throw new IllegalStateException("World " + record.world().location() + " already mapped to pocket " + existingByWorld);
        }

        byId.put(record.id(), record);
        byWorld.put(record.world(), record.id());
    }

    public @Nullable PocketRecord get(UUID id) {
        return byId.get(id);
    }

    public @Nullable PocketRecord get(ResourceKey<Level> world) {
        UUID id = byWorld.get(world);
        return id == null ? null : byId.get(id);
    }

    public boolean contains(UUID id) {
        return byId.containsKey(id);
    }

    public boolean containsWorld(ResourceKey<Level> world) {
        return byWorld.containsKey(world);
    }

    public void remove(UUID id) {
        PocketRecord removed = byId.remove(id);
        if (removed != null) {
            byWorld.remove(removed.world());
        }
    }

    public void remove(ResourceKey<Level> world) {
        UUID id = byWorld.remove(world);
        if (id != null) {
            byId.remove(id);
        }
    }

    public Collection<PocketRecord> values() {
        return Collections.unmodifiableCollection(byId.values());
    }

    public UUID createPocket(Holder<PocketGenerator> generator) {
        var id = UUID.randomUUID();

        while (byId.containsKey(id)) {
            id = UUID.randomUUID();
        }

        var worldId = ResourceKey.create(Registries.DIMENSION, DimensionalDoors.id("pocket/" + id));

        var info = new WorldInfo(worldId, generator);

        add(id, info);

        info.getLevel().getChunkSource().




        return id;
    }
}