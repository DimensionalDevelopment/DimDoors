package org.dimdev.dimdoors.rift.registry;

import net.minecraft.nbt.NbtCompound;
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

	public static NbtCompound toNbt(PocketEntrancePointer vertex) {
		NbtCompound nbt = new NbtCompound();
		nbt.putUuid("id", vertex.id);
		nbt.putString("pocketDim", vertex.getWorld().getValue().toString());
		nbt.putInt("pocketId", vertex.pocketId);
		return nbt;
	}

	public static PocketEntrancePointer fromNbt(NbtCompound nbt) {
		PocketEntrancePointer pointer = new PocketEntrancePointer(RegistryKey.of(Registry.WORLD_KEY, new Identifier(nbt.getString("pocketDim"))), nbt.getInt("pocketId"));
		pointer.id = nbt.getUuid("id");
		return pointer;
	}

	public int getPocketId() {
		return pocketId;
	}
}
