package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.client.ClosingRiftFX;
import com.zixiken.dimdoors.shared.items.ModItems;
import com.zixiken.dimdoors.shared.tileentities.DDTileEntityBase;
import com.zixiken.dimdoors.shared.tileentities.TileEntityRift;
import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation")
public class BlockRift extends Block implements ITileEntityProvider {

    private static final float MIN_IMMUNE_RESISTANCE = 5000.0F;
    public static final String ID = "blockRift";

    private final ArrayList<Block> blocksImmuneToRift;	// List of Vanilla blocks immune to rifts
    private final ArrayList<Block> modBlocksImmuneToRift; // List of DD blocks immune to rifts

    public BlockRift() {
        super(Material.LEAVES); //Fire is replacable. We do not want this block to be replacable. We do want to walf through it though...
        setTickRandomly(true);
        setHardness(1.0F);
        setUnlocalizedName(ID);
        setRegistryName(ID);

        modBlocksImmuneToRift = new ArrayList<Block>();
        modBlocksImmuneToRift.add(ModBlocks.blockDimWall);
        modBlocksImmuneToRift.add(ModBlocks.blockDimDoor);
        modBlocksImmuneToRift.add(ModBlocks.blockDimDoorWarp);
        modBlocksImmuneToRift.add(ModBlocks.blockDimHatch);
        modBlocksImmuneToRift.add(ModBlocks.blockDimDoorChaos);
        modBlocksImmuneToRift.add(ModBlocks.blockRift);
        modBlocksImmuneToRift.add(ModBlocks.blockDimDoorTransient);
        modBlocksImmuneToRift.add(ModBlocks.blockDimDoorGold);
        modBlocksImmuneToRift.add(ModBlocks.blockDoorGold);
        modBlocksImmuneToRift.add(ModBlocks.blockDimDoorPersonal);
        modBlocksImmuneToRift.add(ModBlocks.blockDoorQuartz);

        blocksImmuneToRift = new ArrayList<Block>();
        blocksImmuneToRift.add(Blocks.LAPIS_BLOCK);
        blocksImmuneToRift.add(Blocks.IRON_BLOCK);
        blocksImmuneToRift.add(Blocks.GOLD_BLOCK);
        blocksImmuneToRift.add(Blocks.DIAMOND_BLOCK);
        blocksImmuneToRift.add(Blocks.EMERALD_BLOCK);
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {
        return null;
    }

    /**
     * Returns whether this block is collideable based on the arguments passed
     * in Args: blockMetaData, unknownFlag
     */
    @Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return hitIfLiquid;
    }

    /**
     * Returns Returns true if the given side of this block type should be
     * rendered (if it's solid or not), if the adjacent block is at the given
     * coordinates. Args: blockAccess, x, y, z, side
     */
    @Override
    public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE; //Tile Entity Special Renderer
    }

    public void dropWorldThread(World world, BlockPos pos, Random random) {
        Block block = world.getBlockState(pos).getBlock();

        if (!world.getBlockState(pos).equals(Blocks.AIR) && !(block instanceof BlockLiquid || block instanceof IFluidBlock)) {
            ItemStack thread = new ItemStack(ModItems.itemWorldThread, 1);
            world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), thread));
        }
    }

    /**
     * Lets pistons push through rifts, destroying them
     */
    @Override
    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.NORMAL;
    }

    /**
     * regulates the render effect, especially when multiple rifts start to link
     * up. Has 3 main parts- Grows toward and away from nearest rift, bends
     * toward it, and a randomization function
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand) {
        //ArrayList<BlockPos> targets = findReachableBlocks(worldIn, pos, 2, false);
        //TODO: implement the parts specified in the method comment?
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();

        TileEntityRift tile = (TileEntityRift) worldIn.getTileEntity(pos);
        //renders an extra little blob on top of the actual rift location so its easier to find.
        // Eventually will only render if the player has the goggles.
        /*FMLClientHandler.instance().getClient().effectRenderer.addEffect(new GoggleRiftFX(
                worldIn,
                x + .5, y + .5, z + .5,
                rand.nextGaussian() * 0.01D, rand.nextGaussian() * 0.01D, rand.nextGaussian() * 0.01D));
         */
        if (tile.shouldClose) //renders an opposite color effect if it is being closed by the rift remover{
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(new ClosingRiftFX(
                    worldIn,
                    x + .5, y + .5, z + .5,
                    rand.nextGaussian() * 0.01D, rand.nextGaussian() * 0.01D, rand.nextGaussian() * 0.01D));
    }


    public boolean tryPlacingRift(World world, BlockPos pos) {
        return world != null && !isBlockImmune(world, pos)
                && world.setBlockState(pos, getDefaultState()); //@todo This returns false, because this block does not have blockstates configured correctly. !isBlockImmune doesn't seem to be true either though...
    }

    public boolean isBlockImmune(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        // SenseiKiwi: I've switched to using the block's blast resistance instead of its
        // hardness since most defensive blocks are meant to defend against explosions and
        // may have low hardness to make them easier to build with. However, block.getExplosionResistance()
        // is designed to receive an entity, the source of the blast. We have no entity so
        // I've set this to access blockResistance directly. Might need changing later.
        return block != null
                /*&&
                (block >= MIN_IMMUNE_RESISTANCE*/ || modBlocksImmuneToRift.contains(block)
                || blocksImmuneToRift.contains(block);
    }

    public boolean isModBlockImmune(World world, BlockPos pos) {
        // Check whether the block at the specified location is one of the
        // rift-resistant blocks from DD.
        Block block = world.getBlockState(pos).getBlock();
        return block != null && modBlocksImmuneToRift.contains(block);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return null;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityRift();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        world.removeTileEntity(pos);
    }

    @Override
    public boolean causesSuffocation() {
        return false;
    }

    public DDTileEntityBase getRiftTile(World world, BlockPos pos, IBlockState state) {
        return (DDTileEntityBase) world.getTileEntity(pos);
    }
}
