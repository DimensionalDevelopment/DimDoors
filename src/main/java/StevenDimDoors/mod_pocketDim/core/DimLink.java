package StevenDimDoors.mod_pocketDim.core;

import java.util.LinkedList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ChunkCoordIntPair;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public abstract class DimLink
{	
	protected Point4D point;
	protected int orientation;
	protected DDLock lock;
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
	
	protected DimLink(Point4D point, int orientation, DDLock lock, LinkType linkType)
	{
		/**This really cant happen anymore, I guess.
		 * 
		if ((linkType < LinkTypes.ENUM_MIN || linkType > LinkTypes.ENUM_MAX) && linkType != LinkTypes.CLIENT_SIDE)
		{
			throw new IllegalArgumentException("The specified link type is invalid.");
		}
		**/
		
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
		tail = new LinkTail(LinkType.NORMAL, null);
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
		DimLink destinationLink = PocketManager.getLink(tail.getDestination());
		if (destinationLink != null)
		{
			return destinationLink.orientation();
		}
		return (orientation + 2) % 4;
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
	
	public LinkType linkType()
	{
		return tail.getLinkType();
	}
	
	
	/**
	 * Tries to open this lock. Returns true if the lock is open or if the key can open it
	 * @return
	 */
	public boolean tryToOpen(ItemStack item)
	{
		return lock.tryToOpen(item);
	}
	
	/**
	 * Tests if the given key item fits this lock
	 * @return
	 */
	public boolean doesKeyUnlock(ItemStack item)
	{
		return lock.doesKeyUnlock(item);
	}

	/**
	 * test if there is a lock, regardless if it is locked or not.
	 * @return
	 */
	public boolean hasLock()
	{
		return this.lock!=null;
	}
	
	/**
	 * Tests if the lock is open or not
	 * 
	 */
	public boolean getLockState()
	{
		return this.hasLock()&&this.lock.getLockState();
	}

	/**
	 * gets the actual lock object
	 * @return
	 */
	public DDLock getLock()
	{
		return this.lock;
	}
	
	public ChunkCoordIntPair getChunkCoordinates()
	{
		return new ChunkCoordIntPair(point.getX() >> 4, point.getZ() >> 4);
	}

	@Override
	public String toString()
	{
		return point + " -> " + (hasDestination() ? destination() : "()");
	}
}
