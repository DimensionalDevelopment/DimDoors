package com.zixiken.dimdoors.world.gateways;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.world.LimboProvider;

public class GatewayLimbo extends BaseGateway {
	public GatewayLimbo(DDProperties properties) {
		super(properties);
	}

	@Override
	public boolean generate(World world, BlockPos pos) {
	    IBlockState state = DimDoors.blockLimbo.getDefaultState();
		// Build the gateway out of Unraveled Fabric. Since nearly all the blocks in Limbo are of
		// that type, there is no point replacing the ground.
		world.setBlockState(pos.add(0,3,1), state);
		world.setBlockState(pos.add(0,3,-1), state);
		
		// Build the columns around the door
		world.setBlockState(pos.add(0,2,-1), state);
		world.setBlockState(pos.add(0,2,1), state);
		world.setBlockState(pos.add(0,1,-1), state);
		world.setBlockState(pos.add(0,1,1), state);

		PocketManager.getDimensionData(world).createLink(pos.up(2), LinkType.DUNGEON, EnumFacing.SOUTH);

		ItemDoor.placeDoor(world, pos.up(), EnumFacing.SOUTH, DimDoors.transientDoor);
		return true;
	}

	@Override
	public boolean isLocationValid(World world, BlockPos pos) {
		return (world.provider instanceof LimboProvider);
	}
}
