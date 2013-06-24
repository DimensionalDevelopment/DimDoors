package StevenDimDoors.mod_pocketDim.helpers;

import java.io.File;


public class DeleteFolder
{
	public static boolean deleteFolder(File file)
	{
		try
		{
			File[] files = file.listFiles();
			
			if(files==null)
			{
				file.delete();
				return true;
			}
			for(File inFile : files)
			{
				DeleteFolder.deleteFolder(inFile);
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
}