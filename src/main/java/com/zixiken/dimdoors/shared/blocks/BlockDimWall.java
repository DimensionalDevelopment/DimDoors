package com.zixiken.dimdoors.shared.blocks;

import java.util.List;
import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockDimWall extends Block {

    public static final String ID = "blockDimWall";
    public static final PropertyEnum<BlockDimWall.EnumType> TYPE = PropertyEnum.<BlockDimWall.EnumType>create("type", BlockDimWall.EnumType.class);

    private static final float SUPER_HIGH_HARDNESS = 10000000000000F;
    private static final float SUPER_EXPLOSION_RESISTANCE = 18000000F;

    public BlockDimWall() {
        super(Material.IRON);
        this.setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setLightLevel(1.0F);
        setHardness(0.1F);
        setUnlocalizedName(ID);
        setRegistryName(ID);
        setDefaultState(blockState.getBaseState().withProperty(TYPE, EnumType.FABRIC));
        setSoundType(SoundType.STONE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        if (meta >= 0 && meta <= 2) {
            return getDefaultState().withProperty(TYPE, EnumType.values()[meta]);
        } else {
            return getDefaultState();
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        if (!state.getValue(TYPE).equals(EnumType.ANCIENT)) {
            return this.blockHardness;
        } else {
            return SUPER_HIGH_HARDNESS;
        }
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        if (!world.getBlockState(pos).getValue(TYPE).equals(EnumType.ANCIENT)) {
            return super.getExplosionResistance(world, pos, exploder, explosion);
        } else {
            return SUPER_EXPLOSION_RESISTANCE;
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        int metadata = state.getValue(TYPE).ordinal();
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

    /**
     * replaces the block clicked with the held block, instead of placing the
     * block on top of it. Shift click to disable.
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        //Check if the metadata value is 0 -- we don't want the user to replace Ancient Fabric
        if (heldItem != null && !state.getValue(TYPE).equals(EnumType.ANCIENT)) {
            Block block = Block.getBlockFromItem(heldItem.getItem());
            if (!state.isNormalCube() || block.hasTileEntity(block.getDefaultState())
                    || block == this //this also keeps it from being replaced by Ancient Fabric
                    || player.isSneaking()) {
                return false;
            }
            if (!world.isRemote) { //@todo on a server, returning false or true determines where the block gets placed?
                if (!player.capabilities.isCreativeMode) {
                    heldItem.stackSize--;
                }
                world.setBlockState(pos, block.getStateForPlacement(world, pos, side, hitX, hitY, hitZ, 0, player, heldItem)); //choosing getStateForPlacement over getDefaultState, because it will cause directional blocks, like logs to rotate correctly
                heldItem.onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ);
            }
            return true;
        }
        return false;
    }

    public static enum EnumType implements IStringSerializable {
        FABRIC("fabric"),
        ANCIENT("ancient"),
        ALTERED("altered");

        private final String name;

        private EnumType(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }
}
