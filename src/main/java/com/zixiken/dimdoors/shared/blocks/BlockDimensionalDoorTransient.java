package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.tileentities.TileEntityEntranceRift;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class BlockDimensionalDoorTransient extends BlockDimensionalDoor { // TODO: convert to a more general entrances block (like nether portals)

    public static final String ID = "transient_dimensional_door";

    public BlockDimensionalDoorTransient() {
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
    public void setupRift(TileEntityEntranceRift rift) {
        // TODO
    }
}
