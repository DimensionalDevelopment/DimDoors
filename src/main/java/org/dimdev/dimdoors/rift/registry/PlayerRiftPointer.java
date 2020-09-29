package org.dimdev.dimdoors.rift.registry;

import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.dynamic.DynamicSerializableUuid;

public class PlayerRiftPointer extends RegistryVertex {
    public static final Codec<PlayerRiftPointer> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                DynamicSerializableUuid.field_25122.fieldOf("id").forGetter(a -> a.id),
                DynamicSerializableUuid.field_25122.fieldOf("player").forGetter(a -> a.player)
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
}
