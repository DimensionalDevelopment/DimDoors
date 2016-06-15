package com.zixiken.dimdoors.helpers;

import java.io.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import com.zixiken.dimdoors.core.DimLink;
import com.zixiken.dimdoors.core.DimensionType;
import com.zixiken.dimdoors.core.IDimRegistrationCallback;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.NewDimData;
import com.zixiken.dimdoors.util.Point4D;
import com.zixiken.dimdoors.watcher.ClientLinkData;
import io.netty.buffer.ByteBuf;

public class Compactor
{

	@SuppressWarnings("unused") // ?
	private static class DimComparator implements Comparator<NewDimData>
	{
		@Override
		public int compare(NewDimData a, NewDimData b)
		{
			return a.id() - b.id();
		}
	}
	
	public static void write(Collection<? extends NewDimData> values, ByteBuf output) throws IOException
	{
		// SenseiKiwi: Just encode the data straight up for now. I'll implement fancier compression later.
		output.writeInt(values.size());
		for (NewDimData dimension : values)
		{
			output.writeInt(dimension.id());
			output.writeInt(dimension.root().id());
            output.writeInt(dimension.type().index);
			output.writeInt(dimension.linkCount());
			for (DimLink link : dimension.links())
			{
                (new ClientLinkData(link)).write(output);
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

	public static void readDimensions(ByteBuf input, IDimRegistrationCallback callback) throws IOException
	{
		// Read in the dimensions one by one. Make sure we register root dimensions before
		// attempting to register the dimensions under them.
		
		HashSet<Integer> rootIDs = new HashSet<Integer>();
		
		int dimCount = input.readInt();
		for (int k = 0; k < dimCount; k++)
		{
			int id = input.readInt();
			int rootID = input.readInt();
			DimensionType type = DimensionType.getTypeFromIndex(input.readInt());
			
			if (rootIDs.add(rootID))
			{
				callback.registerDimension(rootID, rootID, type);
			}
			// Don't check if (id != rootID) - we want to retrieve the reference anyway
			NewDimData dimension = callback.registerDimension(id, rootID, type);
			int linkCount = input.readInt();
			for (int h = 0; h < linkCount; h++)
			{
				ClientLinkData link = ClientLinkData.read(input);
				Point4D source = link.point;
				dimension.createLink(source.getX(), source.getY(), source.getZ(), LinkType.CLIENT,0);
			}
		}
	}
}
