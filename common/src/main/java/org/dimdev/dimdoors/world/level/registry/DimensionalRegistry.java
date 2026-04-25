package org.dimdev.dimdoors.world.level.registry;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.dimdev.dimdoors.api.util.StreamUtils;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.PrivateRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DimensionalRegistry {
    public static final int RIFT_DATA_VERSION = 1; // Increment this number every time a new schema is added
    private static Map<ResourceKey<Level>, PocketDirectory> pocketRegistry = new HashMap<>();
    private static RiftRegistry riftRegistry = new RiftRegistry();
    private static PrivateRegistry privateRegistry = new PrivateRegistry();

    private static class DummyData extends SavedData {
    private static final DummyData INSTANCE = new DummyData();

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        writeToNbt(compoundTag, provider);

        return compoundTag;
    }
    }

    public static void setDirty() {
        DummyData.INSTANCE.setDirty();
    }

    public static void init(MinecraftServer server) {
    server.overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<DummyData>(() -> DummyData.INSTANCE, (compoundTag, provider) -> {
            readFromNbt(compoundTag, provider);
            return DummyData.INSTANCE;
        }, DataFixTypes.LEVEL /*TODO: FIgure out if correct for a singlemon data*/), "dimensional_registry");
    }

    public static void readFromNbt(CompoundTag nbt, HolderLookup.Provider provider) {
    int riftDataVersion = nbt.getInt("RiftDataVersion");
    if (riftDataVersion < RIFT_DATA_VERSION) {
        nbt = RiftSchemas.update(riftDataVersion, nbt);
    } else if (RIFT_DATA_VERSION < riftDataVersion) {
        throw new UnsupportedOperationException("Downgrading is not supported!");
    }

    CompoundTag pocketRegistryNbt = nbt.getCompound("pocket_registry");

    pocketRegistry.clear();
        for(var key : pocketRegistryNbt.getAllKeys()) {
            CompoundTag pocketDirectoryNbt = pocketRegistryNbt.getCompound(key);
            pocketRegistry.put(ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(key)), PocketDirectory.readFromNbt(key, pocketDirectoryNbt, provider));
        }

    CompoundTag privateRegistryNbt = nbt.getCompound("private_registry");

        privateRegistry = new PrivateRegistry();
        privateRegistry.fromNbt(privateRegistryNbt);

        CompoundTag riftRegistryNbt = nbt.getCompound("rift_registry");
        riftRegistry = RiftRegistry.fromNbt(pocketRegistry, riftRegistryNbt);
    }

    public static void writeToNbt(CompoundTag nbt, HolderLookup.Provider provider) {
        CompoundTag pocketRegistryNbt = new CompoundTag();
        pocketRegistry.forEach((key, value) -> {
            pocketRegistryNbt.put(key.location().toString(), value.writeToNbt(provider));
        });

        nbt.put("pocket_registry", pocketRegistryNbt);
        nbt.put("rift_registry", riftRegistry.toNbt());
        nbt.put("private_registry", privateRegistry.toNbt(new CompoundTag()));
        nbt.putInt("RiftDataVersion", RIFT_DATA_VERSION);
    }

    public static RiftRegistry getRiftRegistry() {
    return riftRegistry;
    }

    public static PrivateRegistry getPrivateRegistry() {
    return privateRegistry;
    }

    public static PocketDirectory getPocketDirectory(ResourceKey<Level> key) {
    if (!(ModDimensions.isPocketDimension(key))) {
        throw new UnsupportedOperationException("PocketRegistry is only available for pocket dimensions!");
    }

    return pocketRegistry.computeIfAbsent(key, DimensionalRegistry::createPocketRegistry);
    }

    public static PocketDirectory peekPocketDirectory(ResourceKey<Level> key) {
    if (!(ModDimensions.isPocketDimension(key))) {
        return null;
    }

    return pocketRegistry.get(key);
    }

    private static PocketDirectory createPocketRegistry(ResourceKey<Level> key) {
        setDirty();
        return new PocketDirectory(key);
    }

    private static Map<ResourceKey<Level>, PocketDirectory> getPocketDirectories() {
    return pocketRegistry;
    }

    public static boolean isValidWorld(Level level) {
        if (level == null) return false;
        return level.dimension().equals(Level.OVERWORLD);
    }

    public Map<ResourceKey<Level>, PocketDirectory> getPocketRegistry() {
    return pocketRegistry;
    }
}
