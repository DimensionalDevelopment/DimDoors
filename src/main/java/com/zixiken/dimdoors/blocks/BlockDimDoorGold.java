package com.zixiken.dimdoors.blocks;

import com.zixiken.dimdoors.items.ModItems;
import com.zixiken.dimdoors.shared.Location;
import com.zixiken.dimdoors.tileentities.TileEntityDimDoorGold;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDimDoorGold extends BlockDimDoorBase {

    public static final String ID = "blockDimDoorGold";

    public BlockDimDoorGold() {
        super(Material.IRON);
        setHardness(1.0F);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }

    @Override
    public void placeLink(Location location) {
    }

    @Override
    public Item getItemDoor() {
        return ModItems.itemDimDoorGold;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityDimDoorGold();
    }

}
