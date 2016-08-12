package com.zixiken.dimdoors.commands;

import java.util.ArrayList;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.core.DimLink;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import com.zixiken.dimdoors.core.DimData;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.util.Point4D;

public class CommandDeleteRifts extends DDCommandBase
{
	private static CommandDeleteRifts instance = null;

	private CommandDeleteRifts() {
		super("dd-deleterifts", "[dimension number]");
	}

	public static CommandDeleteRifts instance() {
		if (instance == null)
			instance = new CommandDeleteRifts();

		return instance;
	}

	@Override
	protected DDCommandResult processCommand(EntityPlayer sender, String[] command) {
		int linksRemoved = 0;
		int targetDimension;

		if (command.length > 1) {
			return DDCommandResult.TOO_MANY_ARGUMENTS;
		}
		if (command.length == 0) {
			targetDimension = sender.worldObj.provider.getDimensionId();
		} else {
			try {
				targetDimension = Integer.parseInt(command[0]);
			} catch (NumberFormatException e) {
				return DDCommandResult.INVALID_DIMENSION_ID;
			}
		}

		World world = PocketManager.loadDimension(targetDimension);
		if (world == null) {
			return DDCommandResult.UNREGISTERED_DIMENSION;
		}
		
		BlockPos pos;
		Point4D location;
		DimData dimension = PocketManager.createDimensionData(world);
		ArrayList<DimLink> links = dimension.getAllLinks();
		for (DimLink link : links) {
			location = link.source();
			pos = location.toBlockPos();
			if (world.getBlockState(pos).getBlock() == DimDoors.blockRift) {
				// Remove the rift and its link
				world.setBlockToAir(pos);
				dimension.deleteLink(link);
				linksRemoved++;
			} else if (!DimDoors.blockRift.isBlockImmune(world, pos)) {
				// If a block is not immune, then it must not be a DD block.
				// The link would regenerate into a rift eventually.
				// We only need to remove the link.
				dimension.deleteLink(link);
				linksRemoved++;
			}
		}
		sendChat(sender, "Removed " + linksRemoved + " links.");
		return DDCommandResult.SUCCESS;
	}
}