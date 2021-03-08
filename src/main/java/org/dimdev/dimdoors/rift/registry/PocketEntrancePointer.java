package org.dimdev.dimdoors.rift.registry;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class PocketEntrancePointer extends RegistryVertex { // TODO: PocketRiftPointer superclass?
	private int pocketId;

	public PocketEntrancePointer(RegistryKey<World> pocketDim, int pocketId) {
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

	public static CompoundTag toTag(PocketEntrancePointer vertex) {
		CompoundTag tag = new CompoundTag();
		tag.putUuid("id", vertex.id);
		tag.putString("pocketDim", vertex.getWorld().getValue().toString());
		tag.putInt("pocketId", vertex.pocketId);
		return tag;
	}

	public static PocketEntrancePointer fromTag(CompoundTag tag) {
		PocketEntrancePointer pointer = new PocketEntrancePointer(RegistryKey.of(Registry.DIMENSION, new Identifier(tag.getString("pocketDim"))), tag.getInt("pocketId"));
		pointer.id = tag.getUuid("id");
		return pointer;
	}

	public int getPocketId() {
		return pocketId;
	}
}
