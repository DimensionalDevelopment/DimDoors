package StevenDimDoors.mod_pocketDim.core;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonData;
import StevenDimDoors.mod_pocketDim.dungeon.pack.DungeonPack;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.watcher.IOpaqueMessage;
import StevenDimDoors.mod_pocketDim.watcher.IUpdateWatcher;

public abstract class NewDimData
{
	private static class DimLink implements IDimLink
	{
		//DimLink is an inner class here to make it immutable to code outside NewDimData
		
		private static final int EXPECTED_CHILDREN = 2;
		
		private Point4D source;
		private DimLink parent;
		private LinkTail tail;
		private ArrayList<IDimLink> children;
		
		public DimLink(Point4D source, DimLink parent)
		{
			this.parent = parent;
			this.source = source;
			this.tail = parent.tail;
			this.children = new ArrayList<IDimLink>(EXPECTED_CHILDREN);
			parent.children.add(this);
		}
		
		public DimLink(Point4D source, int linkType)
		{
			if (linkType < IDimLink.TYPE_ENUM_MIN || linkType > IDimLink.TYPE_ENUM_MAX)
			{
				throw new IllegalArgumentException("The specified link type is invalid.");
			}
			
			this.parent = null;
			this.source = source;
			this.tail = new LinkTail(linkType, null);
			this.children = new ArrayList<IDimLink>(EXPECTED_CHILDREN);
		}

		@Override
		public Point4D source()
		{
			return source;
		}

		@Override
		public Point4D destination()
		{
			return tail.getDestination();
		}

		@Override
		public boolean hasDestination()
		{
			return (tail.getDestination() != null);
		}
		
		public void setDestination(int x, int y, int z, NewDimData dimension)
		{
			tail.setDestination(new Point4D(x, y, z, dimension.id()));
		}

		@Override
		public Iterable<IDimLink> children()
		{
			return children;
		}

		@Override
		public int childCount()
		{
			return children.size();
		}

		@Override
		public IDimLink parent()
		{
			return parent;
		}
		
		@Override
		public int linkType()
		{
			return tail.getLinkType();
		}
		
		public void clear()
		{
			//Release children
			for (IDimLink child : children)
			{
				((DimLink) child).parent = null;
			}
			children.clear();
			
			//Release parent
			if (parent != null)
			{
				parent.children.remove(this);
			}
			
			parent = null;
			source = null;
			tail = new LinkTail(0, null);
		}
		
		public void overwrite(DimLink nextParent)
		{
			if (nextParent == null)
			{
				throw new IllegalArgumentException("nextParent cannot be null.");
			}
			
			if (this == nextParent)
			{
				//Ignore this request silently
				return;
			}
			
			//Release children
			for (IDimLink child : children)
			{
				((DimLink) child).parent = null;
			}
			children.clear();
			
			//Release parent
			if (parent != null)
			{
				parent.children.remove(this);
			}
			
			//Attach to new parent
			parent = nextParent;
			tail = nextParent.tail;
			nextParent.children.add(this);
		}
		
		public void overwrite(int linkType)
		{	
			//Release children
			for (IDimLink child : children)
			{
				((DimLink) child).parent = null;
			}
			children.clear();
			
			//Release parent
			if (parent != null)
			{
				parent.children.remove(this);
			}
			
			//Attach to new parent
			parent = null;
			tail = new LinkTail(linkType, null);
		}

		@Override
		public String toString()
		{
			return source + " -> " + (hasDestination() ? destination() : "");
		}
		
		public IOpaqueMessage toMessage()
		{
			return null;
		}

		public IOpaqueMessage toKey()
		{
			return null;
		}
	}
	
	private static Random random = new Random();
	
	private final int id;
	private final Map<Point4D, DimLink> linkMapping;
	private final List<DimLink> linkList;
	private final boolean isDungeon;
	private boolean isFilled;
	private final int depth;
	private int packDepth;
	private final NewDimData parent;
	private final NewDimData root;
	private final List<NewDimData> children;
	private Point4D origin;
	private int orientation;
	private DungeonData dungeon;
	private final IUpdateWatcher dimWatcher;
	private final IUpdateWatcher linkWatcher;
	
