package StevenDimDoors.mod_pocketDim.dungeon.pack;

import java.util.ArrayList;
import java.util.HashMap;

import StevenDimDoors.mod_pocketDim.util.WeightedContainer;

public class DungeonChainRule
{
	private final int[] condition;
	private final ArrayList<WeightedContainer<DungeonType>> products;
	
	public DungeonChainRule(DungeonChainRuleDefinition source, HashMap<String, DungeonType> nameToTypeMapping)
	{	
		ArrayList<String> conditionNames = source.getCondition();
		ArrayList<WeightedContainer<String>> productNames = source.getProducts();
		
		//Validate the data, just in case
		if (conditionNames == null)
		{
			throw new NullPointerException("source cannot have null conditions");
		}
		if (productNames == null)
		{
			throw new NullPointerException("source cannot have null products");
		}
		if (productNames.isEmpty())
		{
			throw new IllegalArgumentException("products cannot be an empty list");
		}
		for (WeightedContainer<String> product : productNames)
		{
			//Check for weights less than 1. Those could cause Minecraft's random selection algorithm to throw an exception.
			//At the very least, they're useless values.
			if (product.itemWeight < 1)
			{
				throw new IllegalArgumentException("products cannot contain items with weights less than 1");
			}
		}

		//Obtain the IDs of dungeon types in reverse order. Reverse order makes comparing against chain histories easy.
		condition = new int[conditionNames.size()];
		for (int src = 0, dst = condition.length - 1; src < condition.length; src++, dst--)
		{
			condition[dst] = nameToTypeMapping.get(conditionNames.get(src)).ID;
		}
		products = new ArrayList<WeightedContainer<DungeonType>>(productNames.size());
		for (WeightedContainer<String> product : productNames)
		{
			products.add(new WeightedContainer<DungeonType>(nameToTypeMapping.get(product.getData()), product.itemWeight ));
		}
	}
	
	public int length()
	{
		return condition.length;
	}

	public boolean evaluate(int[] typeHistory)
	{
		if (typeHistory.length >= condition.length)
		{
			for (int k = 0; k < condition.length; k++)
			{
				if (condition[k] != 0 && typeHistory[k] != condition[k])
				{
					return false;
				}
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	public ArrayList<WeightedContainer<DungeonType>> products()
	{
		//Create a deep copy of the internal list of products. That way, if the list is modified externally,
		//it won't affect the reference copy inside this rule.
		ArrayList<WeightedContainer<DungeonType>> copy = new ArrayList<WeightedContainer<DungeonType>>(products.size());
		for (WeightedContainer<DungeonType> container : products)
		{
			copy.add(container.clone());
		}
		
		return copy;
	}
}
