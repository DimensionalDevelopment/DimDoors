package StevenDimDoors.mod_pocketDim.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import StevenDimDoors.mod_pocketDim.watcher.ClientLinkData;
import net.minecraft.item.ItemStack;
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
import StevenDimDoors.mod_pocketDim.watcher.IUpdateWatcher;

public abstract class NewDimData implements IPackable<PackedDimData>
{
	private static class InnerDimLink extends DimLink
	{
		public InnerDimLink(Point4D source, DimLink parent, int orientation, DDLock lock)
		{
			super(source, orientation, lock, parent);
		}
		
		public InnerDimLink(Point4D source, int linkType, int orientation, DDLock lock)
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
		
		public void overwrite(int linkType, int orientation)
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
			this.lock = DDLock.createLock(itemStack, lockKey);
			return true;
		}
		
	}
	protected static Random random = new Random();
	
	protected int id;
	protected Map<Point4D, InnerDimLink> linkMapping;
	protected List<InnerDimLink> linkList;
	protected boolean isDungeon;
	protected boolean isFilled;
	protected int depth;
	protected int packDepth;
	protected NewDimData parent;
	protected NewDimData root;
	protected List<NewDimData> children;
	protected Point4D origin;
	protected int orientation;
	protected DungeonData dungeon;
	protected boolean modified;
	public IUpdateWatcher<ClientLinkData> linkWatcher;
	
	protected NewDimData(int id, NewDimData parent, boolean isPocket, boolean isDungeon,
		IUpdateWatcher<ClientLinkData> linkWatcher)
	{
		// The isPocket flag is redundant. It's meant as an integrity safeguard.
		if (isPocket && (parent == null))
		{
			throw new NullPointerException("Dimensions can be pocket dimensions if and only if they have a parent dimension.");
		}
		if (isDungeon && !isPocket)
		{
			throw new IllegalArgumentException("A dimensional dungeon must also be a pocket dimension.");
		}
		
		this.id = id;
		this.linkMapping = new TreeMap<Point4D, InnerDimLink>(); //Should be stored in oct tree -- temporary solution
		this.linkList = new ArrayList<InnerDimLink>(); //Should be stored in oct tree -- temporary solution
		this.children = new ArrayList<NewDimData>(); 
		this.parent = parent;
		this.packDepth = 0;
		this.isDungeon = isDungeon;
		this.isFilled = false;
		this.orientation = 0;
		this.origin = null;
		this.dungeon = null;
		this.linkWatcher = linkWatcher;
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
	
	protected NewDimData(int id, NewDimData root)
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
		this.isDungeon = false;
		this.isFilled = false;
		this.orientation = 0;
		this.origin = null;
		this.dungeon = null;
		this.linkWatcher = null;
		this.depth = 0;
		this.root = root;
	}
	

	public DimLink findNearestRift(World world, int range, int x, int y, int z)
	{
		//TODO: Rewrite this later to use an octtree

		//Sanity check...
		if (world.provider.dimensionId != id)
		{
			throw new IllegalArgumentException("Attempted to search for links in a World instance for a different dimension!");
		}
		
		//Note: Only detect rifts at a distance > 1, so we ignore the rift
		//that called this function and any adjacent rifts.
		
		DimLink nearest = null;
		DimLink link;
		
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
					if (distance > 0 && distance < minDistance && world.getBlockId(x + i, y + j, z + k) == properties.RiftBlockID)
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
	
	public ArrayList<DimLink> findRiftsInRange(World world, int range, int x, int y, int z)
	{
		ArrayList<DimLink> links = new ArrayList<DimLink>();
		//TODO: Rewrite this later to use an octtree

		//Sanity check...
		if (world.provider.dimensionId != id)
		{
			throw new IllegalArgumentException("Attempted to search for links in a World instance for a different dimension!");
		}
		
		//Note: Only detect rifts at a distance > 1, so we ignore the rift
		//that called this function and any adjacent rifts.
		
		DimLink link;
		
		int distance;
		int i, j, k;
		DDProperties properties = DDProperties.instance();

		for (i = -range; i <= range; i++)
		{
			for (j = -range; j <= range; j++)
			{
				for (k = -range; k <= range; k++)
				{
					distance = getAbsoluteSum(i, j, k);
					if (distance > 0 && world.getBlockId(x + i, y + j, z + k) == properties.RiftBlockID)
					{
						link = getLink(x+i, y+j, z+k);
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
	
	public DimLink createLink(int x, int y, int z, int linkType, int orientation)
	{
		return createLink(new Point4D(x, y, z, id), linkType, orientation, null);
	}
	
	public DimLink createLink(Point4D source, int linkType, int orientation, DDLock locked)
	{
		//Return an existing link if there is one to avoid creating multiple links starting at the same point.
		InnerDimLink link = linkMapping.get(source);
		if (link == null)
		{
			link = new InnerDimLink(source, linkType, orientation, locked);
			linkMapping.put(source, link);
			linkList.add(link);
		}
		else
		{
			link.overwrite(linkType, orientation);
		}
		modified = true;
		
		//Link created!
		if (linkType != LinkTypes.CLIENT_SIDE)
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
		//To avoid having multiple links at a single point, if we find an existing link then we overwrite
		//its destination data instead of creating a new instance.
		
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
			
			//Link created!
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
			//Raise deletion event
			linkWatcher.onDeleted(new ClientLinkData(link));
			target.clear();
			modified = true;
		}
		return (target != null);
	}

	public boolean deleteLink(int x, int y, int z)
	{
		Point4D location = new Point4D(x, y, z, id);
		return this.deleteLink(this.getLink(location));
	}

	public DimLink getLink(int x, int y, int z)
	{
		Point4D location = new Point4D(x, y, z, id);
		return linkMapping.get(location);
	}
	
	public DimLink getLink(Point3D location)
	{
		return linkMapping.get(new Point4D(location.getX(),location.getY(),location.getZ(),this.id));
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
		if (!isDungeon)
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
		setDestination(incoming, originX, originY, originZ);
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
		this.depth = 1;
		this.parent = this.root;
		this.root.children.add(this);
		this.root.modified = true;
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
		else
		{
			return 1;
		}
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
		
		setDestination(incoming, originX, originY, originZ);
		this.origin = incoming.destination();
		this.orientation = orientation;
		this.modified = true;
	}
	
	public void setDestination(DimLink incoming, int x, int y, int z)
	{
		InnerDimLink link = (InnerDimLink) incoming;
		link.setDestination(x, y, z, this);
		this.modified = true;
	}
	
	public void lock(DimLink link, boolean locked)
	{
		InnerDimLink innerLink = (InnerDimLink)link;
		innerLink.lock.lock(locked);
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
		else
		{
			return linkList.get(0);
		}
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
		isDungeon = false;
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
									isDungeon, isFilled,packedDungeon, originPoint, ChildIDs, Links, Tails);
		// FIXME: IMPLEMENTATION PLZTHX
		//I tried
	}
	
	@Override
	public String name()
	{
		return String.valueOf(id);
	}

	
	public String toString()
	{
		return "DimID= " + this.id;
	}
}