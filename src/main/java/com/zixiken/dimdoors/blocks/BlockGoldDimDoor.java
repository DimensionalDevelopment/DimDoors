package com.zixiken.dimdoors.blocks;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.core.DimData;
import com.zixiken.dimdoors.core.DimLink;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.tileentities.TileEntityDimDoorGold;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockGoldDimDoor extends BaseDimDoor {
    public static final String ID = "dimDoorGold";

	public BlockGoldDimDoor() {
		super(Material.iron);
        setHardness(1.0F);
        setUnlocalizedName(ID);
	}

	@Override
	public void placeLink(World world, BlockPos pos) {
		if (!world.isRemote && world.getBlockState(pos.down()).getBlock() == this) {
			DimData dimension = PocketManager.createDimensionData(world);
			DimLink link = dimension.getLink(pos);
			if (link == null)
				dimension.createLink(pos, LinkType.POCKET, world.getBlockState(pos.down()).getValue(BlockDoor.FACING));
		}
	}
	
	@Override
	public Item getDoorItem()
	{
		return DimDoors.itemGoldenDimensionalDoor;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityDimDoorGold();
	}

}
