package StevenDimDoors.mod_pocketDim.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.TreeMap;

import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.watcher.ClientLinkData;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonData;
import StevenDimDoors.mod_pocketDim.dungeon.pack.DungeonPack;
import StevenDimDoors.mod_pocketDim.saving.IPackable;
import StevenDimDoors.mod_pocketDim.saving.PackedDimData;
import StevenDimDoors.mod_pocketDim.saving.PackedDungeonData;
import StevenDimDoors.mod_pocketDim.saving.PackedLinkData;
import StevenDimDoors.mod_pocketDim.saving.PackedLinkTail;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.watcher.ClientLinkData;
import StevenDimDoors.mod_pocketDim.watcher.IUpdateWatcher;

public abstract class NewDimData implements IPackable<PackedDimData>
{
	private static class InnerDimLink extends DimLink
	{
		public InnerDimLink(Point4D source, DimLink parent, int orientation, DDLock lock)
		{
			super(source, orientation, lock, parent);
		}
		
		public InnerDimLink(Point4D source, LinkType linkType, int orientation, DDLock lock)
		{
			super(source, orientation, lock, linkType);
		}

		public void setDestination(int x, int y, int z, NewDimData dimension)
		{
			tail.setDestination(new Point4D(x, y, z, dimension.id()));
		}
		
