package StevenDimDoors.mod_pocketDim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.world.World;



public class DungeonGenerator implements Serializable
{

	public int weight;
	public String schematicPath;
	public ArrayList<HashMap> sideRifts = new ArrayList<HashMap>();
	public LinkData exitLink; 
	public static Random rand = new Random();
	public boolean isOpen;
	public int sideDoorsSoFar=0;
	public int exitDoorsSoFar=0;
	public int deadEndsSoFar=0;
	
	
	
	
	
	public DungeonGenerator(int weight, String schematicPath, Boolean isOpen)
	{
		this.weight=weight;
		this.schematicPath=schematicPath;
		this.isOpen=isOpen;
		
	}
	
	
	
	
	public static void generateDungeonlink(LinkData incoming)
	{
		//DungeonGenerator dungeon = mod_pocketDim.registeredDungeons.get(new Random().nextInt(mod_pocketDim.registeredDungeons.size()));
		DungeonGenerator dungeon;
		int depth = dimHelper.instance.getDimDepth(incoming.locDimID)+2;
		
		int depthWeight = rand.nextInt(depth)+rand.nextInt(depth)-2;
		
		depth=depth-2;
	//	DungeonGenerator
		boolean flag = true;
		int count=10;
		try
		{
		if(incoming.destYCoord>15)
		{
		do
		{
			count--;
			flag = true;
			 dungeon = mod_pocketDim.registeredDungeons.get(rand.nextInt(mod_pocketDim.registeredDungeons.size()));

			if(depth<=1)
			{
				if(rand.nextBoolean())
				{
					dungeon = mod_pocketDim.complexHalls.get(rand.nextInt(mod_pocketDim.complexHalls.size()));

				}
				else if(rand.nextBoolean())
				{
					dungeon = mod_pocketDim.hubs.get(rand.nextInt(mod_pocketDim.hubs.size()));

				}
				else  if(rand.nextBoolean())
				{
					dungeon = mod_pocketDim.hubs.get(rand.nextInt(mod_pocketDim.hubs.size()));

				}
				else if(mod_pocketDim.deadEnds.contains(dungeon)||mod_pocketDim.exits.contains(dungeon))
						{
						flag=false;
						}
				
		
				
			}
			else if(depth<=3&&(mod_pocketDim.deadEnds.contains(dungeon)||mod_pocketDim.exits.contains(dungeon)||rand.nextBoolean()))
			{
				if(rand.nextBoolean())
				{
					dungeon = mod_pocketDim.hubs.get(rand.nextInt(mod_pocketDim.hubs.size()));
					
				}
				else if(rand.nextBoolean())
				{
					dungeon = mod_pocketDim.mazes.get(rand.nextInt(mod_pocketDim.mazes.size()));
				}
				else if(rand.nextBoolean())
				{
					dungeon = mod_pocketDim.pistonTraps.get(rand.nextInt(mod_pocketDim.pistonTraps.size()));

				}
				else
				{
					flag=false;
				}
			}
			else if(rand.nextInt(3)==0&&!mod_pocketDim.complexHalls.contains(dungeon))
			{
				if(rand.nextInt(3)==0)
				{
					dungeon = mod_pocketDim.simpleHalls.get(rand.nextInt(mod_pocketDim.simpleHalls.size()));
				}
				else if(rand.nextBoolean())
				{
					dungeon = mod_pocketDim.pistonTraps.get(rand.nextInt(mod_pocketDim.pistonTraps.size()));

				}
				else if(depth<4)
				{
					dungeon = mod_pocketDim.hubs.get(rand.nextInt(mod_pocketDim.hubs.size()));

				}

			}
			else if(depthWeight-depthWeight/2>depth-4&&(mod_pocketDim.deadEnds.contains(dungeon)||mod_pocketDim.exits.contains(dungeon)))
			{
				if(rand.nextBoolean())
				{
					dungeon = mod_pocketDim.simpleHalls.get(rand.nextInt(mod_pocketDim.simpleHalls.size()));
				}
				else if(rand.nextBoolean())
				{
					dungeon = mod_pocketDim.complexHalls.get(rand.nextInt(mod_pocketDim.complexHalls.size()));
				}
				else if(rand.nextBoolean())
				{
					dungeon = mod_pocketDim.pistonTraps.get(rand.nextInt(mod_pocketDim.pistonTraps.size()));

				}
				else	
				{
					flag=false;
				}
			}
			else if(depthWeight>7&&mod_pocketDim.hubs.contains(dungeon))
			{
				if(rand.nextInt(12)+5<depthWeight)
				{
					if(rand.nextBoolean())
					{
						dungeon = mod_pocketDim.exits.get(rand.nextInt(mod_pocketDim.exits.size()));
					}
					else if(rand.nextBoolean())
					{
						dungeon = mod_pocketDim.deadEnds.get(rand.nextInt(mod_pocketDim.deadEnds.size()));
					}
					else
					{
						dungeon = mod_pocketDim.pistonTraps.get(rand.nextInt(mod_pocketDim.pistonTraps.size()));

					}
					
				}
				else
				{
					flag = false;
				}
			}
			else if(depth>10&&mod_pocketDim.hubs.contains(dungeon))
			{
				flag = false;
			}
			
		}
		while(!flag&&count>0);
		}
		else
		{
			dungeon= mod_pocketDim.defaultUp;
		}
		}
		catch(Exception e)
		{
			dungeon = mod_pocketDim.registeredDungeons.get(rand.nextInt(mod_pocketDim.registeredDungeons.size()));
		}
		
		
		mod_pocketDim.loader.init(dungeon.schematicPath, incoming);
		dimHelper.dimList.get(incoming.destDimID).dungeonGenerator=dungeon;
		//mod_pocketDim.loader.generateSchematic(incoming,0,0,0);
	
		
				

				

			
		
	}
	
}