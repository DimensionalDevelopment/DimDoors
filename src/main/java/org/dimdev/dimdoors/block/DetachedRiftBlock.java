package org.dimdev.dimdoors.block;

import java.util.Random;

import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.particle.client.RiftParticleEffect;
import org.dimdev.dimdoors.world.ModDimensions;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class DetachedRiftBlock extends WaterLoggableBlockWithEntity implements RiftProvider<DetachedRiftBlockEntity>, Waterloggable {
	public static final String ID = "rift";
	public DetachedRiftBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public MapColor getDefaultMapColor() {
		return MapColor.BLACK;
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

		boolean outsidePocket = !ModDimensions.isPocketDimension(world);
		double speed = 0.1;

		if (rift.closing) {
			world.addParticle(RiftParticleEffect.of(outsidePocket),
					pos.getX() + .5,
					pos.getY() + .5,
					pos.getZ() + .5,
					rand.nextGaussian() * speed,
					rand.nextGaussian() * speed,
					rand.nextGaussian() * speed
			);
		}

		world.addParticle(RiftParticleEffect.of(outsidePocket, rift.stabilized),
				pos.getX() + .5,
				pos.getY() + .5,
				pos.getZ() + .5,
				rand.nextGaussian() * speed,
				rand.nextGaussian() * speed,
				rand.nextGaussian() * speed
		);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.fullCube();
	}

	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.fullCube();
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new DetachedRiftBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, ModBlockEntityTypes.DETACHED_RIFT, DetachedRiftBlockEntity::tick);
	}
}
