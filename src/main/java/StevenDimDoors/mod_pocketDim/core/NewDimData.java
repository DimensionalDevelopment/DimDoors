package StevenDimDoors.mod_pocketDim.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.TreeMap;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonData;
import StevenDimDoors.mod_pocketDim.dungeon.pack.DungeonPack;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.watcher.ClientLinkData;
import StevenDimDoors.mod_pocketDim.watcher.IUpdateWatcher;

public abstract class NewDimData
{
	private static class InnerDimLink extends DimLink
	{
		public InnerDimLink(Point4D source, DimLink parent, int orientation)
		{
			super(new ClientLinkData(source, orientation), parent);
		}
		
		public InnerDimLink(Point4D source, int linkType, int orientation)
		{
			super(new ClientLinkData(source, orientation), linkType);
		}

		public void setDestination(int x, int y, int z, NewDimData dimension)
		{
			tail.setDestination(new Point4D(x, y, z, dimension.id()));
		}
		
		public void clear()
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
			
			parent = null;
			link = null;
			tail = new LinkTail(0, null);
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
			if (nextParent.link.point.getDimension() != link.point.getDimension())
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
			this.link.orientation=orientation;
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
			this.link.orientation=orientation;
		}
	}
	
	private static int EXPECTED_LINKS_PER_CHUNK = 2;
	
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
	
	// Don't write this field to a file - it should be recreated on startup
	private Map<ChunkCoordIntPair, List<InnerDimLink>> chunkMapping;
	
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
					if (distance > 0 && distance < minDistance && world.getBlockId(x + i, y + j, z + k) == properties.RiftBlockID)
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
					if (distance > 0 && world.getBlockId(x + i, y + j, z + k) == properties.RiftBlockID)
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
	
	public DimLink createLink(int x, int y, int z, int linkType, int orientation)
	{
		return createLink(new Point4D(x, y, z, id), linkType, orientation);
	}
	
	public DimLink createLink(Point4D source, int linkType, int orientation)
	{
		// Return an existing link if there is one to avoid creating multiple links starting at the same point.
		InnerDimLink link = linkMapping.get(source);
		if (link == null)
		{
			link = new InnerDimLink(source, linkType, orientation);
			linkMapping.put(source, link);
			linkList.add(link);
			
			// If this code is running on the server side, add this link to chunkMapping.
			if (linkType != LinkTypes.CLIENT_SIDE)
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
		
		// Link created!
		if (linkType != LinkTypes.CLIENT_SIDE)
		{
			linkWatcher.onCreated(link.link);
		}
		return link;
	}
	
	public DimLink createChildLink(int x, int y, int z, DimLink parent)
	{
		if (parent == null)
		{
			throw new IllegalArgumentException("parent cannot be null.");
		}
		
		return createChildLink(new Point4D(x, y, z, id), (InnerDimLink) parent);
	}
	
	private DimLink createChildLink(Point4D source, InnerDimLink parent)
	{
		// To avoid having multiple links at a single point, if we find an existing link then we overwrite
		// its destination data instead of creating a new instance.
		
		InnerDimLink link = linkMapping.get(source);
		if (link == null)
		{
			link = new InnerDimLink(source, parent, parent.link.orientation);
			linkMapping.put(source, link);
			linkList.add(link);
			
			// If this code is running on the server side, add this link to chunkMapping.
			// Granted, the client side code should never create child links anyway...
			if (link.linkType() != LinkTypes.CLIENT_SIDE)
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
			linkWatcher.onCreated(link.link);
		}
		else
		{
			if (link.overwrite(parent, parent.link.orientation))
			{
				// Link created!
				linkWatcher.onCreated(link.link);
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
			if (link.linkType() != LinkTypes.CLIENT_SIDE)
			{
				ChunkCoordIntPair chunk = target.getChunkCoordinates();
				List<InnerDimLink> chunkLinks = chunkMapping.get(chunk);
				if (chunkLinks != null)
				{
					chunkLinks.remove(target);
				}
			}
			
			// Raise deletion event
			linkWatcher.onDeleted(target.link);
			target.clear();
			modified = true;
		}
		return (target != null);
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
		if (this.isDungeon)
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
		if (this.isDungeon)
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
	
	@Override
	public String toString()
	{
		return "DimID= " + this.id;
	}
}