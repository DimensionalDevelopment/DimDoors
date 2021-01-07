package org.dimdev.dimdoors.rift.registry;

import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.dynamic.DynamicSerializableUuid;

public class PlayerRiftPointer extends RegistryVertex {
    public static final Codec<PlayerRiftPointer> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                DynamicSerializableUuid.CODEC.fieldOf("id").forGetter(a -> a.id),
                DynamicSerializableUuid.CODEC.fieldOf("player").forGetter(a -> a.player)
        ).apply(instance, (id, player) -> {
            PlayerRiftPointer pointer = new PlayerRiftPointer(player);
            pointer.id = id;
            return pointer;
        });
    });

    public UUID player;

    public PlayerRiftPointer(UUID player) {
        this.player = player;
    }

    @Override
    public RegistryVertexType<? extends RegistryVertex> getType() {
        return RegistryVertexType.PLAYER;
    }

    public String toString() {
        return "PlayerRiftPointer(player=" + this.player + ")";
    }

    public static CompoundTag toTag(PlayerRiftPointer vertex) {
        CompoundTag tag = new CompoundTag();
        tag.putUuid("id", vertex.id);
        tag.putUuid("player", vertex.player);
        return tag;
    }

    public static PlayerRiftPointer fromTag(CompoundTag tag) {
        PlayerRiftPointer vertex = new PlayerRiftPointer(tag.getUuid("id"));
        vertex.player = tag.getUuid("player");
        return vertex;
    }
}
