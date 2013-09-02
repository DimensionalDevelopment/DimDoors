package StevenDimDoors.mod_pocketDim.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.util.Point4D;

import com.google.common.collect.ImmutableList;

public class LinkMessageBuilder implements IMessageBuilder<DimLink>
{
	public static class LinkMessage implements IDataMessage
	{
		//We'll use public fields here since this is a data container object and all the fields are immutable
		public final Point4D Source;
		public final Point4D Destination;
		public final int LinkType;
		public final Point4D Parent;
		public final ImmutableList<Point4D> Children;

		private LinkMessage(DimLink link)
		{
			// TODO: In the case that a child's parent has been removed but the rest of the group still exists,
			// this group bond will be lost to this link on the client side. Currently, that's not a problem since
			// destination data and groups don't matter to the client, but it's something to think about later.

			Source = link.source();
			Destination = link.destination();
			LinkType = link.linkType();
			Parent = link.parent().source();
			ImmutableList.Builder<Point4D> builder = new ImmutableList.Builder<Point4D>(); 
			for (DimLink child : link.children())
			{
				builder.add(child.source());
			}
			Children = builder.build();
		}
		
		private LinkMessage(DataInputStream stream) throws IOException
		{
			Source = Point4D.read(stream);
			Parent = Point4D.read(stream);
			if (Parent == null)
			{
				Destination = Point4D.read(stream);
				LinkType = stream.readInt();
			}
			else
			{
				Destination = null;
				LinkType = -1;
			}
			int childCount = stream.readInt();
			ImmutableList.Builder<Point4D> builder = new ImmutableList.Builder<Point4D>(); 
			for (int k = 0; k < childCount; k++)
			{
				builder.add(Point4D.read(stream));
			}
			Children = builder.build();
		}

		@Override
		public void writeToStream(DataOutputStream stream) throws IOException
		{
			//Write a flag indicating that this is a full message and not a key
			stream.writeBoolean(true);
			Point4D.write(Source, stream);
			Point4D.write(Parent, stream);
			//A link only has its own destination information if it has no parent to provide it
			if (Parent == null)
			{
				Point4D.write(Destination, stream);
				stream.writeInt(LinkType);				
			}
			stream.writeInt(Children.size());
			for (Point4D child : Children)
			{
				Point4D.write(child, stream);
			}
		}
	}
	
	public static class LinkKeyMessage implements IDataMessage
	{
		//We'll use public fields here since this is a data container object and all the fields are immutable
		public final Point4D Source;
		
		private LinkKeyMessage(DimLink link)
		{
			Source = link.source();
		}
		
		private LinkKeyMessage(DataInputStream stream) throws IOException
		{
			Source = Point4D.read(stream);
		}

		@Override
		public void writeToStream(DataOutputStream stream) throws IOException
		{
			//Write a flag indicating that this is a key
			stream.writeBoolean(false);
			Point4D.write(Source, stream);
		}
	}
	
	@Override
	public IDataMessage createKey(DimLink target)
	{
		return new LinkKeyMessage(target);
	}

	@Override
	public IDataMessage createMessage(DimLink target)
	{
		return new LinkMessage(target);
	}

	@Override
	public IDataMessage read(DataInputStream source) throws IOException
	{
		//Check whether the message is a full message or just a key
		if (source.readBoolean())
		{
			return new LinkMessage(source);
		}
		else
		{
			return new LinkKeyMessage(source);
		}
	}	
}
