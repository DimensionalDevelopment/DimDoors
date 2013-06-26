package StevenDimDoors.mod_pocketDim;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockComparator;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockStairs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.ChestGenHooks;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import StevenDimDoors.mod_pocketDim.helpers.yCoordHelper;
import StevenDimDoors.mod_pocketDim.ticking.MobObelisk;

public class SchematicLoader 

{
	private Random rand = new Random();
	public HashMap<Integer,HashMap<Integer, HashMap<Integer,Integer>>> rotationMap = new HashMap<Integer,HashMap<Integer, HashMap<Integer,Integer>>>();
	public int transMeta;







	private DDProperties properties = DDProperties.instance();

	public SchematicLoader() { }

	public int transformMetadata(int metadata, int orientation, int blockID)
	{
		if (DungeonHelper.instance().metadataFlipList.contains(blockID))
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

				else	if(blockID== Block.chest.blockID||blockID== Block.chestTrapped.blockID||blockID== Block.ladder.blockID)
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
				else	if(blockID==Block.vine.blockID)
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

				else	if(blockID== Block.pistonBase.blockID||blockID==Block.pistonExtension.blockID||blockID==Block.pistonStickyBase.blockID||blockID==Block.dispenser.blockID||blockID==Block.dropper.blockID)
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

				else	if(Block.blocksList[blockID] instanceof BlockRedstoneRepeater ||Block.blocksList[blockID] instanceof BlockDoor ||blockID== Block.tripWireSource.blockID||Block.blocksList[blockID] instanceof BlockComparator)
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

				else	if(blockID== Block.chest.blockID||blockID== Block.chestTrapped.blockID||blockID==Block.ladder.blockID)
				{
					switch (metadata)
					{

					case 2:
						metadata = 3;
						break;
					case 3:
						metadata = 2;
						break;					
					case 4:
						metadata = 5;
						break;
					case 5:
						metadata = 4;
						break;



					}

				}

				else	if(blockID==Block.vine.blockID)
				{
					switch (metadata)
					{

					case 1:
						metadata = 4;
						break;
					case 2:
						metadata = 8;
						break;					
					case 4:
						metadata = 1;
						break;
					case 8:
						metadata = 2;
						break;
					}
				}




				else if(blockID== Block.lever.blockID||blockID== Block.torchWood.blockID||blockID== Block.torchRedstoneIdle.blockID||blockID== Block.torchRedstoneActive.blockID)
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

				else	if(blockID== Block.pistonBase.blockID||blockID==Block.pistonStickyBase.blockID||blockID==Block.dispenser.blockID||blockID==Block.dropper.blockID)
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

				else	if(Block.blocksList[blockID] instanceof BlockRedstoneRepeater ||Block.blocksList[blockID] instanceof BlockDoor ||blockID== Block.tripWireSource.blockID||Block.blocksList[blockID] instanceof BlockComparator)
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

				else	if(blockID== Block.chest.blockID||blockID== Block.chestTrapped.blockID||blockID==Block.ladder.blockID)
				{
					switch (metadata)
					{

					case 2:
						metadata = 4;
						break;
					case 3:
						metadata = 5;
						break;					
					case 4:
						metadata = 3;
						break;
					case 5:
						metadata = 2;
						break;



					}

				}

				else	if(blockID==Block.vine.blockID)
				{
					switch (metadata)
					{

					case 1:
						metadata = 8;
						break;
					case 2:
						metadata = 1;
						break;					
					case 4:
						metadata = 2;
						break;
					case 8:
						metadata = 4;
						break;
					}
				}




				else	if(blockID== Block.lever.blockID||blockID== Block.torchWood.blockID||blockID== Block.torchRedstoneIdle.blockID||blockID== Block.torchRedstoneActive.blockID)
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

				else	if(blockID== Block.pistonBase.blockID||blockID==Block.pistonStickyBase.blockID||blockID==Block.dispenser.blockID||blockID==Block.dropper.blockID)

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

				else	if(Block.blocksList[blockID] instanceof BlockRedstoneRepeater ||Block.blocksList[blockID] instanceof BlockDoor ||blockID== Block.tripWireSource.blockID||Block.blocksList[blockID] instanceof BlockComparator)
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

	public void generateSchematic(int riftX, int riftY, int riftZ, int orientation, int destDimID, int originDimID, String schematicPath)
	{
		short width=0;
		short height=0;
		short length=0;

		//list of combined blockIds
		short[] blocks = new short[0];

		//block metaData
		byte[] blockData = new byte[0];

		//first 4 bytes of the block ID
		byte[] blockId = new byte[0];

		//second 4 bytes of the block ID
		byte[] addId = new byte[0];

		NBTTagCompound[] tileEntityList;


		NBTTagList entities;
		NBTTagList tileEntities=null;


		//the wooden door leading into the pocket
		Point3D entrance= new Point3D(0,0,0);

		//the iron dim doors leading to more pockets
		ArrayList<Point3D> sideLinks = new ArrayList<Point3D>();

		//the wooden dim doors leading to the surface
		ArrayList<Point3D> exitLinks = new ArrayList<Point3D>();

		//the locations to spawn monoliths
		ArrayList<Point3D> monolithSpawns = new ArrayList<Point3D>();

		//load the schematic from disk
		try 
		{

			InputStream input;
			String fname= schematicPath ;

			if(!(new File(fname).exists()))
			{
				//get file if its in the Jar
				input = this.getClass().getResourceAsStream(fname);
			}
			else
			{
				//get file if in external library
				System.out.println(new File(fname).exists());
				input = new FileInputStream(fname);
			}
			NBTTagCompound nbtdata = CompressedStreamTools.readCompressed(input);

			//load size of schematic to generate
			width = nbtdata.getShort("Width");
			height = nbtdata.getShort("Height");
			length = nbtdata.getShort("Length");

			//load block info
			blockId = nbtdata.getByteArray("Blocks");
			blockData = nbtdata.getByteArray("Data");
			addId = nbtdata.getByteArray("AddBlocks");

			//create combined block list
			blocks=new short[blockId.length];

			//load ticking things
			tileEntities = nbtdata.getTagList("TileEntities");
			tileEntityList = new NBTTagCompound[width*height*length];
			/**
			for(int count = 0; count<tileEntities.tagCount(); count++)
			{
				NBTTagCompound tag = (NBTTagCompound)tileEntities.tagAt(count);
				tileEntityList[tag.getInteger("y")*width*length+tag.getInteger("z")*width+tag.getInteger("x")]=tag;
			}

			entities = nbtdata.getTagList("Entities");
			tileentities = nbtdata.getTagList("TileEntities");
			 **/
			input.close();

			//combine the split block IDs into a single short[]
			for (int index = 0; index < blockId.length; index++) 
			{
				if ((index >> 1) >= addId.length) 
				{ 
					blocks[index] = (short) (blockId[index] & 0xFF);
				} 
				else 
				{
					if ((index & 1) == 0) 
					{
						blocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (blockId[index] & 0xFF));
					} 
					else 
					{
						blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (blockId[index] & 0xFF));
					}
				}
			}
		}
		catch (Exception e) 
		{
			System.out.println("Error- could not find file "+schematicPath);
			e.printStackTrace();
		}

