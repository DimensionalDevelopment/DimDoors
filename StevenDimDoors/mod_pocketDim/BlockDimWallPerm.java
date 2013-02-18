package StevenDimDoors.mod_pocketDim;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockDimWallPerm extends Block
{
	
	protected BlockDimWallPerm(int i, int j, Material par2Material) 
	{
		 super(i, j, Material.ground);
	        setTickRandomly(true);
	      //  this.setCreativeTab(CreativeTabs.tabBlock);
	      this.setTextureFile("/PocketBlockTextures.png");

	       
	       
	        
	}
    public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5) {}
    
    /**
     * Only matters if the player is in limbo, acts to teleport the player from limbo back to dim 0
     */
    public void onEntityWalking(World par1World, int par2, int par3, int par4, Entity par5Entity) 
    {
    	if(!par1World.isRemote&&par1World.provider.dimensionId==mod_pocketDim.limboDimID)
    	{
			 Random rand = new Random();
    	
    		int size = dimHelper.instance.linksForRendering.size();
    		LinkData link;
    		if(size!=0)
    		{
    			 link = (LinkData) dimHelper.instance.linksForRendering.get(rand.nextInt(size));
    		}
    		else
    		{
    			 link =new LinkData(0,0,0,0);
    		}

    		
    		
    		if(dimHelper.getWorld(link.destDimID)!=null)
    		{
    			World world = dimHelper.getWorld(0);
    			
    			int x = (link.destXCoord + rand.nextInt(100000)-50000);
    			int z = (link.destZCoord + rand.nextInt(100000)-50000);
    			
    			x=x+(x>> 4)+1; //make sure I am in the middle of a chunk, andnot on a boundry, so it doesnt load the chunk next to me
    			z=z+(z>> 4)+1;

    			world.getChunkProvider().loadChunk(x >> 4, z >> 4);
    			
    		   	int y = world.getHeightValue(x, z);
    		   	
    		   	//this complicated chunk teleports the player back to the overworld at some random location. Looks funky becaue it has to load the chunk
        		dimHelper.instance.teleportToPocket(par1World, 
        				new LinkData(0,0,x,y,z,link.locXCoord,link.locYCoord,link.locZCoord,link.isLocPocket), 
        				EntityPlayer.class.cast(par5Entity));

    		    			
    		   	EntityPlayer.class.cast(par5Entity).setPositionAndUpdate( x, y, z );

    		   	//makes sure they can breath when they teleport
    		   	world.setBlockWithNotify(x, y, z, 0);
    		    	
    		}
    	}
    }
}
