package StevenDimDoors.mod_pocketDim;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
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
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.MathHelper;
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
	private final static int EAST_DOOR_METADATA = 0;
	private final static int SOUTH_DOOR_METADATA = 1;
	private final static int WEST_DOOR_METADATA = 2;
	private final static int NORTH_DOOR_METADATA = 3;
	private final static int REFERENCE_DOOR_ORIENTATION = NORTH_DOOR_METADATA;

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

				else if(blockID== Block.chest.blockID||blockID== Block.chestTrapped.blockID||blockID== Block.ladder.blockID)
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
				else if(blockID==Block.vine.blockID)
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
		dimHelper.dimList.get(destDimID).hasBeenFilled = true;

		if (dimHelper.getWorld(destDimID) == null)
		{
			dimHelper.initDimension(destDimID);
		}
		world = dimHelper.getWorld(destDimID);
		
		//The following Random initialization code is based on code from ChunkProviderGenerate.
		//It makes our generation depend on the world seed.
		Random rand = new Random(world.getSeed());
        long factorA = rand.nextLong() / 2L * 2L + 1L;
        long factorB = rand.nextLong() / 2L * 2L + 1L;
        rand.setSeed((riftX >> 4) * factorA + (riftZ >> 4) * factorB ^ world.getSeed());

		//coords relative to the schematic, start at 0 and increase up to max height/width/length
		int x, y, z;

		//The real coordinates where a block will be placed
		int realX = 0;
		int realY = 0;
		int realZ = 0;
		
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
					else if (blockToReplace == Block.endPortalFrame.blockID)
					{
						monolithSpawns.add(new Point3D(x,y,z));
					}
				}
			}
		}

		//Compute the Y-axis translation that places our structure correctly
		int offsetY = riftY - entrance.getY();
		
		//Loop to actually place the blocks
		for (x = 0; x < width; x++) 
		{
			for (z = 0; z < length; z++) 
			{
				//Compute the X-axis and Z-axis translations that will shift
				//and rotate our structure properly.
				switch (orientation)
				{
					case REFERENCE_DOOR_ORIENTATION:
						realX = (x - entrance.getX()) + riftX;
						realZ = (z - entrance.getZ()) + riftZ;
						break;
					case (REFERENCE_DOOR_ORIENTATION + 1) % 4: //270 degree CCW rotation
						realX = (z - entrance.getZ()) + riftX;
						realZ = -(x - entrance.getX()) + riftZ;
						break;
					case (REFERENCE_DOOR_ORIENTATION + 2) % 4: //180 degree rotation
						realX = -(x - entrance.getX()) + riftX;
						realZ = -(z - entrance.getZ()) + riftZ;
						break;
					case (REFERENCE_DOOR_ORIENTATION + 3) % 4: //90 degree CCW rotation
						realX = -(z - entrance.getZ()) + riftX;
						realZ = (x - entrance.getX()) + riftZ;
						break;
				}
				
				for (y = 0; y < height; y++) 
				{
					int index = y * width * length + z * width + x;
					realY = y + offsetY;

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
							setBlockDirectly(world,realX, realY, realZ,properties.DimensionalDoorID, transMeta );
						}
						else if(blockToReplace==Block.doorWood.blockID)
						{
							setBlockDirectly(world,realX, realY, realZ,properties.WarpDoorID, transMeta );
						}
						else
						{							
							setBlockDirectly(world,realX, realY, realZ,blockToReplace, transMeta );
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
							if(world.getBlockTileEntity(realX, realY, realZ) instanceof TileEntityChest)
							{
								TileEntityChest chest = (TileEntityChest) world.getBlockTileEntity(realX, realY, realZ);
								ChestGenHooks info = DDLoot.DungeonChestInfo;
								WeightedRandomChestContent.generateChestContents(rand, info.getItems(rand), (TileEntityChest)world.getBlockTileEntity(realX, realY, realZ), info.getCount(rand));
							}

							//fill dispensers
							if(world.getBlockTileEntity(realX, realY, realZ) instanceof TileEntityDispenser)
							{
								TileEntityDispenser dispenser = (TileEntityDispenser) world.getBlockTileEntity(realX, realY, realZ);
								dispenser.addItem(new ItemStack(Item.arrow, 64));

							}
						}
					}
				}
			}
		}

		//Set up variables to use transformPoint()
		Point3D pocketOrigin = new Point3D(riftX, riftY, riftZ);
		Point3D zeroPoint = new Point3D(0, 0, 0);
		
		//Generate the LinkData defined by the door placement, Iron Dim doors first
		for(Point3D point : sideLinks)
		{
			int depth = dimHelper.instance.getDimDepth(originDimID);
			int forwardNoise = MathHelper.getRandomIntegerInRange(rand, -50 * (depth + 1), 150 * (depth + 1));
			int sidewaysNoise = MathHelper.getRandomIntegerInRange(rand, -10 * (depth + 1), 10 * (depth + 1));

			//Transform doorLocation to the pocket coordinate system.
			Point3D doorLocation = point.clone();
			transformPoint(doorLocation, entrance, orientation - REFERENCE_DOOR_ORIENTATION, pocketOrigin);
			int blockDirection = world.getBlockMetadata(doorLocation.getX(), doorLocation.getY() - 1, doorLocation.getZ());

			//Rotate the link destination noise to point in the same direction as the door exit
			//and add it to the door's location. Use EAST as the reference orientation since linkDestination
			//is constructed as if pointing East.
			Point3D linkDestination = new Point3D(forwardNoise, 0, sidewaysNoise);
			transformPoint(linkDestination, zeroPoint, blockDirection - EAST_DOOR_METADATA, doorLocation);
			
			//Create the link between our current door and its intended exit in destination pocket
			LinkData sideLink = new LinkData(destDimID, 0,
					doorLocation.getX(),
					doorLocation.getY(),
					doorLocation.getZ(),
					linkDestination.getX(),
					linkDestination.getY() + 1,
					linkDestination.getZ(),
					true, blockDirection);
			dimHelper.instance.createPocket(sideLink, true, true);
		}

		//generate linkData for wooden dim doors leading to the overworld
		for(Point3D point : exitLinks)
		{
			//DISABLED FOR TESTING
			/*try
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
			}*/
		}

		//spawn monoliths
		for(Point3D point : monolithSpawns)
		{
			//Transform the frame block's location to the pocket coordinate system
			Point3D frameLocation = point.clone();
			transformPoint(frameLocation, entrance, orientation - REFERENCE_DOOR_ORIENTATION, pocketOrigin);
			
			Entity mob = new MobObelisk(world);
			mob.setLocationAndAngles(frameLocation.getX(), frameLocation.getY(), frameLocation.getZ(), 1, 1); //TODO: Why not set the angles to 0? @.@ ~SenseiKiwi
			world.spawnEntityInWorld(mob);
		}
	}
	
	private void transformPoint(Point3D position, Point3D srcOrigin, int angle, Point3D destOrigin)
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
		
		//Rotations are considered in counterclockwise form because coordinate systems are
		//often assumed to be right-handed and convenient formulas are available for
		//common counterclockwise rotations.
		//Reference: http://en.wikipedia.org/wiki/Rotation_matrix#Common_rotations
		int rx;
		int rz;
		switch (angle)
		{
			case 0: //No rotation
				rx = tx;
				rz = tz;
				break;
			case 1: //270 degrees counterclockwise
				rx = tz;
				rz = -tx;
				break;
			case 2: //180 degrees
				rx = -tx;
				rz = -tz;
				break;
			case 3: //90 degrees counterclockwise
				rx = -tz;
				rz = tx;
				break;
			default: //This should never happen.
				throw new IllegalStateException("Invalid angle value. This should never happen!");
		}
		
		position.setX( rx + destOrigin.getX() );
		position.setY( ty + destOrigin.getY() );
		position.setZ( rz + destOrigin.getZ() );
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
		if (Block.blocksList[id] == null)
		{
			return;
		}

		int cX = x >> 4;
		int cZ = z >> 4;
		int cY = y >> 4;
		Chunk chunk;

		//TODO: This really seems odd to me. Like it's wrong. ~SenseiKiwi
		int chunkX=(x % 16)< 0 ? ((x) % 16)+16 : ((x) % 16);
		int chunkZ=((z) % 16)< 0 ? ((z) % 16)+16 : ((z) % 16);

		try
		{
			chunk = world.getChunkFromChunkCoords(cX, cZ);
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