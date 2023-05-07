package org.dimdev.dimdoors.block.door;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.block.AfterMoveCollidableBlock;
import org.dimdev.dimdoors.api.block.CustomBreakBlock;
import org.dimdev.dimdoors.api.block.ExplosionConvertibleBlock;
import org.dimdev.dimdoors.api.entity.LastPositionProvider;
import org.dimdev.dimdoors.api.util.math.MathUtil;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.dimdev.dimdoors.block.CoordinateTransformerBlock;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class DimensionalDoorBlock extends WaterLoggableDoorBlock implements RiftProvider<EntranceRiftBlockEntity>, CoordinateTransformerBlock, ExplosionConvertibleBlock, CustomBreakBlock, AfterMoveCollidableBlock {
	public DimensionalDoorBlock(BlockBehaviour.Properties settings, BlockSetType blockSetType) {
		super(settings, blockSetType);
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if (world.isClientSide || entity instanceof ServerPlayer) {
			return;
		}
		onCollision(state, world, pos, entity, entity.position().subtract(((LastPositionProvider) entity).getLastPos()));
		super.entityInside(state, world, pos, entity);
	}


	@Override
	public InteractionResult onAfterMovePlayerCollision(BlockState state, ServerLevel world, BlockPos pos, ServerPlayer player, Vec3 positionChange) {
		return onCollision(state, world, pos, player, positionChange);
	}

	private InteractionResult onCollision(BlockState state, Level world, BlockPos pos, Entity entity, Vec3 positionChange) {
		BlockPos top = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos : pos.above();
		BlockPos bottom = top.below();
		BlockState doorState = world.getBlockState(bottom);

		// TODO: decide whether door should need to be open for teleportation
		if (doorState.getBlock() != this || !doorState.getValue(DoorBlock.OPEN)) { // '== this' to check if not half-broken
			return InteractionResult.PASS;
		}
		Vec3 currentPos = entity.position();
		Vec3 previousPos = currentPos.subtract(positionChange);

		// TODO: rewrite this to be usable more universally
		// check whether portal plane was traversed
		double portalHalfWidth = 0.5;
		double portalHeight = 2;
		// check in DefaultTransformation for the correct offset of the portal planes
		double portalOffsetFromCenter = 0.31;
		Vec3 portalNormal = Vec3.atLowerCornerOf(state.getValue(FACING).getOpposite().getNormal().north());
		Vec3 origin = Vec3.atBottomCenterOf(bottom);
		Vec3 bottomMiddlePortalPoint = origin.add(portalNormal.scale(portalOffsetFromCenter));

		double dotCurrent = portalNormal.dot(currentPos.subtract(bottomMiddlePortalPoint));
		double dotPrevious = portalNormal.dot(previousPos.subtract(bottomMiddlePortalPoint));
		if (!(dotCurrent <= 0 && dotPrevious >= 0) && !(dotCurrent >= 0 && dotPrevious <= 0) || (dotCurrent == 0 && dotPrevious == 0)) {
			// start and end point of movement are on same side of the portal plane or both inside the plane
			return InteractionResult.PASS;
		}

		Vec3 yVec = new Vec3(0, 1, 0);
		Vec3 xzVec = portalNormal.cross(yVec);

		Vec3 vecFromPreviousPosToPortalPlane = bottomMiddlePortalPoint.subtract(previousPos);
		Vec3 normalizedPositionChange = positionChange.normalize();
		Vec3 pointOfIntersection = previousPos.add(normalizedPositionChange.scale(vecFromPreviousPosToPortalPlane.dot(normalizedPositionChange) / normalizedPositionChange.dot(normalizedPositionChange)));

		// figure out whether the point of Intersection is actually inside the portal plane;
		Vec3 intersectionRelativeToPortalPlane = pointOfIntersection.subtract(bottomMiddlePortalPoint);
		double relativeIntersectionHeight = intersectionRelativeToPortalPlane.dot(yVec);
		double relativeIntersectionWidth = intersectionRelativeToPortalPlane.dot(xzVec);
		if (relativeIntersectionHeight < 0 || relativeIntersectionHeight > portalHeight || Math.abs(relativeIntersectionWidth) > portalHalfWidth) {
			// intersection is outside of plane width/ height
			return InteractionResult.PASS;
		}

		// TODO: replace with dimdoor cooldown?
		if (entity.isOnPortalCooldown()) {
			entity.setPortalCooldown();
			return InteractionResult.PASS;
		}
		entity.setPortalCooldown();


		this.getRift(world, pos, state).teleport(entity);
		if (DimensionalDoors.getConfig().getDoorsConfig().closeDoorBehind) {
			world.setBlockAndUpdate(top, world.getBlockState(top).setValue(DoorBlock.OPEN, false));
			world.setBlockAndUpdate(bottom, world.getBlockState(bottom).setValue(DoorBlock.OPEN, false));
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		state = state.cycle(OPEN);
		world.setBlock(pos, state, 10);
		if (!world.isClientSide && state.getValue(WATERLOGGED)) {
			world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		world.levelEvent(player, state.getValue(OPEN) ? this.material == Material.METAL ? 1005 : 1006 : this.material == Material.METAL ? 1011 : 1012, pos, 0);
		world.gameEvent(player, this.isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
		return super.canBeReplaced(blockState, blockPlaceContext) || blockState.getBlock() == ModBlocks.DETACHED_RIFT.get();
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
			return null;
		}
		return new EntranceRiftBlockEntity(pos, state);
	}

	public void createDetachedRift(Level world, BlockPos pos) {
		createDetachedRift(world, pos, world.getBlockState(pos));
	}

	/*
	 TODO: rewrite so it can only be used from the lower door block.
	  I fear this method may be called twice otherwise.
	  ~CreepyCre
	 */
	public void createDetachedRift(Level world, BlockPos pos, BlockState state) {
		DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
		BlockPos blockPos = pos;
		BlockState blockState = world.getBlockState(pos);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
			blockPos = pos.below();
			blockState = world.getBlockState(blockPos);
			blockEntity = world.getBlockEntity(blockPos);
		}
		if (blockEntity instanceof EntranceRiftBlockEntity
				&& blockState.getValue(HALF) == DoubleBlockHalf.LOWER) {
			world.setBlockAndUpdate(blockPos, ModBlocks.DETACHED_RIFT.get().defaultBlockState().setValue(WATERLOGGED, blockState.getValue(WATERLOGGED)));
			((DetachedRiftBlockEntity) world.getBlockEntity(blockPos)).setData(((EntranceRiftBlockEntity) blockEntity).getData());
		}
	}

	@Override
	public void wasExploded(Level world, BlockPos pos, Explosion explosion) {
		if(world.getBlockState(pos).isAir()) {
			//LOGGER.log(Level.ERROR, "IS AIR");
			return;
		}
		if(world.isClientSide()) {
			return;
		}
		//LOGGER.log(Level.ERROR, "WAS DESTROYED BY EXPLOSION");
		BlockState state = world.getBlockState(pos);
		super.wasExploded(world, pos, explosion);
	}
	@Override
	public EntranceRiftBlockEntity getRift(Level world, BlockPos pos, BlockState state) {
		BlockEntity bottomEntity;
		BlockEntity topEntity;

		if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
			bottomEntity = world.getBlockEntity(pos);
			topEntity = world.getBlockEntity(pos.above());
		} else {
			bottomEntity = world.getBlockEntity(pos.below());
			topEntity = world.getBlockEntity(pos);
		}

		// TODO: Also notify player in case of error, don't crash
		if (bottomEntity instanceof EntranceRiftBlockEntity && topEntity instanceof EntranceRiftBlockEntity) {
			LOGGER.warn("Dimensional door at " + pos + " in world " + world + " contained two rifts, please report this. Defaulting to bottom.");
			return (EntranceRiftBlockEntity) bottomEntity;
		} else if (bottomEntity instanceof EntranceRiftBlockEntity) {
			return (EntranceRiftBlockEntity) bottomEntity;
		} else if (topEntity instanceof EntranceRiftBlockEntity) {
			return (EntranceRiftBlockEntity) topEntity;
		} else {
			throw new IllegalStateException("Dimensional door at " + pos + " in world " + world + " contained no rift.");
		}
	}

	@Override
	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
		DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
		BlockPos blockPos = pos;
		BlockState blockState = world.getBlockState(pos);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
			blockPos = pos.above();
			blockState = world.getBlockState(blockPos);
			blockEntity = world.getBlockEntity(blockPos);
			if (blockState.is(state.getBlock()) && blockState.getValue(HALF) == DoubleBlockHalf.LOWER) {
				world.setBlock(blockPos, world.getFluidState(blockPos).getType() == Fluids.WATER ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 35);
				world.levelEvent(player, 2001, blockPos, Block.getId(blockState));
			}
			if (blockEntity instanceof EntranceRiftBlockEntity
					&& blockState.getValue(HALF) == DoubleBlockHalf.LOWER
					&& !(player.isCreative()
					&& !DimensionalDoors.getConfig().getDoorsConfig().placeRiftsInCreativeMode)
			) {
				world.setBlockAndUpdate(blockPos, ModBlocks.DETACHED_RIFT.get().defaultBlockState().setValue(WATERLOGGED, blockState.getValue(WATERLOGGED)));
				((DetachedRiftBlockEntity) world.getBlockEntity(blockPos)).setData(((EntranceRiftBlockEntity) blockEntity).getData());
			}
		}
		super.playerWillDestroy(world, pos, state, player);

	}

	@Override
	public VoxelShape getInteractionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
		return Shapes.block();
	}

	@Override
	public TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder(BlockState state, BlockPos pos) {
		return TransformationMatrix3d.builder()
				.inverseTranslate(Vec3.atCenterOf(pos).add(Vec3.atCenterOf(state.getValue(DoorBlock.FACING).getNormal()).scale(-0.31)))
				.inverseRotate(MathUtil.directionEulerAngle(state.getValue(DoorBlock.FACING).getOpposite()));
	}

	@Override
	public TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder(BlockState state, BlockPos pos) {
		return TransformationMatrix3d.builder()
				.inverseRotate(MathUtil.directionEulerAngle(state.getValue(DoorBlock.FACING).getOpposite()));
	}


	@Override
	public boolean isExitFlipped() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isTall(BlockState cachedState) {
		return true;
	}

	@Override
	public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
		super.playerDestroy(world, player, pos, state, blockEntity, itemStack);
		if (player.isCreative() && !DimensionalDoors.getConfig().getDoorsConfig().placeRiftsInCreativeMode) {
			return;
		}

		if (blockEntity instanceof EntranceRiftBlockEntity && state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			world.setBlockAndUpdate(pos, ModBlocks.DETACHED_RIFT.get().defaultBlockState().setValue(WATERLOGGED, state.getValue(WATERLOGGED)));
			((DetachedRiftBlockEntity) world.getBlockEntity(pos)).setData(((EntranceRiftBlockEntity) blockEntity).getData());
		}
	}