	protected NewDimData(int id, NewDimData parent, boolean isPocket, boolean isDungeon,
		IUpdateWatcher dimWatcher, IUpdateWatcher linkWatcher)
	{
		//The isPocket flag is redundant. It's meant as an integrity safeguard.
		if (isPocket == (parent != null))
		{
			throw new NullPointerException("Dimensions can be pocket dimensions if and only if they have a parent dimension.");
		}
		if (isDungeon && !isPocket)
		{
			throw new IllegalArgumentException("A dimensional dungeon must also be a pocket dimension.");
		}
		
		this.id = id;
		this.linkMapping = new TreeMap<Point4D, DimLink>(); //Should be stored in oct tree -- temporary solution
		this.linkList = new ArrayList<DimLink>(); //Should be stored in oct tree -- temporary solution
		this.children = new ArrayList<NewDimData>(); 
		this.parent = parent;
		this.packDepth = 0;
		this.isDungeon = isDungeon;
		this.isFilled = false;
		this.orientation = 0;
		this.origin = null;
		this.dungeon = null;
		this.dimWatcher = dimWatcher;
		this.linkWatcher = linkWatcher;
		
		//Register with parent
		if (parent != null)
		{
			//We don't need to raise an update event for adding a child because the child's creation will be signaled.
			this.root = parent.root;
			this.depth = parent.depth + 1;
			parent.children.add(this);
		}
		else
		{
			this.root = this;
			this.depth = 0;
		}
	}
	
	protected abstract IOpaqueMessage toMessage();
	protected abstract IOpaqueMessage toKey();
	
	public IDimLink findNearestRift(World world, int range, int x, int y, int z)
	{
		//TODO: Rewrite this later to use an octtree

		//Sanity check...
		if (world.provider.dimensionId != id)
		{
			throw new IllegalArgumentException("Attempted to search for links in a World instance for a different dimension!");
		}
		
		//Note: Only detect rifts at a distance > 1, so we ignore the rift
		//that called this function and any adjacent rifts.
		
		IDimLink nearest = null;
		IDimLink link;
		
		int distance;
		int minDistance = Integer.MAX_VALUE;
		int i, j, k;
		DDProperties properties = DDProperties.instance();

		for (i = -range; i <= range; i++)
		{
			for (j = -range; j <= range; j++)
			{
				for (k = -range; k <= range; k++)
				{
					distance = getAbsoluteSum(i, j, k);
					if (distance > 1 && distance < minDistance && world.getBlockId(x + i, y + j, z + k) == properties.RiftBlockID)
					{
						link = getLink(x+i, y+j, z+k);
						if (link != null)
						{
							nearest = link;
							minDistance = distance;
						}
					}
				}
			}
		}

		return nearest;
	}
	
	private static int getAbsoluteSum(int i, int j, int k)
	{
		return Math.abs(i) + Math.abs(j) + Math.abs(k);
	}
	
	public IDimLink createLink(int x, int y, int z, int linkType)
	{
		return createLink(new Point4D(x, y, z, id), linkType);
	}
	
	private IDimLink createLink(Point4D source, int linkType)
	{
		//Return an existing link if there is one to avoid creating multiple links starting at the same point.
		DimLink link = linkMapping.get(source);
		if (link == null)
		{
			link = new DimLink(source, linkType);
			linkMapping.put(source, link);
			linkList.add(link);
		}
		else
		{
			link.overwrite(linkType);
		}
		//Link created!
		linkWatcher.onCreated(link.toMessage());
		return link;
	}
	
	public IDimLink createChildLink(int x, int y, int z, IDimLink parent)
	{
		if (parent == null)
		{
			throw new IllegalArgumentException("parent cannot be null.");
		}
		
		return createChildLink(new Point4D(x, y, z, id), (DimLink) parent);
	}
	
	private IDimLink createChildLink(Point4D source, DimLink parent)
	{
		//To avoid having multiple links at a single point, if we find an existing link then we overwrite
		//its destination data instead of creating a new instance.
		
		DimLink link = linkMapping.get(source);
		if (link == null)
		{
			link = new DimLink(source, parent);
			linkMapping.put(source, link);
			linkList.add(link);
		}
		else
		{
			link.overwrite(parent);
		}
		//Link created!
		linkWatcher.onCreated(link.toMessage());
		return link;
	}

	public boolean deleteLink(IDimLink link)
	{
		if (link.source().getDimension() != id)
		{
			throw new IllegalArgumentException("Attempted to delete a link from another dimension.");
		}
		DimLink target = linkMapping.remove(link.source());
		if (target != null)
		{
			linkList.remove(target);
			//Raise deletion event
			linkWatcher.onDeleted(target.toKey());
			target.clear();
		}
		return (target != null);
	}

