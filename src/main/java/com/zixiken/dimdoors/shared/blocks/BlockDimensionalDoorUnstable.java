package com.zixiken.dimdoors.shared.blocks;

import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.items.ModItems;
import com.zixiken.dimdoors.shared.tileentities.TileEntityEntranceRift;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class BlockDimensionalDoorUnstable extends BlockDimensionalDoor {

    public static final String ID = "unstable_dimensional_door";

    public BlockDimensionalDoorUnstable() {
        super(Material.IRON);
        setHardness(0.2F);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        setLightLevel(0.0F);
    }

    @Override
    public Item getItem() {
        return ModItems.UNSTABLE_DIMENSIONAL_DOOR;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? Items.AIR : Items.IRON_DOOR;
    }

    @Override
    public void setupRift(TileEntityEntranceRift rift) {
        // TODO
    }
}
