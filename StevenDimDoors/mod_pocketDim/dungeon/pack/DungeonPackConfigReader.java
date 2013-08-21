package StevenDimDoors.mod_pocketDim.dungeon.pack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import StevenDimDoors.mod_pocketDim.util.BaseConfigurationProcessor;
import StevenDimDoors.mod_pocketDim.util.ConfigurationProcessingException;
import StevenDimDoors.mod_pocketDim.util.WeightedContainer;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.primitives.Ints;

public class DungeonPackConfigReader extends BaseConfigurationProcessor<DungeonPackConfig>
{
	private interface ILineProcessor
	{
		public void process(String line, DungeonPackConfig config) throws ConfigurationProcessingException;
	}

	//Note: These constants aren't static so that the memory will be released once
	//we're done using it an instance. These aren't objects that we need to hold
	//onto throughout the lifetime of MC, only at loading time.
	
	private final int CONFIG_VERSION = 1;
	private final int LOOKAHEAD_LIMIT = 1024;
	private final int MAX_PRODUCT_WEIGHT = 10000;
	private final int DEFAULT_PRODUCT_WEIGHT = 100;
	private final int MAX_DUNGEON_PACK_WEIGHT = 10000;
	private final int DEFAULT_DUNGEON_PACK_WEIGHT = 100;
	private final String COMMENT_MARKER = "##";

	private final Pattern DUNGEON_TYPE_PATTERN = Pattern.compile("[A-Za-z0-9_\\-]{1,20}");

	private final Splitter WHITESPACE_SPLITTER = Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings();
	private final String SETTING_SEPARATOR = "=";
	private final String RULE_SEPARATOR = "->";
	private final String WEIGHT_SEPARATOR = "#";

	public DungeonPackConfigReader() { }

