package org.dimdev.dimdoors.api.util;

import net.minecraft.nbt.NbtIntArray;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;

public class BlockBoxUtil {
	public static NbtIntArray toNbt(BlockBox box) {
		return new NbtIntArray(new int[]{box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ()});
	}

	public static BlockBox getBox(Chunk chunk) {
		ChunkPos pos = chunk.getPos();
		return BlockBox.create(new Vec3i(pos.getStartX(), chunk.getBottomY(), pos.getStartZ()), new Vec3i(pos.getEndX(), chunk.getTopY() - 1, pos.getEndZ()));
	}
}
