package StevenDimDoors.mod_pocketDim.helpers;

import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.dimHelper;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;


public class yCoordHelper 
{
	public static int getFirstUncovered(LinkData pointerLink)
	{
		return yCoordHelper.getFirstUncovered(pointerLink.destDimID, pointerLink.destXCoord,pointerLink.destYCoord, pointerLink.destZCoord);
	}
	public static int getFirstUncovered(int worldID, int x, int yStart, int z)
	{
		if(dimHelper.getWorld(worldID)==null||dimHelper.getWorld(worldID).provider==null)
	   	{
	   		dimHelper.initDimension(worldID);
	   	}
		
		return yCoordHelper.getFirstUncovered(dimHelper.getWorld(worldID),x,yStart,z);
	}
	public static int getFirstUncovered(World world, int x, int yStart, int z)
	{
		int yCoord=yStart;
	   	
			Chunk chunk = world.getChunkProvider().loadChunk(x >> 4, z >> 4);
			
			int xC=(x % 16)< 0 ? (x % 16)+16 : (x % 16);
			int yC=yCoord;
			int zC=(z % 16)< 0 ? (z % 16)+16 : (z % 16);
			
			
			boolean flag=false;
			
			do
			{
				flag=false;
				if(chunk.getBlockID(xC, yC-1, zC)!=0)
				{
					if(!Block.blocksList[chunk.getBlockID(xC, yC-1, zC)].blockMaterial.isReplaceable()||Block.blocksList[chunk.getBlockID(xC, yC-1, zC)].blockMaterial.isLiquid())
					{
						
							flag=true;
						
					}
				}
				
				 if(chunk.getBlockID(xC, yC, zC)!=0)
				{
					if(!Block.blocksList[chunk.getBlockID(xC, yC, zC)].blockMaterial.isReplaceable()||Block.blocksList[chunk.getBlockID(xC, yC, zC)].blockMaterial.isLiquid())
					{
						
							flag=true;
						
					}
				}
				
				yC++;
			}
			while(flag&&yC<245);
			
			
			return yC-1;
	}
}