package StevenDimDoors.mod_pocketDim.schematic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockComparator;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockStairs;
import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;

public class BlockRotator
{
	//This class is temporary. It's just a place in which to hold the old block rotation and transformation code
	//until we can rewrite it.
	
	public final static int EAST_DOOR_METADATA = 0;
	private final static int BLOCK_ID_COUNT = 4096;
	
	//Provides a fast lookup table for whether blocks have orientations
	private final static boolean[] hasOrientations = new boolean[BLOCK_ID_COUNT];
	
	static
	{
		hasOrientations[Block.dispenser.blockID] = true;
		hasOrientations[Block.stairsStoneBrick.blockID] = true;
		hasOrientations[Block.lever.blockID] = true;
		hasOrientations[Block.stoneButton.blockID] = true;
		hasOrientations[Block.redstoneRepeaterIdle.blockID] = true;
		hasOrientations[Block.redstoneRepeaterActive.blockID] = true;
		hasOrientations[Block.tripWireSource.blockID] = true;
		hasOrientations[Block.torchWood.blockID] = true;
		hasOrientations[Block.torchRedstoneIdle.blockID] = true;
		hasOrientations[Block.torchRedstoneActive.blockID] = true;
		hasOrientations[Block.doorIron.blockID] = true;
		hasOrientations[Block.doorWood.blockID] = true;
		hasOrientations[Block.pistonBase.blockID] = true;
		hasOrientations[Block.pistonStickyBase.blockID] = true;
		hasOrientations[Block.pistonExtension.blockID] = true;
		hasOrientations[Block.redstoneComparatorIdle.blockID] = true;
		hasOrientations[Block.redstoneComparatorActive.blockID] = true;
		hasOrientations[Block.signPost.blockID] = true;
		hasOrientations[Block.signWall.blockID] = true;
		hasOrientations[Block.skull.blockID] = true;
		hasOrientations[Block.ladder.blockID] = true;
		hasOrientations[Block.vine.blockID] = true;
		hasOrientations[Block.anvil.blockID] = true;
		hasOrientations[Block.chest.blockID] = true;
		hasOrientations[Block.chestTrapped.blockID] = true;
		hasOrientations[Block.hopperBlock.blockID] = true;
		hasOrientations[Block.stairsNetherBrick.blockID] = true;
		hasOrientations[Block.stairsCobblestone.blockID] = true;
		hasOrientations[Block.stairsNetherQuartz.blockID] = true;
		hasOrientations[Block.stairsSandStone.blockID] = true;
		hasOrientations[Block.stairsBrick.blockID] = true;
		hasOrientations[Block.stairsWoodBirch.blockID] = true;
		hasOrientations[Block.stairsWoodOak.blockID] = true;
		hasOrientations[Block.stairsWoodJungle.blockID] = true;
		hasOrientations[Block.stairsWoodSpruce.blockID] = true;
		hasOrientations[Block.wood.blockID] = true;
		hasOrientations[Block.blockNetherQuartz.blockID] = true;
		hasOrientations[Block.railPowered.blockID] = true;
		hasOrientations[Block.railDetector.blockID] = true;
		hasOrientations[Block.railActivator.blockID] = true;
		hasOrientations[Block.rail.blockID] = true;
		
		hasOrientations[mod_pocketDim.dimensionalDoor.blockID] = true;
		hasOrientations[mod_pocketDim.warpDoor.blockID] = true;
		
	}

	public static int transformMetadata(int metadata, int turns, int blockID)
	{
		//I changed rotations to reduce the monstrous code we had. It might be
		//slightly less efficient, but it's easier to maintain for now. ~SenseiKiwi
		
		//Correct negative turns and get the minimum number of rotations needed
		turns += 1 << 16;
		turns %= 4;
		
		if (hasOrientations[blockID])
		{
			while (turns > 0)
			{
				metadata = rotateMetadataBy90(metadata, blockID);
				turns--;
			}
		}
		return metadata;
	}
	
