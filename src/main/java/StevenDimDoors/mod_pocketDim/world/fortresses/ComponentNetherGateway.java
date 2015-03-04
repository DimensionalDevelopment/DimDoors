package StevenDimDoors.mod_pocketDim.world.fortresses;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkType;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

public class ComponentNetherGateway extends StructureComponent
{
	// Note: In this case, it doesn't really matter which class we extend, since this class will
	// never be passed to Minecraft. We just need an instance to have access to structure-building methods.
	// If Forge supports adding custom fortress structures in the future, then we might have to change
	// our class to extend ComponentNetherBridgeCrossing or something along those lines. ~SenseiKiwi
	
    public ComponentNetherGateway(int componentType, Random random, StructureBoundingBox bounds, int coordBaseMode)
    {
        super(componentType);
        
        this.boundingBox = bounds;
        this.coordBaseMode = coordBaseMode;
    }

    /**
     * Creates and returns a new component piece. Or null if it could not find enough room to place it.
     */
    public static ComponentNetherGateway createValidComponent(List components, Random random, int minX, int minY, int minZ, int coordBaseMode, int componentType)
    {
        StructureBoundingBox bounds = StructureBoundingBox.getComponentToAddBoundingBox(minX, minY, minZ, -2, 0, 0, 7, 9, 7, coordBaseMode);
        return isAboveGround(bounds) && StructureComponent.findIntersecting(components, bounds) == null ? new ComponentNetherGateway(componentType, random, bounds, coordBaseMode) : null;
    }
    
    public static ComponentNetherGateway createFromComponent(StructureComponent component, Random random)
    {
    	// Create an instance of our gateway component using the same data as another component,
    	// likely a component that we intend to replace during generation
    	return new ComponentNetherGateway( component.getComponentType(), random,
    			component.getBoundingBox(), getCoordBaseMode(component));
    }
    
    private static int getCoordBaseMode(StructureComponent component)
    {
    	// This is a hack to get the value of a component's coordBaseMode field.
    	// It's essentially the orientation of the component... with a weird name.
    	return component.func_143010_b().getInteger("O");
    }

    /**
     * Checks if the bounding box's minY is > 10
     */
    protected static boolean isAboveGround(StructureBoundingBox par0StructureBoundingBox)
    {
        return par0StructureBoundingBox != null && par0StructureBoundingBox.minY > 10;
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...
     */
    @Override
	public boolean addComponentParts(World world, Random random, StructureBoundingBox bounds)
    {
    	int NETHER_SLAB_METADATA = 6;
        
        // Set all the blocks in the area of the room to air
        this.fillWithBlocks(world, bounds, 0, 2, 0, 6, 6, 6, Blocks.air, Blocks.air, false);
        // Set up the platform under the gateway
        this.fillWithBlocks(world, bounds, 0, 0, 0, 6, 1, 6, Blocks.nether_brick, Blocks.nether_brick, false);
        
        // Build the fence at the back of the room
        this.fillWithBlocks(world, bounds, 1, 2, 6, 5, 2, 6, Blocks.nether_brick, Blocks.nether_brick, false);
        this.fillWithBlocks(world, bounds, 1, 3, 6, 5, 3, 6, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);

        // Build the fences at the sides of the room
        this.fillWithBlocks(world, bounds, 0, 2, 0, 0, 2, 6, Blocks.nether_brick, Blocks.nether_brick, false);
        this.fillWithBlocks(world, bounds, 0, 3, 0, 0, 3, 6, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
        
        this.fillWithBlocks(world, bounds, 6, 2, 0, 6, 2, 6, Blocks.nether_brick, Blocks.nether_brick, false);
        this.fillWithBlocks(world, bounds, 6, 3, 0, 6, 3, 6, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
        
        // Build the fence portions closest to the entrance
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick, 0, 1, 2, 0, bounds);
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 1, 3, 0, bounds);
        
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick, 0, 5, 2, 0, bounds);
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 5, 3, 0, bounds);
        
        // Build the first layer of the gateway
        this.fillWithBlocks(world, bounds, 1, 2, 2, 5, 2, 5, Blocks.nether_brick, Blocks.nether_brick, false);
        this.fillWithMetadataBlocks(world, bounds, 1, 2, 1, 5, 2, 1, Blocks.stone_slab, NETHER_SLAB_METADATA, Blocks.stone_slab, NETHER_SLAB_METADATA, false);
        
