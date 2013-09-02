package StevenDimDoors.mod_pocketDim.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.NewDimData.InnerDimLink;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonData;
import StevenDimDoors.mod_pocketDim.util.Point4D;

import com.google.common.collect.ImmutableList;

public class DimMessageBuilder implements IMessageBuilder<NewDimData>
{
	public static class DimMessage implements IDataMessage
	{
		//We'll use public fields here since this is a data container object and all the fields are immutable
		//We will not transfer dungeon, link data, or any data on child dimensions
		//As far as I can tell, the children will handle updating their parents anyway
		
		public final int ID;
		public final boolean IsDungeon;
		public final boolean IsFilled;
		public final int Depth;
		public final int PackDepth;
		public final Integer ParentID;
		public final int RootID;
		public final Point4D Origin;
		public final int Orientation;
		
		private DimMessage(NewDimData dimension)
		{
			ID = dimension.id();
			IsDungeon = dimension.isDungeon();
			IsFilled = dimension.isFilled();
			Depth = dimension.depth();
			PackDepth = dimension.packDepth();
			ParentID = (dimension.parent() != null) ? dimension.parent().id() : null;
			RootID = dimension.root().id();
			Origin = dimension.origin();
			Orientation = dimension.orientation();			
		}
		
		private DimMessage(DataInputStream stream) throws IOException
		{
			ID = stream.readInt();
			IsDungeon = stream.readBoolean();
			IsFilled = stream.readBoolean();
			Depth = stream.readInt();
			PackDepth = stream.readInt();
			ParentID = stream.
		}

		@Override
		public void writeToStream(DataOutputStream stream) throws IOException
		{
			//Write a flag indicating that this is a full message and not a key
			stream.writeBoolean(true);
			
		}
	}
	
	public static class DimKeyMessage implements IDataMessage
	{
		//We'll use public fields here since this is a data container object and all the fields are immutable
		public final int ID;
		
		private DimKeyMessage(NewDimData dimension)
		{
			ID = dimension.id();
		}
		
		private DimKeyMessage(DataInputStream stream) throws IOException
		{
			ID = stream.readInt();
		}

		@Override
		public void writeToStream(DataOutputStream stream) throws IOException
		{
			//Write a flag indicating that this is a key
			stream.writeBoolean(false);
			stream.writeInt(ID);
		}
	}
	
	@Override
	public IDataMessage createKey(NewDimData target)
	{
		return new DimKeyMessage(target);
	}

	@Override
	public IDataMessage createMessage(NewDimData target)
	{
		return new DimMessage(target);
	}

	@Override
	public IDataMessage read(DataInputStream source) throws IOException
	{
		//Check whether the message is a full message or just a key
		if (source.readBoolean())
		{
			return new DimMessage(source);
		}
		else
		{
			return new DimKeyMessage(source);
		}
	}	
}