	private static int rotateMetadataBy90(int metadata, int blockID)
	{
		//TODO: Replace this horrible function with something prettier. We promise we will for the next version,
		//after switching to MC 1.6. PADRE, PLEASE FORGIVE OUR SINS.

		if (blockID == Block.wood.blockID)
		{
			if (metadata >= 4 && metadata < 12)
			{
				metadata = (metadata % 8) + 4;
			}
		}
		else if (blockID == Block.blockNetherQuartz.blockID)
		{
			if (metadata == 3 || metadata == 4)
			{
				metadata = (metadata - 2) % 2 + 3;
			}
		}
		else if (blockID == Block.railPowered.blockID || blockID == Block.railDetector.blockID || blockID == Block.railActivator.blockID)
		{
			switch (metadata)
			{
			//Powered Track/Detector Track/Activator Track (off)
			case 0:
				metadata = 1;
				break;
			case 1:
				metadata = 0;
				break;
			case 2:
				metadata = 5;
				break;
			case 3:
				metadata = 4;
				break;
			case 4:
				metadata = 2;
				break;
			case 5:
				metadata = 3;
				break;
				
			//Powered Track/Detector Track/Activator Track (on)
			case 8:
				metadata = 9;
				break;
			case 9:
				metadata = 8;
				break;
			case 10:
				metadata = 13;
				break;
			case 11:
				metadata = 12;
				break;
			case 12:
				metadata = 10;
				break;
			case 13:
				metadata = 11;
				break;
			}
		}
		else if (blockID==Block.rail.blockID)
		{
			switch (metadata)
			{
			case 0:
				metadata = 1;
				break;
			case 1:
				metadata = 0;
				break;					
			case 8:
				metadata = 9;
				break;
			case 9:
				metadata = 6;
				break;
			case 6:
				metadata = 7;
				break;
			case 7:
				metadata = 8;
				break;					
			}
		}
		else if (Block.blocksList[blockID] instanceof BlockStairs)
		{

			switch (metadata)
			{
			case 0:
				metadata = 2;
				break;
			case 1:
				metadata = 3;
				break;
			case 2:
				metadata = 1;
				break;
			case 3:
				metadata = 0;
				break;
			case 7:
				metadata = 4;
				break;
			case 6:
				metadata = 5;
				break;
			case 5:
				metadata = 7;
				break;
			case 4:
				metadata = 6;
				break;
			}
		}
		else if (blockID == Block.chest.blockID || blockID == Block.chestTrapped.blockID || blockID == Block.ladder.blockID || blockID == Block.hopperBlock.blockID)
		{
			switch (metadata)
			{
			case 2:
				metadata = 5;
				break;
			case 3:
				metadata = 4;
				break;					
			case 4:
				metadata = 2;
				break;
			case 5:
				metadata = 3;
				break;
			}

		}
		else if (blockID==Block.vine.blockID)
		{
			switch (metadata)
			{

			case 1:
				metadata = 2;
				break;
			case 2:
				metadata = 4;
				break;					
			case 4:
				metadata = 8;
				break;
			case 8:
				metadata = 1;
				break;
			}
		}
		else if (blockID==Block.signWall.blockID)
		{
			switch (metadata)
			{

			case 3:
				metadata = 4;
				break;
			case 2:
				metadata = 5;
				break;					
			case 4:
				metadata = 2;
				break;
			case 5:
				metadata = 3;
				break;
			}
		}
		else if (blockID==Block.signPost.blockID)
		{
			switch (metadata)
			{
			case 0:
				metadata = 4;
				break;
			case 1:
				metadata = 5;
				break;
			case 2:
				metadata = 6;
				break;
			case 3:
				metadata = 7;
				break;
			case 4:
				metadata = 8;
				break;
			case 5:
				metadata = 9;
				break;
			case 6:
				metadata = 10;
				break;
			case 7:
				metadata = 11;
				break;
			case 8:
				metadata = 12;
				break;
			case 9:
				metadata = 13;
				break;
			case 10:
				metadata = 14;
				break;
			case 11:
				metadata = 15;
				break;
			case 12:
				metadata = 0;
				break;
			case 13:
				metadata = 1;
				break;
			case 14:
				metadata = 2;
				break;
			case 15:
				metadata = 3;
				break;
			}
		}
		else if(blockID== Block.lever.blockID||blockID== Block.stoneButton.blockID||blockID== Block.woodenButton.blockID||blockID== Block.torchWood.blockID||blockID== Block.torchRedstoneIdle.blockID||blockID== Block.torchRedstoneActive.blockID)
		{
			switch (metadata)
			{
			case 12:
				metadata = 9;
				break;
			case 11:
				metadata = 10;
				break;
			case 10:
				metadata = 12;
				break;
			case 9:
				metadata = 11;
				break;					
			case 2:
				metadata = 4;
				break;
			case 3:
				metadata = 2;
				break;
			case 1:
				metadata = 3;
				break;
			case 4:
				metadata = 1;
				break;
			}
		}
		else if(blockID== Block.pistonBase.blockID||blockID==Block.pistonExtension.blockID||blockID==Block.pistonStickyBase.blockID||blockID==Block.dispenser.blockID||blockID==Block.dropper.blockID)
		{
			switch (metadata)
			{
			case 4:
				metadata = 2;
				break;
			case 5:
				metadata = 3;
				break;
			case 13:
				metadata = 11;
				break;
			case 12:
				metadata = 10;
				break;
			case 3:
				metadata = 4;
				break;
			case 2:
				metadata = 5;
				break;
			case 11:
				metadata = 12;
				break;
			case 10:
				metadata = 13;
				break;
			}
		}
		else if (Block.blocksList[blockID] instanceof BlockRedstoneRepeater || Block.blocksList[blockID] instanceof BlockDoor || blockID== Block.tripWireSource.blockID || Block.blocksList[blockID] instanceof BlockComparator)
		{
			switch (metadata)
			{
			case 0:
				metadata = 1;
				break;
			case 1:
				metadata = 2;
				break;
			case 2:
				metadata = 3;
				break;
			case 3:
				metadata = 0;
				break;
			case 4:
				metadata = 5;
				break;
			case 5:
				metadata = 6;
				break;
			case 6:
				metadata = 7;
				break;
			case 7:
				metadata = 4;
				break;
			case 8:
				metadata = 9;
				break;
			case 9:
				metadata = 10;
				break;
			case 10:
				metadata = 11;
				break;
			case 11:
				metadata = 8;
				break;
			case 12:
				metadata = 13;
				break;
			case 13:
				metadata = 14;
				break;
			case 14:
				metadata = 15;
				break;
			case 15:
				metadata = 12;
				break;
			}
		}
		return metadata;
	}
	
