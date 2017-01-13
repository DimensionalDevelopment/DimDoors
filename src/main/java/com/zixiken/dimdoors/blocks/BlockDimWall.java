package com.zixiken.dimdoors.blocks;

import java.util.List;
import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockDimWall extends Block {

    public static final String ID = "blockDimWall";
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 2);

    private static final float SUPER_HIGH_HARDNESS = 10000000000000F;
    private static final float SUPER_EXPLOSION_RESISTANCE = 18000000F;

    public BlockDimWall() {
        super(Material.IRON);
        this.setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setLightLevel(1.0F);
        setHardness(0.1F);
        setUnlocalizedName(ID);
        setRegistryName(ID);
        setDefaultState(blockState.getBaseState().withProperty(TYPE, 0));
        setSoundType(SoundType.STONE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        if (meta >= 0 && meta <= 2) {
            return getDefaultState().withProperty(TYPE, meta);
        } else {
            return getDefaultState();
        }
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, BlockPos pos) {
        if (world.getBlockState(pos).getValue(TYPE) == 1) {
            return false;
        }
        return true;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        if (state.getValue(TYPE) != 1) {
            return this.blockHardness;
        } else {
            return SUPER_HIGH_HARDNESS;
        }
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        if (world.getBlockState(pos).getValue(TYPE) != 1) {
            return super.getExplosionResistance(world, pos, exploder, explosion);
        } else {
            return SUPER_EXPLOSION_RESISTANCE;
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        int metadata = state.getValue(TYPE);
        //Return 0 to avoid dropping Ancient Fabric even if the player somehow manages to break it
        return metadata == 1 ? 0 : metadata;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (int ix = 0; ix < 3; ix++) {
            subItems.add(new ItemStack(itemIn, 1, ix));
        }
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(world.getBlockState(pos)));
    }

    @Override
    public int quantityDropped(Random par1Random) {
        return 0;
    }
}
