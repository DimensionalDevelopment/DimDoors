package com.zixiken.dimdoors.shared.blocks;

import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.DDConfig;
import com.zixiken.dimdoors.shared.util.RandomUtils;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.util.TeleportUtils;
import com.zixiken.dimdoors.shared.world.limbodimension.LimboDecay;
import com.zixiken.dimdoors.shared.world.limbodimension.WorldProviderLimbo;
import lombok.Getter;
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
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFabric extends Block {

    public static final String ID = "fabric";
    public static final PropertyEnum<EnumType> TYPE = PropertyEnum.create("type", BlockFabric.EnumType.class);

    public enum EnumType implements IStringSerializable {
        REALITY("reality", 0),
        ANCIENT("ancient", 1),
        ALTERED("altered", 2),
        UNRAVELED("unraveled", 3),
        ETERNAL("eternal", 4);

        @Getter private final String name;
        @Getter private final int meta;

        EnumType(String name, int meta) { this.name = name; this.meta = meta; }

        public String toString() {
            return name;
        }
    }

    public BlockFabric() {
        super(Material.CLOTH);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        setUnlocalizedName(ID);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setHardness(0.1F);
        setLightLevel(1.0F);
        setSoundType(SoundType.STONE);

        setTickRandomly(true);
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) { // Just in case
        return false;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        if (meta >= 0 && meta <= 4) {
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
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        if (blockState.getValue(TYPE).equals(EnumType.ANCIENT) || blockState.getValue(TYPE).equals(EnumType.ETERNAL)) {
            return -1; // unbreakable
        } else {
            return blockHardness;
        }
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        IBlockState state = world.getBlockState(pos);
        if (state.getValue(TYPE).equals(EnumType.ANCIENT) || state.getValue(TYPE).equals(EnumType.ETERNAL)) {
            return 6000000.0F / 5;
        } else {
            return super.getExplosionResistance(world, pos, exploder, explosion);
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE).ordinal();
    }

    /**
     * Adds sub blocks to the creative tab based on type.
     * This method has been optimized to add future sub blocks
     * without major code editing.
     *
     * @param itemIn the creative tab blocks are getting dropped into
     * @param items the list that holds items that are registered to the creative
     *              tab
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (BlockFabric.EnumType type : EnumType.values()) {
            items.add(new ItemStack(this, 1, type.getMeta()));
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
    public int quantityDropped(Random random) {
        return 0;
    }

    /**
     * Replaces the block clicked with the held block, instead of placing the
     * block on top of it. Shift click to disable.
     *
     * @param worldIn the world that this block is in
     * @param pos the position this block is at
     * @param state the state this block is in
     * @param playerIn the player right-clicking the block
     * @param hand the hand the player is using
     * @param facing the side of the block that is being clicked
     * @param hitX the x coordinate of the exact place the player is clicking on
     *             the block
     * @param hitY the y coordinate ...
     * @param hitZ the z coordinate ...
     * @return whether or not the item in the player's hand should be used or
     * not?
     */
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = playerIn.getHeldItem(hand);

        if (heldItem.getItem() instanceof ItemBlock
                && (state.getValue(TYPE).equals(EnumType.REALITY) || state.getValue(TYPE).equals(EnumType.ALTERED))) {
            Block block = Block.getBlockFromItem(heldItem.getItem());
            if (!state.isNormalCube() || block.hasTileEntity(block.getDefaultState())
                    || block == this //this also keeps it from being replaced by Ancient Fabric
                    || playerIn.isSneaking()) {
                return false;
            }
            if (!worldIn.isRemote) { //@todo on a server, returning false or true determines where the block gets placed?
                if (!playerIn.isCreative()) {
                    heldItem.setCount(heldItem.getCount()-1);
                }
                worldIn.setBlockState(pos, block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, heldItem.getMetadata(), playerIn)); //choosing getStateForPlacement over getDefaultState, because it will cause directional blocks, like logs to rotate correctly
            }
            return true;
        }
        return false;
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        IBlockState state = worldIn.getBlockState(pos);
        if (state.getValue(TYPE) == EnumType.ETERNAL && worldIn.provider instanceof WorldProviderLimbo && entityIn instanceof EntityPlayer) {
            Location origLocation = new Location(worldIn, pos);
            Location transFormedLocation = RandomUtils.transformLocationRandomly(DDConfig.getOwCoordinateOffsetBase(), DDConfig.getOwCoordinateOffsetPower(), DDConfig.getMaxDungeonDepth(), origLocation);

            BlockPos correctedPos = DimDoors.proxy.getWorldServer(0).getTopSolidOrLiquidBlock(transFormedLocation.getPos());
            Location correctedLocation = new Location(0, correctedPos);
            TeleportUtils.teleport(entityIn, correctedLocation, 0, 0);
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) { //if this creates more problems, because everything ticks, we should probably move this to its own class again
        //Make sure this block is unraveled fabric in Limbo
        if (state.getValue(TYPE) == EnumType.UNRAVELED && worldIn.provider instanceof WorldProviderLimbo) {
            LimboDecay.applySpreadDecay(worldIn, pos);
        }
    }
}
