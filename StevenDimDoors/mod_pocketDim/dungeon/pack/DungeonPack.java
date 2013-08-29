package StevenDimDoors.mod_pocketDim.dungeon.pack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.util.WeightedRandom;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonData;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.util.WeightedContainer;

public class DungeonPack
{
	//There is no precaution against having a dungeon type removed from a config file after dungeons of that type
	//have been generated. That would likely cause one or two problems. It's hard to guard against when I don't know
	//what the save format will be like completely. How should this class behave if it finds a "disowned" type?
	//The ID numbers would be a problem since it couldn't have a valid number, since it wasn't initialized by the pack instance.
	//FIXME: Do not release this code as an update without dealing with disowned types!
	
	private static final int MAX_HISTORY_LENGTH = 30;
	
	private final String name;
	private final HashMap<String, DungeonType> nameToTypeMapping;
	private final ArrayList<ArrayList<DungeonData>> groupedDungeons;
	private final ArrayList<DungeonData> allDungeons;
	private final DungeonPackConfig config;
	private final int maxRuleLength;
	private final ArrayList<DungeonChainRule> rules;
	
	public DungeonPack(DungeonPackConfig config)
	{
		config.validate();
		this.config = config.clone(); //Store a clone of the config so that the caller can't change it externally later
		this.name = config.getName();

		int index;
		int maxLength = 0;
		int typeCount = config.getTypeNames().size();
		this.allDungeons = new ArrayList<DungeonData>();
		this.nameToTypeMapping = new HashMap<String, DungeonType>(typeCount);
		this.groupedDungeons = new ArrayList<ArrayList<DungeonData>>(typeCount);
		
		this.groupedDungeons.add(allDungeons); //Make sure the list of all dungeons is placed at index 0
		this.nameToTypeMapping.put(DungeonType.WILDCARD_TYPE.Name, DungeonType.WILDCARD_TYPE);
		
		index = 1;
		for (String typeName : config.getTypeNames())
		{
			String standardName = typeName.toUpperCase();
			this.nameToTypeMapping.put(standardName, new DungeonType(this, standardName, index));
			this.groupedDungeons.add(new ArrayList<DungeonData>());  
			index++;
		}
		
		//Construct optimized rules from definitions
		ArrayList<DungeonChainRuleDefinition> definitions = config.getRules();
		this.rules = new ArrayList<DungeonChainRule>(definitions.size());
		for (DungeonChainRuleDefinition definition : definitions)
		{
			DungeonChainRule rule = new DungeonChainRule(definition, nameToTypeMapping);
			this.rules.add(rule);
			if (maxLength < rule.length())
			{
				maxLength = rule.length();
			}
		}
		this.maxRuleLength = maxLength;
		
		//Remove unnecessary references to save a little memory - we won't need them here
		this.config.setRules(null);
		this.config.setTypeNames(null);
	}
	
	public String getName()
	{
		return name;
	}

	public DungeonPackConfig getConfig()
	{
		return config.clone();
	}

	public boolean isEmpty()
	{
		return allDungeons.isEmpty();
	}
	
	public DungeonType getType(String typeName)
	{
		DungeonType result = nameToTypeMapping.get(typeName.toUpperCase());
		if (result.Owner == this) //Filter out the wildcard dungeon type
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

	public void addDungeon(DungeonData dungeon)
	{
		//Make sure this dungeon really belongs in this pack
		DungeonType type = dungeon.dungeonType();
		if (type.Owner == this)
		{
			allDungeons.add(dungeon);
			groupedDungeons.get(type.ID).add(dungeon);
		}
		else
		{
			throw new IllegalArgumentException("The dungeon type of generator must belong to this instance of DungeonPack.");
		}
	}

	public DungeonData getNextDungeon(NewDimData dimension, Random random)
	{
		if (allDungeons.isEmpty())
		{
			return null;
		}
		
		//Retrieve a list of the previous dungeons in this chain.
		//If we're not going to check for duplicates in chains, restrict the length of the history to the length
		//of the longest rule we have. Getting any more data would be useless. This optimization could be significant
		//for dungeon packs that can extend arbitrarily deep. We should probably set a reasonable limit anyway.
		
		int maxSearchLength = config.allowDuplicatesInChain() ? maxRuleLength : MAX_HISTORY_LENGTH;
		ArrayList<DungeonData> history = DungeonHelper.getDungeonChainHistory(dimension.parent(), this, maxSearchLength);
		return getNextDungeon(history, random);
	}
	
	private DungeonData getNextDungeon(ArrayList<DungeonData> history, Random random)
	{
		//Extract the dungeon types that have been used from history and convert them into an array of IDs
		int index;
		int[] typeHistory = new int[history.size()];
		HashSet<DungeonData> excludedDungeons = null;
		for (index = 0; index < typeHistory.length; index++)
		{
			typeHistory[index] = history.get(index).dungeonType().ID;
		}
		
		for (DungeonChainRule rule : rules)
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
						if (excludedDungeons == null && !config.allowDuplicatesInChain())
						{
							excludedDungeons = new HashSet<DungeonData>(history);
						}
						
						//List which dungeons are allowed
						ArrayList<DungeonData> candidates;
						ArrayList<DungeonData> group = groupedDungeons.get(nextType.ID);
						if (excludedDungeons != null && !excludedDungeons.isEmpty())
						{
							 candidates = new ArrayList<DungeonData>(group.size());
							 for (DungeonData dungeon : group)
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
						//If we've reached this point, then a dungeon was not selected. Discard the type and try again.
						products.remove(nextType);
					}
				}
				while (nextType != null);
			}
		}
		
		//None of the rules were applicable. Simply return a random dungeon.
		return getRandomDungeon(random);
	}

	public DungeonData getRandomDungeon(Random random)
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
			ArrayList<ArrayList<DungeonData>> groupedDungeons)
	{
		//TODO: Make this faster? This algorithm runs in quadratic time in the worst case because of the random-selection
		//process and the removal search. Might be okay for normal use, though. ~SenseiKiwi
		
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
	
	private static DungeonData getRandomDungeon(Random random, Collection<DungeonData> dungeons)
	{
		//Use Minecraft's WeightedRandom to select our dungeon. =D
		ArrayList<WeightedContainer<DungeonData>> weights =
				new ArrayList<WeightedContainer<DungeonData>>(dungeons.size());
		for (DungeonData dungeon : dungeons)
		{
			weights.add(new WeightedContainer<DungeonData>(dungeon, dungeon.weight()));
		}
		
		@SuppressWarnings("unchecked")
		WeightedContainer<DungeonData> resultContainer = (WeightedContainer<DungeonData>) WeightedRandom.getRandomItem(random, weights);
		return 	(resultContainer != null) ? resultContainer.getData() : null;
	}
}
