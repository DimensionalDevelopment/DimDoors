package StevenDimDoors.mod_pocketDim.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DungeonData;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.helpers.jnbt.ByteArrayTag;
import StevenDimDoors.mod_pocketDim.helpers.jnbt.CompoundTag;
import StevenDimDoors.mod_pocketDim.helpers.jnbt.NBTOutputStream;
import StevenDimDoors.mod_pocketDim.helpers.jnbt.ShortTag;

/**

 * @Return
 */



public class DungeonHelper
{
	
	public DungeonHelper()
	{
		
	}
	
	private Random rand = new Random();
	
	public  HashMap<Integer, LinkData> customDungeonStatus = new HashMap<Integer, LinkData>();

	public  ArrayList<DungeonData> customDungeons = new ArrayList<DungeonData>();
	
	public  ArrayList<DungeonData> registeredDungeons = new ArrayList<DungeonData>();
	
	public  ArrayList<DungeonData> weightedDungeonGenList = new ArrayList<DungeonData>();

	
	public  ArrayList<DungeonData> simpleHalls = new ArrayList<DungeonData>();

	
	public  ArrayList<DungeonData> complexHalls = new ArrayList<DungeonData>();

	
	public  ArrayList<DungeonData> deadEnds = new ArrayList<DungeonData>();

	
	public  ArrayList<DungeonData> hubs = new ArrayList<DungeonData>();

	
	public  ArrayList<DungeonData> mazes = new ArrayList<DungeonData>();

	
	public  ArrayList<DungeonData> pistonTraps = new ArrayList<DungeonData>();

	
	public  ArrayList<DungeonData> exits = new ArrayList<DungeonData>();


	
	public  ArrayList metadataFlipList = new ArrayList();
	
	public  ArrayList metadataNextList = new ArrayList();
	
	public  DungeonData defaultUp = new DungeonData(0, "/schematic/simpleStairsUp.schematic", true);
	
	public void registerCustomDungeon(File schematicFile)
	{
		try
    	{

		if(schematicFile.getName().contains(".schematic"))
		{
			String[] name = schematicFile.getName().split("_");
			
			if(name.length<4)
			{
        		System.out.println("Importing custom dungeon gen mechanics failed, adding to secondary list");
    			this.customDungeons.add(new DungeonData(0,schematicFile.getAbsolutePath(),true));
				System.out.println("Imported "+schematicFile.getName());

        
			}
			else
			{
				int count=0;
				
				boolean open= name[2].equals("open");
	        	
				int weight = Integer.parseInt(name[3].replace(".schematic", ""));
				
				String path = schematicFile.getAbsolutePath();
	
				while(count<weight)
				{
					if(name[0].equals("hub"))
					{
						this.hubs.add(new DungeonData(weight,path,open));
					}
					else if(name[0].equals("simpleHall"))
	        		{
	        			this.simpleHalls.add(new DungeonData(weight,path,open));
	
	        		}
	        		else if(name[0].equals("complexHall"))
	        		{
	        			this.complexHalls.add(new DungeonData(weight,path,open));
	
	        		}
	        		else if(name[0].equals("trap"))
	        		{
	        			this.pistonTraps.add(new DungeonData(weight,path,open));
	
	        		}
	        		else if(name[0].equals("deadEnd"))
	        		{
	        			this.deadEnds.add(new DungeonData(weight,path,open));
	
	        		}
	        		else if(name[0].equals("exit"))
	        		{
	        			this.exits.add(new DungeonData(weight,path,open));
	
	        		}
	        		else if(name[0].equals("mazes"))
	        		{
	        			this.mazes.add(new DungeonData(weight,path,open));
	        		
	        		}
					count++;
					this.weightedDungeonGenList.add(new DungeonData(weight,path,open));
				}
				
        	
				this.registeredDungeons.add(new DungeonData(weight,path,open));
				System.out.println("Imported "+schematicFile.getName());
			}
			
	
		
		}
		
		
		
		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		System.out.println("Importing custom dungeon failed");
    	}
	}
	
	public  void importCustomDungeons(String dir)
	{
		File file = new File(dir);
		File[] schematicNames=file.listFiles();
        
        if(schematicNames!=null)
        {
        
        
        		for(File schematicFile: schematicNames)
        		{
        			
        			this.registerCustomDungeon(schematicFile);
        			
        			
        		}
        	
        	
        	
        }
        
	}
	
	

