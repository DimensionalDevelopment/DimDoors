package StevenDimDoors.mod_pocketDim.dungeon.pack;

import java.util.ArrayList;

public class DungeonPackConfig
{
	private String name;
	private ArrayList<String> typeNames;
	private boolean allowDuplicatesInChain;
	private boolean allowPackChangeIn;
	private boolean allowPackChangeOut;
	private boolean distortDoorCoordinates;
	private int packWeight;
	private ArrayList<DungeonChainRuleDefinition> rules;
	
	public DungeonPackConfig() { }
	
	@SuppressWarnings("unchecked")
	private DungeonPackConfig(DungeonPackConfig source)
	{
		this.name = (source.name != null) ? source.name : null;
		this.typeNames = (source.typeNames != null) ? (ArrayList<String>) source.typeNames.clone() : null;
		this.allowDuplicatesInChain = source.allowDuplicatesInChain;
		this.allowPackChangeIn = source.allowPackChangeIn;
		this.allowPackChangeOut = source.allowPackChangeOut;
		this.distortDoorCoordinates = source.distortDoorCoordinates;
		this.packWeight = source.packWeight;
		this.rules = (source.rules != null) ? (ArrayList<DungeonChainRuleDefinition>) source.rules.clone() : null;
	}
	
	public void validate()
	{
		if (this.name == null)
			throw new NullPointerException("name cannot be null");
		if (this.typeNames == null)
			throw new NullPointerException("typeNames cannot be null");
		if (this.rules == null)
			throw new NullPointerException("rules cannot be null");
	}
	
	@Override
	public DungeonPackConfig clone()
	{
		return new DungeonPackConfig(this);
	}

	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public ArrayList<String> getTypeNames()
	{
		return typeNames;
	}
	
	public void setTypeNames(ArrayList<String> typeNames)
	{
		this.typeNames = typeNames;
	}

	public boolean allowDuplicatesInChain()
	{
		return allowDuplicatesInChain;
	}
	
	public void setAllowDuplicatesInChain(boolean value)
	{
		allowDuplicatesInChain = value;
	}

	public void setRules(ArrayList<DungeonChainRuleDefinition> rules)
	{
		this.rules = rules;
	}

	public ArrayList<DungeonChainRuleDefinition> getRules()
	{
		return rules;
	}

	public boolean allowPackChangeIn()
	{
		return allowPackChangeIn;
	}

	public void setAllowPackChangeIn(boolean value)
	{
		this.allowPackChangeIn = value;
	}

	public boolean allowPackChangeOut()
	{
		return allowPackChangeOut;
	}

	public void setAllowPackChangeOut(boolean value)
	{
		this.allowPackChangeOut = value;
	}

	public int getPackWeight()
	{
		return packWeight;
	}

	public void setPackWeight(int packWeight)
	{
		this.packWeight = packWeight;
	}
	
	public boolean doDistortDoorCoordinates()
	{
		return distortDoorCoordinates;
	}

	public void setDistortDoorCoordinates(boolean value)
	{
		this.distortDoorCoordinates = value;
	}
}
