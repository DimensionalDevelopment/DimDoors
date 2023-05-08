package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class PlayerRiftPointer extends RegistryVertex {
	public static final Codec<PlayerRiftPointer> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				UUIDUtil.CODEC.fieldOf("id").forGetter(a -> a.id),
				UUIDUtil.CODEC.fieldOf("player").forGetter(a -> a.player)
		).apply(instance, (id, player) -> {
			PlayerRiftPointer pointer = new PlayerRiftPointer(player);
			pointer.id = id;
			return pointer;
		});
	});

	private final UUID player;

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

	public static CompoundTag toNbt(PlayerRiftPointer vertex) {
		CompoundTag nbt = new CompoundTag();
		nbt.putUUID("id", vertex.id);
		nbt.putUUID("player", vertex.player);
		return nbt;
	}

	public static PlayerRiftPointer fromNbt(CompoundTag nbt) {
		return new PlayerRiftPointer(nbt.getUUID("id"));
	}

	public UUID getPlayer() {
		return player;
	}
}
