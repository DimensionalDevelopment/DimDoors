package org.dimdev.dimdoors.world.level.registry;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.world.pocket.PocketRegistry;
import org.dimdev.dimdoors.world.pocket.PrivateRegistry;
import org.jetbrains.annotations.NotNull;

public class DimensionalRegistry {
	public static final int RIFT_DATA_VERSION = 1; // Increment this number every time a new schema is added
	private static PocketRegistry pocketRegistry = new PocketRegistry();
	private static RiftRegistry riftRegistry = new RiftRegistry();
	private static PrivateRegistry privateRegistry = new PrivateRegistry();

	private static class DummyData extends SavedData {
		private static final DummyData INSTANCE = new DummyData();

		@Override
		public @NotNull CompoundTag save(CompoundTag nbt, HolderLookup.Provider provider) {
            var op = RegistryOps.create(NbtOps.INSTANCE, provider);

            PocketRegistry.CODEC.encodeStart(op, pocketRegistry).result().ifPresent(tag -> nbt.put("pocket_registry", tag));
            RiftRegistry.CODEC.encodeStart(op, riftRegistry).result().ifPresent(tag -> nbt.put("rift_registry", tag));
            PrivateRegistry.CODEC.encodeStart(op, privateRegistry).result().ifPresent(tag -> nbt.put("private_registry", tag));
            nbt.putInt("RiftDataVersion", RIFT_DATA_VERSION);

			return nbt;
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
        var ops = RegistryOps.create(NbtOps.INSTANCE, provider);

		int riftDataVersion = nbt.getInt("RiftDataVersion");
		if (riftDataVersion < RIFT_DATA_VERSION) {
			nbt = RiftSchemas.update(riftDataVersion, nbt);
		} else if (RIFT_DATA_VERSION < riftDataVersion) {
			throw new UnsupportedOperationException("Downgrading is not supported!");
		}

        pocketRegistry = PocketRegistry.CODEC.parse(ops, nbt.getCompound("pocket_registry")).getOrThrow();
        privateRegistry = PrivateRegistry.CODEC.parse(ops, nbt.getCompound("private_registry")).getOrThrow();
        riftRegistry = RiftRegistry.CODEC.parse(ops, nbt.getCompound("rift_registry")).getOrThrow();
    }

	public static RiftRegistry getRiftRegistry() {
		return riftRegistry;
	}

	public static PrivateRegistry getPrivateRegistry() {
		return privateRegistry;
	}

	public static PocketRegistry getPocketDirectory() {
		return pocketRegistry;
	}

}
