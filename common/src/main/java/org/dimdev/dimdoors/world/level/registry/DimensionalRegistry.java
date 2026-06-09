package org.dimdev.dimdoors.world.level.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.util.CodecUtils;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.PrivateRegistry;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class DimensionalRegistry extends SavedData {
    public static final Codec<Map<ResourceKey<Level>, PocketDirectory>> POCKET_DIRECTORY_MAP_CODEC =
            CodecUtils.unboundedMap(Level.RESOURCE_KEY_CODEC, PocketDirectory.CODEC);

    public static final int RIFT_DATA_VERSION = 1;

    private static final String DATA_NAME = "dimensional_registry";

    private static DimensionalRegistry INSTANCE;

    private Map<ResourceKey<Level>, PocketDirectory> pocketRegistry = new HashMap<>();
    private RiftRegistry riftRegistry = new RiftRegistry();
    private PrivateRegistry privateRegistry = new PrivateRegistry();

    public DimensionalRegistry() {
    }

    public static void init(MinecraftServer server) {
        INSTANCE = server.overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(
                        DimensionalRegistry::new,
                        DimensionalRegistry::load,
                        DataFixTypes.LEVEL
                ),
                DATA_NAME
        );
    }

    private static DimensionalRegistry load(CompoundTag nbt, HolderLookup.Provider provider) {
        DimensionalRegistry registry = new DimensionalRegistry();
        registry.readFromNbt(nbt, provider);
        return registry;
    }

    private static DimensionalRegistry getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("DimensionalRegistry has not been initialized.");
        }

        return INSTANCE;
    }

    public static Pocket<?, ?> createPocket(ResourceKey<Level> key, Pocket.PocketBuilder<?, ?> builder) {
        return getPocketDirectory(key).newPocket(key, builder);
    }

    public static void setIsDirty() {
        getInstance().setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider provider) {
        writeToNbt(nbt, provider);
        return nbt;
    }

    public void readFromNbt(CompoundTag nbt, HolderLookup.Provider provider) {
        int riftDataVersion = nbt.getInt("RiftDataVersion");
        if (riftDataVersion < RIFT_DATA_VERSION) {
            nbt = RiftSchemas.update(riftDataVersion, nbt);
        } else if (RIFT_DATA_VERSION < riftDataVersion) {
            throw new UnsupportedOperationException("Downgrading is not supported!");
        }

        this.pocketRegistry = POCKET_DIRECTORY_MAP_CODEC
                .parse(NbtOps.INSTANCE, nbt.getCompound("pocket_registry"))
                .getOrThrow();

        this.privateRegistry = new PrivateRegistry();
        this.privateRegistry.fromNbt(nbt.getCompound("private_registry"));

        this.riftRegistry = RiftRegistry.fromNbt(this.pocketRegistry, nbt.getCompound("rift_registry"));
    }

    public void writeToNbt(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.put("pocket_registry", POCKET_DIRECTORY_MAP_CODEC
                .encodeStart(NbtOps.INSTANCE, this.pocketRegistry)
                .getOrThrow());

        nbt.put("rift_registry", this.riftRegistry.toNbt());
        nbt.put("private_registry", this.privateRegistry.toNbt(new CompoundTag()));
        nbt.putInt("RiftDataVersion", RIFT_DATA_VERSION);
    }

    public static RiftRegistry getRiftRegistry() {
        return getInstance().riftRegistry;
    }

    public static PrivateRegistry getPrivateRegistry() {
        return getInstance().privateRegistry;
    }

    public static PocketDirectory getPocketDirectory(ResourceKey<Level> key) {
        if (!ModDimensions.isPocketDimension(key)) {
            throw new UnsupportedOperationException("PocketRegistry is only available for pocket dimensions!");
        }

        DimensionalRegistry registry = getInstance();
        return registry.pocketRegistry.computeIfAbsent(key, registry::createPocketRegistry);
    }

    public static PocketDirectory peekPocketDirectory(ResourceKey<Level> key) {
        if (!ModDimensions.isPocketDimension(key)) {
            return null;
        }

        return getInstance().pocketRegistry.get(key);
    }

    private PocketDirectory createPocketRegistry(ResourceKey<Level> key) {
        this.setIsDirty();
        return new PocketDirectory();
    }

    public static void forEachPocketDirectory(BiConsumer<ResourceKey<Level>, PocketDirectory> consumer) {
        getInstance().pocketRegistry.forEach(consumer);
    }

    public static boolean isValidWorld(Level level) {
        if (level == null) return false;
        return level.dimension().equals(Level.OVERWORLD);
    }

    public Map<ResourceKey<Level>, PocketDirectory> getPocketRegistry() {
        return Collections.unmodifiableMap(this.pocketRegistry);
    }
}