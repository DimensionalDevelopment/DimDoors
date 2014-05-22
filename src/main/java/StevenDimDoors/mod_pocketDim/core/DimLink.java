package StevenDimDoors.mod_pocketDim.core;

import java.util.LinkedList;
import java.util.List;
import net.minecraft.item.ItemStack;
import StevenDimDoors.mod_pocketDim.items.ItemDDKey;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public abstract class DimLink
{	
	protected Point4D point;
	protected int orientation;
	private DDLock lock;
	protected DimLink parent;
	protected LinkTail tail;
	protected List<DimLink> children;
	
	protected DimLink(Point4D point, int orientation, DDLock lock, DimLink parent)
	{
		
		if (parent.point.getDimension() != point.getDimension())
		{
			// Ban having children in other dimensions to avoid serialization issues with cross-dimensional tails
			throw new IllegalArgumentException("source and parent.source must have the same dimension.");
		}
		this.lock = lock;
		this.parent = parent;
		this.point = point;
		this.tail = parent.tail;
		this.orientation = orientation;
		this.children = new LinkedList<DimLink>();
		parent.children.add(this);
	}
	
	protected DimLink(Point4D point, int orientation, DDLock lock, int linkType)
	{
		if ((linkType < LinkTypes.ENUM_MIN || linkType > LinkTypes.ENUM_MAX) && linkType != LinkTypes.CLIENT_SIDE)
		{
			throw new IllegalArgumentException("The specified link type is invalid.");
		}
		this.lock = lock;
		this.parent = null;
		this.point = point;
		this.orientation = orientation;
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
	
	
	public String toString()
	{
		return point + " -> " + (hasDestination() ? destination() : "");
	}
	
	/**
	 * Tries to open this lock. Returns true if the lock is open or if the key can open it
	 * @return
	 */
	public boolean open(ItemStack item)
	{
		return lock.open(item);
	}
	
	/**
	 * Tries to open this lock. Returns true if the lock is open or if the key can open it
	 * @return
	 */
	public boolean canOpen(ItemStack item)
	{
		return lock.canOpen(item);
	}

	/**
	 * test if there is a lock, regardless if it is locked or not.
	 * @return
	 */
	public boolean hasLock()
	{
		return this.lock!=null;
	}
	
	public boolean isLocked()
	{
		return this.hasLock()&&this.lock.isLocked();
	}
	
	public DDLock getLock()
	{
		PocketManager.getDimensionData(this.source().getDimension()).flagModified();
		return this.lock;
	}
	 /**
	  * only use this on the client to update errything
	  * @param lock
	  */
	public void setLock(DDLock lock)
	{
		PocketManager.getDimensionData(this.source().getDimension()).flagModified();
		this.lock = lock;
	}
	
	/**
	 * create a lock from a key. Returns false if this door already has a lock, or if they has already locked a door
	 * @param itemStack
	 * @return
	 */
	public boolean createLock(ItemStack itemStack, int lockKey)
	{
		if(this.hasLock()||DDLock.hasCreatedLock(itemStack))
		{
			return false;
		}
		this.lock = DDLock.createLock(itemStack, lockKey);
		PocketManager.getDimensionData(this.source().getDimension()).flagModified();
		return true;
	}

}
