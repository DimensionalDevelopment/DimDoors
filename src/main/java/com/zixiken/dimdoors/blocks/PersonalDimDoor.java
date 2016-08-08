package com.zixiken.dimdoors.blocks;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.world.PersonalPocketProvider;
import com.zixiken.dimdoors.core.DimLink;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.PocketManager;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import com.zixiken.dimdoors.core.DimData;

public class PersonalDimDoor extends BaseDimDoor {
	public static final String ID = "dimDoorPersonal";

	public PersonalDimDoor() {
		super(Material.rock);
        setHardness(0.1F);
        setUnlocalizedName(ID);
	}

	@Override
	public void placeLink(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos.down());
		if (!world.isRemote && state.getBlock() == this) {
			DimData dimension = PocketManager.getDimensionData(world);
			DimLink link = dimension.getLink(pos);
			if (link == null) {
                if (world.provider instanceof PersonalPocketProvider)
                    dimension.createLink(pos, LinkType.LIMBO, state.getValue(BlockDoor.FACING));
                else
				    dimension.createLink(pos, LinkType.PERSONAL, state.getValue(BlockDoor.FACING));
			}
		}
	}

	@Override
	public Item getDoorItem()
	{
		return DimDoors.itemPersonalDoor;
	}

}
