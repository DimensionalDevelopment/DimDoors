package StevenDimDoors.mod_pocketDim.core;

import java.util.LinkedList;
import java.util.List;

import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.watcher.ClientLinkData;

public abstract class DimLink
{	
	protected Point4D point;
	protected int orientation;
	protected boolean isLocked;
	protected DimLink parent;
	protected LinkTail tail;
	protected List<DimLink> children;
	
	protected DimLink(Point4D point, int orientation, boolean locked, DimLink parent)
	{
		
		if (parent.point.getDimension() != point.getDimension())
		{
			// Ban having children in other dimensions to avoid serialization issues with cross-dimensional tails
			throw new IllegalArgumentException("source and parent.source must have the same dimension.");
		}
		this.parent = parent;
		this.point = point;
		this.tail = parent.tail;
		this.orientation = orientation;
		this.isLocked = locked;
		this.children = new LinkedList<DimLink>();
		parent.children.add(this);
	}
	
	protected DimLink(Point4D point, int orientation, boolean locked, int linkType)
	{
		if ((linkType < LinkTypes.ENUM_MIN || linkType > LinkTypes.ENUM_MAX) && linkType != LinkTypes.CLIENT_SIDE)
		{
			throw new IllegalArgumentException("The specified link type is invalid.");
		}

		this.parent = null;
		this.point = point;
		this.orientation = orientation;
		this.isLocked = locked;
		this.tail = new LinkTail(linkType, null);
		this.children = new LinkedList<DimLink>();
	}

	public Point4D source()
	{
		return point;
	}

	public void clear()
	{
		//Release children
		for (DimLink child : children)
		{
			 child.parent = null;
		}
		children.clear();
		
		//Release parent
		if (parent != null)
		{
			parent.children.remove(this);
		}
		
		parent = null;
		point = null;
		tail = new LinkTail(0, null);
	}
	
	public int orientation()
	{
		return orientation;
	}

	public Point4D destination()
	{
		return tail.getDestination();
	}
	public int getDestinationOrientation()
	{
		DimLink link = PocketManager.getLink(this.destination().getX(), this.destination().getY(), this.destination().getZ(), this.destination().getDimension());
		if(link !=null)
		{
			return link.orientation();
		}
		return (this.orientation()+2)%4;
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
	
	public boolean isLocked()
	{
		return isLocked;
	}
	
	public void setLocked(boolean bol)
	{
		isLocked = bol;
	}

	public String toString()
	{
		return point + " -> " + (hasDestination() ? destination() : "");
	}
}
