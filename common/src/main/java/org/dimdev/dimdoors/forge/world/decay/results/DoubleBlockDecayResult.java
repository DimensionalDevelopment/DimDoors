package org.dimdev.dimdoors.forge.world.decay.results;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.forge.world.decay.DecayResultType;

public class DoubleBlockDecayResult extends BlockDecayResult<DoubleBlockDecayResult> {
	public static final Codec<DoubleBlockDecayResult> CODEC = RecordCodecBuilder.create(instance -> blockDecayCodec(instance).apply(instance, DoubleBlockDecayResult::new));

	public static final String KEY = "double_block";


	public DoubleBlockDecayResult(int entropy, float worldThreadChance, Block block) {
		super(entropy, worldThreadChance, block);
	}

	@Override
	public DecayResultType<DoubleBlockDecayResult> getType() {
		return DecayResultType.DOUBLE_BLOCK_RESULT_TYPE.get();
	}

	@Override
	public int process(Level world, BlockPos pos, BlockState origin, BlockState target, FluidState targetFluid, DecaySource source) {
		if(target.getBlock() instanceof DoorBlock) {
			BlockPos otherPos = target.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos.above();

			Direction facing = target.getValue(DoorBlock.FACING);

			if(target.getValue(DoorBlock.OPEN)) facing = target.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT ? facing.getCounterClockWise() : facing.getClockWise();

			BlockState newState = block.defaultBlockState().setValue(TrapDoorBlock.OPEN, true).setValue(TrapDoorBlock.FACING, facing);

			world.setBlockAndUpdate(pos, newState);
			world.setBlockAndUpdate(otherPos, newState);

			return entropy;
		} else if(target.getBlock() instanceof BedBlock) {
			BlockPos otherPos = pos.relative(BedBlock.getConnectedDirection(target));
			BlockState newState = block.defaultBlockState();

			world.setBlockAndUpdate(pos, newState);
			world.setBlockAndUpdate(otherPos, newState);
		}

		return 0;
	}

	@Override
	public Object produces(Object prior) {
		return new ItemStack(block, 2);
	}
}
