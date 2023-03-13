package org.dimdev.dimdoors.mixin;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import org.dimdev.dimdoors.api.block.CustomBreakBlock;

@Mixin(Level.class)
public abstract class WorldMixin {

	/**
	 * @author DimDoors
	 * @reason Fluid state hacks
	 */
	@Overwrite
	public FluidState getFluidState(BlockPos pos) {
		Level world = (Level) (Object) this;
		FluidState defState;
		if (world.isOutsideBuildHeight(pos)) defState =  Fluids.EMPTY.defaultFluidState();
		else {
			LevelChunk levelChunk = world.getChunkAt(pos);
			defState =  levelChunk.getFluidState(pos);
		}
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (!(block instanceof CustomBreakBlock)) return defState;
		InteractionResultHolder<Tuple<BlockState, Consumer<BlockEntity>>> result = ((CustomBreakBlock) block).customBreakBlock(world, pos, blockState, null);
		if (!result.getResult().consumesAction()) return defState;
		Tuple<BlockState, Consumer<BlockEntity>> pair = result.getObject();
		return new CustomBreakBlock.HackyFluidState(pair.getA(), pair.getB());
	}

	@Inject(method = "destroyBlock",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
					ordinal = 0,
					shift = At.Shift.AFTER))
	private void applyBlockEntityModification(BlockPos pos, boolean drop, Entity breakingEntity, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir, BlockState blockState, FluidState fluidState) {
		if (!(fluidState instanceof CustomBreakBlock.HackyFluidState)) {
			return;
		}
		Consumer<BlockEntity> blockEntityConsumer = ((CustomBreakBlock.HackyFluidState) fluidState).getBlockEntityConsumer();
		if (blockEntityConsumer == null) {
			return;
		}
		BlockEntity blockEntity = ((Level) (Object) this).getBlockEntity(pos);
		if (blockEntity != null) {
			blockEntityConsumer.accept(blockEntity);
		}
	}

	/*
	This is where I'd inject if it turns out the method used above does actually have an issue
	 */
//	@Inject(method = "Lnet/minecraft/world/World;breakBlock(Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/entity/Entity;I)Z",
//			cancellable = true,
//			at = @At(
//					value = "INVOKE",
//					target = "Lnet/minecraft/world/WorldAccess;syncWorldEvent(ILnet/minecraft/util/math/BlockPos;I)V",
//					ordinal = 0,
//					shift = At.Shift.BY,
//					by = 2
//			)
//	)


}