		World world;
		Chunk chunk;
		dimHelper.dimList.get(destDimID).hasBeenFilled=true;

		if(dimHelper.getWorld(destDimID)==null)
		{
			dimHelper.initDimension(destDimID);
		}
		world=dimHelper.getWorld(destDimID);

		//coords relative to the schematic, start at 0 and increase up to max height/width/length
		int x, y, z;

		//relative offset between the schematic coords and world coords
		int offsetX = 0;
		int offsetY = 0;
		int offsetZ = 0;
		
		//first loop through the .schematic to load in all rift locations, and monolith spawn locations.
		//also finds the incomingLink location, which determines the final position of the generated .schematic
		for ( x = 0; x < width; ++x) 
		{
			for ( y = 0; y < height; ++y) 
			{
				for ( z = 0; z < length; ++z) 
				{

					int index = y * width * length + z * width + x;

					int blockToReplace=blocks[index];
					int blockMetaData=blockData[index];

					//NBTTagList tileEntity = tileEntities;
					//int size = tileEntity.tagCount();


					if(blockToReplace==Block.doorIron.blockID&&blocks[ (y-1) * width * length + z * width + x]==Block.doorIron.blockID)
					{
						sideLinks.add(new Point3D(x,y,z));
					}
					else if(blockToReplace==Block.doorWood.blockID)
					{
						if( ((y-2) * width * length + z * width + x)>=0&&blocks[ (y-2) * width * length + z * width + x]==Block.sandStone.blockID&&blocks[ (y-1) * width * length + z * width + x]==Block.doorWood.blockID)
						{
							exitLinks.add(new Point3D(x,y,z));
						}
						else if(((y-1) * width * length + z * width + x)>=0&&blocks[ (y-1) * width * length + z * width + x]==Block.doorWood.blockID)
						{
							entrance=(new Point3D(x,y,z));

						}
					}
					else if(blockToReplace==Block.endPortalFrame.blockID)
					{
						monolithSpawns.add(new Point3D(x,y,z));

					}

				}
			}
		}