	@Override
	public DungeonPackConfig readFromStream(InputStream inputStream) throws ConfigurationProcessingException
	{
		BufferedReader reader = null;
		try
		{
			DungeonPackConfig config = new DungeonPackConfig();
			reader = new BufferedReader(new InputStreamReader(inputStream));

			//Check the config format version
			int version = readVersion(reader);
			if (version != CONFIG_VERSION)
			{
				throw new ConfigurationProcessingException("The dungeon pack config has an incompatible version.");
			}

			config.setTypeNames(new ArrayList<String>());
			config.setRules(new ArrayList<DungeonChainRuleDefinition>());

			//Read the dungeon types
			if (findSection("Types", reader))
			{
				processLines(reader, config, new DungeonTypeProcessor());
			}

			//Load default settings
			config.setAllowDuplicatesInChain(true);
			config.setAllowPackChangeIn(true);
			config.setAllowPackChangeOut(true);
			config.setDistortDoorCoordinates(false);
			config.setPackWeight(DEFAULT_DUNGEON_PACK_WEIGHT);
			
			//Read the settings section
			if (findSection("Settings", reader))
			{
				processLines(reader, config, new DungeonSettingsParser());
			}

			//Read the rules section
			if (findSection("Rules", reader))
			{
				processLines(reader, config, new RuleDefinitionParser());
			}

			return config;
		}
		catch (ConfigurationProcessingException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new ConfigurationProcessingException("An unexpected error occurred while trying to read the configuration file.", ex);
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException ex) { }
			}
		}
	}

	private int readVersion(BufferedReader reader) throws ConfigurationProcessingException, IOException
	{
		String firstLine = reader.readLine();
		String[] parts = firstLine.split("\\s", 0);
		Integer version = null;

		if (parts.length == 2 && parts[0].equalsIgnoreCase("version"))
		{
			version = Ints.tryParse(parts[1]);
		}

		if (version == null)
		{
			throw new ConfigurationProcessingException("Could not parse the config format version.");
		}
		return version;
	}

	private void processLines(BufferedReader reader, DungeonPackConfig config, ILineProcessor processor) throws IOException, ConfigurationProcessingException
	{
		String line;

		while (reader.ready())
		{
			reader.mark(LOOKAHEAD_LIMIT);
			line = reader.readLine();
			if (!line.startsWith(COMMENT_MARKER))
			{
				line = line.trim();
				if (line.length() > 0)
				{
					if (line.endsWith(":"))
					{
						//Consider this line a section header, reset the reader to undo consuming it
						reader.reset();
						break;
					}
					else
					{
						processor.process(line, config);
					}
				}
			}
		}
	}

	private boolean findSection(String name, BufferedReader reader) throws IOException, ConfigurationProcessingException
	{
		boolean found = false;
		boolean matched = false;
		String line = null;
		String label = name + ":";

		//Find the next section header
		//Ignore blank lines and comment lines, stop for headers, and throw an exception for anything else
		while (!found && reader.ready())
		{
			reader.mark(LOOKAHEAD_LIMIT);
			line = reader.readLine();
			if (!line.startsWith(COMMENT_MARKER))
			{
				line = line.trim();
				if (line.length() > 0)
				{
					if (line.endsWith(":"))
					{
						//Consider this line a section header
						found = true;
						matched = line.equalsIgnoreCase(label);
					}
					else
					{
						//This line is invalid
						throw new ConfigurationProcessingException("The dungeon pack config has an incorrect line where a section was expected: " + line);
					}
				}
			}
		}

		//Check if the header matches the one we're looking for.
		//If it doesn't match, undo consuming the line so it can be read later.
		if (found && !matched)
		{
			reader.reset();
		}
		return found;
	}

	private class DungeonTypeProcessor implements ILineProcessor
	{
		public void process(String line, DungeonPackConfig config) throws ConfigurationProcessingException
		{
			List<String> typeNames = config.getTypeNames();
			
			//Check if the dungeon type has a name that meets our restrictions
			if (DUNGEON_TYPE_PATTERN.matcher(line).matches())
			{
				//Ignore duplicate dungeon types
				line = line.toUpperCase();
				if (!typeNames.contains(line))
				{
					typeNames.add(line);
				}
			}
			else
			{
				throw new ConfigurationProcessingException("The dungeon pack config has a dungeon type with illegal characters in its name: " + line);
			}
		}
	}
	
	private class DungeonSettingsParser implements ILineProcessor
	{
		public void process(String line, DungeonPackConfig config) throws ConfigurationProcessingException
		{
			//The various settings that we support will be hardcoded here.
			//In the future, if we get more settings, then this should be
			//refactored to use a more lookup-driven approach.
			
			boolean valid = true;
			String[] settingParts = line.split(SETTING_SEPARATOR, 2);
			if (settingParts.length == 2)
			{
				try
				{
					String name = settingParts[0];
					String value = settingParts[1];
					if (name.equalsIgnoreCase("AllowDuplicatesInChain"))
					{
						config.setAllowDuplicatesInChain(parseBoolean(value));
					}
					else if (name.equalsIgnoreCase("AllowPackChangeOut"))
					{
						config.setAllowPackChangeOut(parseBoolean(value));
					}
					else if (name.equalsIgnoreCase("AllowPackChangeIn"))
					{
						config.setAllowPackChangeIn(parseBoolean(value));
					}
					else if (name.equalsIgnoreCase("DistortDoorCoordinates"))
					{
						config.setDistortDoorCoordinates(parseBoolean(value));
					}
					else if (name.equalsIgnoreCase("PackWeight"))
					{
						int weight = Integer.parseInt(value);
						if (weight >= 0 && weight <= MAX_DUNGEON_PACK_WEIGHT)
						{
							config.setPackWeight(weight);
						}
						else
						{
							valid = false;
						}
					}
				}
				catch (Exception e)
				{
					valid = false;
				}
			}
			else
			{
				valid = false;
			}
			
			if (!valid)
			{
				throw new ConfigurationProcessingException("The dungeon pack config has an invalid setting: " + line);
			}
		}
	}

	private class RuleDefinitionParser implements ILineProcessor
	{
		public void process(String definition, DungeonPackConfig config) throws ConfigurationProcessingException
		{
			String[] ruleParts;
			String[] productParts;
			String ruleCondition;
			String ruleProduct;
			ArrayList<String> condition;
			ArrayList<WeightedContainer<String>> products;
			List<String> typeNames = config.getTypeNames();

			ruleParts = definition.toUpperCase().split(RULE_SEPARATOR, -1);
			if (ruleParts.length != 2)
			{
				throw new ConfigurationProcessingException("The dungeon pack config has an invalid rule: " + definition);
			}

			ruleCondition = ruleParts[0];
			ruleProduct = ruleParts[1];
			condition = new ArrayList<String>();
			products = new ArrayList<WeightedContainer<String>>();

			for (String typeName : WHITESPACE_SPLITTER.split(ruleCondition))
			{
				if (isKnownDungeonType(typeName, typeNames))
				{
					condition.add(typeName);
				}
				else
				{
					throw new ConfigurationProcessingException("The dungeon pack config has a rule condition with an unknown dungeon type: " + typeName);
				}
			}

			for (String product : WHITESPACE_SPLITTER.split(ruleProduct))
			{
				Integer weight;
				String typeName;

				productParts = product.split(WEIGHT_SEPARATOR, -1);
				if (productParts.length > 2 || productParts.length == 0)
				{
					throw new ConfigurationProcessingException("The dungeon pack config has a rule with an invalid product: " + product);
				}

				typeName = productParts[0];
				if (isKnownDungeonType(typeName, typeNames))
				{
					if (productParts.length > 1)
					{
						weight = Ints.tryParse(productParts[1]);
						if (weight == null  || (weight > MAX_PRODUCT_WEIGHT) || (weight < 0))
						{
							throw new ConfigurationProcessingException("The dungeon pack config has a rule with an invalid product weight: " + product);
						}
					}
					else
					{
						weight = DEFAULT_PRODUCT_WEIGHT;
					}
					products.add(new WeightedContainer<String>(typeName, weight));
				}
				else
				{
					throw new ConfigurationProcessingException("The dungeon pack config has an unknown dungeon type in a rule: " + typeName);
				}
			}
			config.getRules().add( new DungeonChainRuleDefinition(condition, products) );
		}
	}

	private static boolean isKnownDungeonType(String typeName, List<String> typeNames)
	{
		return typeName.equals(DungeonType.WILDCARD_TYPE.Name) || typeNames.contains(typeName);
	}

	private static boolean parseBoolean(String value)
	{
		if (value.equalsIgnoreCase("true"))
			return true;
		if (value.equalsIgnoreCase("false"))
			return false;
		throw new IllegalArgumentException("The boolean value must be either \"true\" or \"false\", ignoring case.");
	}

	@Override
	public boolean canWrite()
	{
		return false;
	}

	@Override
	public void writeToStream(OutputStream outputStream, DungeonPackConfig data) throws ConfigurationProcessingException
	{
		throw new UnsupportedOperationException("DungeonPackConfigReader does not support writing.");
	}
}
