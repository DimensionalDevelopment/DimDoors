package StevenDimDoors.mod_pocketDim.core;

import java.util.LinkedList;
import java.util.List;

import StevenDimDoors.mod_pocketDim.util.Point4D;

public abstract class DimLink
{	
	protected Point4D source;
	protected DimLink parent;
	protected LinkTail tail;
	protected List<DimLink> children;
	
	protected DimLink(Point4D source, DimLink parent)
	{
		this.parent = parent;
		this.source = source;
		this.tail = parent.tail;
		this.children = new LinkedList<DimLink>();
		parent.children.add(this);
	}
	
	protected DimLink(Point4D source, int linkType)
	{
		if (linkType < LinkTypes.ENUM_MIN || linkType > LinkTypes.ENUM_MAX && linkType != LinkTypes.CLIENT_SIDE)
		{
			throw new IllegalArgumentException("The specified link type is invalid.");
		}
		
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

	public String toString()
	{
		return source + " -> " + (hasDestination() ? destination() : "");
	}
}
