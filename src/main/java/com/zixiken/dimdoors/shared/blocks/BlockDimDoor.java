package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.items.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class BlockDimDoor extends BlockDimDoorBase {

    public static final String ID = "dimensional_door";

    public BlockDimDoor() {
        super(Material.IRON);
        setHardness(1.0F);
        setResistance(2000.0F);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }

    @Override
    public Item getItemDoor() {
        return ModItems.DIMENSIONAL_DOOR;
    }
}
