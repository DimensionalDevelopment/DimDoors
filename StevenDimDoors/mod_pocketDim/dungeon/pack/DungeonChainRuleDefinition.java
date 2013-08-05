package StevenDimDoors.mod_pocketDim.dungeon.pack;

import java.util.ArrayList;

import StevenDimDoors.mod_pocketDim.util.WeightedContainer;

public class DungeonChainRuleDefinition
{
	private ArrayList<String> conditions;
	private ArrayList<WeightedContainer<String>> products;
	
	public DungeonChainRuleDefinition(ArrayList<String> conditions, ArrayList<WeightedContainer<String>> products)
	{
		this.conditions = conditions;
		this.products = products;
	}
	
	public ArrayList<String> getCondition()
	{
		return conditions;
	}

	public ArrayList<WeightedContainer<String>> getProducts()
	{
		return products;
	}

}
