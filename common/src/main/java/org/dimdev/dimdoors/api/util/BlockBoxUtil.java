package org.dimdev.dimdoors.api.util;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class BlockBoxUtil {
	public static IntArrayTag toNbt(BoundingBox box) {
		return new IntArrayTag(new int[]{box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ()});
	}

	public static BoundingBox getBox(ChunkAccess chunk) {
		ChunkPos pos = chunk.getPos();
		return BoundingBox.fromCorners(new Vec3i(pos.getStartX(), chunk.getBottomY(), pos.getStartZ()), new Vec3i(pos.getEndX(), chunk.getTopY() - 1, pos.getEndZ()));
	}

	public static BoundingBox intersect(BoundingBox box1, BoundingBox box2) {
		int minX = Math.max(box1.minX(), box2.minX());
		int minY = Math.max(box1.minY(), box2.minY());
		int minZ = Math.max(box1.minZ(), box2.minZ());
		int maxX = Math.min(box1.maxX(), box2.maxX());
		int maxY = Math.min(box1.maxY(), box2.maxY());
		int maxZ = Math.min(box1.maxZ(), box2.maxZ());

		return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
	}
}
