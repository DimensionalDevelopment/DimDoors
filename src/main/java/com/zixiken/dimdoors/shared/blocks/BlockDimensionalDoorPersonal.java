package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.items.ModItems;
import com.zixiken.dimdoors.shared.rifts.RiftDestination;
import com.zixiken.dimdoors.shared.tileentities.TileEntityEntranceRift;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class BlockDimensionalDoorPersonal extends BlockDimensionalDoor {

    public static final String ID = "quartz_dimensional_door";

    public BlockDimensionalDoorPersonal() {
        super(Material.ROCK);
        setHardness(0.1F);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }

    @Override
    public Item getItem() {
        return ModItems.PERSONAL_DIMENSIONAL_DOOR;
    }

    @Override
    public void setupRift(TileEntityEntranceRift rift) {
        RiftDestination.PrivateDestination destination = RiftDestination.PrivateDestination.builder().build();
        rift.setSingleDestination(destination);
        rift.setChaosWeight(0);
    }
}
