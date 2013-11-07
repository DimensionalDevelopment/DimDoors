package StevenDimDoors.mod_pocketDim.core;

import java.util.LinkedList;
import java.util.List;

import StevenDimDoors.mod_pocketDim.util.Point4D;
public abstract class DimLink
{	
	protected Point4D source;
	protected DimLink parent;
	protected LinkTail tail;
	protected int orientation;
	protected List<DimLink> children;
	
	protected DimLink(Point4D source, DimLink parent, int orientation)
	{
		
		if (parent.source.getDimension() != source.getDimension())
		{
			// Ban having children in other dimensions to avoid serialization issues with cross-dimensional tails
			throw new IllegalArgumentException("source and parent.source must have the same dimension.");
		}
		this.orientation=orientation;
		this.parent = parent;
		this.source = source;
		this.tail = parent.tail;
		this.children = new LinkedList<DimLink>();
		parent.children.add(this);
	}
	
	protected DimLink(Point4D source, int linkType, int orientation)
	{
		if ((linkType < LinkTypes.ENUM_MIN || linkType > LinkTypes.ENUM_MAX) && linkType != LinkTypes.CLIENT_SIDE)
		{
			throw new IllegalArgumentException("The specified link type is invalid.");
		}
		this.orientation = orientation;
		this.parent = null;
		this.source = source;
		this.tail = new LinkTail(linkType, null);
		this.children = new LinkedList<DimLink>();
	}

	public Point4D source()
	{
		return source;
	}

	public Point4D destination()
	{
		return tail.getDestination();
	}
	public int getDestinationOrientation()
	{
		return PocketManager.getLink(source.getX(), source.getY(), source.getZ(), source.getDimension()).orientation();
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
	public int orientation()
	{
		return orientation;
	}

	public String toString()
	{
		return source + " -> " + (hasDestination() ? destination() : "");
	}
}
