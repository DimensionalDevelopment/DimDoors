package StevenDimDoors.mod_pocketDim.schematic;

import net.minecraft.block.*;
import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import net.minecraft.init.Blocks;

import java.util.HashMap;
import java.util.Map;

public class BlockRotator
{
	//This class is temporary. It's just a place in which to hold the old block rotation and transformation code
	//until we can rewrite it.
	
	public final static int EAST_DOOR_METADATA = 0;
	private final static int BLOCK_ID_COUNT = 4096;
	
	//Provides a fast lookup table for whether blocks have orientations
	private final static Map<Block, Boolean> hasOrientations = new HashMap<Block, Boolean>();
	
	public static void setupOrientations()
	{
		hasOrientations.put(Blocks.dispenser, true);
        hasOrientations.put(Blocks.dropper, true);
        hasOrientations.put(Blocks.stone_brick_stairs, true);
		hasOrientations.put(Blocks.lever, true);
		hasOrientations.put(Blocks.stone_button, true);
        hasOrientations.put(Blocks.wooden_button, true);
		hasOrientations.put(Blocks.unpowered_repeater, true);
        hasOrientations.put(Blocks.powered_repeater, true);
        hasOrientations.put(Blocks.tripwire_hook, true);
        hasOrientations.put(Blocks.torch, true);
        hasOrientations.put(Blocks.redstone_torch, true);
        hasOrientations.put(Blocks.unlit_redstone_torch, true);
		hasOrientations.put(Blocks.iron_door, true);
        hasOrientations.put(Blocks.wooden_door, true);
        hasOrientations.put(Blocks.piston, true);
        hasOrientations.put(Blocks.sticky_piston, true);
        hasOrientations.put(Blocks.piston_head, true);
		hasOrientations.put(Blocks.powered_comparator, true);
        hasOrientations.put(Blocks.unpowered_comparator, true);
        hasOrientations.put(Blocks.standing_sign, true);
        hasOrientations.put(Blocks.wall_sign, true);
		hasOrientations.put(Blocks.skull, true);
        hasOrientations.put(Blocks.ladder, true);
        hasOrientations.put(Blocks.vine, true);
        hasOrientations.put(Blocks.anvil, true);
        hasOrientations.put(Blocks.chest, true);
        hasOrientations.put(Blocks.trapped_chest, true);
        hasOrientations.put(Blocks.hopper, true);
		hasOrientations.put(Blocks.nether_brick_stairs, true);
        hasOrientations.put(Blocks.stone_stairs, true);
        hasOrientations.put(Blocks.quartz_stairs, true);
        hasOrientations.put(Blocks.sandstone_stairs, true);
        hasOrientations.put(Blocks.brick_stairs, true);
        hasOrientations.put(Blocks.birch_stairs, true);
        hasOrientations.put(Blocks.oak_stairs, true);
        hasOrientations.put(Blocks.jungle_stairs, true);
        hasOrientations.put(Blocks.spruce_stairs, true);
        hasOrientations.put(Blocks.log, true);
        hasOrientations.put(Blocks.log2, true);
		hasOrientations.put(Blocks.quartz_block, true);
        hasOrientations.put(Blocks.rail, true);
        hasOrientations.put(Blocks.activator_rail, true);
        hasOrientations.put(Blocks.detector_rail, true);
        hasOrientations.put(Blocks.golden_rail, true);
        hasOrientations.put(Blocks.furnace, true);
        hasOrientations.put(Blocks.lit_furnace, true);
        hasOrientations.put(Blocks.bed, true);

        hasOrientations.put(mod_pocketDim.dimensionalDoor, true);
        hasOrientations.put(mod_pocketDim.warpDoor, true);
        hasOrientations.put(mod_pocketDim.goldenDimensionalDoor, true);
        hasOrientations.put(mod_pocketDim.personalDimDoor, true);
	}

	public static int transformMetadata(int metadata, int turns, Block block)
	{
		//I changed rotations to reduce the monstrous code we had. It might be
		//slightly less efficient, but it's easier to maintain for now. ~SenseiKiwi
		
		//Correct negative turns and get the minimum number of rotations needed
		turns += 1 << 16;
		turns %= 4;
		
		if (hasOrientations.containsKey(block) && hasOrientations.get(block))
		{
			while (turns > 0)
			{
				metadata = rotateMetadataBy90(metadata, block);
				turns--;
			}
		}
		return metadata;
	}
	
	private static int rotateMetadataBy90(int metadata, Block block)
	{
		//TODO: Replace this horrible function with something prettier. We promise we will for the next version,
		//after switching to MC 1.6. PADRE, PLEASE FORGIVE OUR SINS.

		if (block == Blocks.log || block == Blocks.log2)
		{
			if (metadata >= 4 && metadata < 12)
			{
				metadata = (metadata % 8) + 4;
			}
		}
		else if (block == Blocks.quartz_block)
		{
			if (metadata == 3 || metadata == 4)
			{
				metadata = (metadata - 2) % 2 + 3;
			}
		}
		else if (block == Blocks.golden_rail || block == Blocks.detector_rail || block == Blocks.activator_rail)
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
		else if (block==Blocks.rail)
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
		else if (block==Blocks.bed)
		{
			switch (metadata)
			{
			case 2:
				metadata = 1;
				break;
			case 1:
				metadata = 0;
				break;					
			case 0:
				metadata = 3;
				break;
			case 3:
				metadata = 2;
				break;	
			case 10:
				metadata = 9;
				break;
			case 9:
				metadata = 8;
				break;					
			case 8:
				metadata = 11;
				break;
			case 11:
				metadata = 10;
				break;	
			}
		}
		else if (block instanceof BlockStairs)
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
		else if (block == Blocks.chest || block == Blocks.trapped_chest || block == Blocks.ladder || block == Blocks.lit_furnace || block == Blocks.furnace)
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
		else if (block == Blocks.hopper)
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
		else if (block==Blocks.vine)
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
		else if (block==Blocks.wall_sign)
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
		else if (block==Blocks.standing_sign)
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
		else if(block== Blocks.lever || block == Blocks.stone_button || block == Blocks.wooden_button || block== Blocks.torch||block== Blocks.unlit_redstone_torch||block== Blocks.redstone_torch)
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
		else if(block== Blocks.piston||block==Blocks.piston_head||block==Blocks.sticky_piston || block == Blocks.dispenser || block == Blocks.dropper)
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
		else if (block instanceof BlockRedstoneRepeater || block instanceof BlockDoor || block== Blocks.tripwire_hook || block instanceof BlockRedstoneComparator)
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
