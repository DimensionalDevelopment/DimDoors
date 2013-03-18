package StevenDimDoors.mod_pocketDim;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class RiftGenerator implements IWorldGenerator
{
    private int minableBlockId;
    private int numberOfBlocks;
    int cycles=40;
    boolean shouldSave = false;
    int count = 0;
    int i;
    int k;
    int j;
    Random rand = new Random();
    boolean shouldGenHere=true;
    LinkData link;
   
    
    DimData dimData;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,	IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
    	//Long ntime = System.nanoTime();
    	shouldGenHere=true;
    	  
    	if(world.provider.getDimensionName()=="PocketDim"||!mod_pocketDim.riftsInWorldGen ||world.isRemote)
        {
    	
    			this.shouldGenHere=false;
    			
        	
        }
    	
    
    	if(this.shouldGenHere)
    	{
    
         
    		if(random.nextInt(3500)==0)
    		{
    			i=chunkX*16-random.nextInt(16);
    			k=chunkZ*16-random.nextInt(16);
    
    			j= world.getHeightValue(i, k);
			
    			if(j>20&&world.getBlockId(i, j, k)==0)
    			{
		//			System.out.println(String.valueOf(i)+"x "+String.valueOf(j)+"y "+String.valueOf(k)+"z"+"Large gen");
				
    				link = new LinkData(world.provider.dimensionId, 0,  i, j+1, k, i, j+1, k, true);
    				link = dimHelper.instance.createPocket(link,true, true);
    				this.shouldSave=true;
    		
			
		//	SchematicLoader loader = new SchematicLoader();
		//	loader.init(link);
		//	loader.generateSchematic(link);
			
    	
    				count=0;
    				while(random.nextInt(4)!=1)
    				{
    					i=chunkX*16-random.nextInt(16);
    					k=chunkZ*16-random.nextInt(16);
        
    					j= world.getHeightValue(i, k);
        	
    					if(world.isAirBlock(i, j+1, k))
    					{
    				
    					
        				 
    						 link = dimHelper.instance.createLink(link.locDimID,link.destDimID, i, j+1, k,link.destXCoord,link.destYCoord,link.destZCoord);
    						 

        				

    					
    				
        	
    					}
        	 
    				}
         
    			}
    		}
    	
    		if(random.nextInt(540)==0)
    		{
    			i=chunkX*16-random.nextInt(16);
    			k=chunkZ*16-random.nextInt(16);
    
    			j= world.getHeightValue(i, k);
    			if(j>20&&world.getBlockId(i, j, k)==0)
    			{
    				//System.out.println(String.valueOf(i)+"x "+String.valueOf(j)+"y "+String.valueOf(k)+"z"+"med gen");

    				link = new LinkData(world.provider.dimensionId, 0,  i, j+1, k, i, j+1, k, true);
    				link = dimHelper.instance.createPocket(link,true, true);
    				this.shouldSave=true;
			
			
    				//	SchematicLoader loader = new SchematicLoader();
    				//	loader.init(link);
    				//	loader.generateSchematic(link);
    				count=0;
    		
    		
    				while(random.nextInt(3)!=1)
    				{
    					i=chunkX*16-random.nextInt(16);
    					k=chunkZ*16-random.nextInt(16);
        
    					j= world.getHeightValue(i, k);
        	
    					if(world.isAirBlock(i, j+1, k))
    					{
    			
    					
        				 
    						link = dimHelper.instance.createLink(link.locDimID,link.destDimID, i, j+1, k,link.destXCoord,link.destYCoord,link.destZCoord);


    					
    				
        	
    					}
        	 
    				}
         
    			}
    		}
    	
    	}
    		if(random.nextInt(220)==0&&world.provider.getDimensionName()!="PocketDim"&&!world.isRemote&&mod_pocketDim.riftsInWorldGen)
    		{
    		//	System.out.println("tryingToGen");
    			int blockID=Block.stoneBrick.blockID;
    			if(world.provider.dimensionId==mod_pocketDim.limboDimID)
    			{
    				blockID= mod_pocketDim.blockLimboID;
    			}
    			i=chunkX*16-random.nextInt(16);
    			k=chunkZ*16-random.nextInt(16);
    
    			j= world.getHeightValue(i, k);
    			if(j>20&&world.getBlockId(i, j, k)==0)
    			{
    				//System.out.println(String.valueOf(i)+"x "+String.valueOf(j)+"y "+String.valueOf(k)+"z"+"small gen");

    				count=0; 
				
    			
    				if(world.isAirBlock(i, j+1, k))
    				{
    				
    					
        				 
    					
    					if(world.isBlockOpaqueCube(i, j-2, k)||world.isBlockOpaqueCube(i, j-1, k))
    					{
    						link = new LinkData(world.provider.dimensionId, 0,  i, j+1, k, i, j+1, k, true);
        					link =dimHelper.instance.createPocket(link,true, true);
    						
    						for(int xc=-3;xc<4;xc++)
    						{
    							for(int zc=-3;zc<4;zc++)
    							{
        							for(int yc=0;yc<200;yc++)
        							{
        								if(yc==0&&world.isBlockOpaqueCube(i+xc, j-2,k +zc))
        								{
        									
        									if(Math.abs(xc)+Math.abs(zc)<rand.nextInt(3)+2)
        									{
        										world.setBlock(i+xc, j-1+yc, k+zc, blockID);
        									}
        									else if(Math.abs(xc)+Math.abs(zc)<rand.nextInt(3)+3)

        									{
        										world.setBlockAndMetadata(i+xc, j-1+yc, k+zc, blockID,2);

        									}
        								}

        							}

    							}
    						}
    						
    						ItemRiftBlade.placeDoorBlock(world, i, j+1, k, 0, mod_pocketDim.transientDoor);

    						{
							world.setBlockAndMetadata(i, j+1, k-1, blockID,0);
							world.setBlockAndMetadata(i, j+1, k+1, blockID,0);
							world.setBlockAndMetadata(i, j, k-1, blockID,0);
							world.setBlockAndMetadata(i, j, k+1, blockID,0);
							world.setBlockAndMetadata(i, j+2, k+1, blockID,3);
							world.setBlockAndMetadata(i, j+2, k-1, blockID,3);
    						}
    						

    						
						
    						
    						
    						
							this.shouldSave=true;

    					}
    					
    					//	 dimData = dimHelper.instance.dimList.get(link.destDimID);
    						
    						
    					//	SchematicLoader loader = new SchematicLoader();
    					//	loader.init(link);
    					//	loader.generateSchematic(link);
    						
    						
    					
    						

    						
    						
        	 
    				}
         
    			}
    		}
    		if(this.shouldSave)
    		{
    		//	dimHelper.instance.save();
    		}
    		
    	//	mod_pocketDim.genTime=((System.nanoTime()-ntime)+mod_pocketDim.genTime);
    	//	System.out.println(	mod_pocketDim.genTime);
    	//	System.out.println(	(System.nanoTime()-ntime));

    	//	ntime=0L;
    		
    	}
    
    
}
