package StevenDimDoors.mod_pocketDim.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowing;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
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
	private final ArrayList<Integer> blocksImmuneToRift;
	
	public BlockRift(int i, int j, Material par2Material, DDProperties properties) 
	{
		super(i, par2Material);
		this.setTickRandomly(true);
		this.properties = properties;
		this.blocksImmuneToRift = new ArrayList<Integer>();
		this.blocksImmuneToRift.add(properties.FabricBlockID);
		this.blocksImmuneToRift.add(properties.PermaFabricBlockID);
		this.blocksImmuneToRift.add(properties.DimensionalDoorID);
		this.blocksImmuneToRift.add(properties.WarpDoorID);
		this.blocksImmuneToRift.add(properties.TransTrapdoorID);
		this.blocksImmuneToRift.add(properties.UnstableDoorID);
		this.blocksImmuneToRift.add(properties.RiftBlockID);
		this.blocksImmuneToRift.add(properties.TransientDoorID);
		this.blocksImmuneToRift.add(properties.GoldenDimensionalDoorID);
		this.blocksImmuneToRift.add(properties.GoldenDoorID);
		this.blocksImmuneToRift.add(properties.PersonalDimDoorID);

		this.blocksImmuneToRift.add(Block.blockLapis.blockID);
		this.blocksImmuneToRift.add(Block.blockIron.blockID);
		this.blocksImmuneToRift.add(Block.blockGold.blockID);
		this.blocksImmuneToRift.add(Block.blockDiamond.blockID);
		this.blocksImmuneToRift.add(Block.blockEmerald.blockID);
	}
	
	@Override
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName());
	}
	
	@Override
	public boolean isCollidable()
	{
		return false;
	}
	
	@Override
	public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5) {}

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
	
	//this doesnt do anything yet.
	@Override
	public int getRenderType()
	{
		if (mod_pocketDim.isPlayerWearingGoogles)
		{
			return 0;
		}
		return 8;
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
					((TileEntityRift) world.getBlockTileEntity(x, y, z)).updateNearestRift() )
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
				dropWorldThread(world.getBlockId(target.getX(), target.getY(), target.getZ()), world, x, y, z, random);
				world.destroyBlock(target.getX(), target.getY(), target.getZ(), false);
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
		
	private void dropWorldThread(int blockID, World world, int x, int y, int z, Random random)
	{
		if (blockID != 0 && (random.nextInt(MAX_WORLD_THREAD_DROP_CHANCE) < properties.WorldThreadDropChance)
				&& !(Block.blocksList[blockID] instanceof BlockFlowing ||
					Block.blocksList[blockID] instanceof BlockFluid ||
					Block.blocksList[blockID] instanceof IFluidBlock))
		{
			ItemStack thread = new ItemStack(mod_pocketDim.itemWorldThread, 1);
			world.spawnEntityInWorld(new EntityItem(world, x, y, z, thread));
		}
	}
	
	private void addAdjacentBlocks(int x, int y, int z, int distance, HashMap<Point3D, Integer> pointDistances, Queue<Point3D> points)
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

	public void regenerateRift(World world, int x, int y, int z, Random random)
	{
		if (!this.isBlockImmune(world, x, y, z) && world.getChunkProvider().chunkExists(x >> 4, z >> 4))
		{
			int blockID = world.getBlockId(x, y, z);
			if (world.setBlock(x, y, z, properties.RiftBlockID))
				dropWorldThread(blockID, world, x, y, z, random);
		}
	}
	
	public boolean spreadRift(NewDimData dimension, DimLink parent, World world, Random random)
	{
		int x, y, z, blockID;
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
			blockID = world.getBlockId(x, y, z);
			if (world.setBlock(x, y, z, properties.RiftBlockID))
			{
				dimension.createChildLink(x, y, z, parent);
				dropWorldThread(blockID, world, x, y, z, random);
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
	
	public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random rand)
	{

		int count;
		//growth in the direction towards the nearby rift
		float xGrowth=0;
		float yGrowth=0;
		float zGrowth=0;
		//growth away from the nearby rift
		float xGrowthn=0;
		float yGrowthn=0;
		float zGrowthn=0;
		//how far the particles are away from original rift. Used to decrease noise the farther they are away. 
		float xChange = 0;
		float yChange = 0;
		float zChange = 0;

		TileEntityRift tile = (TileEntityRift)par1World.getBlockTileEntity(par2, par3, par4);

		float Xoffset=0;
		float Yoffset=0;
		float Zoffset=0;
		for (count = 0; count < 12 && tile!=null; ++count)
		{
			//TODO change to a switch statement for clarity
			if(tile.xOffset>0)
			{
				if(rand.nextInt(tile.xOffset)==0)
				{
					xGrowth =xGrowth+.15F*tile.xOffset;

				}
			}
			else if(tile.xOffset<0)
			{
				if(rand.nextInt(-tile.xOffset)==0)
				{
					xGrowthn =xGrowthn-.15F*-tile.xOffset;

				}
			}

			if(tile.yOffset>0)
			{
				if(rand.nextInt(tile.yOffset)==0)
				{
					yGrowth =yGrowth+.15F*tile.yOffset;

				}
			}
			else if(tile.yOffset<0)
			{
				if(rand.nextInt(-tile.yOffset)==0)
				{
					yGrowthn =yGrowthn-.15F*-tile.yOffset;

				}
			}

			if(tile.zOffset>0)
			{
				if(rand.nextInt(tile.zOffset)==0)
				{
					zGrowth =zGrowth+.15F*tile.zOffset;

				}
			}
			else if(tile.zOffset<0)
			{
				if(rand.nextInt(-tile.zOffset)==0)
				{
					zGrowthn =zGrowthn-.15F*-tile.zOffset;

				}
			}


			xChange=(float) ((xGrowth+xGrowthn)+rand.nextGaussian()*.05F);
			yChange=(float) ((yGrowth+yGrowthn)+rand.nextGaussian()*.05F);
			zChange=(float) ((zGrowth+zGrowthn)+rand.nextGaussian()*.05F);

			Xoffset=  ((0.25F/(1+Math.abs(xChange))));

			Yoffset=  ((0.25F/(1+Math.abs(yChange))));
			Zoffset=  ((0.25F/(1+Math.abs(zChange))));




			FMLClientHandler.instance().getClient().effectRenderer.addEffect(new RiftFX(par1World,par2+.5+xChange+Xoffset*rand.nextGaussian(), par3+.5+yChange+Yoffset*rand.nextGaussian() , par4+.5+zChange+Zoffset*rand.nextGaussian(), rand.nextGaussian() * 0.001D, rand.nextGaussian()  * 0.001D, rand.nextGaussian() * 0.001D, FMLClientHandler.instance().getClient().effectRenderer));
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(new RiftFX(par1World,par2+.5-xChange-Xoffset*rand.nextGaussian(), par3+.5-yChange-Yoffset*rand.nextGaussian() , par4+.5-zChange-Zoffset*rand.nextGaussian(), rand.nextGaussian() * 0.001D, rand.nextGaussian()  * 0.001D, rand.nextGaussian() * 0.001D, FMLClientHandler.instance().getClient().effectRenderer));


			if(rand.nextBoolean())
			{
				//renders an extra little blob on top of the actual rift location so its easier to find. Eventually will only render if the player has the goggles. 
				FMLClientHandler.instance().getClient().effectRenderer.addEffect(new GoggleRiftFX(par1World,par2+.5, par3+.5, par4+.5, rand.nextGaussian() * 0.01D, rand.nextGaussian()  * 0.01D, rand.nextGaussian() * 0.01D, FMLClientHandler.instance().getClient().effectRenderer));
			}
			if(tile.shouldClose)
			{
				//renders an opposite color effect if it is being closed by the rift remover
				FMLClientHandler.instance().getClient().effectRenderer.addEffect(new ClosingRiftFX(par1World,par2+.5, par3+.5, par4+.5, rand.nextGaussian() * 0.01D, rand.nextGaussian()  * 0.01D, rand.nextGaussian() * 0.01D, FMLClientHandler.instance().getClient().effectRenderer));

			}
		}
	}

	public boolean isBlockImmune(World world, int x, int y, int z)
	{
		Block block = Block.blocksList[world.getBlockId(x, y, z)];
		if (block != null)
		{
			// SenseiKiwi: I've switched to using the block's blast resistance instead of its
			// hardness since most defensive blocks are meant to defend against explosions and
			// may have low hardness to make them easier to build with. However, block.getExplosionResistance()
			// is designed to receive an entity, the source of the blast. We have no entity so
			// I've set this to access blockResistance directly. Might need changing later.
			
			return (block.blockResistance >= MIN_IMMUNE_RESISTANCE || blocksImmuneToRift.contains(block.blockID));
		}
		return false;
	}
	
	@Override
	public int idPicked(World par1World, int par2, int par3, int par4)
	{
		return 0;
	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3)
	{
		return 0;
	}

	@Override
	public TileEntity createNewTileEntity(World world) 
	{
		return new TileEntityRift();
	}
}