	public static void transformPoint(Point3D position, Point3D srcOrigin, int angle, Point3D destOrigin)
	{
		//This function receives a position (e.g. point in schematic space), translates it relative
		//to a source coordinate system (e.g. the point that will be the center of a schematic),
		//then rotates and translates it to obtain the corresponding point in a destination
		//coordinate system (e.g. the location of the entry rift in the pocket being generated).
		//The result is returned by overwriting the original position so that new object references
		//aren't needed. That way, repeated use of this function will not incur as much overhead.
		
		//Position is only overwritten at the end, so it's okay to provide it as srcOrigin or destOrigin as well.
		
		int tx = position.getX() - srcOrigin.getX();
		int ty = position.getY() - srcOrigin.getY();
		int tz = position.getZ() - srcOrigin.getZ();
		
		//"int angle" specifies a rotation consistent with Minecraft's orientation system.
		//That means each increment of 1 in angle would be a 90-degree clockwise turn.
		//Given a starting direction A and a destination direction B, the rotation would be
		//calculated by (B - A).

		//Adjust angle into the expected range
		if (angle < 0)
		{
			int correction = -(angle / 4);
			angle = angle + 4 * (correction + 1);
		}
		angle = angle % 4;
		
		int rx;
		int rz;
		switch (angle)
		{
			case 0: //No rotation
				rx = tx;
				rz = tz;
				break;
			case 1: //90 degrees clockwise
				rx = -tz;
				rz = tx;
				break;
			case 2: //180 degrees
				rx = -tx;
				rz = -tz;
				break;
			case 3: //270 degrees clockwise
				rx = tz;
				rz = -tx;
				
				break;
			default: //This should never happen
				throw new IllegalStateException("Invalid angle value. This should never happen!");
		}
		
		position.setX( rx + destOrigin.getX() );
		position.setY( ty + destOrigin.getY() );
		position.setZ( rz + destOrigin.getZ() );
	}
}
