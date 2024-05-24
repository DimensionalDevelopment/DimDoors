package org.dimdev.dimdoors.block;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.particle.client.RiftParticleOptions;
import org.dimdev.dimdoors.world.ModDimensions;
import org.jetbrains.annotations.Nullable;

import static org.dimdev.dimdoors.block.DimensionalPortalBlock.Dummy.checkType;

public class DetachedRiftBlock extends WaterLoggableBlockWithEntity implements RiftProvider<DetachedRiftBlockEntity>, SimpleWaterloggedBlock {
	public static final String ID = "rift";
	public DetachedRiftBlock(BlockBehaviour.Properties settings) {
		super(settings);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return null;
	}

	@Override
	public DetachedRiftBlockEntity getRift(Level world, BlockPos pos, BlockState state) {
		return (DetachedRiftBlockEntity) world.getBlockEntity(pos);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return super.use(state, world, pos, player, hand, hit);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		// randomDisplayTick can be called before the tile entity is created in multiplayer
		if (!(blockEntity instanceof DetachedRiftBlockEntity rift)) return;

		boolean outsidePocket = !ModDimensions.isPocketDimension(world);
		double speed = 0.1;

		if (rift.closing) {
			world.addParticle(RiftParticleOptions.of(outsidePocket),
					pos.getX() + .5,
					pos.getY() + .5,
					pos.getZ() + .5,
					rand.nextGaussian() * speed,
					rand.nextGaussian() * speed,
					rand.nextGaussian() * speed
			);
		}

		world.addParticle(RiftParticleOptions.of(outsidePocket, rift.stabilized),
				pos.getX() + .5,
				pos.getY() + .5,
				pos.getZ() + .5,
				rand.nextGaussian() * speed,
				rand.nextGaussian() * speed,
				rand.nextGaussian() * speed
		);
	}

	@Override
	public RenderShape getRenderShape(BlockState blockState) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.empty();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DetachedRiftBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, ModBlockEntityTypes.DETACHED_RIFT.get(), DetachedRiftBlockEntity::tick);
	}
}
