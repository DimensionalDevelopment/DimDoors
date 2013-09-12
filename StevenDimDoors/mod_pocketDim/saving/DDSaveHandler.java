package StevenDimDoors.mod_pocketDim.saving;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.util.FileFilters;

import com.google.common.io.Files;

public class DDSaveHandler
{
	public static boolean loadAll()
	{
		// SenseiKiwi: Loading up our save data is not as simple as just reading files.
		// To properly restore dimensions, we need to make sure we always load
		// a dimension's parent and root before trying to load it. We'll use
		// topological sorting to determine the order in which to recreate the
		// dimension objects such that we respect those dependencies.
		// Links must be loaded after instantiating all the dimensions and must
		// be checked against our dimension blacklist.
		
		// Don't surround this code with try-catch. Our mod should crash if an error
		// occurs at this level, since it could lead to some nasty problems.
		
		String basePath = DimensionManager.getCurrentSaveRootDirectory() + "/DimensionalDoors/data/";
		File dataDirectory = new File(basePath);
		
		// Check if the folder exists. If it doesn't, just return.
		if (!dataDirectory.exists())
		{
			return true;
		}
		
		// Load the dimension blacklist
		// --insert code here--
		
		// List any dimension data files and read each dimension
		DimDataProcessor reader = new DimDataProcessor();
		List<PackedDimData> packedDims = new ArrayList<PackedDimData>();
		FileFilter dataFileFilter = new FileFilters.RegexFileFilter("dim_-?\\d+\\.txt");
		
		File[] dataFiles = dataDirectory.listFiles(dataFileFilter);
		for (File dataFile : dataFiles)
		{
			PackedDimData packedDim = readDimension(dataFile, reader);
		}
		return true;
	}
	
	private static PackedDimData readDimension(File dataFile, DimDataProcessor reader)
	{
		try
		{
			return reader.readFromFile(dataFile);
		}
		catch (Exception e)
		{
			System.err.println("Could not read dimension data from: " + dataFile.getAbsolutePath());
			System.err.println("The following error occurred:");
			printException(e, false);
			return null;
		}
	}
	
	public static boolean saveAll(Iterable<? extends IPackable<PackedDimData>> dimensions) throws IOException
	{
		// Create the data directory for our dimensions
		// Don't catch exceptions here. If we can't create this folder,
		// the mod should crash to let the user know early on.

		String basePath = DimensionManager.getCurrentSaveRootDirectory() + "/DimensionalDoors/data/";
		File basePathFile = new File(basePath);
		Files.createParentDirs(basePathFile);
		basePathFile = null;
		basePath += "dim_";
		
		boolean succeeded = true;
		DimDataProcessor writer = new DimDataProcessor();
		for (IPackable<PackedDimData> dimension : dimensions)
		{
			succeeded &= writeDimension(dimension, writer, basePath);
		}
		return succeeded;
	}
	
	private static boolean writeDimension(IPackable<PackedDimData> dimension, DimDataProcessor writer, String basePath)
	{
		try
		{
			File tempFile = new File(basePath + (dimension.name() + ".tmp"));
			File saveFile = new File(basePath + (dimension.name() + ".txt"));
			writer.writeToFile(tempFile, dimension.pack());
			saveFile.delete();
			tempFile.renameTo(saveFile);
			return true;
		}
		catch (Exception e)
		{
			System.err.println("Could not save data for dimension #" + dimension.name() + ". The following error occurred:");
			printException(e, false);
			return false;
		}
	}
	
	private static void printException(Exception e, boolean verbose)
	{
		if (e.getCause() == null)
		{
			if (verbose)
			{
				e.printStackTrace();
			}
			else
			{
				System.err.println(e.getMessage());
			}
		}
		else
		{
			System.out.println(e.getMessage());
			System.err.println("Caused by an underlying error:");
			if (verbose)
			{
				e.getCause().printStackTrace();
			}
			else
			{
				System.err.println(e.getCause().getMessage());
			}
		}
	}
}