	public boolean deleteLink(int x, int y, int z)
	{
		Point4D location = new Point4D(x, y, z, id);
		DimLink target = linkMapping.remove(location);
		if (target != null)
		{
			linkList.remove(target);
			//Raise deletion event
			linkWatcher.onDeleted(target.toKey());
			target.clear();
		}
		return (target != null);
	}

	public IDimLink getLink(int x, int y, int z)
	{
		Point4D location = new Point4D(x, y, z, id);
		return linkMapping.get(location);
	}
	
	public IDimLink getLink(Point4D location)
	{
		if (location.getDimension() != id)
			return null;
		
		return linkMapping.get(location);
	}

	public ArrayList<IDimLink> getAllLinks()
	{
		ArrayList<IDimLink> results = new ArrayList<IDimLink>(linkMapping.size());
		results.addAll(linkMapping.values());
		return results;
	}
	
	public boolean isPocketDimension()
	{
		return (parent != null);
	}
	
	public boolean isDungeon()
	{
		return isDungeon;
	}
	
	public boolean isFilled()
	{
		return isFilled;
	}
	
	public void setFilled(boolean isFilled)
	{
		this.isFilled = isFilled;
		//Raise the dim update event
		dimWatcher.onUpdated(this.toMessage());
	}
	
	public int id()
	{
		return id;
	}
	
	public int depth()
	{
		return depth;
	}
	
	public int packDepth()
	{
		return packDepth;
	}
	
	public Point4D origin()
	{
		return origin;
	}
	
	public NewDimData parent()
	{
		return parent;
	}

	public NewDimData root()
	{
		return root;
	}
	
	public int orientation()
	{
		return orientation;
	}
	
	public DungeonData dungeon()
	{
		return dungeon;
	}
	
	public boolean isInitialized()
	{
		return (origin != null);
	}
	
	public int linkCount()
	{
		return linkMapping.size();
	}
	
	public Iterable<NewDimData> children()
	{
		return children;
	}
	
	public void initializeDungeon(int originX, int originY, int originZ, int orientation, IDimLink incoming, DungeonData dungeon)
	{
		if (!isDungeon)
		{
			throw new IllegalStateException("Cannot invoke initializeDungeon() on a non-dungeon dimension.");
		}
		if (isInitialized())
		{
			throw new IllegalStateException("The dimension has already been initialized.");
		}
		
		setDestination(incoming, originX, originY, originZ);
		this.origin = incoming.destination();
		this.orientation = orientation;
		this.dungeon = dungeon;
		this.packDepth = calculatePackDepth(parent, dungeon);
		//Raise the dim update event
		dimWatcher.onUpdated(this.toMessage());
	}
	
	private static int calculatePackDepth(NewDimData parent, DungeonData current)
	{
		DungeonData predecessor = parent.dungeon();
		if (current == null)
		{
			throw new IllegalArgumentException("current cannot be null.");
		}
		if (predecessor == null)
		{
			return 1;
		}
		
		DungeonPack predOwner = predecessor.dungeonType().Owner;
		DungeonPack currentOwner = current.dungeonType().Owner;
		if (currentOwner == null)
		{
			return 1;
		}
		if (predOwner == null)
		{
			return 1;
		}
		if (predOwner == currentOwner)
		{
			return parent.packDepth + 1;
		}
		else
		{
			return 1;
		}
	}

	public void initializePocket(int originX, int originY, int originZ, int orientation, IDimLink incoming)
	{
		if (!isPocketDimension())
		{
			throw new IllegalStateException("Cannot invoke initializePocket() on a non-pocket dimension.");
		}
		if (isInitialized())
		{
			throw new IllegalStateException("The dimension has already been initialized.");
		}
		
		setDestination(incoming, originX, originY, originZ);
		this.origin = incoming.destination();
		this.orientation = orientation;
		//Raise the dim update event
		dimWatcher.onUpdated(this.toMessage());
	}
	
	public void setDestination(IDimLink incoming, int x, int y, int z)
	{
		DimLink link = (DimLink) incoming;
		link.setDestination(x, y, z, this);
		//Raise update event
		linkWatcher.onUpdated(link.toMessage());
	}

	public IDimLink getRandomLink()
	{
		if (linkMapping.isEmpty())
		{
			throw new IllegalStateException("There are no links to select from in this dimension.");
		}
		if (linkList.size() > 1)
		{
			return linkList.get(random.nextInt(linkList.size()));
		}
		else
		{
			return linkList.get(0);
		}
	}
}