		//Compute the Y-axis translation that places our structure correctly
		offsetY = riftY - entrance.getY();
		
		//Loop to actually place the blocks
		for ( x = 0; x < width; x++) 
		{
			for ( z = 0; z < length; z++) 
			{
				//Compute the X-axis and Z-axis translations that will shift
				//and rotate our structure properly.
				switch (orientation)
				{
					case 0: //South
						offsetX = entrance.getZ() + riftX;
						offsetZ = -entrance.getX() + riftZ;
						break;
					case 1: //West
						offsetX = entrance.getX() + riftX;
						offsetZ = entrance.getZ() + riftZ;
						break;
					case 2: //North
						offsetX = -entrance.getZ() + riftX;
						offsetZ = entrance.getX() + riftZ;
						break;
					case 3: //East
						offsetX = -entrance.getX() + riftX;
						offsetZ = -entrance.getZ() + riftZ;
						break;
				}
				
				for ( y = 0; y < height; y++) 
				{

					int index = y * width * length + z * width + x;

					int blockToReplace=blocks[index];
					int blockMetaData=blockData[index];
					NBTTagList tileEntity = tileEntities;

					//replace tagging blocks with air, and mob blocks with FoR
					if(blockToReplace==Block.endPortalFrame.blockID)
					{
						blockToReplace=0;
					}
					else if(Block.blocksList[blockToReplace]==null&&blockToReplace!=0||blockToReplace>158) //TODO- replace 158 with max vanilla block ID
					{
						blockToReplace=mod_pocketDim.blockDimWall.blockID;
					}

					//place blocks and metaData
					if(blockToReplace>0)
					{
						//rotate the metaData blocks
						this.transMeta=this.transformMetadata(blockMetaData, orientation, blockToReplace);

						//convert vanilla doors to dim doors, then place vanilla blocks
						if(blockToReplace==Block.doorIron.blockID)
						{
							setBlockDirectly(world,x+offsetX,y+offsetY,z+offsetZ,properties.DimensionalDoorID, transMeta );
						}
						else if(blockToReplace==Block.doorWood.blockID)
						{
							setBlockDirectly(world,x+offsetX,y+offsetY,z+offsetZ,properties.WarpDoorID, transMeta );
						}
						else
						{							
							setBlockDirectly(world,x+offsetX,y+offsetY,z+offsetZ,blockToReplace, transMeta );
						}

						//generate container inventories
						if(Block.blocksList[blockToReplace] instanceof BlockContainer)
						{
							/**
							TileEntity tile = world.getBlockTileEntity(i+xCooe, j+yCooe, k+zCooe);
							NBTTagCompound tag = this.tileEntityList[index];
							if(tag!=null)
							{
								tile.readFromNBT(tag);
							}
							 **/

							//fill chests
							if(world.getBlockTileEntity(x+offsetX, y+offsetY, z+offsetZ) instanceof TileEntityChest)
							{
								TileEntityChest chest = (TileEntityChest) world.getBlockTileEntity(x+offsetX, y+offsetY, z+offsetZ);
								ChestGenHooks info = DDLoot.DungeonChestInfo;
								WeightedRandomChestContent.generateChestContents(rand, info.getItems(rand), (TileEntityChest)world.getBlockTileEntity(x+offsetX, y+offsetY, z+offsetZ), info.getCount(rand));
							}

							//fill dispensers
							if(world.getBlockTileEntity(x+offsetX, y+offsetY, z+offsetZ) instanceof TileEntityDispenser)
							{
								TileEntityDispenser dispenser = (TileEntityDispenser) world.getBlockTileEntity(x+offsetX, y+offsetY, z+offsetZ);
								dispenser.addItem(new ItemStack(Item.arrow, 64));

							}
						}
					}
				}
			}
		}

		//generate the LinkData defined by the door placement, Iron Dim doors first
		for(Point3D point : sideLinks)
		{


			int depth = dimHelper.instance.getDimDepth(originDimID);
			int xNoise = 0;
			int zNoise =0;
			switch(world.getBlockMetadata(point.getX()+offsetX, point.getY()+offsetY-1, point.getZ()+offsetZ))
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

			LinkData sideLink = new LinkData(destDimID,0,point.getX()+offsetX, point.getY()+offsetY, point.getZ()+offsetZ,xNoise+point.getX()+offsetX, point.getY()+offsetY+1, zNoise+point.getZ()+offsetZ,true,world.getBlockMetadata(point.getX()+offsetX, point.getY()+offsetY-1, point.getZ()+offsetZ));
			dimHelper.instance.createPocket(sideLink, true, true);
		}