        this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, NETHER_SLAB_METADATA, 1, 2, 2, bounds);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, NETHER_SLAB_METADATA, 5, 2, 2, bounds);
        
        // Build the second layer of the gateway
        int orientation = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 2);
        this.fillWithBlocks(world, bounds, 2, 3, 3, 2, 3, 4, Blocks.nether_brick, Blocks.nether_brick, false);
        this.fillWithBlocks(world, bounds, 4, 3, 3, 4, 3, 4, Blocks.nether_brick, Blocks.nether_brick, false);
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick, 0, 3, 3, 4, bounds);
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, orientation, 3, 3, 5, bounds);
        
        // Build the third layer of the gateway
        // We add 4 to get the rotated metadata for upside-down stairs
        // because Minecraft only supports metadata rotations for normal stairs -_-
        this.fillWithMetadataBlocks(world, bounds, 2, 4, 4, 4, 4, 4, Blocks.nether_brick_stairs, orientation, Blocks.nether_brick_stairs, orientation, false);
        
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.getMetadataWithOffset(Blocks.nether_brick_stairs, 0) + 4, 2, 4, 3, bounds);
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.getMetadataWithOffset(Blocks.nether_brick_stairs, 1) + 4, 4, 4, 3, bounds);
        
        // Build the fourth layer of the gateway
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick, 0, 3, 5, 3, bounds);
        
        this.placeBlockAtCurrentPosition(world, Blocks.netherrack, 0, 2, 5, 3, bounds);
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.getMetadataWithOffset(Blocks.nether_brick_stairs, 0) + 4, 1, 5, 3, bounds);
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.getMetadataWithOffset(Blocks.nether_brick_stairs, 3) + 4, 2, 5, 2, bounds);
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.getMetadataWithOffset(Blocks.nether_brick_stairs, 2) + 4, 2, 5, 4, bounds);
        
        this.placeBlockAtCurrentPosition(world, Blocks.netherrack, 0, 4, 5, 3, bounds);
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.getMetadataWithOffset(Blocks.nether_brick_stairs, 1) + 4, 5, 5, 3, bounds);
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.getMetadataWithOffset(Blocks.nether_brick_stairs, 3) + 4, 4, 5, 2, bounds);
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, this.getMetadataWithOffset(Blocks.nether_brick_stairs, 2) + 4, 4, 5, 4, bounds);
        
        // Build the top layer of the gateway
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 3, 6, 3, bounds);
        
        this.placeBlockAtCurrentPosition(world, Blocks.fire, 0, 2, 6, 3, bounds);
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 1, 6, 3, bounds);
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 2, 6, 2, bounds);
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 2, 6, 4, bounds);
        
        this.placeBlockAtCurrentPosition(world, Blocks.fire, 0, 4, 6, 3, bounds);
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 5, 6, 3, bounds);
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 4, 6, 2, bounds);
        this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 4, 6, 4, bounds);
        
        // Place the transient door
        int y = this.getYWithOffset(3);
        int x = this.getXWithOffset(3, 3);
        int z = this.getZWithOffset(3, 3);
        DimLink link;
        NewDimData dimension;

        // This function might run multiple times for a single component
        // due to the way Minecraft handles structure generation!
        if (bounds.isVecInside(x, y, z) && bounds.isVecInside(x, y + 1, z))
        {
        	orientation = this.getMetadataWithOffset(Blocks.wooden_door, 1);
        	dimension = PocketManager.createDimensionData(world);
        	link = dimension.getLink(x, y + 1, z);
        	if (link == null)
        	{
        		link = dimension.createLink(x, y + 1, z, LinkType.DUNGEON, orientation);
        	}
        	ItemDoor.placeDoorBlock(world, x, y, z, orientation, mod_pocketDim.transientDoor);
        }

        for (x = 0; x <= 6; ++x)
        {
            for (z = 0; z <= 6; ++z)
            {
                this.func_151554_b(world, Blocks.nether_brick, 0, x, -1, z, bounds);
            }
        }

        return true;
    }

	@Override
	protected void func_143012_a(NBTTagCompound tag) { }

	@Override
	protected void func_143011_b(NBTTagCompound tag) { }
}
