package com.zixiken.dimdoors.blocks;

import com.zixiken.dimdoors.shared.Location;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
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

    /**
     * called when a door is placed to determine how it will place a link
     *
     * @param location
     */
    public void placeLink(Location location);

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
