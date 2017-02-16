package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.shared.items.ModItems;
import com.zixiken.dimdoors.shared.tileentities.TileEntityDimDoorChaos;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockDimDoorChaos extends BlockDimDoorBase {

    public static final String ID = "blockDimDoorChaos";

    public BlockDimDoorChaos() {
        super(Material.IRON);
        setHardness(.2F);
        setUnlocalizedName(ID);
        setRegistryName(ID);
        setLightLevel(.0F);
    }

    @Override
    public Item getItemDoor() {
        return ModItems.itemDimDoorChaos;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random random, int fortune) {
        return Items.IRON_DOOR;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityDimDoorChaos();
    }
}
