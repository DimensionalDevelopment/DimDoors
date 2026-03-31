package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class PocketEntrancePointer extends RegistryVertex { // TODO: PocketRiftPointer superclass?
    public static final MapCodec<PocketEntrancePointer> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).and(UUIDUtil.CODEC.fieldOf("pocketId").forGetter(a -> a.pocketId)).apply(instance, PocketEntrancePointer::new));

	private final UUID pocketId;

	public PocketEntrancePointer(UUID pocketId) {
        super();
		this.pocketId = pocketId;
	}

    public PocketEntrancePointer(UUID id, UUID pocketId) {
        super(id);
        this.pocketId = pocketId;
    }


	@Override
	public RegistryVertexType<? extends RegistryVertex> getType() {
		return RegistryVertexType.ENTRANCE.get();
	}

	public static CompoundTag toNbt(PocketEntrancePointer vertex) {
		CompoundTag nbt = new CompoundTag();
		nbt.putUUID("id", vertex.id);
		nbt.putString("pocketDim", vertex.getWorld().location().toString());
		nbt.putInt("pocketId", vertex.pocketId);
		return nbt;
	}

	public static PocketEntrancePointer fromNbt(CompoundTag nbt) {
		PocketEntrancePointer pointer = new PocketEntrancePointer(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(nbt.getString("pocketDim"))), nbt.getInt("pocketId"));
		pointer.id = nbt.getUUID("id");
		return pointer;
	}

	public UUID getPocketId() {
		return pocketId;
	}
}