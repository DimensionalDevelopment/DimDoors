package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.items.ModItems;
import com.zixiken.dimdoors.shared.tileentities.TileEntityEntranceRift;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class BlockDimensionalDoorWarp extends BlockDimensionalDoor {

    public static final String ID = "warp_dimensional_door";

    public BlockDimensionalDoorWarp() {
        super(Material.WOOD);
        setHardness(1.0F);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }

    @Override
    public Item getItem() {
        return ModItems.WARP_DIMENSIONAL_DOOR;
    }

    @Override
    public void setupRift(TileEntityEntranceRift rift) {
        // TODO
    }
}
