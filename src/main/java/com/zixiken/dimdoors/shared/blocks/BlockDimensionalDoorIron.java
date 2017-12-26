package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.items.ModItems;
import com.zixiken.dimdoors.shared.rifts.NewPublicDestination;
import com.zixiken.dimdoors.shared.tileentities.TileEntityEntranceRift;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class BlockDimensionalDoorIron extends BlockDimensionalDoor {

    public static final String ID = "dimensional_door";

    public BlockDimensionalDoorIron() {
        super(Material.IRON);
        setHardness(1.0F);
        setResistance(2000.0F);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }

    @Override
    public Item getItem() {
        return ModItems.DIMENSIONAL_DOOR;
    }

    @Override
    public void setupRift(TileEntityEntranceRift rift) {
        NewPublicDestination destination = NewPublicDestination.builder().build();
        rift.setSingleDestination(destination);
    }
}
