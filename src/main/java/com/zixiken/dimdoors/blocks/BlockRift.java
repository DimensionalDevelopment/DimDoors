package com.zixiken.dimdoors.blocks;

import java.util.*;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.client.ClosingRiftFX;
import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.core.DimData;
import com.zixiken.dimdoors.core.DimLink;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.tileentities.TileEntityRift;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import com.zixiken.dimdoors.util.Point4D;
import com.zixiken.dimdoors.client.GoggleRiftFX;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRift extends Block implements ITileEntityProvider {
	private static final float MIN_IMMUNE_RESISTANCE = 5000.0F;
	private static final int BLOCK_DESTRUCTION_RANGE = 4;
	private static final int RIFT_SPREAD_RANGE = 5;
	private static final int MAX_BLOCK_SEARCH_CHANCE = 100;
	private static final int BLOCK_SEARCH_CHANCE = 50;
	private static final int MAX_BLOCK_DESTRUCTION_CHANCE = 100;
	private static final int BLOCK_DESTRUCTION_CHANCE = 50;

	public static final int MAX_WORLD_THREAD_DROP_CHANCE = 1000;
    public static final String ID = "rift";
	
	private final DDProperties properties;
	private final ArrayList<Block> blocksImmuneToRift;	// List of Vanilla blocks immune to rifts
	private final ArrayList<Block> modBlocksImmuneToRift; // List of DD blocks immune to rifts
	
	public BlockRift() {
		super(Material.fire);
		setTickRandomly(true);
		properties = DDProperties.instance();
        setHardness(1.0F);
        setUnlocalizedName(ID);

		modBlocksImmuneToRift = new ArrayList<Block>();
		modBlocksImmuneToRift.add(DimDoors.blockDimWall);
		modBlocksImmuneToRift.add(DimDoors.blockDimWallPerm);
		modBlocksImmuneToRift.add(DimDoors.dimensionalDoor);
		modBlocksImmuneToRift.add(DimDoors.warpDoor);
		modBlocksImmuneToRift.add(DimDoors.transTrapdoor);
		modBlocksImmuneToRift.add(DimDoors.unstableDoor);
		modBlocksImmuneToRift.add(DimDoors.blockRift);
		modBlocksImmuneToRift.add(DimDoors.transientDoor);
		modBlocksImmuneToRift.add(DimDoors.goldenDimensionalDoor);
		modBlocksImmuneToRift.add(DimDoors.goldenDoor);
		modBlocksImmuneToRift.add(DimDoors.personalDimDoor);
		
		blocksImmuneToRift = new ArrayList<Block>();
		blocksImmuneToRift.add(Blocks.lapis_block);
		blocksImmuneToRift.add(Blocks.iron_block);
		blocksImmuneToRift.add(Blocks.gold_block);
		blocksImmuneToRift.add(Blocks.diamond_block);
		blocksImmuneToRift.add(Blocks.emerald_block);
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
	public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid)
	{
		return hitIfLiquid;
	}

	/**
	 * Returns Returns true if the given side of this block type should be rendered (if it's solid or not), if the
	 * adjacent block is at the given coordinates. Args: blockAccess, x, y, z, side
	 */
	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
	{
		return true;
	}
	
	@Override
	public int getRenderType() {
        return 2; //Tile Entity Special Renderer
    }
	
	//function that regulates how many blocks it eats/ how fast it eats them. 
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (properties.RiftGriefingEnabled && !worldIn.isRemote &&
				PocketManager.getLink(pos, worldIn.provider.getDimensionId()) != null) {
			//Randomly decide whether to search for blocks to destroy. This reduces the frequency of search operations,
			//moderates performance impact, and controls the apparent speed of block destruction.
			if (rand.nextInt(MAX_BLOCK_SEARCH_CHANCE) < BLOCK_SEARCH_CHANCE &&
					((TileEntityRift) worldIn.getTileEntity(pos)).updateNearestRift() ) {
				destroyNearbyBlocks(worldIn, pos, rand);
			}
		}
	}
	
	private void destroyNearbyBlocks(World world, BlockPos pos, Random random) {
		// Find reachable blocks that are vulnerable to rift damage (ignoring air, of course)
		ArrayList<BlockPos> targets = findReachableBlocks(world, pos, BLOCK_DESTRUCTION_RANGE, false);
		
		// For each block, randomly decide whether to destroy it.
		// The randomness makes it so the destroyed area appears "noisy" if the rift is exposed to a large surface.
		for (BlockPos target : targets) {
			if (random.nextInt(MAX_BLOCK_DESTRUCTION_CHANCE) < BLOCK_DESTRUCTION_CHANCE) {
				dropWorldThread(world, pos, random);
				world.destroyBlock(target, false);
			}
		}
	}
	
	private ArrayList<BlockPos> findReachableBlocks(World world, BlockPos pos, int range, boolean includeAir) {
		int searchVolume = (int) Math.pow(2 * range + 1, 3);
		HashMap<BlockPos, Integer> pointDistances = new HashMap<BlockPos, Integer>(searchVolume);
		Queue<BlockPos> points = new LinkedList<BlockPos>();
		ArrayList<BlockPos> targets = new ArrayList<BlockPos>();
		
		// Perform a breadth-first search outwards from the point at which the rift is located.
		// Record the distances of the points we visit to stop the search at its maximum range.
		pointDistances.put(pos, 0);
		addAdjacentBlocks(pos, 0, pointDistances, points);
		while (!points.isEmpty()) {
			BlockPos current = points.remove();
			int distance = pointDistances.get(current);
			
			// If the current block is air, continue searching. Otherwise, add the block to our list.
			if (world.isAirBlock(current)) {
				if (includeAir) targets.add(current);

				// Make sure we stay within the search range
				if (distance < BLOCK_DESTRUCTION_RANGE) addAdjacentBlocks(current, distance, pointDistances, points);
			} else {
				// Check if the current block is immune to destruction by rifts. If not, add it to our list.
				if (!isBlockImmune(world, current)) targets.add(current);
			}
		}
		return targets;
	}
		
	public void dropWorldThread(World world, BlockPos pos, Random random) {
        Block block = world.getBlockState(pos).getBlock();

		if (!block.isAir(world, pos) &&
                (random.nextInt(MAX_WORLD_THREAD_DROP_CHANCE) < properties.WorldThreadDropChance) &&
                !(block instanceof BlockLiquid || block instanceof IFluidBlock)) {
			ItemStack thread = new ItemStack(DimDoors.itemWorldThread, 1);
			world.spawnEntityInWorld(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), thread));
		}
	}
	
	private static void addAdjacentBlocks(BlockPos pos, int distance, HashMap<BlockPos, Integer> pointDistances, Queue<BlockPos> points) {
		BlockPos[] neighbors = {
				pos.north(),
				pos.south(),
				pos.up(),
				pos.down(),
				pos.west(),
				pos.east()
		};

		for (BlockPos neighbor : neighbors) {
			if (!pointDistances.containsKey(neighbor)) {
				pointDistances.put(neighbor, distance + 1);
				points.add(neighbor);
			}
		}
	}
	
	public boolean spreadRift(DimData dimension, DimLink parent, World world, Random random) {
		Point4D source = parent.source();
		
		// Find reachable blocks that are vulnerable to rift damage and include air
		ArrayList<BlockPos> targets = findReachableBlocks(world, source.toBlockPos(), RIFT_SPREAD_RANGE, true);
		
		if (!targets.isEmpty()) {
			// Choose randomly from among the possible locations where we can spawn a new rift
			BlockPos target = targets.get( random.nextInt(targets.size()) );

			// Create a child, replace the block with a rift, and consider dropping World Thread
			if (world.setBlockState(target, getDefaultState())) {
				dimension.createChildLink(target, parent);
				dropWorldThread(world, target, random);
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
	 * regulates the render effect, especially when multiple rifts start to link up.
     * Has 3 main parts- Grows toward and away from nearest rift, bends toward it, and a randomization function
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        //ArrayList<BlockPos> targets = findReachableBlocks(worldIn, pos, 2, false);
        //TODO: implement the parts specified in the method comment?
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();

		TileEntityRift tile = (TileEntityRift)worldIn.getTileEntity(pos);
        //renders an extra little blob on top of the actual rift location so its easier to find.
        // Eventually will only render if the player has the goggles.
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(new GoggleRiftFX(
                worldIn,
                x+.5, y+.5, z+.5,
                rand.nextGaussian()*0.01D, rand.nextGaussian()*0.01D, rand.nextGaussian()*0.01D,
                FMLClientHandler.instance().getClient().effectRenderer));

		if(tile.shouldClose)
            //renders an opposite color effect if it is being closed by the rift remover
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(new ClosingRiftFX(
                    worldIn,
                    x+.5, y+.5, z+.5,
                    rand.nextGaussian()*0.01D, rand.nextGaussian()*0.01D, rand.nextGaussian()*0.01D,
                    FMLClientHandler.instance().getClient().effectRenderer));
	}
	
	public boolean tryPlacingRift(World world, BlockPos pos) {
		return world != null && !isBlockImmune(world, pos) && world.setBlockState(pos, getDefaultState());
	}

	public boolean isBlockImmune(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		// SenseiKiwi: I've switched to using the block's blast resistance instead of its
		// hardness since most defensive blocks are meant to defend against explosions and
		// may have low hardness to make them easier to build with. However, block.getExplosionResistance()
		// is designed to receive an entity, the source of the blast. We have no entity so
		// I've set this to access blockResistance directly. Might need changing later.
		return block != null &&
                (block.blockResistance >= MIN_IMMUNE_RESISTANCE ||
				modBlocksImmuneToRift.contains(block) ||
				blocksImmuneToRift.contains(block));
	}

	public boolean isModBlockImmune(World world, BlockPos pos) {
		// Check whether the block at the specified location is one of the
		// rift-resistant blocks from DD.
		Block block = world.getBlockState(pos).getBlock();
		return block != null && modBlocksImmuneToRift.contains(block);
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
        return null;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {return null;}

    @Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityRift();
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		world.removeTileEntity(pos);
        
        // Schedule rift regeneration for this block if it was changed
        if (world.getBlockState(pos).getBlock() != state.getBlock())
            DimDoors.riftRegenerator.scheduleSlowRegeneration(pos, world);
    }
}