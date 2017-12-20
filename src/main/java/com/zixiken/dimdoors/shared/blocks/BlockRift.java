package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.client.ParticleRiftEffect;
import com.zixiken.dimdoors.shared.items.ModItems;
import com.zixiken.dimdoors.shared.tileentities.TileEntityFloatingRift;

import java.util.*;
import net.minecraft.block.Block;
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
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRift extends Block implements ITileEntityProvider {

    public static final String ID = "rift";

    private final ArrayList<Block> blocksImmuneToRift; // TODO

    public BlockRift() {
        super(Material.LEAVES); //Fire is replacable. We do not want this block to be replacable. We do want to walk through it though... TODO
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        setUnlocalizedName(ID);
        setTickRandomly(true);
        setBlockUnbreakable();

        blocksImmuneToRift = new ArrayList<>();
        blocksImmuneToRift.add(ModBlocks.FABRIC);
        blocksImmuneToRift.add(ModBlocks.DIMENSIONAL_DOOR);
        blocksImmuneToRift.add(ModBlocks.WARP_DIMENSIONAL_DOOR);
        blocksImmuneToRift.add(ModBlocks.DIMENSIONAL_TRAPDOOR);
        blocksImmuneToRift.add(ModBlocks.UNSTABLE_DIMENSIONAL_DOOR);
        blocksImmuneToRift.add(ModBlocks.RIFT);
        blocksImmuneToRift.add(ModBlocks.TRANSIENT_DIMENSIONAL_DOOR);
        blocksImmuneToRift.add(ModBlocks.GOLD_DIMENSIONAL_DOOR);
        blocksImmuneToRift.add(ModBlocks.GOLD_DOOR);
        blocksImmuneToRift.add(ModBlocks.PERSONAL_DIMENSIONAL_DOOR);
        blocksImmuneToRift.add(ModBlocks.QUARTZ_DOOR);

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
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
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
     * Returns true if the given side of this block type should be
     * rendered (if it's solid or not), if the adjacent block is at the given
     * coordinates. Args: blockAccess, x, y, z, side
     */
    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE; //Tile Entity Special Renderer
    }

    public void dropWorldThread(World world, BlockPos pos, Random random) {
        if (!world.getBlockState(pos).equals(Blocks.AIR)) {
            ItemStack thread = new ItemStack(ModItems.WORLD_THREAD, 1);
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
     * regulates the renderDoorRift effect, especially when multiple rifts start to link
     * up. Has 3 main parts- Grows toward and away from nearest rift, bends
     * toward it, and a randomization function
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        //ArrayList<BlockPos> targets = findReachableBlocks(worldIn, pos, 2, false); // TODO
        TileEntityFloatingRift rift = (TileEntityFloatingRift) worldIn.getTileEntity(pos);

        if (0 < 0) {
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(new ParticleRiftEffect.GogglesRiftEffect( // TODO: this effect was unfinished in the 1.6.4 mod too
                    worldIn,
                    pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
                    rand.nextGaussian() * 0.01D, rand.nextGaussian() * 0.01D, rand.nextGaussian() * 0.01D));
        }

        if (rift.shouldClose) { // Renders an opposite color effect if it is being closed by the rift remover
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(new ParticleRiftEffect.ClosingRiftEffect(
                    worldIn,
                    pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
                    rand.nextGaussian() * 0.01D, rand.nextGaussian() * 0.01D, rand.nextGaussian() * 0.01D));
        }
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
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityFloatingRift();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntityFloatingRift rift = (TileEntityFloatingRift) worldIn.getTileEntity(pos);
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return false;
    }

    public TileEntityFloatingRift getRiftTile(World world, BlockPos pos, IBlockState state) {
        return (TileEntityFloatingRift) world.getTileEntity(pos);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return false; // TODO
    }
}
