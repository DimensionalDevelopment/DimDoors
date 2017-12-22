package com.zixiken.dimdoors.shared.blocks;

import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.VirtualLocation;
import ddutils.Location;
import ddutils.TeleportUtils;
import com.zixiken.dimdoors.shared.world.limbodimension.LimboDecay;
import com.zixiken.dimdoors.shared.world.limbodimension.WorldProviderLimbo;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFabric extends Block {

    public static final Material FIREPROOF_FABRIC = new Material(MapColor.BLACK);
    public static final String ID = "fabric";
    public static final PropertyEnum<EnumType> TYPE = PropertyEnum.create("type", BlockFabric.EnumType.class);

    public enum EnumType implements IStringSerializable {
        REALITY("reality", 0),
        ANCIENT("ancient", 1),
        ALTERED("altered", 2),
        ANCIENT_ALTERED("ancient_altered", 3),
        UNRAVELED("unraveled", 4),
        ETERNAL("eternal", 5);

        @Getter private final String name;
        @Getter private final int meta;

        EnumType(String name, int meta) {
            this.name = name;
            this.meta = meta;
        }

        public String toString() {
            return name;
        }
    }

    public BlockFabric() {
        super(FIREPROOF_FABRIC);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        setUnlocalizedName(ID);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setHardness(0.1F);
        setLightLevel(1.0F);
        setSoundType(SoundType.STONE);
        setDefaultState(getDefaultState().withProperty(TYPE, EnumType.REALITY));

        setTickRandomly(true);
    }

    // States

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        if (meta < EnumType.values().length) {
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
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public void getSubBlocks(CreativeTabs item, NonNullList<ItemStack> items) {
        for (BlockFabric.EnumType type : EnumType.values()) {
            items.add(new ItemStack(this, 1, type.getMeta()));
        }
    }

    // Block properties

    @Override
    @SuppressWarnings("deprecation")
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        if (state.getValue(TYPE).equals(EnumType.ANCIENT) || state.getValue(TYPE).equals(EnumType.ANCIENT_ALTERED) || state.getValue(TYPE).equals(EnumType.ETERNAL)) {
            return -1; // unbreakable
        } else {
            return super.getBlockHardness(state, world, pos);
        }
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        IBlockState state = world.getBlockState(pos);
        if (state.getValue(TYPE).equals(EnumType.ANCIENT) || state.getValue(TYPE).equals(EnumType.ANCIENT_ALTERED) || state.getValue(TYPE).equals(EnumType.ETERNAL)) {
            return 6000000.0F / 5;
        } else {
            return super.getExplosionResistance(world, pos, exploder, explosion);
        }
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        switch (state.getValue(TYPE)) {
            case REALITY:
            case ANCIENT:
                return MapColor.BLOCK_COLORS[EnumDyeColor.BLACK.getMetadata()];
            case ALTERED:
            case ANCIENT_ALTERED:
                return MapColor.BLOCK_COLORS[EnumDyeColor.WHITE.getMetadata()];
            case UNRAVELED:
                return MapColor.BLOCK_COLORS[EnumDyeColor.GRAY.getMetadata()]; // TODO: make black?
            case ETERNAL:
                return MapColor.BLOCK_COLORS[EnumDyeColor.PINK.getMetadata()];
        }
        return MapColor.BLACK;
    }

    // Block logic

    /**
     * Replace the block clicked with the held block instead of placing the
     * block on top of it. Shift click to disable.
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);

        if (heldItem.getItem() instanceof ItemBlock && (state.getValue(TYPE).equals(EnumType.REALITY) || state.getValue(TYPE).equals(EnumType.ALTERED))) {
            Block block = Block.getBlockFromItem(heldItem.getItem());
            if (!state.isNormalCube() || block.hasTileEntity(block.getDefaultState())
                    || block == this // this also keeps it from being replaced by Ancient Fabric
                    || player.isSneaking()) { // TODO: what if the player is holding shift but not sneaking?
                return false;
            }
            if (!world.isRemote) { //@todo on a server, returning false or true determines where the block gets placed?
                if (!player.isCreative()) heldItem.setCount(heldItem.getCount() - 1);
                world.setBlockState(pos, block.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, heldItem.getMetadata(), player, hand)); //choosing getStateForPlacement over getDefaultState, because it will cause directional blocks, like logs to rotate correctly
            }
            return true;
        }
        return false;
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entityIn) {
        IBlockState state = world.getBlockState(pos);
        if (state.getValue(TYPE) == EnumType.ETERNAL && world.provider instanceof WorldProviderLimbo && entityIn instanceof EntityPlayer) {
            Location loc = VirtualLocation.fromLocation(new Location(world, pos)).projectToWorld();
            BlockPos correctedPos = loc.getWorld().getTopSolidOrLiquidBlock(loc.getPos());
            Random random = new Random();
            TeleportUtils.teleport(entityIn, new Location(loc.getDim(), correctedPos), random.nextFloat() * 360, random.nextFloat() * 360);
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        // Spread unravelled fabric decay in Limbo
        if (state.getValue(TYPE) == EnumType.UNRAVELED && worldIn.provider instanceof WorldProviderLimbo) {
            LimboDecay.applySpreadDecay(worldIn, pos);
        }
    }
}
