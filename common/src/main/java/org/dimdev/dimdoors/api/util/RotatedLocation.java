package org.dimdev.dimdoors.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class RotatedLocation extends Location {
	public final float yaw;
	public final float pitch;

	public RotatedLocation(ResourceKey<Level> world, BlockPos pos, float yaw, float pitch) {
		super(world, pos);
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public static CompoundTag serialize(RotatedLocation location) {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("world", location.world.location().toString());
		nbt.putIntArray("pos", new int[]{location.getX(), location.getY(), location.getZ()});
		nbt.putFloat("yaw", location.pitch);
		nbt.putFloat("pitch", location.pitch);
		return nbt;
	}

	public static RotatedLocation deserialize(CompoundTag nbt) {
		int[] pos = nbt.getIntArray("pos");
		return new RotatedLocation(
				ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(nbt.getString("world"))),
				new BlockPos(pos[0], pos[1], pos[2]),
				nbt.getFloat("yaw"),
				nbt.getFloat("pitch")
		);
	}
}
