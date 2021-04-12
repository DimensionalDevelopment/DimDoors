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
		NbtCompound nbt = new NbtCompound();
		nbt.putString("world", location.world.getValue().toString());
		nbt.putIntArray("pos", new int[]{location.getX(), location.getY(), location.getZ()});
		nbt.putFloat("yaw", location.pitch);
		nbt.putFloat("pitch", location.pitch);
		return nbt;
	}

	public static RotatedLocation deserialize(NbtCompound nbt) {
		int[] pos = nbt.getIntArray("pos");
		return new RotatedLocation(
				RegistryKey.of(Registry.WORLD_KEY, new Identifier(nbt.getString("world"))),
				new BlockPos(pos[0], pos[1], pos[2]),
				nbt.getFloat("yaw"),
				nbt.getFloat("pitch")
		);
	}
}
