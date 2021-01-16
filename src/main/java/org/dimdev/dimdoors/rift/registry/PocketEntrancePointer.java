package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class PocketEntrancePointer extends RegistryVertex { // TODO: PocketRiftPointer superclass?
	public static final Codec<PocketEntrancePointer> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				DynamicSerializableUuid.CODEC.fieldOf("id").forGetter(a -> a.id),
				World.CODEC.fieldOf("pocketDim").forGetter(a -> a.world),
				Codec.INT.fieldOf("pocketId").forGetter(a -> a.pocketId)
		).apply(instance, (id, pocketDim, pocketId) -> {
			PocketEntrancePointer pointer = new PocketEntrancePointer(pocketDim, pocketId);
			pointer.id = id;
			return pointer;
		});
	});

	public int pocketId;

	public PocketEntrancePointer(RegistryKey<World> pocketDim, int pocketId) {
		this.world = pocketDim;
		this.pocketId = pocketId;
	}

	public PocketEntrancePointer() {
	}

	@Override
	public RegistryVertexType<? extends RegistryVertex> getType() {
		return RegistryVertexType.ENTRANCE;
	}

	public String toString() {
		return "PocketEntrancePointer(pocketDim=" + this.world + ", pocketId=" + this.pocketId + ")";
	}

	public static CompoundTag toTag(PocketEntrancePointer vertex) {
		CompoundTag tag = new CompoundTag();
		tag.putUuid("id", vertex.id);
		tag.putString("pocketDim", vertex.world.getValue().toString());
		tag.putInt("pocketId", vertex.pocketId);
		return tag;
	}

	public static PocketEntrancePointer fromTag(CompoundTag tag) {
		PocketEntrancePointer pointer = new PocketEntrancePointer(RegistryKey.of(Registry.DIMENSION, new Identifier(tag.getString("pocketDim"))), tag.getInt("pocketId"));
		pointer.id = tag.getUuid("id");
		return pointer;
	}
}
