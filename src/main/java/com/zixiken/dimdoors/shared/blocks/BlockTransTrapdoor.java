package com.zixiken.dimdoors.shared.blocks;

import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.tileentities.TileEntityTransTrapdoor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTransTrapdoor extends BlockTrapDoor implements IDimDoor, ITileEntityProvider {

    public static final String ID = "dimensional_trapdoor";

    public BlockTransTrapdoor() {
        super(Material.WOOD);
        setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setHardness(1.0F);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        setSoundType(SoundType.WOOD);
    }

    //Teleports the player to the exit link of that dimension, assuming it is a pocket
    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        enterDimDoor(worldIn, pos, entityIn);
    }

    public boolean checkCanOpen(World world, BlockPos pos) {
        return checkCanOpen(world, pos, null);
    }

    public boolean checkCanOpen(World world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return checkCanOpen(worldIn, pos, playerIn)
                && super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (checkCanOpen(worldIn, pos)) {
            super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        }
    }

    @Override
    public void enterDimDoor(World world, BlockPos pos, Entity entity) {
        IBlockState state = world.getBlockState(pos);
        if (!world.isRemote && state.getValue(BlockTrapDoor.OPEN)) {
            if (entity instanceof EntityPlayer) {
                state.cycleProperty(BlockTrapDoor.OPEN);
                world.markBlockRangeForRenderUpdate(pos, pos);
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityTransTrapdoor();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(getItemDoor(), 1, 0);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(Blocks.TRAPDOOR);
    }

    @Override
    public Item getItemDoor() {
        return Item.getItemFromBlock(ModBlocks.DIMENSIONAL_TRAPDOOR);
    }

    public static boolean isTrapdoorSetLow(IBlockState state) {
        return state.getValue(BlockTrapDoor.HALF) == DoorHalf.BOTTOM;
    }

    @Override
    public boolean isDoorOnRift(World world, BlockPos pos) {
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        // This function runs on the server side after a block is replaced
        // We MUST call super.breakBlock() since it involves removing tile entities
        super.breakBlock(worldIn, pos, state);
    }
}
