package StevenDimDoors.mod_pocketDim.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class DDWorldProperties
{
	/**
	 * World Generation Settings
	 */
	public final DimensionFilter RiftClusterDimensions;
	public final DimensionFilter RiftGatewayDimensions;
	
	/**
	 * General Flags
	 */
	public final boolean LimboEscapeEnabled;
	public final boolean UniversalLimboEnabled;
	
	//Names of categories
	private static final String CATEGORY_WORLD_GENERATION = "world generation";
	
	public DDWorldProperties(File configFile)
	{
		// TODO: For the next major update (e.g. to MC 1.7), please move all world-specific settings
		// into this config file instead of using the global ID file.
		
		Configuration config = new Configuration(configFile);
		config.load();
		
		config.addCustomCategoryComment(CATEGORY_WORLD_GENERATION,
				"The following settings require lists of dimensions in a specific format. " + 
				"A list must consist of ranges separated by commas. A range may be a single number to indicate " +
				"just one dimension or two numbers in the form \"X - Y\". Spaces are permitted " + 
				"but not required. Example: -100, -10 - -1, 20 - 30");

		RiftClusterDimensions = loadFilter(config, "Rift Cluster", "Rift Clusters");
		RiftGatewayDimensions = loadFilter(config, "Rift Gateway", "Rift Gateways");
		
		LimboEscapeEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Limbo Escape", true,
				"Sets whether players are teleported out of Limbo when walking over the Eternal Fabric that " + 
				"generates near the bottom of the dimension. If disabled, players could still leave through " +
				"dungeons in Limbo or by dying (if Hardcore Limbo is disabled). The default value is true.").getBoolean(true);
		
		UniversalLimboEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Universal Limbo", false,
				"Sets whether players are teleported to Limbo when they die in any dimension (except Limbo). " +
				"Normally, players only go to Limbo if they die in a pocket dimension. This setting will not " + 
				"affect deaths in Limbo, which can be set with the Hardcore Limbo option. " +
				"The default value is false.").getBoolean(false);
		
		config.save();
	}
	
	private static DimensionFilter loadFilter(Configuration config, String prefix, String description)
	{
		boolean enableBlacklist = config.get(CATEGORY_WORLD_GENERATION, "Enable " + prefix + " Blacklist", true,
				"Sets whether " + description + " will not generate in certain blacklisted dimensions. " +
				"If set to false, then " + description + " will follow a whitelist instead.").getBoolean(true);
		
		String whitelist = config.get(CATEGORY_WORLD_GENERATION, prefix + " Whitelist", "",
				"A list of the only dimensions in which " + description + " may generate.").getString();
		
		String blacklist = config.get(CATEGORY_WORLD_GENERATION, prefix + " Blacklist", "",
				"A list of dimensions in which " + description + " may not generate.").getString();
		
		try
		{
			if (enableBlacklist)
			{
				return DimensionFilter.parseBlacklist(blacklist);
			}
			else
			{
				return DimensionFilter.parseWhitelist(whitelist);
			}
		}
		catch (Exception inner)
		{
			throw new RuntimeException("An error occurred while loading a whitelist or blacklist setting for " +
					description + ". Please make sure that your configuration file is set up correctly.", inner);
		}
	}
}