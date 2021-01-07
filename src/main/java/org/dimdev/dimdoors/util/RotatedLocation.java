package org.dimdev.dimdoors.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class RotatedLocation extends Location {
	static Codec<RotatedLocation> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				World.CODEC.fieldOf("world").forGetter(location -> location.world),
				BlockPos.CODEC.fieldOf("pos").forGetter(location -> location.pos),
				Codec.FLOAT.fieldOf("yaw").forGetter(a -> a.yaw),
				Codec.FLOAT.fieldOf("pitch").forGetter(a -> a.pitch)
		).apply(instance, RotatedLocation::new);
	});

	public final float yaw;
	public final float pitch;

	public RotatedLocation(RegistryKey<World> world, BlockPos pos, float yaw, float pitch) {
		super(world, pos);
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public static CompoundTag serialize(RotatedLocation location) {
		CompoundTag tag = new CompoundTag();
		tag.putString("world", location.world.getValue().toString());
		tag.putIntArray("pos", new int[]{location.getX(), location.getY(), location.getZ()});
		tag.putFloat("yaw", location.pitch);
		tag.putFloat("pitch", location.pitch);
		return tag;
	}

	public static RotatedLocation deserialize(CompoundTag tag) {
		int[] pos = tag.getIntArray("pos");
		return new RotatedLocation(
				RegistryKey.of(Registry.DIMENSION, new Identifier(tag.getString("world"))),
				new BlockPos(pos[0], pos[1], pos[2]),
				tag.getFloat("yaw"),
				tag.getFloat("pitch")
		);
	}
}
