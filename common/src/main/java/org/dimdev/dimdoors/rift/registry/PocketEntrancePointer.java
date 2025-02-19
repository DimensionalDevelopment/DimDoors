package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class PocketEntrancePointer extends RegistryVertex {
	public static final MapCodec<PocketEntrancePointer> CODEC = RecordCodecBuilder.<PocketEntrancePointer>mapCodec(instance -> instance.group(
			UUIDUtil.CODEC.fieldOf("id").forGetter(RegistryVertex::getId),
			ResourceKey.codec(Registries.DIMENSION).fieldOf("pocketDim").forGetter(RegistryVertex::getWorld),
			Codec.INT.fieldOf("pocketId").forGetter(PocketEntrancePointer::getPocketId)
	).apply(instance, (id, pocketDim, pocketId) -> {
		var entrance = new PocketEntrancePointer(pocketDim, pocketId);
		entrance.setId(id);
		return entrance;
	})); // TODO: PocketRiftPointer superclass?
	private int pocketId;

	public PocketEntrancePointer(ResourceKey<Level> pocketDim, int pocketId) {
		this.setWorld(pocketDim);
		this.pocketId = pocketId;
	}

	public PocketEntrancePointer() {
	}

	@Override
	public RegistryVertexType<? extends RegistryVertex> getType() {
		return RegistryVertexType.ENTRANCE.get();
	}

	public String toString() {
		return "PocketEntrancePointer(pocketDim=" + this.getWorld() + ", pocketId=" + this.pocketId + ")";
	}

	public int getPocketId() {
		return pocketId;
	}
}
