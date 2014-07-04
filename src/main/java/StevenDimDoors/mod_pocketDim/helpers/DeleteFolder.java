package StevenDimDoors.mod_pocketDim.helpers;

import java.io.File;

public class DeleteFolder
{
	public static boolean deleteFolder(File directory)
	{
		try
		{
			File[] contents = directory.listFiles();
			if (contents != null)
			{
				for (File entry : contents)
				{
					if (entry.isDirectory())
					{
						deleteFolder(entry);
					}
					else
					{
						entry.delete();
					}
				}
			}
			return directory.delete();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}