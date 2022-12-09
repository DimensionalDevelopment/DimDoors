package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Uuids;
<<<<<<< HEAD
=======

import java.util.UUID;
>>>>>>> 1094dcf08ea591e210aafa16d4b4c1141a2fae7b

public class PlayerRiftPointer extends RegistryVertex {
	public static final Codec<PlayerRiftPointer> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				Uuids.CODEC.fieldOf("id").forGetter(a -> a.id),
				Uuids.CODEC.fieldOf("player").forGetter(a -> a.player)
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

	public static NbtCompound toNbt(PlayerRiftPointer vertex) {
		NbtCompound nbt = new NbtCompound();
		nbt.putUuid("id", vertex.id);
		nbt.putUuid("player", vertex.player);
		return nbt;
	}

	public static PlayerRiftPointer fromNbt(NbtCompound nbt) {
		return new PlayerRiftPointer(nbt.getUuid("id"));
	}

	public UUID getPlayer() {
		return player;
	}
}
