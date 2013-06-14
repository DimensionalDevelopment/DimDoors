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

public class SchematicLoader 

{
	public short width;
	public short height;
	public short length;

	public short[] blocks = new short[0];
	public byte[] blockData = new byte[0];
	public byte[] blockId = new byte[0];

	public byte[] addId = new byte[0];

	public NBTTagCompound[] tileEntityList;


	public NBTTagList entities;
	public NBTTagList tileEntities;

	private Random rand = new Random();

	public Point3D incomingLink= new Point3D(0,0,0);

	public ArrayList<Point3D> sideLinks = new ArrayList<Point3D>();
	public ArrayList<Point3D> exitLinks = new ArrayList<Point3D>();

	public HashMap<Integer,HashMap<Integer, HashMap<Integer,Integer>>> rotationMap = new HashMap<Integer,HashMap<Integer, HashMap<Integer,Integer>>>();

	public int transMeta;

	public int cX;
	public int cZ;
	public int cY;

	public boolean didRead = false;
	public String schematic;

	private DDProperties properties = DDProperties.instance();
	
	public SchematicLoader() { }

	public void init(LinkData link)
	{
		String filePath = dimHelper.dimList.get(link.destDimID).dungeonGenerator.schematicPath;

		this.schematic=filePath;
		try 
		{

			InputStream input;
			String fname= schematic ;

			if(!(new File(fname).exists()))
			{


				input = this.getClass().getResourceAsStream(fname);
			}
			else
			{
				System.out.println(new File(fname).exists());
				input = new FileInputStream(fname);
			}
			//FileInputStream fileinputstream = new FileInputStream(file);
			NBTTagCompound nbtdata = CompressedStreamTools.readCompressed(input);

			width = nbtdata.getShort("Width");
			height = nbtdata.getShort("Height");
			length = nbtdata.getShort("Length");

			blockId = nbtdata.getByteArray("Blocks");
			blockData = nbtdata.getByteArray("Data");

			blocks=new short[blockId.length];

			addId = nbtdata.getByteArray("AddBlocks");

			tileEntities = nbtdata.getTagList("TileEntities");
			tileEntityList = new NBTTagCompound[width*height*length];

			for(int count = 0; count<tileEntities.tagCount(); count++)
			{
				NBTTagCompound tag = (NBTTagCompound)tileEntities.tagAt(count);
				tileEntityList[tag.getInteger("y")*width*length+tag.getInteger("z")*width+tag.getInteger("x")]=tag;
			}

			// entities = nbtdata.getTagList("Entities");
			//tileentities = nbtdata.getTagList("TileEntities");



			this.didRead=true;
			input.close();


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
			this.didRead=false;
			System.out.println("Error- could not find file "+schematic);
			e.printStackTrace();

		}

		this.generateSchematic(link, 0, 0, 0);

	}
	public int transformMetadata(int metadata, int orientation, int blockID)
	{
		if(mod_pocketDim.dungeonHelper.metadataFlipList.contains(blockID))
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

				else	if(blockID== Block.pistonBase.blockID||blockID==Block.pistonStickyBase.blockID||blockID==Block.dispenser.blockID||blockID==Block.dropper.blockID)
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

					int index = y * width * length + z * width + x;

					int blockToReplace=loader.blocks[index];
					int blockMetaData=loader.blockData[index];
					NBTTagList tileEntity = loader.tileEntities;
					//int size = tileEntity.tagCount();


					if(blockToReplace==Block.doorIron.blockID)
					{
						this.sideLinks.add(new Point3D(i+xCooe, j+yCooe, k+zCooe));
					}
					if(blockToReplace==Block.doorWood.blockID)
					{
						this.exitLinks.add(new Point3D(i+xCooe, j+yCooe, k+zCooe));
					}

					if(Block.blocksList[blockToReplace]==null&&blockToReplace!=0||blockToReplace>158)
					{
						blockToReplace=mod_pocketDim.blockDimWall.blockID;
					}

					if(blockToReplace>0)
					{

						this.transMeta=this.transformMetadata(blockMetaData, link.linkOrientation, blockToReplace);

						if(blockToReplace==Block.doorIron.blockID)
						{
							setBlockDirectly(world,i+xCooe,j+yCooe,k+zCooe,properties.DimensionalDoorID, transMeta );
						}
						else
							if(blockToReplace==Block.doorWood.blockID)
							{
								setBlockDirectly(world,i+xCooe,j+yCooe,k+zCooe,properties.WarpDoorID, transMeta );
							}
							else
							{

								setBlockDirectly(world,i+xCooe,j+yCooe,k+zCooe,blockToReplace, transMeta );
							}

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


							
		                    	//	System.out.println("found container");
		                    		if(world.getBlockTileEntity(i+xCooe, j+yCooe, k+zCooe) instanceof TileEntityChest)
		                    		{
		                    			TileEntityChest chest = (TileEntityChest) world.getBlockTileEntity(i+xCooe, j+yCooe, k+zCooe);

		                    			ChestGenHooks info = ChestGenHooks.getInfo(DDLoot.DIMENSIONAL_DUNGEON_CHEST);
		                    			WeightedRandomChestContent.generateChestContents(rand, info.getItems(rand), (TileEntityChest)world.getBlockTileEntity(i+xCooe, j+yCooe, k+zCooe), info.getCount(rand));
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
			if(world.getBlockId(point.getX(), point.getY(), point.getZ())==properties.DimensionalDoorID&&world.getBlockId(point.getX(), point.getY()-1, point.getZ())==properties.DimensionalDoorID)
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

				if(world.getBlockId(point.getX(), point.getY(), point.getZ())==properties.WarpDoorID&&world.getBlockId(point.getX(), point.getY()-1, point.getZ())==properties.WarpDoorID&&world.getBlockId(point.getX(), point.getY()-2, point.getZ())==Block.sandStone.blockID)
				{

					LinkData randomLink=dimHelper.instance.getRandomLinkData(false);

					LinkData sideLink = new LinkData(link.destDimID,dimHelper.dimList.get(link.locDimID).exitDimLink.destDimID,point.getX(), point.getY(), point.getZ(),point.getX(), 0, point.getZ(),true,world.getBlockMetadata(point.getX(), point.getY()-1, point.getZ()));

					if(sideLink.destDimID==properties.LimboDimensionID)
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

					if(world.getBlockId(point.getX(), point.getY()-3, point.getZ()) == properties.FabricBlockID)
					{
						setBlockDirectly(world,point.getX(), point.getY()-2, point.getZ(),Block.stoneBrick.blockID,0);

					}
					else
					{
						setBlockDirectly(world,point.getX(), point.getY()-2, point.getZ(),world.getBlockId(point.getX(), point.getY()-3, point.getZ()),world.getBlockMetadata(point.getX(), point.getY()-3, point.getZ()));

					}
				}
				else if ((world.getBlockId(point.getX(), point.getY(), point.getZ()) == properties.WarpDoorID&&world.getBlockId(point.getX(), point.getY()-1, point.getZ())==properties.WarpDoorID&&world.getBlockId(point.getX(), point.getY()-2, point.getZ())!=Block.sandStone.blockID))
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
			dimHelper.instance.createLink(link);

		}

	}


	public void setBlockDirectly(World world, int x, int y, int z,int id, int metadata)
	{
		if(Block.blocksList[id]==null)
		{
			return;
		}
		Chunk chunk;
		this.cX = x >> 4;
					this.cZ = z >> 4;
						this.cY=y >>4;

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