	public void registerFlipBlocks()
	{
		this.metadataFlipList.add(Block.dispenser.blockID);
		this.metadataFlipList.add(Block.stairsStoneBrick.blockID);
		this.metadataFlipList.add(Block.lever.blockID);
		this.metadataFlipList.add(Block.stoneButton.blockID);
		this.metadataFlipList.add(Block.redstoneRepeaterIdle.blockID);
		this.metadataFlipList.add(Block.redstoneRepeaterActive.blockID);
		this.metadataFlipList.add(Block.tripWireSource.blockID);
		this.metadataFlipList.add(Block.torchWood.blockID);
		this.metadataFlipList.add(Block.torchRedstoneIdle.blockID);
		this.metadataFlipList.add(Block.torchRedstoneActive.blockID);
		this.metadataFlipList.add(Block.doorIron.blockID);
		this.metadataFlipList.add(Block.doorWood.blockID);
		this.metadataFlipList.add(Block.pistonBase.blockID);
		this.metadataFlipList.add(Block.pistonStickyBase.blockID);
		this.metadataFlipList.add(Block.redstoneComparatorActive);
		this.metadataFlipList.add(Block.redstoneComparatorIdle);
		
		this.metadataFlipList.add(Block.stairsNetherBrick.blockID);
		this.metadataFlipList.add(Block.stairsCobblestone.blockID);
		this.metadataFlipList.add(Block.stairsNetherBrick.blockID);
		this.metadataFlipList.add(Block.stairsNetherQuartz.blockID);
		this.metadataFlipList.add(Block.stairsSandStone.blockID);


		this.metadataNextList.add(Block.redstoneRepeaterIdle.blockID);
		this.metadataNextList.add(Block.redstoneRepeaterActive.blockID);
		
	}
	
