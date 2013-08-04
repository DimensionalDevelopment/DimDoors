package StevenDimDoors.mod_pocketDim.dungeon.pack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.minecraft.util.WeightedRandom;
import StevenDimDoors.mod_pocketDim.DungeonGenerator;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import StevenDimDoors.mod_pocketDim.util.WeightedContainer;

public class DungeonPack
{
	//Why final? I just felt like it, honestly. ~SenseiKiwi
	
	private static final DungeonType WILDCARD_TYPE = new DungeonType(null, "?", 0);
	
	private final String name;
	private final HashMap<String, DungeonType> nameToTypeMapping;
	private final ArrayList<ArrayList<DungeonGenerator>> groupedDungeons;
	private final ArrayList<DungeonGenerator> allDungeons;
	private final DungeonPackConfig config;
	private final int maxRuleLength;
	private final ArrayList<OptimizedRule> rules;
	
	public DungeonPack(DungeonPackConfig config)
	{
		config.validate();
		this.config = config.clone(); //Store a clone of the config so that the caller can't change it externally later
		this.name = config.getName();

		int index;
		int maxLength = 0;
		int typeCount = config.getTypeNames().size();
		this.allDungeons = new ArrayList<DungeonGenerator>();
		this.nameToTypeMapping = new HashMap<String, DungeonType>(typeCount);
		this.groupedDungeons = new ArrayList<ArrayList<DungeonGenerator>>(typeCount);
		
		this.groupedDungeons.add(allDungeons); //Make sure the list of all dungeons is placed at index 0
		this.nameToTypeMapping.put(WILDCARD_TYPE.Name, WILDCARD_TYPE);
		
		index = 1;
		for (String typeName : config.getTypeNames())
		{
			String standardName = typeName.toUpperCase();
			this.nameToTypeMapping.put(standardName, new DungeonType(this, standardName, index));
			this.groupedDungeons.add(new ArrayList<DungeonGenerator>());
			index++;
		}
		
		//Construct optimized rules from config rules
		ArrayList<DungeonChainRule> chainRules = config.getRules();
		this.rules = new ArrayList<OptimizedRule>(chainRules.size());
		for (DungeonChainRule rule : chainRules)
		{
			OptimizedRule optimized = rule.optimize(nameToTypeMapping);
			this.rules.add(optimized);
			if (maxLength < optimized.length())
			{
				maxLength = optimized.length();
			}
		}
		this.maxRuleLength = maxLength;
		
		//Remove the reference to the non-optimized rules to free up memory - we won't need them here
		this.config.setRules(null);
	}
	
	public String getName()
	{
		return name;
	}

	public boolean isEmpty()
	{
		return allDungeons.isEmpty();
	}
	
	public DungeonType getType(String typeName)
	{
		DungeonType result = nameToTypeMapping.get(typeName.toUpperCase());
		if (result != WILDCARD_TYPE)
		{
			return result;
		}
		else
		{
			return null;
		}
	}
	
	public boolean isKnownType(String typeName)
	{
		return (this.getType(typeName) != null);
	}

	public DungeonGenerator getNextDungeon(LinkData inbound, Random random)
	{
		if (allDungeons.isEmpty())
		{
			return null;
		}
		
		//Retrieve a list of the previous dungeons in this chain. Restrict the length of the
		//search to the length of the longest rule. Getting more data is useless.
		dimHelper helper = dimHelper.instance;
		
		//TODO: Add dungeon pack parameter! We can't use dungeon types from other packs.
		ArrayList<DungeonGenerator> history = DungeonHelper.getDungeonChainHistory(helper.getDimData(inbound.locDimID), maxRuleLength);
		return getNextDungeon(history, random);
	}
	
