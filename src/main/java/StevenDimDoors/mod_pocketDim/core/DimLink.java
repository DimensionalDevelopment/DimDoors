package StevenDimDoors.mod_pocketDim.core;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.ChunkCoordIntPair;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.watcher.ClientLinkData;

public abstract class DimLink
{	
	protected ClientLinkData link;
	protected DimLink parent;
	protected LinkTail tail;
	protected List<DimLink> children;
	
	protected DimLink(ClientLinkData link, DimLink parent)
	{
		if (parent.link.point.getDimension() != link.point.getDimension())
		{
			// Ban having children in other dimensions to avoid serialization issues with cross-dimensional tails
			throw new IllegalArgumentException("source and parent.source must have the same dimension.");
		}
		this.parent = parent;
		this.link = link;
		this.tail = parent.tail;
		this.children = new LinkedList<DimLink>();
		parent.children.add(this);
	}
	
	protected DimLink(ClientLinkData link, int linkType)
	{
		if ((linkType < LinkTypes.ENUM_MIN || linkType > LinkTypes.ENUM_MAX) && linkType != LinkTypes.CLIENT_SIDE)
		{
			throw new IllegalArgumentException("The specified link type is invalid.");
		}

		this.parent = null;
		this.link = link;
		this.tail = new LinkTail(linkType, null);
		this.children = new LinkedList<DimLink>();
	}

	public Point4D source()
	{
		return link.point;
	}

	public int orientation()
	{
		return link.orientation;
	}

	public ClientLinkData link()
	{
		return link;
	}

	public Point4D destination()
	{
		return tail.getDestination();
	}
	
	public int getDestinationOrientation()
	{
		DimLink destinationLink = PocketManager.getLink(tail.getDestination());
		if (destinationLink != null)
		{
			return destinationLink.orientation();
		}
		return (link.orientation + 2) % 4;
	}
	
	public boolean hasDestination()
	{
		return (tail.getDestination() != null);
	}

	public Iterable<DimLink> children()
	{
		return children;
	}

	public int childCount()
	{
		return children.size();
	}

	public DimLink parent()
	{
		return parent;
	}
	
	public int linkType()
	{
		return tail.getLinkType();
	}
	
	public ChunkCoordIntPair getChunkCoordinates()
	{
		return new ChunkCoordIntPair(link.point.getX() >> 4, link.point.getZ() >> 4);
	}

	@Override
	public String toString()
	{
		return link.point + " -> " + (hasDestination() ? destination() : "()");
	}
}
