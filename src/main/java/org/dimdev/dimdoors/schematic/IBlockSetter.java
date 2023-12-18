package org.dimdev.dimdoors.schematic;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public interface IBlockSetter {
    void setBlock(World world, int x, int y, int z, Block block, int metadata);
}
