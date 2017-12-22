package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.tileentities.TileEntityEntranceRift;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public class BlockDimensionalTrapdoorWood extends BlockDimensionalTrapdoor {

    public static final String ID = "dimensional_trapdoor";

    public BlockDimensionalTrapdoorWood() {
        super(Material.WOOD);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        setUnlocalizedName(ID);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setHardness(1.0F);
        setSoundType(SoundType.WOOD);
    }

    @Override
    public void setupRift(TileEntityEntranceRift rift) {
        // TODO
    }
}
