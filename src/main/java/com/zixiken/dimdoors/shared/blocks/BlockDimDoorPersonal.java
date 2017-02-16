package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.shared.items.ModItems;
import com.zixiken.dimdoors.shared.tileentities.TileEntityDimDoorPersonal;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockDimDoorPersonal extends BlockDimDoorBase {

    public static final String ID = "blockDimDoorPersonal";

    public BlockDimDoorPersonal() {
        super(Material.ROCK);
        setHardness(0.1F);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }

    @Override
    public Item getItemDoor() {
        return ModItems.itemDimDoorPersonal;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityDimDoorPersonal();
    }

}
