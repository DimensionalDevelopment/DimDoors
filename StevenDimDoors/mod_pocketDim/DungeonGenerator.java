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
}