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

import java.util.UUID;

public class PocketEntrancePointer extends RegistryVertex { // TODO: PocketRiftPointer superclass?
    public static final MapCodec<PocketEntrancePointer> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(PocketEntrancePointer::getId),
            Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(PocketEntrancePointer::getWorld),
            Codec.INT.fieldOf("pocket_id").forGetter(PocketEntrancePointer::getPocketId)
    ).apply(instance, PocketEntrancePointer::new));
    public static final Codec<PocketEntrancePointer> CODEC = MAP_CODEC.codec();

    private int pocketId;

    public PocketEntrancePointer(ResourceKey<Level> pocketDim, int pocketId) {
        this.setWorld(pocketDim);
        this.pocketId = pocketId;
    }

    private PocketEntrancePointer(UUID id, ResourceKey<Level> pocketDim, int pocketId) {
        this(pocketDim, pocketId);
        this.id = id;
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
        PocketEntrancePointer pointer = new PocketEntrancePointer(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(nbt.getString("pocketDim"))), nbt.getInt("pocketId"));
        pointer.id = nbt.getUUID("id");
        return pointer;
    }

    public int getPocketId() {
        return pocketId;
    }
}
