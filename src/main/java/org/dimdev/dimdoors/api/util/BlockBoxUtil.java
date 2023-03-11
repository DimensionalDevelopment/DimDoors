package org.dimdev.dimdoors.api.util;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class BlockBoxUtil {
	public static IntArrayTag toNbt(BoundingBox box) {
		return new IntArrayTag(new int[]{box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ()});
	}

	public static BoundingBox getBox(ChunkAccess chunk) {
		ChunkPos pos = chunk.getPos();
		return BoundingBox.fromCorners(new Vec3i(pos.getMinBlockX(), chunk.getMinBuildHeight(), pos.getMinBlockZ()), new Vec3i(pos.getMaxBlockX(), chunk.getMaxBuildHeight() - 1, pos.getMaxBlockZ()));
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
