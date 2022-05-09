package org.dimdev.dimdoors.shared.blocks;

import java.util.*;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.client.RiftParticle;
import org.dimdev.dimdoors.shared.ModConfig;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;
import org.dimdev.ddutils.blocks.BlockSpecialAir;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.dimdoors.shared.world.ModDimensions;

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
        return MapColor.BLACK;
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
        if (ModConfig.rifts.riftBoundingBoxInCreative) {
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

    // Render rift effects
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        TileEntity tileEntity = world.getTileEntity(pos);
        // randomDisplayTick can be called before the tile entity is created in multiplayer
        if (!(tileEntity instanceof TileEntityFloatingRift)) return;
        TileEntityFloatingRift rift = (TileEntityFloatingRift) tileEntity;

        boolean outsidePocket = !ModDimensions.isDimDoorsPocketDimension(world);
        double speed = 0.1D;

        if (rift.closing) {
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(new RiftParticle(
                    world,
                    pos.getX() + .5, pos.getY() + 1.5, pos.getZ() + .5,
                    rand.nextGaussian() * speed, rand.nextGaussian() * speed, rand.nextGaussian() * speed,
                    outsidePocket ? 0.8f : 0.4f, 0.55f, 2000, 2000));
        }

        FMLClientHandler.instance().getClient().effectRenderer.addEffect(new RiftParticle(
                world,
                pos.getX() + .5, pos.getY() + 1.5, pos.getZ() + .5,
                rand.nextGaussian() * speed, rand.nextGaussian() * speed, rand.nextGaussian() * speed,
                outsidePocket ? 0.0f : 0.7f, 0.55f, rift.stabilized ? 750 : 2000, rift.stabilized ? 750 : 2000));
    }
}
