package StevenDimDoors.mod_pocketDim.dungeon.pack;

import java.util.ArrayList;

public class DungeonPackConfig
{
	private String name;
	private ArrayList<String> typeNames;
	private boolean allowDuplicatesInChain;
	private ArrayList<DungeonChainRuleDefinition> rules;
	
	public DungeonPackConfig() { }
	
	@SuppressWarnings("unchecked")
	private DungeonPackConfig(DungeonPackConfig source)
	{
		this.name = source.name;
		this.typeNames = (ArrayList<String>) source.typeNames.clone();
		this.allowDuplicatesInChain = source.allowDuplicatesInChain;
		this.rules = (ArrayList<DungeonChainRuleDefinition>) source.rules.clone();
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
}
