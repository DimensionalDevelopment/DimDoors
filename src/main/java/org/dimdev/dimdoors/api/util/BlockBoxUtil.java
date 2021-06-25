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

	public static BlockBox intersect(BlockBox box1, BlockBox box2) {
		int minX = Math.max(box1.getMinX(), box2.getMinX());
		int minY = Math.max(box1.getMinY(), box2.getMinY());
		int minZ = Math.max(box1.getMinZ(), box2.getMinZ());
		int maxX = Math.min(box1.getMaxX(), box2.getMaxX());
		int maxY = Math.min(box1.getMaxY(), box2.getMaxY());
		int maxZ = Math.min(box1.getMaxZ(), box2.getMaxZ());

		return new BlockBox(minX, minY, minZ, maxX, maxY, maxZ);
	}
}