		public boolean overwrite(InnerDimLink nextParent,int orientation)
		{
			if (nextParent == null)
			{
				throw new IllegalArgumentException("nextParent cannot be null.");
			}			
			if (this == nextParent)
			{
				//Ignore this request silently
				return false;
			}
			if (nextParent.point.getDimension() != point.getDimension())
			{
				// Ban having children in other dimensions to avoid serialization issues with cross-dimensional tails
				throw new IllegalArgumentException("source and parent.source must have the same dimension.");
			}
			
			//Release children
			for (DimLink child : children)
			{
				((InnerDimLink) child).parent = null;
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
			this.orientation=orientation;
			return true;
		}
		
		public void overwrite(LinkType linkType, int orientation)
		{	
			//Release children
			for (DimLink child : children)
			{
				((InnerDimLink) child).parent = null;
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
			//Set new orientation
			this.orientation=orientation;
		}
		
		 /**
		  * only use this on the client to update errything
		  * @param lock
		  */
		public void setLock(DDLock lock)
		{
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
			this.lock = DDLock.generateLockKeyPair(itemStack, lockKey);
			return true;
		}
		
		public void removeLock(ItemStack itemStack, InnerDimLink link)
		{
			if(link.doesKeyUnlock(itemStack))
			{
				link.lock = null;
			}
		}
		
	}

	
	private static int EXPECTED_LINKS_PER_CHUNK = 2;
	protected static Random random = new Random();
	
	protected int id;
	protected Map<Point4D, InnerDimLink> linkMapping;
	protected List<InnerDimLink> linkList;
	protected boolean isFilled;
	protected int depth;
	protected int packDepth;
	protected DimensionType type;
	protected NewDimData parent;
	protected NewDimData root;
	protected List<NewDimData> children;
	protected Point4D origin;
	protected int orientation;
	protected DungeonData dungeon;
	protected boolean modified;
	public IUpdateWatcher<ClientLinkData> linkWatcher;
	

	// Don't write this field to a file - it should be recreated on startup
	private Map<ChunkCoordIntPair, List<InnerDimLink>> chunkMapping;
	
	protected NewDimData(int id, NewDimData parent, DimensionType type, IUpdateWatcher<ClientLinkData> linkWatcher)
	{
		if (type != DimensionType.ROOT && (parent == null))
		{
			throw new NullPointerException("Dimensions can be pocket dimensions if and only if they have a parent dimension.");
		}
		
		this.id = id;
		this.linkMapping = new TreeMap<Point4D, InnerDimLink>(); //Should be stored in oct tree -- temporary solution
		this.linkList = new ArrayList<InnerDimLink>(); //Should be stored in oct tree -- temporary solution
		this.children = new ArrayList<NewDimData>(); 
		this.parent = parent;
		this.packDepth = 0;
		this.type = type;
		this.isFilled = false;
		this.orientation = 0;
		this.origin = null;
		this.dungeon = null;
		this.linkWatcher = linkWatcher;
		this.chunkMapping = new HashMap<ChunkCoordIntPair, List<InnerDimLink>>();
		this.modified = true;
		
		//Register with parent
		if (parent != null)
		{
			//We don't need to raise an update event for adding a child because the child's creation will be signaled.
			this.root = parent.root;
			this.depth = parent.depth + 1;
			parent.children.add(this);
			parent.modified = true;
		}
		else
		{
			this.root = this;
			this.depth = 0;
		}
	}
	
	protected NewDimData(int id, NewDimData root, DimensionType type)
	{
		// This constructor is meant for client-side code only
		if (root == null)
		{
			throw new IllegalArgumentException("root cannot be null.");
		}
		
		this.id = id;
		this.linkMapping = new TreeMap<Point4D, InnerDimLink>(); //Should be stored in oct tree -- temporary solution
		this.linkList = new ArrayList<InnerDimLink>(); //Should be stored in oct tree -- temporary solution
		this.children = new ArrayList<NewDimData>(); 
		this.parent = null;
		this.packDepth = 0;
		this.type = type;
		this.isFilled = false;
		this.orientation = 0;
		this.origin = null;
		this.dungeon = null;
		this.linkWatcher = null;
		this.depth = 0;
		this.root = root;
		this.chunkMapping = null;
	}
	

	public DimLink findNearestRift(World world, int range, int x, int y, int z)
	{
		// Sanity check...
		if (world.provider.dimensionId != id)
		{
			throw new IllegalArgumentException("Attempted to search for links in a World instance for a different dimension!");
		}
		
		// Note: Only detect rifts at a distance > 0, so we ignore the rift
		// at the center of the search space.
		DimLink link;
		DimLink nearest = null;

		int i, j, k;
		int distance;
		int minDistance = Integer.MAX_VALUE;
		DDProperties properties = DDProperties.instance();

		for (i = -range; i <= range; i++)
		{
			for (j = -range; j <= range; j++)
			{
				for (k = -range; k <= range; k++)
				{
					distance = getAbsoluteSum(i, j, k);
					if (distance > 0 && distance < minDistance && world.getBlock(x + i, y + j, z + k) == mod_pocketDim.blockRift)
					{
						link = getLink(x + i, y + j, z + k);
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
	
	public ArrayList<DimLink> findRiftsInRange(World world, int range, int x, int y, int z)
	{
		// Sanity check...
		if (world.provider.dimensionId != id)
		{
			throw new IllegalArgumentException("Attempted to search for links in a World instance for a different dimension!");
		}

		// Note: Only detect rifts at a distance > 0, so we ignore the rift
		// at the center of the search space.
		int i, j, k;
		int distance;
		DimLink link;
		DDProperties properties = DDProperties.instance();
		ArrayList<DimLink> links = new ArrayList<DimLink>();
		
		for (i = -range; i <= range; i++)
		{
			for (j = -range; j <= range; j++)
			{
				for (k = -range; k <= range; k++)
				{
					distance = getAbsoluteSum(i, j, k);
					if (distance > 0 && world.getBlock(x + i, y + j, z + k) == mod_pocketDim.blockRift)
					{
						link = getLink(x + i, y + j, z + k);
						if (link != null)
						{
							links.add(link);
						}
					}
				}
			}
		}

		return links;
	}
	
	private static int getAbsoluteSum(int i, int j, int k)
	{
		return Math.abs(i) + Math.abs(j) + Math.abs(k);
	}
	
	public DimLink createLink(int x, int y, int z, LinkType linkType, int orientation)
	{
		return createLink(new Point4D(x, y, z, id), linkType, orientation, null);
	}
	
	public DimLink createLink(Point4D source, LinkType linkType, int orientation, DDLock locked)
	{
		// Return an existing link if there is one to avoid creating multiple links starting at the same point.
		InnerDimLink link = linkMapping.get(source);
		if (link == null)
		{
			link = new InnerDimLink(source, linkType, orientation, locked);
			linkMapping.put(source, link);
			linkList.add(link);
			
			// If this code is running on the server side, add this link to chunkMapping.
			if (linkType != LinkType.CLIENT)
			{
				ChunkCoordIntPair chunk = link.getChunkCoordinates();
				List<InnerDimLink> chunkLinks = chunkMapping.get(chunk);
				if (chunkLinks == null)
				{
					chunkLinks = new ArrayList<InnerDimLink>(EXPECTED_LINKS_PER_CHUNK);
					chunkMapping.put(chunk, chunkLinks);
				}
				chunkLinks.add(link);
			}
		}
		else
		{
			link.overwrite(linkType, orientation);
		}
		modified = true;
		
		//Link created!
		if (linkType != LinkType.CLIENT)

		{
			linkWatcher.onCreated(new ClientLinkData(link));
		}
		return link;
	}
	
	public DimLink createChildLink(int x, int y, int z, DimLink parent)
	{
		return createChildLink(new Point4D(x, y, z, id), (InnerDimLink) parent, null);
	}
	
	public DimLink createChildLink(Point4D source, DimLink parent, DDLock locked)
	{
		// To avoid having multiple links at a single point, if we find an existing link then we overwrite
		// its destination data instead of creating a new instance.
		
		if (parent == null)
		{
			throw new IllegalArgumentException("parent cannot be null.");
		}
		InnerDimLink link = linkMapping.get(source);
		if (link == null)
		{
			link = new InnerDimLink(source, parent, parent.orientation, locked);
			linkMapping.put(source, link);
			linkList.add(link);
			

			// If this code is running on the server side, add this link to chunkMapping.
			// Granted, the client side code should never create child links anyway...
			if (link.linkType() != LinkType.CLIENT)
			{
				ChunkCoordIntPair chunk = link.getChunkCoordinates();
				List<InnerDimLink> chunkLinks = chunkMapping.get(chunk);
				if (chunkLinks == null)
				{
					chunkLinks = new ArrayList<InnerDimLink>(EXPECTED_LINKS_PER_CHUNK);
					chunkMapping.put(chunk, chunkLinks);
				}
				chunkLinks.add(link);
			}
			
			// Link created!
			linkWatcher.onCreated(new ClientLinkData(link));
		}
		else
		{
			if (link.overwrite((InnerDimLink) parent, parent.orientation))
			{
				//Link created!
				linkWatcher.onCreated(new ClientLinkData(link));
			}
		}
		modified = true;
		return link;
	}

	public boolean deleteLink(DimLink link)
	{
		if (link.source().getDimension() != id)
		{
			throw new IllegalArgumentException("Attempted to delete a link from another dimension.");
		}
		InnerDimLink target = linkMapping.remove(link.source());
		if (target != null)
		{
			linkList.remove(target);

			// If this code is running on the server side, remove this link to chunkMapping.
			if (link.linkType() != LinkType.CLIENT)
			{
				ChunkCoordIntPair chunk = target.getChunkCoordinates();
				List<InnerDimLink> chunkLinks = chunkMapping.get(chunk);
				if (chunkLinks != null)
				{
					chunkLinks.remove(target);
				}
			}
			
			// Raise deletion event
            if (linkWatcher != null)
			    linkWatcher.onDeleted(new ClientLinkData(link));
			target.clear();
			modified = true;
		}
		return (target != null);
	}

	public boolean deleteLink(int x, int y, int z)
	{
		return this.deleteLink(this.getLink(x, y, z));
	}
	public boolean deleteLink(Point4D location)
	{
		return this.deleteLink(this.getLink(location));
	}

	public DimLink getLink(int x, int y, int z)
	{
		Point4D location = new Point4D(x, y, z, id);
		return linkMapping.get(location);
	}
	
	public DimLink getLink(Point3D location)
	{
		return linkMapping.get(new Point4D(location.getX(), location.getY(), location.getZ(), this.id));
	}
	
	public DimLink getLink(Point4D location)
	{
		if (location.getDimension() != id)
			return null;
		
		return linkMapping.get(location);
	}

	public ArrayList<DimLink> getAllLinks()
	{
		ArrayList<DimLink> results = new ArrayList<DimLink>(linkMapping.size());
		results.addAll(linkMapping.values());
		return results;
	}
	
	public boolean isPocketDimension()
	{
		return (root != this);
	}
	
	public DimensionType type()
	{
		return this.type;
	}
	public boolean isFilled()
	{
		return isFilled;
	}
	
	public void setFilled(boolean isFilled)
	{
		this.isFilled = isFilled;
		this.modified = true;
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
		return linkList.size();
	}
	
	public Iterable<NewDimData> children()
	{
		return children;
	}
	
	public Iterable<? extends DimLink> links()
	{
		return linkList;
	}
	
	public void initializeDungeon(int originX, int originY, int originZ, int orientation, DimLink incoming, DungeonData dungeon)
	{
		if (this.type != DimensionType.DUNGEON)
		{
			throw new IllegalStateException("Cannot invoke initializeDungeon() on a non-dungeon dimension.");
		}
		if (isInitialized())
		{
			throw new IllegalStateException("The dimension has already been initialized.");
		}
		if (orientation < 0 || orientation > 3)
		{
			throw new IllegalArgumentException("orientation must be between 0 and 3, inclusive.");
		}
		setLinkDestination(incoming, originX, originY, originZ);
		this.origin = incoming.destination();
		this.orientation = orientation;
		this.dungeon = dungeon;
		this.packDepth = calculatePackDepth(parent, dungeon);
		this.modified = true;
	}
	
	/**
	 * Effectively moves the dungeon to the 'top' of a chain as far as dungeon generation is concerned. 
	 */
	public void setParentToRoot()
	{
		// Update this dimension's information
		if (parent != null)
		{
			parent.children.remove(this);
		}
		this.depth = 1;
		this.parent = this.root;
		this.root.children.add(this);
		this.root.modified = true;
		this.modified = true;
		if (this.type == DimensionType.DUNGEON)
		{
			this.packDepth = calculatePackDepth(this.parent, this.dungeon);
		}
		
		// Update the depths for child dimensions using a depth-first traversal
		Stack<NewDimData> ordering = new Stack<NewDimData>();
		ordering.addAll(this.children);
		
		while (!ordering.isEmpty())
		{
			NewDimData current = ordering.pop();
			current.resetDepth();
			ordering.addAll(current.children);
		}
	}
	
	private void resetDepth()
	{
		// We assume that this is only applied to dimensions with parents
		this.depth = this.parent.depth + 1;
		if (this.type == DimensionType.DUNGEON)
		{
			this.packDepth = calculatePackDepth(this.parent, this.dungeon);
		}
		this.modified = true;
	}
	
	public static int calculatePackDepth(NewDimData parent, DungeonData current)
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
		return 1;
	}

	public void initializePocket(int originX, int originY, int originZ, int orientation, DimLink incoming)
	{
		if (!isPocketDimension())
		{
			throw new IllegalStateException("Cannot invoke initializePocket() on a non-pocket dimension.");
		}
		if (isInitialized())
		{
			throw new IllegalStateException("The dimension has already been initialized.");
		}
		
		setLinkDestination(incoming, originX, originY, originZ);
		this.origin = incoming.destination();
		this.orientation = orientation;
		this.modified = true;
	}
	
	public void setLinkDestination(DimLink incoming, int x, int y, int z)
	{
		InnerDimLink link = (InnerDimLink) incoming;
		link.setDestination(x, y, z, this);
		this.modified = true;
	}
	
	public void lock(DimLink link, boolean locked)
	{
		InnerDimLink innerLink = (InnerDimLink)link;
		innerLink.lock.setLockState(locked);
		modified = true;
	}
	
	public void setLock(DimLink link, DDLock lock)
	{
		InnerDimLink innerLink = (InnerDimLink)link;
		innerLink.setLock(lock);
		modified = true;
	}
	
	public void createLock(DimLink link, ItemStack item, int lockKey)
	{
		InnerDimLink innerLink = (InnerDimLink)link;
		innerLink.createLock(item, lockKey);
		modified = true;
	}
	
	public void removeLock(DimLink link, ItemStack item)
	{
		InnerDimLink innerLink = (InnerDimLink)link;
		innerLink.removeLock(item, innerLink);
		modified = true;
	}

	public DimLink getRandomLink()
	{
		if (linkMapping.isEmpty())
		{
			throw new IllegalStateException("There are no links to select from in this dimension.");
		}
		if (linkList.size() > 1)
		{
			return linkList.get(random.nextInt(linkList.size()));
		}
		return linkList.get(0);
	}
	
	public Iterable<? extends DimLink> getChunkLinks(int chunkX, int chunkZ)
	{
		List<InnerDimLink> chunkLinks = chunkMapping.get(new ChunkCoordIntPair(chunkX, chunkZ));
		if (chunkLinks != null)
		{
			return chunkLinks;
		}
		return new ArrayList<InnerDimLink>(0);
	}
	
	public boolean isModified()
	{
		return modified;
	}
	
	public void clearModified()
	{
		this.modified = false;
	}
	
	public void clear()
	{
		// If this dimension has a parent, remove it from its parent's list of children
		if (parent != null)
		{
			parent.children.remove(this);
		}
		// Remove this dimension as the parent of its children
		for (NewDimData child : children)
		{
			child.parent = null;
		}
		// Clear all fields
		id = Integer.MIN_VALUE;
		linkMapping.clear();
		linkMapping = null;
		linkList.clear();
		linkList = null;
		children.clear();
		children = null;
		type = null;
		isFilled = false;
		depth = Integer.MIN_VALUE;
		packDepth = Integer.MIN_VALUE;
		origin = null;
		orientation = Integer.MIN_VALUE;
		dungeon = null;
		linkWatcher = null;
	}
	
	public PackedDimData pack()
	{
		ArrayList<Integer> ChildIDs = new ArrayList<Integer>();
		ArrayList<PackedLinkData> Links = new ArrayList<PackedLinkData>();
		ArrayList<PackedLinkTail> Tails = new ArrayList<PackedLinkTail>();
		PackedDungeonData packedDungeon=null; 
		
		if(this.dungeon!=null)
		{
			packedDungeon= new PackedDungeonData(dungeon.weight(), dungeon.isOpen(), dungeon.isInternal(), 
					dungeon.schematicPath(), dungeon.schematicName(), dungeon.dungeonType().Name, 
					dungeon.dungeonType().Owner.getName());
		}
		//Make a list of children
		for(NewDimData data : this.children)
		{
			ChildIDs.add(data.id);
		}
		for(DimLink link:this.links())
		{
			ArrayList<Point3D> children = new ArrayList<Point3D>();
			Point3D parentPoint = new Point3D(-1,-1,-1);
			if(link.parent!=null)
			{
				parentPoint=link.parent.point.toPoint3D();
			}
			
			for(DimLink childLink : link.children)
			{
				children.add(childLink.source().toPoint3D());
			}
			PackedLinkTail tail = new PackedLinkTail(link.tail.getDestination(),link.tail.getLinkType());
			Links.add(new PackedLinkData(link.point,parentPoint,tail,link.orientation,children,link.lock));
			
			PackedLinkTail tempTail = new PackedLinkTail(link.tail.getDestination(),link.tail.getLinkType());
			if(Tails.contains(tempTail))
			{
				Tails.add(tempTail);
			}
			
			
		}
		int parentID=this.id;
		Point3D originPoint=new Point3D(0,0,0);
		if(this.parent!=null)
		{
			parentID = this.parent.id;
		}
		if(this.origin!=null)
		{
			originPoint=this.origin.toPoint3D();
		}
		return new PackedDimData(this.id, depth, this.packDepth, parentID, this.root().id(), orientation, 
									type, isFilled,packedDungeon, originPoint, ChildIDs, Links, Tails);
		// FIXME: IMPLEMENTATION PLZTHX
		//I tried
	}
	
	@Override
	public String name()
	{
		return String.valueOf(id);
	}

	@Override
	public String toString()
	{
		return "DimID= " + this.id;
	}
}