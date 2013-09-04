package StevenDimDoors.mod_pocketDim.helpers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.IDimRegistrationCallback;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public class Compactor
{

	private static class DimComparator implements Comparator<NewDimData>
	{
		@Override
		public int compare(NewDimData a, NewDimData b)
		{
			return a.id() - b.id();
		}
	}
	
	public static void write(Collection<? extends NewDimData> values, DataOutputStream output) throws IOException
	{
		// SenseiKiwi: Just encode the data straight up for now. I'll implement fancier compression later.
		output.writeInt(values.size());
		for (NewDimData dimension : values)
		{
			output.writeInt(dimension.id());
			output.writeInt(dimension.root().id());
			output.writeInt(dimension.linkCount());
			for (DimLink link : dimension.links())
			{
				Point4D.write(link.source(), output);
			}
		}
		
		
		// Note to self: the root ID can be "compressed" by grouping
		// dimensions by their root ID and then only sending it once
		
		/*
		// To compress the dimension IDs, we'll sort them by ID
		// and write the _difference_ between their ID numbers.
		NewDimData[] dimensions = new NewDimData[values.size()];
		dimensions = values.toArray(dimensions);
		Arrays.sort(dimensions, new DimComparator());
		*/
	}

	public static void readDimensions(DataInputStream input, IDimRegistrationCallback callback) throws IOException
	{
		// Read in the dimensions one by one. Make sure we register root dimensions before
		// attempting to register the dimensions under them.
		
		HashSet<Integer> rootIDs = new HashSet<Integer>();
		
		int dimCount = input.readInt();
		for (int k = 0; k < dimCount; k++)
		{
			int id = input.readInt();
			int rootID = input.readInt();
			
			if (rootIDs.add(rootID))
			{
				callback.registerDimension(rootID, rootID);
			}
			// Don't check if (id != rootID) - we want to retrieve the reference anyway
			NewDimData dimension = callback.registerDimension(id, rootID);
			int linkCount = input.readInt();
			for (int h = 0; h < linkCount; h++)
			{
				Point4D source = Point4D.read(input);
				dimension.createLink(source.getX(), source.getY(), source.getZ(), LinkTypes.CLIENT_SIDE);
			}
		}
	}
}
