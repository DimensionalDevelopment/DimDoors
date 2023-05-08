package org.dimdev.dimdoors.rift.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class PocketEntrancePointer extends RegistryVertex { // TODO: PocketRiftPointer superclass?
	private int pocketId;

	public PocketEntrancePointer(ResourceKey<Level> pocketDim, int pocketId) {
		this.setWorld(pocketDim);
		this.pocketId = pocketId;
	}

	public PocketEntrancePointer() {
	}

	@Override
	public RegistryVertexType<? extends RegistryVertex> getType() {
		return RegistryVertexType.ENTRANCE;
	}

	public String toString() {
		return "PocketEntrancePointer(pocketDim=" + this.getWorld() + ", pocketId=" + this.pocketId + ")";
	}

	public static CompoundTag toNbt(PocketEntrancePointer vertex) {
		CompoundTag nbt = new CompoundTag();
		nbt.putUUID("id", vertex.id);
		nbt.putString("pocketDim", vertex.getWorld().location().toString());
		nbt.putInt("pocketId", vertex.pocketId);
		return nbt;
	}

	public static PocketEntrancePointer fromNbt(CompoundTag nbt) {
		PocketEntrancePointer pointer = new PocketEntrancePointer(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(nbt.getString("pocketDim"))), nbt.getInt("pocketId"));
		pointer.id = nbt.getUUID("id");
		return pointer;
	}

	public int getPocketId() {
		return pocketId;
	}
}
