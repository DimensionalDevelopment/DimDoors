package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public class PlayerRiftPointer extends RegistryVertex {
	public static final MapCodec<PlayerRiftPointer> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(a -> a.id),
            UUIDUtil.CODEC.fieldOf("player").forGetter(a -> a.player)
    ).apply(instance, (id, player) -> {
        PlayerRiftPointer pointer = new PlayerRiftPointer(player);
        pointer.id = id;
        return pointer;
    }));

	private final UUID player;

	public PlayerRiftPointer(UUID player) {
		this.player = player;
	}

	@Override
	public RegistryVertexType<? extends RegistryVertex> getType() {
		return RegistryVertexType.PLAYER.get();
	}

	public String toString() {
		return "PlayerRiftPointer(player=" + this.player + ")";
	}

	public UUID getPlayer() {
		return player;
	}
}
