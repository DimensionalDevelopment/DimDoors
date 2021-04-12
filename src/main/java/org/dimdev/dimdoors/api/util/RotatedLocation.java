package org.dimdev.dimdoors.api.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class RotatedLocation extends Location {
	public final float yaw;
	public final float pitch;

	public RotatedLocation(RegistryKey<World> world, BlockPos pos, float yaw, float pitch) {
		super(world, pos);
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public static NbtCompound serialize(RotatedLocation location) {
		NbtCompound tag = new NbtCompound();
		tag.putString("world", location.world.getValue().toString());
		tag.putIntArray("pos", new int[]{location.getX(), location.getY(), location.getZ()});
		tag.putFloat("yaw", location.pitch);
		tag.putFloat("pitch", location.pitch);
		return tag;
	}

	public static RotatedLocation deserialize(NbtCompound tag) {
		int[] pos = tag.getIntArray("pos");
		return new RotatedLocation(
				RegistryKey.of(Registry.WORLD_KEY, new Identifier(tag.getString("world"))),
				new BlockPos(pos[0], pos[1], pos[2]),
				tag.getFloat("yaw"),
				tag.getFloat("pitch")
		);
	}
}
