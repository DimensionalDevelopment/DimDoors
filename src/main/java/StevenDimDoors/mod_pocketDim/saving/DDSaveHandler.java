package StevenDimDoors.mod_pocketDim.saving;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.DimensionType;
import StevenDimDoors.mod_pocketDim.core.LinkType;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonData;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.util.DDLogger;
import StevenDimDoors.mod_pocketDim.util.FileFilters;
import StevenDimDoors.mod_pocketDim.util.Point4D;
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
		
		DDLogger.startTimer("Loading data");
		
		String basePath = DimensionManager.getCurrentSaveRootDirectory() + "/DimensionalDoors/data/";
		File dataDirectory = new File(basePath);
		
		// Check if the folder exists. If it doesn't, just return.
		if (!dataDirectory.exists())
		{
			return true;
		}
		
		// Load the dimension blacklist
		File blacklistFile = new File(basePath+"blacklist.txt");
		
		if(blacklistFile.exists())
		{
			BlacklistProcessor blacklistReader = new BlacklistProcessor();
			List<Integer> blacklist = readBlacklist(blacklistFile,blacklistReader);
			PocketManager.createAndRegisterBlacklist(blacklist);
		}
		
		// Load the personal pockets mapping		
		File personalPocketMap = new File(basePath+"personalPockets.txt");
		HashMap<String, Integer> ppMap = new HashMap<String, Integer>();
		if(personalPocketMap.exists())
		{
			PersonalPocketMappingProcessor ppMappingProcessor = new PersonalPocketMappingProcessor();
			ppMap = readPersonalPocketsMapping(personalPocketMap,ppMappingProcessor);
		}						
		
		// List any dimension data files and read each dimension
		DimDataProcessor reader = new DimDataProcessor();
		HashMap<Integer, PackedDimData> packedDims = new HashMap<Integer, PackedDimData>();
		FileFilter dataFileFilter = new FileFilters.RegexFileFilter("dim_-?\\d+\\.txt");
		
		File[] dataFiles = dataDirectory.listFiles(dataFileFilter);
		for (File dataFile : dataFiles)
		{
			PackedDimData packedDim = readDimension(dataFile, reader);
			if(packedDim == null)
			{
				throw new IllegalStateException("The DD data for "+dataFile.getName().replace(".txt", "")+" at "+dataFile.getPath()+" is corrupted. Please report this on the MCF or on the DD github issues tracker.");
			}
			packedDims.put(packedDim.ID,packedDim);

		}
		
		List<PackedLinkData> linksToUnpack = new ArrayList<PackedLinkData>();
		//get the grand list of all links to unpack
		for(PackedDimData packedDim : packedDims.values())
		{
			linksToUnpack.addAll(packedDim.Links);
		}
		unpackDimData(packedDims);
		unpackLinkData(linksToUnpack);
		
		HashMap<String, NewDimData> personalPocketsMap = new HashMap<String, NewDimData>();
		for(Entry<String, Integer> pair : ppMap.entrySet())
		{
			personalPocketsMap.put(pair.getKey(), PocketManager.getDimensionData(pair.getValue()));
		}
		PocketManager.setPersonalPocketsMapping(personalPocketsMap);
		
		return true;
	}
	
	/**
	 * Takes a list of packedDimData and rebuilds the DimData for it
	 * @param packedDims
	 * @return
	 */
	public static boolean unpackDimData(HashMap<Integer,PackedDimData> packedDims)
	{
		LinkedList<Integer> dimsToRegister = new LinkedList<Integer>();
		
		for(PackedDimData packedDim : packedDims.values())
		{						
			//fix pockets without parents
			verifyParents(packedDim, packedDims);
			
			//Load roots first by inserting them in the LinkedList first.
			if(packedDim.RootID==packedDim.ID)
			{
				dimsToRegister.addFirst(packedDim.ID);
			}
		}
		
		//load the children for each root
		while(!dimsToRegister.isEmpty())
		{
			Integer childID = dimsToRegister.pop();
			PackedDimData data = packedDims.get(childID);
			dimsToRegister.addAll(verifyChildren(data, packedDims));
			PocketManager.registerPackedDimData(data);
		}
		return true;
	}
	/**
	 * Fixes the case where a child of a parent has been deleted.
	 * -removes the child from parent
	 * 
	 * @param packedDim
	 * @param packedDims
	 * @return
	 */
	private static ArrayList<Integer> verifyChildren(PackedDimData packedDim,HashMap<Integer,PackedDimData> packedDims)
	{			
		ArrayList<Integer> children = new ArrayList<Integer>();
		children.addAll(packedDim.ChildIDs);
		boolean isMissing = false;
		for(Integer childID : packedDim.ChildIDs)
		{
			if(!packedDims.containsKey(childID))
			{
				children.remove(childID);
				isMissing=true;
			}
		}
		if(isMissing)
		{
			packedDim=(new PackedDimData(packedDim.ID, packedDim.Depth, packedDim.PackDepth, packedDim.ParentID, packedDim.RootID, packedDim.Orientation, DimensionType.getTypeFromIndex(packedDim.DimensionType), packedDim.IsFilled, packedDim.DungeonData, packedDim.Origin, children, packedDim.Links, packedDim.Tails));
			packedDims.put(packedDim.ID, packedDim);
		}
		return children;
	}
	
	/**
	 * Fixes the case where a child had its parent deleted OR where a parent forgot about its child
	 * -Changes the missing parent to the dims root if its original parent is gone.
	 * -Finds the new parent and adds it to its list of children or reminds the old parent if it forgot its child
	 * 
	 * @param packedDim
	 * @param packedDims
	 */
	public static void verifyParents(PackedDimData packedDim,HashMap<Integer,PackedDimData> packedDims)
	{
		ArrayList<Integer> fosterChildren = new ArrayList<Integer>();
		fosterChildren.add(packedDim.ID);
		DimensionType type = DimensionType.getTypeFromIndex(packedDim.DimensionType);
		//fix pockets without parents
		if(!packedDims.containsKey(packedDim.ParentID))
		{
			//Fix the orphan by changing its root to its parent, re-connecting it to the list
			packedDim=(new PackedDimData(packedDim.ID, 1, packedDim.PackDepth, packedDim.RootID, packedDim.RootID, packedDim.Orientation,type, packedDim.IsFilled, packedDim.DungeonData, packedDim.Origin, packedDim.ChildIDs, packedDim.Links, packedDim.Tails));
			packedDims.put(packedDim.ID, packedDim);
		}
		//fix pockets whose parents have forgotten about them
		PackedDimData fosterParent = packedDims.get(packedDim.ParentID);
		if(!fosterParent.ChildIDs.contains(packedDim.ID)&&packedDim.ID!=packedDim.RootID)
		{
			//find the root, and fix it by adding the orphan's ID to its children
			fosterChildren.addAll(fosterParent.ChildIDs);
			fosterParent=(new PackedDimData(fosterParent.ID, fosterParent.Depth, fosterParent.PackDepth, fosterParent.ParentID, fosterParent.RootID, fosterParent.Orientation, type, fosterParent.IsFilled, fosterParent.DungeonData, fosterParent.Origin, fosterChildren, fosterParent.Links, fosterParent.Tails));
			packedDims.put(fosterParent.ID, fosterParent);	
		}
			
	}
	
	public static boolean unpackLinkData(List<PackedLinkData> linksToUnpack)
	{
		Point3D fakePoint = new Point3D(-1,-1,-1);
		List<PackedLinkData> unpackedLinks = new ArrayList<PackedLinkData>();
		/**
		 * sort through the list, unpacking links that do not have parents. 
		 */
		//TODO- what we have a loop of links?
		for(PackedLinkData packedLink : linksToUnpack)
		{
			if(packedLink.parent.equals(fakePoint))
			{
				NewDimData data = PocketManager.getDimensionData(packedLink.source.getDimension());
				LinkType linkType = LinkType.getLinkTypeFromIndex(packedLink.tail.linkType);

				
				DimLink link = data.createLink(packedLink.source, linkType, packedLink.orientation, packedLink.lock);
				Point4D destination = packedLink.tail.destination;
				if(destination!=null)
				{
					PocketManager.createDimensionDataDangerously(destination.getDimension()).setLinkDestination(link, destination.getX(),destination.getY(),destination.getZ());
				}
				unpackedLinks.add(packedLink);
			}
		}
		linksToUnpack.removeAll(unpackedLinks);
		
		//unpack remaining children
		while(!linksToUnpack.isEmpty())
		{
			for(PackedLinkData packedLink : linksToUnpack)
			{
				NewDimData data = PocketManager.createDimensionDataDangerously(packedLink.source.getDimension());
				if(data.getLink(packedLink.parent)!=null)
				{
					data.createChildLink(packedLink.source, data.getLink(packedLink.parent), packedLink.lock);
				}
				unpackedLinks.add(packedLink);
			}
			linksToUnpack.removeAll(unpackedLinks);
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
	
	public static boolean saveAll(Iterable<? extends IPackable<PackedDimData>> dimensions,
			List<Integer> blacklist, boolean checkModified) throws IOException
	{
		// Create the data directory for our dimensions
		// Don't catch exceptions here. If we can't create this folder,
		// the mod should crash to let the user know early on.

		// Get the save directory path
		File saveDirectory = new File(mod_pocketDim.instance.getCurrentSavePath() + "/DimensionalDoors/data/");
		String savePath = saveDirectory.getAbsolutePath();
		String baseSavePath = savePath + "/dim_";
		File backupDirectory = new File(savePath + "/backup");
		String baseBackupPath = backupDirectory.getAbsolutePath() + "/dim_";

		if (!saveDirectory.exists())
		{
			// Create the save directory
			Files.createParentDirs(saveDirectory);
			saveDirectory.mkdir();
		}
		if (!backupDirectory.exists())
		{
			// Create the backup directory
			backupDirectory.mkdir();
		}
		
		// Create and write the blackList
		writeBlacklist(blacklist, savePath);
		
		//create and write personal pocket mapping
		writePersonalPocketMap(PocketManager.getPersonalPocketMapping(), savePath);
		
		// Write the dimension save data
		boolean succeeded = true;
		DimDataProcessor writer = new DimDataProcessor();
		for (IPackable<PackedDimData> dimension : dimensions)
		{
			// Check if the dimension should be saved
			if (!checkModified || dimension.isModified())
			{
				if (writeDimension(dimension, writer, baseSavePath, baseBackupPath))
				{
					dimension.clearModified();
				}
				else
				{
					succeeded = false;
				}
			}
		}
		
		return succeeded;
	}
	
	private static boolean writeBlacklist(List<Integer> blacklist, String savePath)
	{
		try
		{
			BlacklistProcessor writer = new BlacklistProcessor();
			File tempFile = new File(savePath + "/blacklist.tmp");
			File saveFile = new File(savePath + "/blacklist.txt");
			writer.writeToFile(tempFile, blacklist);
			saveFile.delete();
			tempFile.renameTo(saveFile);
			return true;
		}
		catch (Exception e)
		{
			System.err.println("Could not save blacklist. The following error occurred:");
			printException(e, true);
			return false;
		}	
	}
	
	private static boolean writePersonalPocketMap(HashMap<String, NewDimData> hashMap, String savePath)
	{
		try
		{
			HashMap<String, Integer> ppMap = new HashMap<String, Integer>();
			
			for(Entry<String, NewDimData> pair : hashMap.entrySet())
			{
				ppMap.put(pair.getKey(), pair.getValue().id());
			}
			PersonalPocketMappingProcessor writer = new PersonalPocketMappingProcessor();
			File tempFile = new File(savePath + "/personalPockets.tmp");
			File saveFile = new File(savePath + "/personalPockets.txt");
			writer.writeToFile(tempFile, ppMap);
			saveFile.delete();
			tempFile.renameTo(saveFile);
			return true;
		}
		catch (Exception e)
		{
			System.err.println("Could not save personal pockets mapping. The following error occurred:");
			printException(e, true);
			return false;
		}	
	}
	
	private static boolean writeDimension(IPackable<PackedDimData> dimension, DimDataProcessor writer, String basePath, String backupPath)
	{
		try
		{
			File saveFile = new File(basePath + dimension.name() + ".txt");
			
			// If the save file already exists, back it up.
			if (saveFile.exists())
			{
				Files.move(saveFile, new File(backupPath + dimension.name() + ".txt"));
			}

			writer.writeToFile(saveFile, dimension.pack());
			return true;
		}
		catch (Exception e)
		{
			System.err.println("Could not save data for dimension #" + dimension.name() + ". The following error occurred:");
			printException(e, true);
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

	//TODO - make this more robust
	public static DungeonData unpackDungeonData(PackedDungeonData packedDungeon)
	{	
		for(DungeonData data  : DungeonHelper.instance().getRegisteredDungeons())
		{
			if(data.schematicName().equals(packedDungeon.SchematicName))
			{
				return data;
			}
		}
		return null;
	}

	public static List<Integer> readBlacklist(File blacklistFile, BlacklistProcessor reader)
	{
		try
		{
			List<Integer> list = reader.readFromFile(blacklistFile);
            if (list == null)
                return new ArrayList<Integer>(0);
            return list;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return new ArrayList<Integer>(0);
		}
	}
	
	public static HashMap<String,Integer> readPersonalPocketsMapping(File ppMap, PersonalPocketMappingProcessor reader)
	{
		try
		{
			return reader.readFromFile(ppMap);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