		//generate linkData for wooden dim doors leading to the overworld
		for(Point3D point : exitLinks)
		{
			try
			{
				LinkData randomLink=dimHelper.instance.getRandomLinkData(false);
				LinkData sideLink = new LinkData(destDimID,dimHelper.dimList.get(originDimID).exitDimLink.destDimID,point.getX()+offsetX, point.getY()+offsetY, point.getZ()+offsetZ,point.getX()+offsetX, 0, point.getZ()+offsetZ,true,world.getBlockMetadata(point.getX()+offsetX, point.getY()+offsetY-1, point.getZ()+offsetZ));

				if(sideLink.destDimID==properties.LimboDimensionID)
				{
					sideLink.destDimID=0;
				}
				else if((rand.nextBoolean()&&randomLink!=null))
				{
					sideLink.destDimID=randomLink.locDimID;
					// System.out.println("randomLink");
				}

				sideLink.destYCoord=yCoordHelper.getFirstUncovered(sideLink.destDimID, point.getX()+offsetX,10,point.getZ()+offsetZ);

				if(sideLink.destYCoord<5)
				{
					sideLink.destYCoord=70;
				}

				sideLink.linkOrientation=world.getBlockMetadata(point.getX()+offsetX, point.getY()+offsetY-1, point.getZ()+offsetZ);

				dimHelper.instance.createLink(sideLink);
				dimHelper.instance.createLink(sideLink.destDimID , sideLink.locDimID, sideLink.destXCoord, sideLink.destYCoord, sideLink.destZCoord, sideLink.locXCoord, sideLink.locYCoord, sideLink.locZCoord, dimHelper.instance.flipDoorMetadata(sideLink.linkOrientation));

				if(world.getBlockId(point.getX()+offsetX, point.getY()+offsetY-3, point.getZ()+offsetZ) == properties.FabricBlockID)
				{
					setBlockDirectly(world,point.getX()+offsetX, point.getY()+offsetY-2, point.getZ()+offsetZ,Block.stoneBrick.blockID,0);
				}
				else
				{
					setBlockDirectly(world,point.getX()+offsetX, point.getY()+offsetY-2, point.getZ()+offsetZ,world.getBlockId(point.getX()+offsetX, point.getY()+offsetY-3, point.getZ()+offsetZ),world.getBlockMetadata(point.getX()+offsetX, point.getY()+offsetY-3, point.getZ()+offsetZ));
				}
			}
			catch(Exception E)
			{
				E.printStackTrace();
			}
		}

		//spawn monoliths
		for(Point3D point : monolithSpawns)
		{
			Entity mob = new MobObelisk(world);
			mob.setLocationAndAngles(point.getX()+offsetX,point.getY()+offsetY, point.getZ()+offsetZ, 1, 1);
			world.spawnEntityInWorld(mob);
		}
	}

	public void generateDungeonPocket(LinkData link)
	{
		String filePath=DungeonHelper.instance().defaultBreak.schematicPath;
		if(dimHelper.dimList.containsKey(link.destDimID))
		{
			if(dimHelper.dimList.get(link.destDimID).dungeonGenerator==null)
			{
				DungeonHelper.instance().generateDungeonLink(link);
			}
			filePath = dimHelper.dimList.get(link.destDimID).dungeonGenerator.schematicPath;	
		}
		this.generateSchematic(link.destXCoord, link.destYCoord, link.destZCoord, link.linkOrientation, link.destDimID, link.locDimID, filePath);
	}


	public void setBlockDirectly(World world, int x, int y, int z,int id, int metadata)
	{
		if(Block.blocksList[id]==null)
		{
			return;
		}

		int cX;
		int cZ;
		int cY;

		Chunk chunk;
		cX = x >> 4;
				cZ = z >> 4;
		cY=y >>4;

						int chunkX=(x % 16)< 0 ? ((x) % 16)+16 : ((x) % 16);
						int chunkZ=((z) % 16)< 0 ? ((z) % 16)+16 : ((z) % 16);


						//	this.chunk=new EmptyChunk(world,cX, cZ);
						try
						{
							chunk=world.getChunkFromChunkCoords(cX, cZ);
							if (chunk.getBlockStorageArray()[cY] == null) 
							{
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