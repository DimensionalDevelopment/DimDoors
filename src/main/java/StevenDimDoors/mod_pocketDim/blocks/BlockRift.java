package StevenDimDoors.mod_pocketDim.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityRift;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDimClient.ClosingRiftFX;
import StevenDimDoors.mod_pocketDimClient.GoggleRiftFX;
import StevenDimDoors.mod_pocketDimClient.RiftFX;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRift extends Block implements ITileEntityProvider
{
	private static final float MIN_IMMUNE_RESISTANCE = 5000.0F;
	private static final int BLOCK_DESTRUCTION_RANGE = 4;
	private static final int RIFT_SPREAD_RANGE = 5;
	private static final int MAX_BLOCK_SEARCH_CHANCE = 100;
	private static final int BLOCK_SEARCH_CHANCE = 50;
	private static final int MAX_BLOCK_DESTRUCTION_CHANCE = 100;
	private static final int BLOCK_DESTRUCTION_CHANCE = 50;

	public static final int MAX_WORLD_THREAD_DROP_CHANCE = 1000;
	
	private final DDProperties properties;
	private final ArrayList<Block> blocksImmuneToRift;	// List of Vanilla blocks immune to rifts
	private final ArrayList<Block> modBlocksImmuneToRift; // List of DD blocks immune to rifts
	
	public BlockRift(Material par2Material, DDProperties properties)
	{
		super(par2Material);
		this.setTickRandomly(true);
		this.properties = properties;
		this.modBlocksImmuneToRift = new ArrayList<Block>();
		this.modBlocksImmuneToRift.add(mod_pocketDim.blockDimWall);
		this.modBlocksImmuneToRift.add(mod_pocketDim.blockDimWallPerm);
		this.modBlocksImmuneToRift.add(mod_pocketDim.dimensionalDoor);
		this.modBlocksImmuneToRift.add(mod_pocketDim.warpDoor);
		this.modBlocksImmuneToRift.add(mod_pocketDim.transTrapdoor);
		this.modBlocksImmuneToRift.add(mod_pocketDim.unstableDoor);
		this.modBlocksImmuneToRift.add(mod_pocketDim.blockRift);
		this.modBlocksImmuneToRift.add(mod_pocketDim.transientDoor);
		this.modBlocksImmuneToRift.add(mod_pocketDim.goldenDimensionalDoor);
		this.modBlocksImmuneToRift.add(mod_pocketDim.goldenDoor);
		
		this.blocksImmuneToRift = new ArrayList<Block>();
		
		this.blocksImmuneToRift.add(mod_pocketDim.blockDimWall);
		this.blocksImmuneToRift.add(mod_pocketDim.blockDimWallPerm);
		this.blocksImmuneToRift.add(mod_pocketDim.dimensionalDoor);
		this.blocksImmuneToRift.add(mod_pocketDim.warpDoor);
		this.blocksImmuneToRift.add(mod_pocketDim.transTrapdoor);
		this.blocksImmuneToRift.add(mod_pocketDim.unstableDoor);
		this.blocksImmuneToRift.add(mod_pocketDim.blockRift);
		this.blocksImmuneToRift.add(mod_pocketDim.transientDoor);
		this.blocksImmuneToRift.add(mod_pocketDim.goldenDimensionalDoor);
		this.blocksImmuneToRift.add(mod_pocketDim.goldenDoor);
		this.blocksImmuneToRift.add(mod_pocketDim.personalDimDoor);
		this.blocksImmuneToRift.add(Blocks.lapis_block);
		this.blocksImmuneToRift.add(Blocks.iron_block);
		this.blocksImmuneToRift.add(Blocks.gold_block);
		this.blocksImmuneToRift.add(Blocks.diamond_block);
		this.blocksImmuneToRift.add(Blocks.emerald_block);
	}
	
	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName());
	}
	
	@Override
	public boolean isCollidable()
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	/**
	 * Returns whether this block is collideable based on the arguments passed in Args: blockMetaData, unknownFlag
	 */
	@Override
	public boolean canCollideCheck(int par1, boolean par2)
	{
		return par2;
	}

	/**
	 * Returns Returns true if the given side of this block type should be rendered (if it's solid or not), if the
	 * adjacent block is at the given coordinates. Args: blockAccess, x, y, z, side
	 */
	@Override
	public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		return true;
	}
	
	@Override
	public int getRenderType()
	{
		// This doesn't do anything yet
		return 0;
	}

	/**
	 * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
	 * coordinates.  Args: blockAccess, x, y, z, side
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		return true;
	}

	/**
	 * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
	 * cleared to be reused)
	 */
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
	{
		return null;
	}
	
	//function that regulates how many blocks it eats/ how fast it eats them. 
	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		if (properties.RiftGriefingEnabled && !world.isRemote &&
				PocketManager.getLink(x, y, z, world.provider.dimensionId) != null)
		{
			//Randomly decide whether to search for blocks to destroy. This reduces the frequency of search operations,
			//moderates performance impact, and controls the apparent speed of block destruction.
			if (random.nextInt(MAX_BLOCK_SEARCH_CHANCE) < BLOCK_SEARCH_CHANCE &&
					((TileEntityRift) world.getTileEntity(x, y, z)).updateNearestRift() )
			{
				destroyNearbyBlocks(world, x, y, z, random);
			}
		}
	}
	
	private void destroyNearbyBlocks(World world, int x, int y, int z, Random random)
	{
		// Find reachable blocks that are vulnerable to rift damage (ignoring air, of course)
		ArrayList<Point3D> targets = findReachableBlocks(world, x, y, z, BLOCK_DESTRUCTION_RANGE, false);
		
		// For each block, randomly decide whether to destroy it.
		// The randomness makes it so the destroyed area appears "noisy" if the rift is exposed to a large surface.
		for (Point3D target : targets)
		{
			if (random.nextInt(MAX_BLOCK_DESTRUCTION_CHANCE) < BLOCK_DESTRUCTION_CHANCE)
			{
				dropWorldThread(world.getBlock(target.getX(), target.getY(), target.getZ()), world, x, y, z, random);
				world.func_147480_a(target.getX(), target.getY(), target.getZ(), false);
			}
		}
	}
	
	private ArrayList<Point3D> findReachableBlocks(World world, int x, int y, int z, int range, boolean includeAir)
	{
		int searchVolume = (int) Math.pow(2 * range + 1, 3);
		HashMap<Point3D, Integer> pointDistances = new HashMap<Point3D, Integer>(searchVolume);
		Queue<Point3D> points = new LinkedList<Point3D>();
		ArrayList<Point3D> targets = new ArrayList<Point3D>();
		
		// Perform a breadth-first search outwards from the point at which the rift is located.
		// Record the distances of the points we visit to stop the search at its maximum range.
		pointDistances.put(new Point3D(x, y, z), 0);
		addAdjacentBlocks(x, y, z, 0, pointDistances, points);
		while (!points.isEmpty())
		{
			Point3D current = points.remove();
			int distance = pointDistances.get(current);
			
			// If the current block is air, continue searching. Otherwise, add the block to our list.
			if (world.isAirBlock(current.getX(), current.getY(), current.getZ()))
			{
				if (includeAir)
				{
					targets.add(current);
				}
				// Make sure we stay within the search range
				if (distance < BLOCK_DESTRUCTION_RANGE)
				{
					addAdjacentBlocks(current.getX(), current.getY(), current.getZ(), distance, pointDistances, points);
				}
			}
			else
			{
				// Check if the current block is immune to destruction by rifts. If not, add it to our list.
				if (!isBlockImmune(world, current.getX(), current.getY(), current.getZ()))
				{
					targets.add(current);
				}
			}
		}
		return targets;
	}
		
	public void dropWorldThread(Block block, World world, int x, int y, int z, Random random)
	{
		if (!block.isAir(world, x, y, z) && (random.nextInt(MAX_WORLD_THREAD_DROP_CHANCE) < properties.WorldThreadDropChance)
				&& !(block instanceof BlockLiquid ||
					block instanceof IFluidBlock))
		{
			ItemStack thread = new ItemStack(mod_pocketDim.itemWorldThread, 1);
			world.spawnEntityInWorld(new EntityItem(world, x, y, z, thread));
		}
	}
	
	private static void addAdjacentBlocks(int x, int y, int z, int distance, HashMap<Point3D, Integer> pointDistances, Queue<Point3D> points)
	{
		Point3D[] neighbors = new Point3D[] {
				new Point3D(x - 1, y, z),
				new Point3D(x + 1, y, z),
				new Point3D(x, y - 1, z),
				new Point3D(x, y + 1, z),
				new Point3D(x, y, z - 1),
				new Point3D(x, y, z + 1)
		};
		for (int index = 0; index < neighbors.length; index++)
		{
			if (!pointDistances.containsKey(neighbors[index]))
			{
				pointDistances.put(neighbors[index], distance + 1);
				points.add(neighbors[index]);
			}
		}
	}
	
	public boolean spreadRift(NewDimData dimension, DimLink parent, World world, Random random)
	{
		int x, y, z;
        Block block = null;
		Point4D source = parent.source();
		
		// Find reachable blocks that are vulnerable to rift damage and include air
		ArrayList<Point3D> targets = findReachableBlocks(world, source.getX(), source.getY(), source.getZ(),
				RIFT_SPREAD_RANGE, true);
		
		if (!targets.isEmpty())
		{
			// Choose randomly from among the possible locations where we can spawn a new rift
			Point3D target = targets.get( random.nextInt(targets.size()) );
			x = target.getX();
			y = target.getY();
			z = target.getZ();

			// Create a child, replace the block with a rift, and consider dropping World Thread
			block = world.getBlock(x, y, z);
			if (world.setBlock(x, y, z, mod_pocketDim.blockRift))
			{
				dimension.createChildLink(x, y, z, parent);
				dropWorldThread(block, world, x, y, z, random);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Lets pistons push through rifts, destroying them
	 */
	@Override
	public int getMobilityFlag()
    {
        return 1;
    }
	
	/**
	 * regulates the render effect, especially when multiple rifts start to link up. Has 3 main parts- Grows toward and away from nearest rft, bends toward it, and a randomization function
	 */
	@Override
	@SideOnly(Side.CLIENT)
	
	public void randomDisplayTick(World world, int x, int y, int z, Random rand)
	{

		
		 ArrayList<Point3D> targets=findReachableBlocks(world, x, y, z, 2, false);
		
		
		TileEntityRift tile = (TileEntityRift)world.getTileEntity(x, y, z);
		
		

				//renders an extra little blob on top of the actual rift location so its easier to find. Eventually will only render if the player has the goggles. 
				FMLClientHandler.instance().getClient().effectRenderer.addEffect(new GoggleRiftFX(world,x+.5, y+.5, z+.5, rand.nextGaussian() * 0.01D, rand.nextGaussian()  * 0.01D, rand.nextGaussian() * 0.01D, FMLClientHandler.instance().getClient().effectRenderer));

		if(tile.shouldClose)
			{
				//renders an opposite color effect if it is being closed by the rift remover
				FMLClientHandler.instance().getClient().effectRenderer.addEffect(new ClosingRiftFX(world,x+.5, y+.5, z+.5, rand.nextGaussian() * 0.01D, rand.nextGaussian()  * 0.01D, rand.nextGaussian() * 0.01D, FMLClientHandler.instance().getClient().effectRenderer));

			}
		
	}
	
	public boolean tryPlacingRift(World world, int x, int y, int z)
	{
		if (world != null && !isBlockImmune(world, x, y, z))
		{
			return world.setBlock(x, y, z, mod_pocketDim.blockRift);
		}
		return false;
	}

	public boolean isBlockImmune(World world, int x, int y, int z)
	{
		Block block = world.getBlock(x, y, z);
		if (block != null)
		{
			// SenseiKiwi: I've switched to using the block's blast resistance instead of its
			// hardness since most defensive blocks are meant to defend against explosions and
			// may have low hardness to make them easier to build with. However, block.getExplosionResistance()
			// is designed to receive an entity, the source of the blast. We have no entity so
			// I've set this to access blockResistance directly. Might need changing later.
			
			return (block.blockResistance >= MIN_IMMUNE_RESISTANCE ||
					modBlocksImmuneToRift.contains(block) ||
					blocksImmuneToRift.contains(block));
		}
		return false;
	}
	
	public boolean isModBlockImmune(World world, int x, int y, int z)
	{
		// Check whether the block at the specified location is one of the
		// rift-resistant blocks from DD.
		Block block = world.getBlock(x, y, z);
		if (block != null)
		{
			return modBlocksImmuneToRift.contains(block);
		}
		return false;
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
    {
        return null;
    }

	@Override
	public Item getItemDropped(int par1, Random par2Random, int par3)
	{
		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityRift();
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldMeta)
    {
		// This function runs on the server side after a block is replaced
		// We MUST call super.breakBlock() since it involves removing tile entities
        super.breakBlock(world, x, y, z, oldBlock, oldMeta);
        
        // Schedule rift regeneration for this block if it was changed
        if (world.getBlock(x, y, z) != oldBlock)
        {
        	mod_pocketDim.riftRegenerator.scheduleSlowRegeneration(x, y, z, world);
        }
    }
}