	public void registerBaseDungeons()
	{
			this.hubs.add(new DungeonData(0, "/schematics/4WayBasicHall.schematic", false));
	 		this.hubs.add(new DungeonData(0, "/schematics/4WayBasicHall.schematic", false));
	 		this.hubs.add(new DungeonData(0, "/schematics/doorTotemRuins.schematic", true));
	 		this.hubs.add(new DungeonData(0, "/schematics/hallwayTrapRooms1.schematic", false));
	 		this.hubs.add(new DungeonData(0, "/schematics/longDoorHallway.schematic", false));
	 		this.hubs.add(new DungeonData(0, "/schematics/smallRotundaWithExit.schematic", false));
	 		this.hubs.add(new DungeonData(0, "/schematics/fortRuins.schematic", true));
	 		this.hubs.add(new DungeonData(0, "/schematics/4WayHallExit.schematic", false));
	 		this.hubs.add(new DungeonData(0, "/schematics/4WayHallExit.schematic", false));


	 		this.simpleHalls.add(new DungeonData(0, "/schematics/collapsedSingleTunnel1.schematic", false));
	 		this.simpleHalls.add(new DungeonData(0, "/schematics/singleStraightHall1.schematic", false));
	 		this.simpleHalls.add(new DungeonData(0, "/schematics/smallBranchWithExit.schematic", false));
	 		this.simpleHalls.add(new DungeonData(0, "/schematics/smallSimpleLeft.schematic", false));
	 		this.simpleHalls.add(new DungeonData(0, "/schematics/smallSimpleRight.schematic", false));
	 		this.simpleHalls.add(new DungeonData(0, "/schematics/simpleStairsUp.schematic", false));
	 		this.simpleHalls.add(new DungeonData(0, "/schematics/simpleStairsDown.schematic", false));
	 		this.simpleHalls.add(new DungeonData(0, "/schematics/simpleSmallT1.schematic", false));


	 		this.complexHalls.add(new DungeonData(0, "/schematics/brokenPillarsO.schematic", true));
	 		this.complexHalls.add(new DungeonData(0, "/schematics/buggyTopEntry1.schematic", true));
	 		this.complexHalls.add(new DungeonData(0, "/schematics/exitRuinsWithHiddenDoor.schematic", true));
	 		this.complexHalls.add(new DungeonData(0, "/schematics/hallwayHiddenTreasure.schematic", false));
	 		this.complexHalls.add(new DungeonData(0, "/schematics/mediumPillarStairs.schematic", true));
	 		this.complexHalls.add(new DungeonData(0, "/schematics/ruinsO.schematic", true));
	 		this.complexHalls.add(new DungeonData(0, "/schematics/pitStairs.schematic", true));

	 		
	 		this.deadEnds.add(new DungeonData(0, "/schematics/azersDungeonO.schematic", false));
	 		this.deadEnds.add(new DungeonData(0, "/schematics/diamondTowerTemple1.schematic", true));
	 		this.deadEnds.add(new DungeonData(0, "/schematics/fallingTrapO.schematic", false));
	 		this.deadEnds.add(new DungeonData(0, "/schematics/hiddenStaircaseO.schematic", true));
	 		this.deadEnds.add(new DungeonData(0, "/schematics/lavaTrapO.schematic", true));
	 		this.deadEnds.add(new DungeonData(0, "/schematics/randomTree.schematic", true));
	 		this.deadEnds.add(new DungeonData(0, "/schematics/smallHiddenTowerO.schematic", true));
	 		this.deadEnds.add(new DungeonData(0, "/schematics/smallSilverfishRoom.schematic", false));
	 		this.deadEnds.add(new DungeonData(0, "/schematics/tntTrapO.schematic", false));
			this.deadEnds.add(new DungeonData(0, "/schematics/smallDesert.schematic", true));
	 		this.deadEnds.add(new DungeonData(0, "/schematics/smallPond.schematic", true));
	 		
	 		
	 		this.pistonTraps.add(new DungeonData(0, "/schematics/fakeTNTTrap.schematic", false));
	 		this.pistonTraps.add(new DungeonData(0, "/schematics/hallwayPitFallTrap.schematic", false));
	 		this.pistonTraps.add(new DungeonData(0, "/schematics/hallwayPitFallTrap.schematic", false));
	 		this.pistonTraps.add(new DungeonData(0, "/schematics/pistonFallRuins.schematic", false));
	 		this.pistonTraps.add(new DungeonData(0, "/schematics/pistonFloorHall.schematic", false));
	 		this.pistonTraps.add(new DungeonData(0, "/schematics/pistonFloorHall.schematic", false));
	 //		this.pistonTraps.add(new DungeonGenerator(0, "/schematics/pistonHallway.schematic", null));
	 		this.pistonTraps.add(new DungeonData(0, "/schematics/pistonSmasherHall.schematic", false));
	 	//	this.pistonTraps.add(new DungeonGenerator(0, "/schematics/raceTheTNTHall.schematic", false));
	 		this.pistonTraps.add(new DungeonData(0, "/schematics/simpleDropHall.schematic", false));
	 		this.pistonTraps.add(new DungeonData(0, "/schematics/wallFallcomboPistonHall.schematic", false));
	 		this.pistonTraps.add(new DungeonData(0, "/schematics/wallFallcomboPistonHall.schematic", false));
	 		this.pistonTraps.add(new DungeonData(0, "/schematics/lavaPyramid.schematic", true));

	 	

	 		this.mazes.add(new DungeonData(0, "/schematics/smallMaze1.schematic", false));
	 		this.mazes.add(new DungeonData(0, "/schematics/smallMultilevelMaze.schematic", false));
	 	

	 		this.exits.add(new DungeonData(0, "/schematics/exitCube.schematic", true));
	 		this.exits.add(new DungeonData(0, "/schematics/lockingExitHall.schematic", false));
	 		this.exits.add(new DungeonData(0, "/schematics/smallExitPrison.schematic", true));
	 		this.exits.add(new DungeonData(0, "/schematics/lockingExitHall.schematic", false));

	 		
	 		
	 		

	 		this.weightedDungeonGenList.addAll(this.simpleHalls);
	 	 	this.weightedDungeonGenList.addAll(this.exits);
	 		this.weightedDungeonGenList.addAll(this.pistonTraps);
	 		this.weightedDungeonGenList.addAll(this.mazes);
	 		this.weightedDungeonGenList.addAll(this.deadEnds);
	 		this.weightedDungeonGenList.addAll(this.complexHalls);
	 		this.weightedDungeonGenList.addAll(this.hubs);
	 		
	 		for(DungeonData data : this.weightedDungeonGenList)
	 		{
	 			if(!this.registeredDungeons.contains(data))
	 			{
	 				this.registeredDungeons.add(data);
	 		
	 			}
	 		}
	 		
	}
	
