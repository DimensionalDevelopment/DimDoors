package org.dimdev.dimdoors.block.door;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.explosion.Explosion;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
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

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.Consumer;

public class DimensionalDoorBlock extends WaterLoggableDoorBlock implements RiftProvider<EntranceRiftBlockEntity>, CoordinateTransformerBlock, ExplosionConvertibleBlock, CustomBreakBlock, AfterMoveCollidableBlock {
	public DimensionalDoorBlock(Settings settings) {
		super(settings);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (world.isClient || entity instanceof ServerPlayerEntity) {
			return;
		}
		onCollision(state, world, pos, entity, entity.getPos().subtract(((LastPositionProvider) entity).getLastPos()));
		super.onEntityCollision(state, world, pos, entity);
	}

	@Override
	public ActionResult onAfterMovePlayerCollision(BlockState state, ServerWorld world, BlockPos pos, ServerPlayerEntity player, Vec3d positionChange) {
		return onCollision(state, world, pos, player, positionChange);
	}

	private ActionResult onCollision(BlockState state, World world, BlockPos pos, Entity entity, Vec3d positionChange) {
		BlockPos top = state.get(HALF) == DoubleBlockHalf.UPPER ? pos : pos.up();
		BlockPos bottom = top.down();
		BlockState doorState = world.getBlockState(bottom);

		// TODO: decide whether door should need to be open for teleportation
		if (doorState.getBlock() != this || !doorState.get(DoorBlock.OPEN)) { // '== this' to check if not half-broken
			return ActionResult.PASS;
		}
		Vec3d currentPos = entity.getPos();
		Vec3d previousPos = currentPos.subtract(positionChange);

		// TODO: rewrite this to be usable more universally
		// check whether portal plane was traversed
		double portalHalfWidth = 0.5;
		double portalHeight = 2;
		// check in DefaultTransformation for the correct offset of the portal planes
		double portalOffsetFromCenter = 0.31;
		Vec3d portalNormal = Vec3d.of(state.get(FACING).getOpposite().getVector());
		Vec3d origin = Vec3d.ofBottomCenter(bottom);
		Vec3d bottomMiddlePortalPoint = origin.add(portalNormal.multiply(portalOffsetFromCenter));

		double dotCurrent = portalNormal.dotProduct(currentPos.subtract(bottomMiddlePortalPoint));
		double dotPrevious = portalNormal.dotProduct(previousPos.subtract(bottomMiddlePortalPoint));
		if (!(dotCurrent <= 0 && dotPrevious >= 0) && !(dotCurrent >= 0 && dotPrevious <= 0) || (dotCurrent == 0 && dotPrevious == 0)) {
			// start and end point of movement are on same side of the portal plane or both inside the plane
			return ActionResult.PASS;
		}

		Vec3d yVec = new Vec3d(0, 1, 0);
		Vec3d xzVec = portalNormal.crossProduct(yVec);

		Vec3d vecFromPreviousPosToPortalPlane = bottomMiddlePortalPoint.subtract(previousPos);
		Vec3d normalizedPositionChange = positionChange.normalize();
		Vec3d pointOfIntersection = previousPos.add(normalizedPositionChange.multiply(vecFromPreviousPosToPortalPlane.dotProduct(normalizedPositionChange) / normalizedPositionChange.dotProduct(normalizedPositionChange)));

		// figure out whether the point of Intersection is actually inside the portal plane;
		Vec3d intersectionRelativeToPortalPlane = pointOfIntersection.subtract(bottomMiddlePortalPoint);
		double relativeIntersectionHeight = intersectionRelativeToPortalPlane.dotProduct(yVec);
		double relativeIntersectionWidth = intersectionRelativeToPortalPlane.dotProduct(xzVec);
		if (relativeIntersectionHeight < 0 || relativeIntersectionHeight > portalHeight || Math.abs(relativeIntersectionWidth) > portalHalfWidth) {
			// intersection is outside of plane width/ height
			return ActionResult.PASS;
		}

		// TODO: replace with dimdoor cooldown?
		if (entity.hasNetherPortalCooldown()) {
			entity.resetNetherPortalCooldown();
			return ActionResult.PASS;
		}
		entity.resetNetherPortalCooldown();


		this.getRift(world, pos, state).teleport(entity);
		if (DimensionalDoorsInitializer.getConfig().getDoorsConfig().closeDoorBehind) {
			world.setBlockState(top, world.getBlockState(top).with(DoorBlock.OPEN, false));
			world.setBlockState(bottom, world.getBlockState(bottom).with(DoorBlock.OPEN, false));
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
		state = state.cycle(OPEN);
		world.setBlockState(pos, state, 10);
		if (!world.isClient && state.get(WATERLOGGED)) {
			world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		world.syncWorldEvent(player, state.get(OPEN) ? this.material == Material.METAL ? 1005 : 1006 : this.material == Material.METAL ? 1011 : 1012, pos, 0);
		world.emitGameEvent(player, this.isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
		return ActionResult.SUCCESS;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		return super.canReplace(state, context) || state.getBlock() == ModBlocks.DETACHED_RIFT;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		if (state.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
			return null;
		}
		return new EntranceRiftBlockEntity(pos, state);
	}

	public void createDetachedRift(World world, BlockPos pos) {
		createDetachedRift(world, pos, world.getBlockState(pos));
	}

	/*
	 TODO: rewrite so it can only be used from the lower door block.
	  I fear this method may be called twice otherwise.
	  ~CreepyCre
	 */
	public void createDetachedRift(World world, BlockPos pos, BlockState state) {
		DoubleBlockHalf doubleBlockHalf = state.get(HALF);
		BlockPos blockPos = pos;
		BlockState blockState = world.getBlockState(pos);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
			blockPos = pos.down();
			blockState = world.getBlockState(blockPos);
			blockEntity = world.getBlockEntity(blockPos);
		}
		if (blockEntity instanceof EntranceRiftBlockEntity
				&& blockState.get(HALF) == DoubleBlockHalf.LOWER) {
			world.setBlockState(blockPos, ModBlocks.DETACHED_RIFT.getDefaultState().with(WATERLOGGED, blockState.get(WATERLOGGED)));
			((DetachedRiftBlockEntity) world.getBlockEntity(blockPos)).setData(((EntranceRiftBlockEntity) blockEntity).getData());
		}
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
		if(world.getBlockState(pos).isAir()) {
			//LOGGER.log(Level.ERROR, "IS AIR");
			return;
		}
		if(world.isClient()) {
			return;
		}
		//LOGGER.log(Level.ERROR, "WAS DESTROYED BY EXPLOSION");
		BlockState state = world.getBlockState(pos);
		super.onDestroyedByExplosion(world, pos, explosion);
	}
	@Override
	public EntranceRiftBlockEntity getRift(World world, BlockPos pos, BlockState state) {
		BlockEntity bottomEntity;
		BlockEntity topEntity;

		if (state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
			bottomEntity = world.getBlockEntity(pos);
			topEntity = world.getBlockEntity(pos.up());
		} else {
			bottomEntity = world.getBlockEntity(pos.down());
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
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		DoubleBlockHalf doubleBlockHalf = state.get(HALF);
		BlockPos blockPos = pos;
		BlockState blockState = world.getBlockState(pos);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
			blockPos = pos.down();
			blockState = world.getBlockState(blockPos);
			blockEntity = world.getBlockEntity(blockPos);
			if (blockState.isOf(state.getBlock()) && blockState.get(HALF) == DoubleBlockHalf.LOWER) {
				world.setBlockState(blockPos, world.getFluidState(blockPos).getFluid() == Fluids.WATER ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState(), 35);
				world.syncWorldEvent(player, 2001, blockPos, Block.getRawIdFromState(blockState));
			}
			if (blockEntity instanceof EntranceRiftBlockEntity
					&& blockState.get(HALF) == DoubleBlockHalf.LOWER
					&& !(player.isCreative()
						&& !DimensionalDoorsInitializer.getConfig().getDoorsConfig().placeRiftsInCreativeMode
						)
			) {
				world.setBlockState(blockPos, ModBlocks.DETACHED_RIFT.getDefaultState().with(WATERLOGGED, blockState.get(WATERLOGGED)));
				((DetachedRiftBlockEntity) world.getBlockEntity(blockPos)).setData(((EntranceRiftBlockEntity) blockEntity).getData());
			}
		}
		super.onBreak(world, pos, state, player);

	}

	@Override
	public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.fullCube();
	}

	@Override
	public TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder(BlockState state, BlockPos pos) {
		return TransformationMatrix3d.builder()
				.inverseTranslate(Vec3d.ofCenter(pos).add(Vec3d.of(state.get(DoorBlock.FACING).getVector()).multiply(-0.31)))
				.inverseRotate(MathUtil.directionEulerAngle(state.get(DoorBlock.FACING).getOpposite()));
	}

	@Override
	public TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder(BlockState state, BlockPos pos) {
		return TransformationMatrix3d.builder()
				.inverseRotate(MathUtil.directionEulerAngle(state.get(DoorBlock.FACING).getOpposite()));
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

	static {
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			if (player.isCreative() && !DimensionalDoorsInitializer.getConfig().getDoorsConfig().placeRiftsInCreativeMode) {
				return;
			}
			if (blockEntity instanceof EntranceRiftBlockEntity && state.get(HALF) == DoubleBlockHalf.LOWER) {
				world.setBlockState(pos, ModBlocks.DETACHED_RIFT.getDefaultState().with(WATERLOGGED, state.get(WATERLOGGED)));
				((DetachedRiftBlockEntity) world.getBlockEntity(pos)).setData(((EntranceRiftBlockEntity) blockEntity).getData());
			}
		});
	}

	@Override
	public ActionResult explode(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (blockEntity == null) {
			return ActionResult.PASS;
		}
		createDetachedRift(world, pos, state);
		return ActionResult.SUCCESS;
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return state.get(HALF) == DoubleBlockHalf.LOWER ? PistonBehavior.BLOCK : super.getPistonBehavior(state);
	}

	@Override
	public TypedActionResult<Pair<BlockState, Consumer<BlockEntity>>> customBreakBlock(World world, BlockPos pos, BlockState blockState, Entity breakingEntity) {
		if (blockState.get(HALF) != DoubleBlockHalf.LOWER) {
			return TypedActionResult.pass(null);
		}
		RiftData data = ((EntranceRiftBlockEntity) world.getBlockEntity(pos)).getData();
		return TypedActionResult.success(new Pair<>(ModBlocks.DETACHED_RIFT.getDefaultState().with(WATERLOGGED, blockState.get(WATERLOGGED)), blockEntity -> {
			((DetachedRiftBlockEntity) blockEntity).setData(data);
		}));
	}
}
