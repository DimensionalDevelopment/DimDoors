package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class PlayerRiftPointer extends RegistryVertex {
    public static final MapCodec<PlayerRiftPointer> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(RegistryVertex::getId)).apply(instance, PlayerRiftPointer::new));
    public static final Codec<PlayerRiftPointer> CODEC = MAP_CODEC.codec();

    public PlayerRiftPointer() {
        super();
    }

    public PlayerRiftPointer(UUID id) {
        this();
        this.id = id;
    }

    @Override
    public RegistryVertexType<? extends RegistryVertex> getType() {
        return RegistryVertexType.PLAYER;
    }

    public String toString() {
        return "PlayerRiftPointer(id=" + this.id + ")";
    }

    public static CompoundTag toNbt(PlayerRiftPointer vertex) {
        CompoundTag nbt = new CompoundTag();
        nbt.putUUID("id", vertex.id);
        return nbt;
    }

    public static PlayerRiftPointer fromNbt(CompoundTag nbt) {
        return new PlayerRiftPointer(nbt.getUUID("id"));
    }
}
