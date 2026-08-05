package org.dimdev.dimdoors.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class PocketFogDebug {
    public static final BoundingBox TEST_BOX = BoundingBox.fromCorners(new BlockPos(64, 64, 64), new BlockPos(64 + 32, 64 + 32, 64 + 32));

    private PocketFogDebug() {
    }
}
