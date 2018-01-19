package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.dimdoors.DimDoors;

import java.util.Random;

public class BlockFabric extends BlockColored {

    public static final Material FABRIC = new Material(MapColor.BLACK);
    public static final String ID = "fabric";

    public BlockFabric() {
        super(FABRIC);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        setUnlocalizedName(ID);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setDefaultState(getDefaultState().withProperty(COLOR, EnumDyeColor.BLACK));
        setHardness(0.1F);
        setSoundType(SoundType.STONE);
        setLightLevel(1);
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);

        Block block = Block.getBlockFromItem(heldItem.getItem());
        if (!block.getDefaultState().isNormalCube() || block.hasTileEntity(block.getDefaultState())
            || block == this
            || player.isSneaking()) { // TODO: what if the player is holding shift but not sneaking?
            return false;
        }
        if (!world.isRemote) { //@todo on a server, returning false or true determines where the block gets placed?
            if (!player.isCreative()) heldItem.setCount(heldItem.getCount() - 1);
            world.setBlockState(pos, block.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, heldItem.getMetadata(), player, hand)); //choosing getStateForPlacement over getDefaultState, because it will cause directional blocks, like logs to rotate correctly
        }
        return true;
    }
}
