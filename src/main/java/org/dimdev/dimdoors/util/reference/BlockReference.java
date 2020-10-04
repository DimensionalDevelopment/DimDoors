package org.dimdev.dimdoors.util.reference;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockReference {
    public final World world;
    public final int x;
    public final int y;
    public final int z;

    public BlockReference(World world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPos getBlockPos() {
        return new BlockPos(x, y, z);
    }
}