	public  DungeonData exportDungeon(World world, int xI, int yI, int zI, String file)
	{
		
		int xMin;
		int yMin;
		int zMin;
		
		int xMax;
		int yMax;
		int zMax;
		
		xMin=xMax=xI;
		yMin=yMax=yI;
		zMin=zMax=zI;
		
		for(int count=0;count<50;count++)
		{
		
			if(world.getBlockId(xMin, yI, zI)!=mod_pocketDim.blockDimWallPermID)
			{
				xMin--;
			}
			if(world.getBlockId(xI, yMin, zI)!=mod_pocketDim.blockDimWallPermID)
			{
				yMin--;
			}
			if(world.getBlockId(xI, yI, zMin)!=mod_pocketDim.blockDimWallPermID)
			{
				zMin--;
			}
			if(world.getBlockId(xMax, yI, zI)!=mod_pocketDim.blockDimWallPermID)
			{
				xMax++;
			}
			if(world.getBlockId(xI, yMax, zI)!=mod_pocketDim.blockDimWallPermID)
			{
				yMax++;
			}
			if(world.getBlockId(xI, yI, zMax)!=mod_pocketDim.blockDimWallPermID)
			{
				zMax++;
			}
		}
		
		short width =(short) (xMax-xMin);
		short height= (short) (yMax-yMin);
		short length= (short) (zMax-zMin);
		
	
		
		 byte[] blocks = new byte[width * height * length];
	        byte[] addBlocks = null;
	        byte[] blockData = new byte[width * height * length];

	        for (int x = 0; x < width; ++x) 
	        {
	            for (int y = 0; y < height; ++y) {
	                for (int z = 0; z < length; ++z) {
	                    int index = y * width * length + z * width + x;
	                    int blockID = world.getBlockId(x+xMin, y+yMin, z+zMin);
	                    int meta= world.getBlockMetadata(x+xMin, y+yMin, z+zMin);
	                    
	                    if(blockID==mod_pocketDim.dimDoorID)
	                    {
	                    	blockID=Block.doorIron.blockID;
	                    }
	                    if(blockID==mod_pocketDim.ExitDoorID)
	                    {
	                    	blockID=Block.doorWood.blockID;

	                    }
	                   
	                    // Save 4096 IDs in an AddBlocks section
	                    if (blockID > 255) {
	                        if (addBlocks == null) { // Lazily create section
	                            addBlocks = new byte[(blocks.length >> 1) + 1];
	                        }

	                        addBlocks[index >> 1] = (byte) (((index & 1) == 0) ?
	                                addBlocks[index >> 1] & 0xF0 | (blockID >> 8) & 0xF
	                                : addBlocks[index >> 1] & 0xF | ((blockID >> 8) & 0xF) << 4);
	                    }

	                    blocks[index] = (byte) blockID;
	                    blockData[index] = (byte) meta;
	                }
	            }
	        }
	        /**
	         *   
	         *   this.nbtdata.setShort("Width", width);
	        this.nbtdata.setShort("Height", height);
	        this.nbtdata.setShort("Length", length);
	        
	    	 this.nbtdata.setByteArray("Blocks", blocks);
        	 this.nbtdata.setByteArray("Data", blockData);
	         */
	        
	        HashMap schematic = new HashMap();
	        schematic.put("Blocks", new ByteArrayTag("Blocks", blocks));
	        schematic.put("Data", new ByteArrayTag("Data", blockData));
	        
	        schematic.put("Width", new ShortTag("Width", (short) width));
	        schematic.put("Length", new ShortTag("Length", (short) length));
	        schematic.put("Height", new ShortTag("Height", (short) height));
	        if (addBlocks != null) {
	            schematic.put("AddBlocks", new ByteArrayTag("AddBlocks", addBlocks));
	        }
	      
	        CompoundTag schematicTag = new CompoundTag("Schematic", schematic);
	        try
	        {
	        	
	        
        	 NBTOutputStream stream = new NBTOutputStream(new FileOutputStream(file));
             stream.writeTag(schematicTag);
             stream.close();
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
	    	this.registerCustomDungeon(new File(file));
	    	
	        return new DungeonData(0,file,true);
	}
	
	
	
	public void generateDungeonlink(LinkData incoming)
	{
		//DungeonGenerator dungeon = mod_pocketDim.registeredDungeons.get(new Random().nextInt(mod_pocketDim.registeredDungeons.size()));
		DungeonData dungeon;
		int depth = dimHelper.instance.getDimDepth(incoming.locDimID)+2;
		
		int depthWeight = rand.nextInt(depth)+rand.nextInt(depth)-2;
		
		depth=depth-2;
	//	DungeonGenerator
		boolean flag = true;
		int count=10;
		try
		{
			
			if(dimHelper.dimList.get(incoming.destDimID)!=null&&dimHelper.dimList.get(incoming.destDimID).dungeonGenerator!=null)
			{
				mod_pocketDim.loader.init(incoming);
				dimHelper.dimList.get(incoming.destDimID).dungeonGenerator=dimHelper.dimList.get(incoming.destDimID).dungeonGenerator;
				return;
			}
		if(incoming.destYCoord>15)
		{
		do
		{
			count--;
			flag = true;
			 dungeon = this.weightedDungeonGenList.get(rand.nextInt(weightedDungeonGenList.size()));

			if(depth<=1)
			{
				if(rand.nextBoolean())
				{
					dungeon = complexHalls.get(rand.nextInt(complexHalls.size()));

				}
				else if(rand.nextBoolean())
				{
					dungeon = hubs.get(rand.nextInt(hubs.size()));

				}
				else  if(rand.nextBoolean())
				{
					dungeon = hubs.get(rand.nextInt(hubs.size()));

				}
				else if(deadEnds.contains(dungeon)||exits.contains(dungeon))
						{
						flag=false;
						}
				
		
				
			}
			else if(depth<=3&&(deadEnds.contains(dungeon)||exits.contains(dungeon)||rand.nextBoolean()))
			{
				if(rand.nextBoolean())
				{
					dungeon = hubs.get(rand.nextInt(hubs.size()));
					
				}
				else if(rand.nextBoolean())
				{
					dungeon = mazes.get(rand.nextInt(mazes.size()));
				}
				else if(rand.nextBoolean())
				{
					dungeon = pistonTraps.get(rand.nextInt(pistonTraps.size()));

				}
				else
				{
					flag=false;
				}
			}
			else if(rand.nextInt(3)==0&&!complexHalls.contains(dungeon))
			{
				if(rand.nextInt(3)==0)
				{
					dungeon = simpleHalls.get(rand.nextInt(simpleHalls.size()));
				}
				else if(rand.nextBoolean())
				{
					dungeon = pistonTraps.get(rand.nextInt(pistonTraps.size()));

				}
				else if(depth<4)
				{
					dungeon = hubs.get(rand.nextInt(hubs.size()));

				}

			}
			else if(depthWeight-depthWeight/2>depth-4&&(deadEnds.contains(dungeon)||exits.contains(dungeon)))
			{
				if(rand.nextBoolean())
				{
					dungeon = simpleHalls.get(rand.nextInt(simpleHalls.size()));
				}
				else if(rand.nextBoolean())
				{
					dungeon = complexHalls.get(rand.nextInt(complexHalls.size()));
				}
				else if(rand.nextBoolean())
				{
					dungeon = pistonTraps.get(rand.nextInt(pistonTraps.size()));

				}
				else	
				{
					flag=false;
				}
			}
			else if(depthWeight>7&&hubs.contains(dungeon))
			{
				if(rand.nextInt(12)+5<depthWeight)
				{
					if(rand.nextBoolean())
					{
						dungeon = exits.get(rand.nextInt(exits.size()));
					}
					else if(rand.nextBoolean())
					{
						dungeon = deadEnds.get(rand.nextInt(deadEnds.size()));
					}
					else
					{
						dungeon = pistonTraps.get(rand.nextInt(pistonTraps.size()));

					}
					
				}
				else
				{
					flag = false;
				}
			}
			else if(depth>10&&hubs.contains(dungeon))
			{
				flag = false;
			}
			
		}
		while(!flag&&count>0);
		}
		else
		{
			dungeon= defaultUp;
		}
		}
		catch(Exception e)
		{
			if(weightedDungeonGenList.size()>0)
			{
				dungeon = weightedDungeonGenList.get(rand.nextInt(weightedDungeonGenList.size()));
			}
			else
			{
				e.printStackTrace();
				return;
			}
		}
		
		
	
		dimHelper.dimList.get(incoming.destDimID).dungeonGenerator=dungeon;
		//loader.generateSchematic(incoming,0,0,0);
	
		
				

				

			
		
	}
	
	
	
	
}