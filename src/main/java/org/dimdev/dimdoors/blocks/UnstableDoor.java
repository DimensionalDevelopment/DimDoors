package org.dimdev.dimdoors.blocks;

import org.dimdev.dimdoors.config.DDProperties;
import org.dimdev.dimdoors.core.LinkType;
import org.dimdev.dimdoors.core.NewDimData;
import org.dimdev.dimdoors.core.PocketManager;
import org.dimdev.dimdoors.DimDoors;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import java.util.Random;

public class UnstableDoor extends BaseDimDoor {
    public UnstableDoor(Material material, DDProperties properties) {
        super(material, properties);
    }

    @Override
    public void placeLink(World world, int x, int y, int z) {
        if (!world.isRemote && world.getBlock(x, y - 1, z) == this) {
            NewDimData dimension = PocketManager.getDimensionData(world);
            dimension.createLink(x, y, z, LinkType.RANDOM, world.getBlockMetadata(x, y - 1, z));
        }
    }

    @Override
    public Item getDoorItem() {
        return DimDoors.itemUnstableDoor;
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return Items.iron_door;
    }
}