/*
	Saved incase needed.
	- Waterpicker
* */
//	static {
//		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
//			if (player.isCreative() && !DimensionalDoors.getConfig().getDoorsConfig().placeRiftsInCreativeMode) {
//				return;
//			}
//			if (blockEntity instanceof EntranceRiftBlockEntity && state.get(HALF) == DoubleBlockHalf.LOWER) {
//				world.setBlockState(pos, ModBlocks.DETACHED_RIFT.getDefaultState().with(WATERLOGGED, state.get(WATERLOGGED)));
//				((DetachedRiftBlockEntity) world.getBlockEntity(pos)).setData(((EntranceRiftBlockEntity) blockEntity).getData());
//			}
//		});
//	}


	@Override
	public InteractionResult explode(Level world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (blockEntity == null) {
			return InteractionResult.PASS;
		}
		createDetachedRift(world, pos, state);
		return InteractionResult.SUCCESS;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return state.getValue(HALF) == DoubleBlockHalf.LOWER ? PushReaction.BLOCK : super.getPistonPushReaction(state);
	}

	@Override
	public InteractionResultHolder<Pair<BlockState, Consumer<BlockEntity>>> customBreakBlock(Level world, BlockPos pos, BlockState blockState, Entity breakingEntity) {
		if (blockState.getValue(HALF) != DoubleBlockHalf.LOWER) {
			return InteractionResultHolder.pass(null);
		}
		RiftData data = ((EntranceRiftBlockEntity) world.getBlockEntity(pos)).getData();
		return InteractionResultHolder.success(new Pair<>(ModBlocks.DETACHED_RIFT.get().defaultBlockState().setValue(WATERLOGGED, blockState.getValue(WATERLOGGED)), blockEntity -> {
			((DetachedRiftBlockEntity) blockEntity).setData(data);
		}));
	}
}
