package org.dimdev.dimdoors.block;

import java.util.Random;

import net.minecraft.block.*;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.client.RiftParticle;
import org.dimdev.dimdoors.world.ModDimensions;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class DetachedRiftBlock extends Block implements RiftProvider<DetachedRiftBlockEntity> {
    public static final String ID = "rift";

    public DetachedRiftBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new DetachedRiftBlockEntity();
    }

    @Override
    public MaterialColor getDefaultMaterialColor() {
        return MaterialColor.BLACK;
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        ((DetachedRiftBlockEntity) world.getBlockEntity(pos)).unregister();
        super.onBroken(world, pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext entityContext) {
        return VoxelShapes.empty();
    }

    @Override
    public DetachedRiftBlockEntity getRift(World world, BlockPos pos, BlockState state) {
        return (DetachedRiftBlockEntity) world.getBlockEntity(pos);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        // randomDisplayTick can be called before the tile entity is created in multiplayer
        if (!(blockEntity instanceof DetachedRiftBlockEntity)) return;
        DetachedRiftBlockEntity rift = (DetachedRiftBlockEntity) blockEntity;

        boolean outsidePocket = !ModDimensions.isDimDoorsPocketDimension(world);
        double speed = 0.1D;

        if (rift.closing) {
            MinecraftClient.getInstance().particleManager.addParticle(
                    new RiftParticle(
                            (ClientWorld) world,
                            pos.getX() + .5, pos.getY() + 1.5, pos.getZ() + .5,
                            rand.nextGaussian() * speed, rand.nextGaussian() * speed, rand.nextGaussian() * speed,
                            outsidePocket ? 0.8f : 0.4f, 0.55f, 2000, 2000
                    )
            );
        }

        /*MinecraftClient.getInstance().particleManager.addParticle(new RiftParticle(
                (ClientWorld) world,
                pos.getX() + .5, pos.getY() + 1.5, pos.getZ() + .5,
                rand.nextGaussian() * speed, rand.nextGaussian() * speed, rand.nextGaussian() * speed,
                outsidePocket ? 0.0f : 0.7f, 0.55f, rift.stabilized ? 750 : 2000, rift.stabilized ? 750 : 2000)
        );*/
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
