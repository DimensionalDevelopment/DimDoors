package com.zixiken.dimdoors.schematic;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.block.*;
import com.zixiken.dimdoors.Point3D;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.HashMap;
import java.util.Map;

public class BlockRotator {
	public static IBlockState transform(IBlockState state, int turns) {
		//I changed rotations to reduce the monstrous code we had. It might be
		//slightly less efficient, but it's easier to maintain for now. ~SenseiKiwi
		
		//Correct negative turns and get the minimum number of rotations needed
		turns += 1 << 16;
		turns %= 4;

		while (turns > 0) {
			for (IProperty prop : (java.util.Set<IProperty>)state.getProperties().keySet()) {
				if (prop.getName().equals("facing") || prop.getName().equals("rotation")) {
					state.cycleProperty(prop);
				}
			}

			turns--;
		}

		return state;
	}
	
	public static void transformPoint(BlockPos position, BlockPos srcOrigin, EnumFacing angle, BlockPos destOrigin) {
		//This function receives a position (e.g. point in schematic space), translates it relative
		//to a source coordinate system (e.g. the point that will be the center of a schematic),
		//then rotates and translates it to obtain the corresponding point in a destination
		//coordinate system (e.g. the location of the entry rift in the pocket being generated).
		//The result is returned by overwriting the original position so that new object references
		//aren't needed. That way, repeated use of this function will not incur as much overhead.
		
		//Position is only overwritten at the end, so it's okay to provide it as srcOrigin or destOrigin as well.
		
		BlockPos t = position.subtract(srcOrigin);
		
		//"int angle" specifies a rotation consistent with Minecraft's orientation system.
		//That means each increment of 1 in angle would be a 90-degree clockwise turn.
		//Given a starting direction A and a destination direction B, the rotation would be
		//calculated by (B - A).

		//Adjust angle into the expected range

		int rx;
		int rz;

		switch (angle) {
			case SOUTH: //No rotation
				rx = t.getX();
				rz = t.getZ();
				break;
			case WEST: //90 degrees clockwise
				rx = -t.getZ();
				rz = t.getX();
				break;
			case NORTH: //180 degrees
				rx = -t.getX();
				rz = -t.getZ();
				break;
			case EAST: //270 degrees clockwise
				rx = t.getZ();
				rz = -t.getX();
				
				break;
			default: //This should never happen
				throw new IllegalStateException("Invalid angle value. This should never happen!");
		}
		
		position = new BlockPos(rx + destOrigin.getX(), t.getY() + destOrigin.getY(), rz + destOrigin.getZ() );
	}
}
