package com.zixiken.dimdoors.commands;

import java.util.Collection;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.core.DimData;
import com.zixiken.dimdoors.core.DimLink;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.dungeon.DungeonData;
import com.zixiken.dimdoors.helpers.DungeonHelper;
import com.zixiken.dimdoors.world.PocketBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class CommandCreateDungeonRift extends DDCommandBase {
	private static CommandCreateDungeonRift instance = null;

	private CommandCreateDungeonRift() {
		super("dd-rift", "<dungeon name>");
	}

	public static CommandCreateDungeonRift instance() {
		if (instance == null)
			instance = new CommandCreateDungeonRift();

		return instance;
	}

	@Override
	protected DDCommandResult processCommand(EntityPlayer sender, String[] command) {
		DimData dimension;
		DungeonHelper dungeonHelper = DungeonHelper.instance();

		if (command.length == 0) {
			return DDCommandResult.TOO_FEW_ARGUMENTS;
		} if (command.length > 1) {
			return DDCommandResult.TOO_MANY_ARGUMENTS;
		}
		
		DimLink link;
		DungeonData result;

		BlockPos pos = new BlockPos(MathHelper.floor_double(sender.posX), MathHelper.floor_double(sender.posY), MathHelper.floor_double (sender.posZ));

		EnumFacing facing = EnumFacing.fromAngle(sender.rotationYaw).getOpposite();

		result = findDungeonByPartialName(command[0], dungeonHelper.getRegisteredDungeons());

		if (result == null) {
			result = findDungeonByPartialName(command[0], dungeonHelper.getUntaggedDungeons());
		}
		
		// Check if we found any matches
		if (result != null) {
			dimension = PocketManager.getDimensionData(sender.worldObj);
			link = dimension.createLink(pos.up(), LinkType.DUNGEON, facing);

			if (PocketBuilder.generateSelectedDungeonPocket(link, DimDoors.properties, result)) {
				// Create a rift to our selected dungeon and notify the player
				sender.worldObj.setBlockState(pos.up(), DimDoors.blockRift.getDefaultState());
				sendChat(sender, "Created a rift to \"" + result.schematicName() + "\" dungeon (Dimension ID = " + link.destination().getDimension() + ").");
			} else {
				// Dungeon generation failed somehow. Notify the user and remove the useless link.
				dimension.deleteLink(link);
				sendChat(sender, "Dungeon generation failed unexpectedly!");
			}
		} else {
			//No matches!
			return new DDCommandResult("Error: The specified dungeon was not found. Use 'dd-list' to see a list of the available dungeons.");
		}
		return DDCommandResult.SUCCESS;
	}

	private static DungeonData findDungeonByPartialName(String query, Collection<DungeonData> dungeons) {
		//Search for the shortest dungeon name that contains the lowercase query string.
		String dungeonName;
		String normalQuery = query.toLowerCase();
		DungeonData bestMatch = null;
		int matchLength = Integer.MAX_VALUE;

		for (DungeonData dungeon : dungeons) {
			//We need to extract the file's name. Comparing against schematicPath could
			//yield false matches if the query string is contained within the path.
			dungeonName = dungeon.schematicName().toLowerCase();
			if (dungeonName.length() < matchLength && dungeonName.contains(normalQuery)) {
				matchLength = dungeonName.length();
				bestMatch = dungeon;
			}
		}

		return bestMatch;
	}
}