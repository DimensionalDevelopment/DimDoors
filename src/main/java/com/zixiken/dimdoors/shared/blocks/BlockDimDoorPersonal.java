package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.items.ModItems;
import com.zixiken.dimdoors.shared.rifts.RiftDestination;
import com.zixiken.dimdoors.shared.tileentities.TileEntityVerticalEntranceRift;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BlockDimDoorPersonal extends BlockDimDoorBase {

    public static final String ID = "quartz_dimensional_door";

    public BlockDimDoorPersonal() {
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
    protected void setupRift(TileEntityVerticalEntranceRift rift) {
        RiftDestination.PrivateDestination destination = RiftDestination.PrivateDestination.builder().build();
        rift.setSingleDestination(destination);
    }
}
