package org.dimdev.dimdoors.api.util;

import net.minecraft.util.math.BlockBox;

public class BlockBoxUtil {
	// intersection might be non real box, check with isRealBox
	public static BlockBox intersection(BlockBox box1, BlockBox box2) {
		return new BlockBox(Math.max(box1.minX, box2.minX), Math.max(box1.minY, box2.minY), Math.max(box1.minZ, box2.minZ), Math.min(box1.maxX, box2.maxX), Math.min(box1.maxY, box2.maxY), Math.min(box1.maxZ, box2.maxZ));
	}

	public static boolean isRealBox(BlockBox box) {
		return box.minX <= box.maxX && box.minY <= box.maxY && box.minZ <= box.maxZ;
	}
}
