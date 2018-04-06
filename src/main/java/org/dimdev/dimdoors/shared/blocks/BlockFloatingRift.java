package org.dimdev.dimdoors.shared.blocks;

import java.util.*;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.client.ParticleRiftEffect;
import org.dimdev.dimdoors.shared.ModConfig;
import org.dimdev.dimdoors.shared.items.ModItems;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;
import org.dimdev.ddutils.blocks.BlockSpecialAir;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFloatingRift extends BlockSpecialAir implements ITileEntityProvider, IRiftProvider<TileEntityFloatingRift> {

    public static final String ID = "rift";

    public BlockFloatingRift() {
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        setUnlocalizedName(ID);
        setTickRandomly(true);
        setResistance(6000000.0F); // Same as bedrock
        setLightLevel(0.5f);
    }

    @Override
    public TileEntityFloatingRift createNewTileEntity(World world, int meta) {
        return new TileEntityFloatingRift();
    }

    @Override
    @SuppressWarnings("deprecation")
    public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
        return MapColor.BLUE;
    }

    // Unregister the rift on break
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntityFloatingRift rift = (TileEntityFloatingRift) world.getTileEntity(pos);
        rift.unregister();
        super.breakBlock(world, pos, state);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos) {
        if (ModConfig.general.riftBoundingBoxInCreative) {
            EntityPlayer player = DimDoors.proxy.getLocalPlayer();
            if (player != null && player.isCreative()) {
                return blockState.getBoundingBox(world, pos);
            }
        }
        return null;
    }

    @Override
    public TileEntityFloatingRift getRift(World world, BlockPos pos, IBlockState state) {
        return (TileEntityFloatingRift) world.getTileEntity(pos);
    }

    public void dropWorldThread(World world, BlockPos pos, Random random) {
        if (!world.getBlockState(pos).equals(Blocks.AIR)) {
            ItemStack thread = new ItemStack(ModItems.WORLD_THREAD, 1);
            world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), thread));
        }
    }

    // Render rift effects
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        TileEntity tileEntity = world.getTileEntity(pos);
        // randomDisplayTick can be called before the tile entity is created in multiplayer
        if (!(tileEntity instanceof TileEntityFloatingRift)) return;
        TileEntityFloatingRift rift = (TileEntityFloatingRift) tileEntity;

        if (rift.closing) { // Renders an opposite color effect if it is being closed by the rift remover
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(new ParticleRiftEffect.ClosingRiftEffect(
                    world,
                    pos.getX() + .5, pos.getY() + 1.5, pos.getZ() + .5,
                    rand.nextGaussian() * 0.1D, rand.nextGaussian() * 0.1D, rand.nextGaussian() * 0.1D));
        }

        // TODO: depend on size, stabilization status, direction of particles should be rift orientation
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(new ParticleRiftEffect.Rift(
                world,
                pos.getX() + .5, pos.getY() + 1.5, pos.getZ() + .5,
                rand.nextGaussian() * 0.1D, rand.nextGaussian() * 0.1D, rand.nextGaussian() * 0.1D));
    }
}
