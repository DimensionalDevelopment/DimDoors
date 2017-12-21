package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.items.ModItems;
import com.zixiken.dimdoors.shared.tileentities.TileEntityEntranceRift;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class BlockDimensionalDoorGold extends BlockDimensionalDoor {

    public static final String ID = "gold_dimensional_door";

    public BlockDimensionalDoorGold() {
        super(Material.IRON);
        setHardness(1.0F);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }

    @Override
    public Item getItem() {
        return ModItems.GOLD_DIMENSIONAL_DOOR;
    }

    @Override
    public void setupRift(TileEntityEntranceRift rift) {
        // TODO
    }
}
