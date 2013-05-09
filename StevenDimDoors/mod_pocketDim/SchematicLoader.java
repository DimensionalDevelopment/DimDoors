package StevenDimDoors.mod_pocketDim;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import StevenDimDoors.mod_pocketDim.helpers.yCoordHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockStairs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.ChestGenHooks;
 
public class SchematicLoader 

{
    public short width;
    public short height;
    public short length;
    
    public byte[] blocks;
    public byte[] data;
    public NBTTagList entities;
    public NBTTagList tileentities;
    private Random rand = new Random();
  //  public World world;
    public Point3D incomingLink= new Point3D(0,0,0);
    public ArrayList<Point3D> sideLinks = new ArrayList<Point3D>();
    public ArrayList<Point3D> exitLinks = new ArrayList<Point3D>();
    public int transMeta;
  //  public Chunk chunk;
    public int cX;
    public int cZ;
    public int cY;
    

    
    public boolean didRead=false;
    public String schematic;
    
    public SchematicLoader()
    {
    //	this.schematic="/schematics/"+filePath;
    }
        
    
    public void init(String filePath, LinkData link, int x, int y , int z)
    {
    	
    	this.schematic="/schematics/"+filePath;
        try 
        {
        	
       
        	String fname= schematic ;
        	
        	InputStream input = this.getClass().getResourceAsStream(fname);
        	//FileInputStream fileinputstream = new FileInputStream(file);
        	NBTTagCompound nbtdata = CompressedStreamTools.readCompressed(input);


            
             width = nbtdata.getShort("Width");
             height = nbtdata.getShort("Height");
             length = nbtdata.getShort("Length");
 
             blocks = nbtdata.getByteArray("Blocks");
             data = nbtdata.getByteArray("Data");
             
             entities = nbtdata.getTagList("Entities");
             tileentities = nbtdata.getTagList("TileEntities");
             this.didRead=true;
             input.close();
          
        }
        catch (Exception e) 
        {
        	this.didRead=false;
        	System.out.println("Error- could not find file "+schematic);
            e.printStackTrace();
            
        }
        
        this.generateSchematic(link, 0, 0, 0);
        
    }
    	public int transformMetadata(int metadata, int orientation, int blockID)
    	{
    		if(mod_pocketDim.metadataFlipList.contains(blockID))
    		{
    			switch (orientation)
				{
				case 0:
					
					if(Block.blocksList[blockID] instanceof BlockStairs)
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
					
					if(blockID== Block.lever.blockID||blockID== Block.torchWood.blockID||blockID== Block.torchRedstoneIdle.blockID||blockID== Block.torchRedstoneActive.blockID)
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
			
					if(blockID== Block.pistonBase.blockID||blockID==Block.pistonStickyBase.blockID||blockID==Block.dispenser.blockID)
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
					
					if(blockID== Block.redstoneRepeaterActive.blockID||blockID==Block.redstoneRepeaterIdle.blockID||blockID== Block.tripWireSource.blockID||blockID== Block.doorIron.blockID||blockID==Block.doorWood.blockID)
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
					
					
					
					
				
					break;
				case 1:

					
					if(Block.blocksList[blockID] instanceof BlockStairs)
	    			{
					
	    				switch (metadata)
	    				{
	    				case 0:
	    					metadata = 1;
	    					break;
	    				case 1:
	    					metadata = 0;
	    					break;
	    				case 2:
	    					metadata = 3;
	    					break;
	    				case 3:
	    					metadata = 2;
	    					break;
	    				case 7:
	    					metadata = 6;
	    					break;
	    				case 6:
	    					metadata = 7;
	    					break;
	    				case 5:
	    					metadata = 4;
	    					break;
	    				case 4:
	    					metadata = 5;
	    					break;
	    			
	    				}
	    			}
					
					if(blockID== Block.lever.blockID||blockID== Block.torchWood.blockID||blockID== Block.torchRedstoneIdle.blockID||blockID== Block.torchRedstoneActive.blockID)
	    			{
	    				switch (metadata)
	    				{
	    				case 12:
	    					metadata = 11;
	    					break;
	    				case 11:
	    					metadata = 12;
	    					break;
	    				case 10:
	    					metadata = 9;
	    					break;
	    				case 9:
	    					metadata = 10;
	    					break;					
	    				case 2:
	    					metadata = 1;
	    					break;
	    				case 3:
	    					metadata = 4;
	    					break;
	    				case 1:
	    					metadata = 2;
	    					break;
	    				case 4:
	    					metadata = 3;
	    					
	    					break;

	    				}

	    			}
			
					if(blockID== Block.pistonBase.blockID||blockID==Block.pistonStickyBase.blockID||blockID==Block.dispenser.blockID)
	    			{
	    				switch (metadata)
	    				{
	    				case 4:
	    					metadata = 5;
	    					break;
	    				case 5:
	    					metadata = 4;
	    					break;
	    				case 13:
	    					metadata = 12;
	    					break;
	    				case 12:
	    					metadata = 13;
	    					break;
	    				case 3:
	    					metadata = 2;
	    					break;
	    				case 2:
	    					metadata = 3;
	    					break;
	    				case 11:
	    					metadata = 10;
	    					break;
	    				case 10:
	    					metadata = 11;
	    					break;
	    			
	    				}
	    			
					
					
	    			}
					
					if(blockID== Block.redstoneRepeaterActive.blockID||blockID==Block.redstoneRepeaterIdle.blockID||blockID== Block.tripWireSource.blockID||blockID== Block.doorIron.blockID||blockID==Block.doorWood.blockID)
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
	    					metadata = 0;
	    					break;
	    				case 3:
	    					metadata = 1;
	    					break;
	    				case 4:
	    					metadata = 6;
	    					break;
	    				case 5:
	    					metadata = 7;
	    					break;
	    				case 6:
	    					metadata = 4;
	    					break;
	    				case 7:
	    					metadata = 5;
	    					break;
	    				case 8:
	    					metadata = 10;
	    					break;
	    				case 9:
	    					metadata = 11;
	    					break;
	    				case 10:
	    					metadata = 8;
	    					break;
	    				case 11:
	    					metadata = 9;
	    					break;
	    				case 12:
	    					metadata = 14;
	    					break;
	    				case 13:
	    					metadata = 15;
	    					break;
	    				case 14:
	    					metadata = 12;
	    					break;
	    				case 15:
	    					metadata = 13;
	    					break;
	    				
	    			
	    				}
	    			
					
					
	    			}
					
					break;
				case 2:
					
					if(Block.blocksList[blockID] instanceof BlockStairs)
	    			{
					
						switch (metadata)
	    				{
	    				case 2:
	    					metadata = 0;
	    					break;
	    				case 3:
	    					metadata = 1;
	    					break;
	    				case 1:
	    					metadata = 2;
	    					break;
	    				case 0:
	    					metadata = 3;
	    					break;
	    				case 4:
	    					metadata = 7;
	    					break;
	    				case 5:
	    					metadata = 6;
	    					break;
	    				case 7:
	    					metadata = 5;
	    					break;
	    				case 6:
	    					metadata = 4;
	    					break;
	    			
	    				}
	    			}
					
					if(blockID== Block.lever.blockID||blockID== Block.torchWood.blockID||blockID== Block.torchRedstoneIdle.blockID||blockID== Block.torchRedstoneActive.blockID)
	    			{
	    				switch (metadata)
	    				{
	    				case 9:
	    					metadata = 12;
	    					break;
	    				case 10:
	    					metadata = 11;
	    					break;
	    				case 12:
	    					metadata = 10;
	    					break;
	    				case 11:
	    					metadata = 9;
	    					break;					
	    				case 4:
	    					metadata = 2;
	    					break;
	    				case 2:
	    					metadata = 3;
	    					break;
	    				case 3:
	    					metadata = 1;
	    					break;
	    				case 1:
	    					metadata = 4;
	    					
	    					break;

	    				}

	    			}
			
					if(blockID== Block.pistonBase.blockID||blockID==Block.pistonStickyBase.blockID||blockID==Block.dispenser.blockID)
	    			{
	    				switch (metadata)
	    				{
	    				case 2:
	    					metadata = 4;
	    					break;
	    				case 3:
	    					metadata = 5;
	    					break;
	    				case 11:
	    					metadata = 13;
	    					break;
	    				case 10:
	    					metadata = 12;
	    					break;
	    				case 4:
	    					metadata = 3;
	    					break;
	    				case 5:
	    					metadata = 2;
	    					break;
	    				case 12:
	    					metadata = 11;
	    					break;
	    				case 13:
	    					metadata = 10;
	    					break;
	    				
	    			
	    				}
	    			
					
					
	    			}
					
					if(blockID== Block.redstoneRepeaterActive.blockID||blockID==Block.redstoneRepeaterIdle.blockID||blockID== Block.tripWireSource.blockID||blockID== Block.doorIron.blockID||blockID==Block.doorWood.blockID)
	    			{
	    				switch (metadata)
	    				{
	    				case 1:
	    					metadata = 0;
	    					break;
	    				case 2:
	    					metadata = 1;
	    					break;
	    				case 3:
	    					metadata = 2;
	    					break;
	    				case 0:
	    					metadata = 3;
	    					break;
	    				case 5:
	    					metadata = 4;
	    					break;
	    				case 6:
	    					metadata = 5;
	    					break;
	    				case 7:
	    					metadata = 6;
	    					break;
	    				case 4:
	    					metadata = 7;
	    					break;
	    				case 9:
	    					metadata = 8;
	    					break;
	    				case 10:
	    					metadata = 9;
	    					break;
	    				case 11:
	    					metadata = 10;
	    					break;
	    				case 8:
	    					metadata = 11;
	    					break;
	    				case 13:
	    					metadata = 12;
	    					break;
	    				case 14:
	    					metadata = 13;
	    					break;
	    				case 15:
	    					metadata = 14;
	    					break;
	    				case 12:
	    					metadata = 15;
	    					break;
	    				
	    			
	    				}
	    			
					
					
	    			}
					
					
					
					
					
					
					break;
				case 3:
					/**
					 * this is the default case- never need to change anything here
					 * 
					 */
					
					
					
					
					break;
			
				}
    			
    			
    		}
    		return metadata;
    	}

		public void generateSchematic(LinkData link, int xOffset, int yOffset, int zOffset)
		{
			
			World world;
			Chunk chunk;
			dimHelper.dimList.get(link.destDimID).hasBeenFilled=this.didRead;
			SchematicLoader loader=this;
			
			int i = link.destXCoord;
			int j = link.destYCoord-1;
			int k = link.destZCoord;
			
		
			
			
			
			
			if(dimHelper.getWorld(link.destDimID)==null)
			{
				dimHelper.initDimension(link.destDimID);
			}
			world=dimHelper.getWorld(link.destDimID);
		
			int x;
			int y;
			int z;
			
			
			int xCooe=0;
            int yCooe=0;
            int zCooe=0;
            
            
        	
        	
		        
			
		         
			
			  for ( x = 0; x < loader.width; ++x) 
			  {
		            for ( y = 0; y < loader.height; ++y) 
		            {
		                for ( z = 0; z < loader.length; ++z) 
		                {
		                	if(link.linkOrientation==0)
		                	{
		                		zCooe=x-20;
		                		yCooe=y-6;
		                		xCooe=-z+35;
		                		
		                	}
		                	if(link.linkOrientation==1)
		                	{
		                		xCooe=-x+20;
		                		yCooe=y-6;
		                		zCooe=-z+35;
		                	}
		                	if(link.linkOrientation==2)
		                	{
		                		zCooe=-x+20;
		                		yCooe=y-6;
		                		xCooe=+z-35;
		                	}
		                	if(link.linkOrientation==3)
		                	{
		                		xCooe=x-20;
		                		yCooe=y-6;
		                		zCooe=z-35;
		                	}
		                		
		                    int index = y * loader.width * loader.length + z * loader.width + x;
		                    
		                    int blockToReplace=loader.blocks[index];
		                    int blockMetaData=loader.data[index];
		                    NBTTagList tileEntity = loader.tileentities;
		                    HashMap tileEntityMap= new HashMap();
		                    int size = tileEntity.tagCount();
		                    
		                    
		                    if(blockToReplace==Block.doorIron.blockID)
		                    {
		                    	this.sideLinks.add(new Point3D(i+xCooe, j+yCooe, k+zCooe));
		                    }
		                    if(blockToReplace==Block.doorWood.blockID)
		                    {
		                    	this.exitLinks.add(new Point3D(i+xCooe, j+yCooe, k+zCooe));
		                    }
		                    if(blockToReplace==-124)
		                    {
		                    	blockToReplace=Block.tripWire.blockID;
		                    }
		                    if(blockToReplace==-125)
		                    {
		                    	blockToReplace=Block.tripWireSource.blockID;
		                    }
		                    if(blockToReplace<0&&blockToReplace!=-39)
		                    {
		                    }
		                    
		                    if(blockToReplace<0)
		                    {
		                    	blockToReplace=mod_pocketDim.blockDimWallID;
		                    }
		                    
		                    if(blockToReplace>0)
		                    {
		                    	
		                    	this.transMeta=this.transformMetadata(blockMetaData, link.linkOrientation, blockToReplace);

		                    	if(blockToReplace==Block.doorIron.blockID)
				                {
		                    		setBlockDirectly(world,i+xCooe,j+yCooe,k+zCooe,mod_pocketDim.dimDoorID, transMeta );
				                }
		                    	else
				                if(blockToReplace==Block.doorWood.blockID)
				                {
				                	setBlockDirectly(world,i+xCooe,j+yCooe,k+zCooe,mod_pocketDim.ExitDoorID, transMeta );
				                }
				                else
				                {
				                    
		                    	setBlockDirectly(world,i+xCooe,j+yCooe,k+zCooe,blockToReplace, transMeta );
				                }
	                    		
		                    	if(Block.blocksList[blockToReplace] instanceof BlockContainer)
		                    	{
		                    	//	System.out.println("found container");
		                    		Random rand= new Random();
		                    		if(world.getBlockTileEntity(i+xCooe, j+yCooe, k+zCooe) instanceof TileEntityChest)
		                    		{
		                    			TileEntityChest chest = (TileEntityChest) world.getBlockTileEntity(i+xCooe, j+yCooe, k+zCooe);
		                    			
		                    		   ChestGenHooks info = ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST);
		                    		   if(rand.nextBoolean())
		                    		   {
		                    			   chest.setInventorySlotContents(rand.nextInt(27), new ItemStack(mod_pocketDim.itemDimDoor, 1));
		                    		   }
		                    		   if(rand.nextBoolean())
		                    		   {
		                    			   chest.setInventorySlotContents(rand.nextInt(27), new ItemStack(mod_pocketDim.itemLinkSignature, 1));
		                    		   }
		                    		   if(rand.nextBoolean())
		                    		   {
		                    			   chest.setInventorySlotContents(rand.nextInt(27), new ItemStack(mod_pocketDim.itemRiftRemover, 1));
		                    		   }
		                    		   if(rand.nextBoolean())
		                    		   {
		                    			   chest.setInventorySlotContents(rand.nextInt(27), new ItemStack(mod_pocketDim.itemRiftBlade, 1));
		                    		   }
		                    		   if(rand.nextBoolean())
		                    		   {
		                    			   chest.setInventorySlotContents(rand.nextInt(27), new ItemStack(mod_pocketDim.blockDimWall, rand.nextInt(20)+5));
		                    		   }
	                                    WeightedRandomChestContent.generateChestContents(rand, info.getItems(rand),(TileEntityChest)world.getBlockTileEntity(i+xCooe, j+yCooe, k+zCooe)  , info.getCount(rand));

                                    

		                    		}
		                    		if(world.getBlockTileEntity(i+xCooe, j+yCooe, k+zCooe) instanceof TileEntityDispenser)
		                    		{
		                    			TileEntityDispenser dispenser = (TileEntityDispenser) world.getBlockTileEntity(i+xCooe, j+yCooe, k+zCooe);
		                    			dispenser.addItem(new ItemStack(Item.arrow, 64));
		                    	
		                    		}
		                    		
		                    	}
		                    }
		            }
		        }
			
			  
			  }
			  
			  
			  
			 LinkData outgoingLink = dimHelper.instance.getLinkDataFromCoords(link.destXCoord, link.destYCoord, link.destZCoord, link.destDimID);
			 
			 

			 	
				 
				
			 
			 for(Point3D point : this.sideLinks)
			 {
				 if(world.getBlockId(point.getX(), point.getY(), point.getZ())==mod_pocketDim.dimDoorID&&world.getBlockId(point.getX(), point.getY()-1, point.getZ())==mod_pocketDim.dimDoorID)
				 {
					 
					int depth = dimHelper.instance.getDimDepth(link.locDimID);
					int xNoise = 0;
					int zNoise =0;
					switch(world.getBlockMetadata(point.getX(), point.getY()-1, point.getZ()))
					{
					case 0:
						 xNoise = (int)rand.nextInt(depth+1*200)+depth*50;
						 zNoise = (int)rand.nextInt(depth+1*20)-(10)*depth;
						
						break;
					case 1:
						xNoise =  (int)rand.nextInt(depth+1*20)-(10)*depth;
						zNoise = (int) rand.nextInt(depth+1*200)+depth*50;
						
						break;
					case 2:
						 xNoise = - (rand.nextInt(depth+1*200)+depth*50);
						 zNoise =  (int)rand.nextInt(depth+1*20)-(10)*depth;
						
						break;
					case 3:
						 xNoise =  (int)rand.nextInt(depth+1*20)-(10)*depth;
						 zNoise = -(rand.nextInt(depth+1*200)+depth*50);
						
						break;
					
					}
				

					
					
					
					
					
				 	LinkData sideLink = new LinkData(link.destDimID,0,point.getX(), point.getY(), point.getZ(),xNoise+point.getX(), point.getY()+1, zNoise+point.getZ(),true,world.getBlockMetadata(point.getX(), point.getY()-1, point.getZ()));
					dimHelper.instance.createPocket(sideLink, true, true);
					

				

				
				 }
			 }
			 for(Point3D point : this.exitLinks)
			 {
				 try
				 {
					 
					 if(world.getBlockId(point.getX(), point.getY(), point.getZ())==mod_pocketDim.ExitDoorID&&world.getBlockId(point.getX(), point.getY()-1, point.getZ())==mod_pocketDim.ExitDoorID&&world.getBlockId(point.getX(), point.getY()-2, point.getZ())==Block.sandStone.blockID)
					 {
					 	
						 LinkData randomLink=dimHelper.instance.getRandomLinkData(false);
						 	
						LinkData sideLink = new LinkData(link.destDimID,dimHelper.dimList.get(link.locDimID).exitDimLink.destDimID,point.getX(), point.getY(), point.getZ(),point.getX(), 0, point.getZ(),true,world.getBlockMetadata(point.getX(), point.getY()-1, point.getZ()));

						if(sideLink.destDimID==mod_pocketDim.limboDimID)
						{
							sideLink.destDimID=0;
						}
						else if((rand.nextBoolean()&&randomLink!=null))
						 {
							 sideLink.destDimID=randomLink.locDimID;
							// System.out.println("randomLink");
						 }
					
					 	
						 	
						 	sideLink.destYCoord=yCoordHelper.getFirstUncovered(sideLink.destDimID, point.getX(),10,point.getZ());
						 	
						 	if(sideLink.destYCoord<5)
						 	{
						 		sideLink.destYCoord=70;
						 	}

						sideLink.linkOrientation=world.getBlockMetadata(point.getX(), point.getY()-1, point.getZ());
						dimHelper.instance.createLink(sideLink);
						dimHelper.instance.createLink(sideLink.destDimID , sideLink.locDimID, sideLink.destXCoord, sideLink.destYCoord, sideLink.destZCoord, sideLink.locXCoord, sideLink.locYCoord, sideLink.locZCoord, dimHelper.instance.flipDoorMetadata(sideLink.linkOrientation));
						
						setBlockDirectly(world,point.getX(), point.getY()-2, point.getZ(),Block.stoneBrick.blockID,0);
					//	setBlockDirectly(world,point.getX(), point.getY()-1, point.getZ(),mod_pocketDim.ExitDoorID,sideLink.linkOrientation);
					//	setBlockDirectly(world,point.getX(), point.getY(), point.getZ(),mod_pocketDim.ExitDoorID,8);

					 }
					 else if ((world.getBlockId(point.getX(), point.getY(), point.getZ())==mod_pocketDim.ExitDoorID&&world.getBlockId(point.getX(), point.getY()-1, point.getZ())==mod_pocketDim.ExitDoorID&&world.getBlockId(point.getX(), point.getY()-2, point.getZ())!=Block.sandStone.blockID))
					 {
						 this.incomingLink = point;
					 }
				 }
				 
					 
				catch(Exception E)
				{
					E.printStackTrace();
				}
				 
					 
				 
			 }
			 
			 
			 if(!this.incomingLink.equals(new Point3D(0,0,0)))
			 	{
			 		outgoingLink.locXCoord=this.incomingLink.getX();
			 		outgoingLink.locYCoord=this.incomingLink.getY();
			 		outgoingLink.locZCoord=this.incomingLink.getZ();
			 		outgoingLink.linkOrientation=world.getBlockMetadata(incomingLink.getX(), incomingLink.getY()-1, incomingLink.getZ());
				 	dimHelper.instance.createLink(outgoingLink);

			 		link.destXCoord=this.incomingLink.getX();
			 		link.destYCoord=this.incomingLink.getY();
			 		link.destZCoord=this.incomingLink.getZ();
			 	}

		}

		
		public void setBlockDirectly(World world, int x, int y, int z,int id, int metadata)
		{
			Chunk chunk;
			this.cX=x >>4;
			this.cZ=z >>4;
			this.cY=y >>4;

			int chunkX=(x % 16)< 0 ? ((x) % 16)+16 : ((x) % 16);
			int chunkY=y;
			int chunkZ=((z) % 16)< 0 ? ((z) % 16)+16 : ((z) % 16);
			
			
    		//	this.chunk=new EmptyChunk(world,cX, cZ);
			try
			{
			chunk=world.getChunkFromChunkCoords(cX, cZ);
				 if (chunk.getBlockStorageArray()[cY] == null) {
					 chunk.getBlockStorageArray()[cY] = new ExtendedBlockStorage(cY << 4, !world.provider.hasNoSky);
             }
    		
    		
    		chunk.getBlockStorageArray()[cY].setExtBlockID(chunkX, (y) & 15, chunkZ, id);
    		chunk.getBlockStorageArray()[cY].setExtBlockMetadata(chunkX, (y) & 15, chunkZ, metadata);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
        	
		}
	
 
}