package StevenDimDoors.mod_pocketDim.world.fortresses;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import net.minecraft.world.gen.structure.StructureNetherBridgePieces;
import net.minecraft.world.gen.structure.StructureStart;

public class DDStructureNetherBridgeStart extends StructureStart
{
	public static final int MAX_GATEWAY_GENERATION_CHANCE = 100;
	
	private boolean hasGateway;
	private int minX;
	private int minY;
	private int minZ;
	
	public DDStructureNetherBridgeStart() { }
	
	public DDStructureNetherBridgeStart(World world, Random random, int chunkX, int chunkZ, DDProperties properties)
    {
		// StructureNetherBridgeStart handles designing the fortress for us
    	super(chunkX, chunkZ);
    	
    	Iterator componentIterator;
    	StructureComponent component;
    	StructureBoundingBox bounds;
    	ArrayList<StructureNetherBridgePieces.Throne> spawnerRooms;
    	hasGateway = false;
    	
    	// Randomly decide whether to build a gateway in this fortress
    	if (random.nextInt(MAX_GATEWAY_GENERATION_CHANCE) < properties.FortressGatewayGenerationChance)
    	{
    		// Search for all the blaze spawners in a fortress
    		spawnerRooms = new ArrayList<StructureNetherBridgePieces.Throne>();
    		componentIterator = this.components.iterator();
    		while (componentIterator.hasNext())
    		{
    			component = (StructureComponent) componentIterator.next();
    			if (component instanceof StructureNetherBridgePieces.Throne)
    			{
    				spawnerRooms.add((StructureNetherBridgePieces.Throne) component);
    			}
    		}
    		
    		// If any spawner rooms were found, choose one to randomly replace
    		if (!spawnerRooms.isEmpty())
    		{
    			hasGateway = true;
    			component = spawnerRooms.get(random.nextInt(spawnerRooms.size()));
    			// Store enough data to identify the room when it's going to be built later
    			bounds = component.getBoundingBox();
    			minX = bounds.minX;
    			minY = bounds.minY;
    			minZ = bounds.minZ;
    		}
    	}
    }
    
	@Override
	public NBTTagCompound func_143021_a(int chunkX, int chunkZ)
    {
		// We override the function for writing NBT data to add our own gateway data
		NBTTagCompound fortressTag = super.func_143021_a(chunkX, chunkZ);
		
		// Add a compound tag with our data
		NBTTagCompound dimensionalTag = new NBTTagCompound();
		dimensionalTag.setBoolean("HasGateway", this.hasGateway);
		if (hasGateway)
		{
			dimensionalTag.setInteger("GatewayMinX", this.minX);
			dimensionalTag.setInteger("GatewayMinY", this.minY);
			dimensionalTag.setInteger("GatewayMinZ", this.minZ);
		}
		fortressTag.setTag("DimensionalDoors", dimensionalTag);
		
		return fortressTag;
    }

	@Override
    public void func_143020_a(World world, NBTTagCompound fortressTag)
    {
		// We override the function for reading NBT data to load gateway data
		super.func_143020_a(world, fortressTag);
		
        NBTTagCompound dimensionalTag = fortressTag.getCompoundTag("DimensionalDoors");
        if (dimensionalTag != null)
        {
        	this.hasGateway = dimensionalTag.getBoolean("HasGateway");
        	if (hasGateway)
        	{
        		minX = dimensionalTag.getInteger("GatewayMinX");
    			minY = dimensionalTag.getInteger("GatewayMinY");
    			minZ = dimensionalTag.getInteger("GatewayMinZ");
        	}
        }
    }
	
    /**
     * Keeps iterating Structure Pieces and spawning them until the checks tell it to stop
     */
    @Override
	public void generateStructure(World world, Random random, StructureBoundingBox generationBounds)
    {
    	if (hasGateway)
    	{
    		// Use a modified version of Vanilla's fortress generation code
    		// Try to detect the room that we intend to replace with our gateway
    		Iterator iterator = this.components.iterator();

            while (iterator.hasNext())
            {
                StructureComponent component = (StructureComponent)iterator.next();
                StructureBoundingBox bounds = component.getBoundingBox();
                
                if (bounds.intersectsWith(generationBounds))
                {
                	// Check if this is our replacement target
                	// Checking the location is enough because structures aren't allowed to have
                	// intersecting bounding boxes - nothing else can have these min coordinates.
                	if (bounds.minX == this.minX && bounds.minY == this.minY && bounds.minZ == this.minZ)
                	{
                		component = ComponentNetherGateway.createFromComponent(component, random);
                	}
                	// Now for the last bit of Vanilla's generation code
                	if (!component.addComponentParts(world, random, generationBounds))
                    {
                        iterator.remove();
                    }
                }
            }
    	}
    	else
    	{
    		// Just run the usual structure generation
    		super.generateStructure(world, random, generationBounds);
    	}
    }
}
