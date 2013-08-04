package StevenDimDoors.mod_pocketDim.dungeon.pack;

import java.util.ArrayList;
import java.util.List;

public class DungeonPackConfig
{
	public DungeonPackConfig() { }
	
	private DungeonPackConfig(DungeonPackConfig source)
	{
		
	}
	
	public void validate()
	{
		
	}
	
	public DungeonPackConfig clone()
	{
		return new DungeonPackConfig(this);
	}

	public String getName()
	{
		return null;
	}

	public List<String> getTypeNames()
	{
		return null;
	}

	public boolean allowDuplicatesInChain()
	{
		return false;
	}

	public void setRules(Object object) {
		// TODO Auto-generated method stub
		
	}

	public ArrayList<DungeonChainRule> getRules() {
		// TODO Auto-generated method stub
		return null;
	}
}
