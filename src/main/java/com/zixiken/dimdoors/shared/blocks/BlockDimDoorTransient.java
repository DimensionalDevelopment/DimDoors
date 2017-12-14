package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.tileentities.TileEntityVerticalEntranceRift;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class BlockDimDoorTransient extends BlockDimDoorBase { // TODO: convert to a more general entrance block (like nether portals)

    public static final String ID = "transient_dimensional_door";

    public BlockDimDoorTransient() {
        super(Material.IRON);
        setHardness(1.0F);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        setDefaultState(super.getDefaultState().withProperty(OPEN, true));
    }

    @Override
    public Item getItem() {
        return null;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    protected void setupRift(TileEntityVerticalEntranceRift rift) {
        // TODO
    }
}
