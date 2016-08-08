package com.zixiken.dimdoors.blocks;

import com.zixiken.dimdoors.core.DimLink;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.DimDoors;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import com.zixiken.dimdoors.core.DimData;

public class WarpDoor extends BaseDimDoor {
    public static final String ID = "dimDoorWarp";

	public WarpDoor() {
		super(Material.wood);
        setHardness(1.0F);
        setUnlocalizedName(ID);
	}

	@Override
	public void placeLink(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos.down());
		if (!world.isRemote && state.getBlock() == this) {
			DimData dimension = PocketManager.createDimensionData(world);
			DimLink link = dimension.getLink(pos);
			if (link == null && dimension.isPocketDimension()) {
				dimension.createLink(pos, LinkType.SAFE_EXIT, state.getValue(BlockDoor.FACING));
			}
		}
	}
	
	@Override
	public Item getDoorItem() {return DimDoors.itemWarpDoor;}
}