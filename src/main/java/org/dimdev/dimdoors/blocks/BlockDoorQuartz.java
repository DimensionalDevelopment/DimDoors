package org.dimdev.dimdoors.blocks;

import org.dimdev.dimdoors.DimDoors;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import java.util.Random;

public class BlockDoorQuartz extends BlockDoor {
    public BlockDoorQuartz(Material par2Material) {
        super(par2Material);
    }

    @SideOnly(Side.CLIENT)
    protected String getTextureName() {
        return DimDoors.modid + ":" + this.getUnlocalizedName();
    }

    @Override
    public Item getItemDropped(int par1, Random par2Random, int par3) {
        return (par1 & 8) != 0 ? null : DimDoors.itemQuartzDoor;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z) {
        return DimDoors.itemQuartzDoor;
    }
}
