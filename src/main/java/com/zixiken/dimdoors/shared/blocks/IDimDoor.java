package com.zixiken.dimdoors.shared.blocks;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IDimDoor {

    /**
     * A function to enter a dim door and traverse its link, called when a
     * player collides with an open door
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param entity
     */
    public void enterDimDoor(World world, BlockPos pos, Entity entity);

    public Item getItemDoor();

    /**
     * checks if any of this doors blocks are overlapping with a rift
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @return
     */
    public boolean isDoorOnRift(World world, BlockPos pos);

}