	private DungeonGenerator getNextDungeon(ArrayList<DungeonGenerator> history, Random random)
	{
		//Extract the dungeon types that have been used from history and convert them into an array of IDs
		int index;
		int[] typeHistory = new int[history.size()];
		HashSet<DungeonGenerator> excludedDungeons = null;
		for (index = 0; index < typeHistory.length; index++)
		{
			typeHistory[index] = getDungeonType(history.get(index)).ID;
		}
		
		for (OptimizedRule rule : rules)
		{
			if (rule.evaluate(typeHistory))
			{
				//Pick a random dungeon type to be generated next based on the rule's products				
				ArrayList<WeightedContainer<DungeonType>> products = rule.products();
				DungeonType nextType;
				do
				{
					nextType = getRandomDungeonType(random, products, groupedDungeons);
					if (nextType != null)
					{
						//Initialize the set of excluded dungeons if needed
						if (excludedDungeons == null && config.allowDuplicatesInChain())
						{
							//TODO: Finish implementing this!
						}
					
						//List which dungeons are allowed
						ArrayList<DungeonGenerator> candidates;
						ArrayList<DungeonGenerator> group = groupedDungeons.get(nextType.ID);
						if (excludedDungeons != null)
						{
							 candidates = new ArrayList<DungeonGenerator>(group.size());
							 for (DungeonGenerator dungeon : group)
							 {
								 if (!excludedDungeons.contains(dungeon))
								 {
									 candidates.add(dungeon);
								 }
							 }
						}
						else
						{
							candidates = group;
						}
						if (!candidates.isEmpty())
						{
							return getRandomDungeon(random, candidates);
						}
					}
				}
				while (nextType != null);
			}
		}
		
		//None of the rules were applicable. Simply return a random dungeon.
		return getRandomDungeon(random);
	}
	
	private DungeonType getDungeonType(DungeonGenerator generator)
	{
		//This function is a workaround for DungeonGenerator not having a dungeon type or pack field.
		//I really don't want to go messing around with that serializable type.
		//TODO: Remove this function once we transition to using the new save format. ~SenseiKiwi
		
		//TODO: Finish implementing this!
		return null;
	}

	public DungeonGenerator getRandomDungeon(Random random)
	{
		if (!allDungeons.isEmpty())
		{
			return getRandomDungeon(random, allDungeons);
		}
		else
		{
			return null;
		}
	}
	
	private static DungeonType getRandomDungeonType(Random random, Collection<WeightedContainer<DungeonType>> types,
			ArrayList<ArrayList<DungeonGenerator>> groupedDungeons)
	{
		//TODO: Make this faster? This algorithm runs in quadratic time in the worst case because of the random-selection
		//process and the removal search. Should be okay for normal use, though. ~SenseiKiwi
		
		//Pick a random dungeon type based on weights. Repeat this process until a non-empty group is found or all groups are checked.
		while (!types.isEmpty())
		{
			//Pick a random dungeon type
			@SuppressWarnings("unchecked")
			WeightedContainer<DungeonType> resultContainer = (WeightedContainer<DungeonType>) WeightedRandom.getRandomItem(random, types);
			
			//Check if there are any dungeons of that type
			DungeonType selectedType = resultContainer.getData();
			if (!groupedDungeons.get(selectedType.ID).isEmpty())
			{
				//Choose this type
				return selectedType;
			}
			else
			{
				//We can't use this type because there are no dungeons of this type
				//Remove it from the list of types and try again
				types.remove(resultContainer);
			}
		}
		
		//We have run out of types to try
		return null;
	}
	
	private static DungeonGenerator getRandomDungeon(Random random, Collection<DungeonGenerator> dungeons)
	{
		//Use Minecraft's WeightedRandom to select our dungeon. =D
		ArrayList<WeightedContainer<DungeonGenerator>> weights =
				new ArrayList<WeightedContainer<DungeonGenerator>>(dungeons.size());
		for (DungeonGenerator dungeon : dungeons)
		{
			weights.add(new WeightedContainer<DungeonGenerator>(dungeon, dungeon.weight));
		}
		
		@SuppressWarnings("unchecked")
		WeightedContainer<DungeonGenerator> resultContainer = (WeightedContainer<DungeonGenerator>) WeightedRandom.getRandomItem(random, weights);
		return 	(resultContainer != null) ? resultContainer.getData() : null;
	}
}
