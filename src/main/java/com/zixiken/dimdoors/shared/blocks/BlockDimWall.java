package com.zixiken.dimdoors.shared.blocks;

import java.util.List;
import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.DDConfig;
import com.zixiken.dimdoors.shared.TeleporterDimDoors;
import com.zixiken.dimdoors.shared.util.DDRandomUtils;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.world.limbodimension.LimboDecay;
import com.zixiken.dimdoors.shared.world.limbodimension.WorldProviderLimbo;
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

    public static final String ID = "blockFabric";
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
        setDefaultState(blockState.getBaseState().withProperty(TYPE, EnumType.REALITY));
        setSoundType(SoundType.STONE);

        setTickRandomly(true);
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
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        if (state.getValue(TYPE).equals(EnumType.ANCIENT) || state.getValue(TYPE).equals(EnumType.ETERNAL)) {
            return SUPER_HIGH_HARDNESS;
        } else {
            return this.blockHardness;
        }
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        IBlockState state = world.getBlockState(pos);
        if (state.getValue(TYPE).equals(EnumType.ANCIENT) || state.getValue(TYPE).equals(EnumType.ETERNAL)) {
            return SUPER_EXPLOSION_RESISTANCE;
        } else {
            return super.getExplosionResistance(world, pos, exploder, explosion);
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        if (state.getValue(TYPE).equals(EnumType.ANCIENT) || state.getValue(TYPE).equals(EnumType.ETERNAL)) {
            //Return 0 to avoid dropping Ancient or Eternal Fabric even if the player somehow manages to break it
            return EnumType.REALITY.ordinal();
        } else {
            return state.getValue(TYPE).ordinal();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < 5; i++) {
            subItems.add(new ItemStack(itemIn, 1, i));
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
     *
     * @param world the world that this block is in
     * @param pos the position this block is at
     * @param state the state this block is in
     * @param player the player right-clicking the block
     * @param hand the hand the player is using
     * @param heldItem the item the player is holding in that hand
     * @param side the side of the block that is being clicked
     * @param hitX the x coordinate of the exact place the player is clicking on
     * the block
     * @param hitY the y coordinate ...
     * @param hitZ the z coordinate ...
     * @return whether or not the item in the player's hand should be used or
     * not?
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (heldItem != null && heldItem.getItem() instanceof ItemBlock
                && (state.getValue(TYPE).equals(EnumType.REALITY) || state.getValue(TYPE).equals(EnumType.ALTERED))) {
            Block block = Block.getBlockFromItem(heldItem.getItem());
            if (!state.isNormalCube() || block.hasTileEntity(block.getDefaultState())
                    || block == this //this also keeps it from being replaced by Ancient Fabric
                    || player.isSneaking()) {
                return false;
            }
            if (!world.isRemote) { //@todo on a server, returning false or true determines where the block gets placed?
                if (!player.isCreative()) {
                    heldItem.stackSize--;
                }
                world.setBlockState(pos, block.getStateForPlacement(world, pos, side, hitX, hitY, hitZ, heldItem.getMetadata(), player, heldItem)); //choosing getStateForPlacement over getDefaultState, because it will cause directional blocks, like logs to rotate correctly
            }
            return true;
        }
        return false;
    }

    public enum EnumType implements IStringSerializable {
        REALITY("reality"),
        ANCIENT("ancient"),
        ALTERED("altered"),
        UNRAVELED("unraveled"),
        ETERNAL("eternal");

        private final String name;

        EnumType(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) { //@todo move this to update or updateTick, because of the teleport and because you now have to fall through the block in order to teleport (wouldn't that be nice though?)
        if (state.getValue(TYPE) == EnumType.ETERNAL && world.provider instanceof WorldProviderLimbo && entity instanceof EntityPlayer) {
            Location origLocation = new Location(world, pos);
            Location transFormedLocation = DDRandomUtils.transformLocationRandomly(DDConfig.getOwCoordinateOffsetBase(), DDConfig.getOwCoordinateOffsetPower(), DDConfig.getMaxDungeonDepth(), origLocation);

            BlockPos correctedPos = DimDoors.proxy.getWorldServer(0).getTopSolidOrLiquidBlock(transFormedLocation.getPos());
            Location correctedLocation = new Location(0, correctedPos);
            TeleporterDimDoors.instance().teleport(entity, correctedLocation);
        }
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) { //if this creates more problems, because everything ticks, we should probably move this to its own class again
        //Make sure this block is unraveled fabric in Limbo
        if (state.getValue(TYPE) == EnumType.UNRAVELED && world.provider instanceof WorldProviderLimbo) {
            LimboDecay.applySpreadDecay(world, pos);
        }
    }
}
