package StevenDimDoors.mod_pocketDim.blocks;

import java.util.Random;

import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.dimHelper;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockDimWallPerm extends Block
{
	
	public BlockDimWallPerm(int i, int j, Material par2Material) 
	{
		 super(i, Material.ground);
	        setTickRandomly(true);
	      //  this.setCreativeTab(CreativeTabs.tabBlock);
	     

	       
	       
	        
	}

	public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2().replace("perm", ""));
    }
	 public int quantityDropped(Random par1Random)
	    {
	        
	        
	            return 0;
	        
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
    	
    		LinkData link=dimHelper.instance.getRandomLinkData(false);
    		if(link==null)
    		{
    			 link =new LinkData(0,0,0,0);    		
    		}
    		

    		if(dimHelper.getWorld(0)==null)
    		{
    			dimHelper.initDimension(0);
    		}
    		
    		
    		if(dimHelper.getWorld(0)!=null)
    		{
    			
    			
    			int x = (link.destXCoord + rand.nextInt(mod_pocketDim.limboExitRange)-mod_pocketDim.limboExitRange/2);
    			int z = (link.destZCoord + rand.nextInt(mod_pocketDim.limboExitRange)-mod_pocketDim.limboExitRange/2);
    			
    			x=x+(x>> 4)+1; //make sure I am in the middle of a chunk, andnot on a boundry, so it doesnt load the chunk next to me
    			z=z+(z>> 4)+1;

    			dimHelper.getWorld(0).getChunkProvider().loadChunk(x >> 4, z >> 4);
    			
    		   	int y = dimHelper.getWorld(0).getHeightValue(x, z);
    		   	
    		   	//this complicated chunk teleports the player back to the overworld at some random location. Looks funky becaue it has to load the chunk
        		dimHelper.instance.teleportToPocket(par1World, new LinkData(par1World.provider.dimensionId,0,x,y,z,link.locXCoord,link.locYCoord,link.locZCoord,link.isLocPocket), 
        				EntityPlayer.class.cast(par5Entity));

    		    			
    		   	EntityPlayer.class.cast(par5Entity).setPositionAndUpdate( x, y, z );

    		   	//makes sure they can breath when they teleport
    		   	dimHelper.getWorld(0).setBlock(x, y, z, 0);
    		   	int i=x;
    		   	int j=y-1;
    		   	int k=z;
    		   	
    		   	
    		   	for(int xc=-3;xc<4;xc++)
				{
					for(int zc=-3;zc<4;zc++)
					{
						for(int yc=0;yc<200;yc++)
						{
							if(yc==0&&dimHelper.getWorld(0).isBlockOpaqueCube(i+xc, j-2,k +zc))
							{
								
								if(Math.abs(xc)+Math.abs(zc)<rand.nextInt(3)+2)
								{
									dimHelper.getWorld(0).setBlock(i+xc, j-1+yc, k+zc, mod_pocketDim.blockLimboID);
								}
								else if(Math.abs(xc)+Math.abs(zc)<rand.nextInt(3)+3)

								{
									dimHelper.getWorld(0).setBlock(i+xc, j-1+yc, k+zc,  mod_pocketDim.blockLimboID,2,0);

								}
							}

						}

					}
				}
				
			

				{
			
				}
				
    		    	
    		}
    	}
    }
}
