package org.dimdev.dimdoors.world.decay.results;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.dimdev.dimdoors.world.decay.Decay;
import org.dimdev.dimdoors.world.decay.DecayInventoryHelper;

import java.util.List;

public class DoubleBlockDecayResult extends BlockDecayResult<DoubleBlockDecayResult> {
    public static final MapCodec<DoubleBlockDecayResult> CODEC = RecordCodecBuilder.mapCodec(instance -> blockDecayCodec(instance).apply(instance, DoubleBlockDecayResult::new));

    public static final String KEY = "double_block";


    public DoubleBlockDecayResult(int entropy, float worldThreadChance, Block block) {
    super(entropy, worldThreadChance, block);
    }

    @Override
    public DecayResultType<DoubleBlockDecayResult> getType() {
    return DecayResultType.DOUBLE_BLOCK_RESULT_TYPE;
    }

    @Override
    public int process(Decay.DecayContext context) {
        var target = context.targetBlockState();
        var pos = context.targetBlockPos();
        var world = context.world();
        List<ItemStack> contents = DecayInventoryHelper.takeContents(world, pos);

    if(target.getBlock() instanceof DoorBlock) {
        BlockPos otherPos = target.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos.above();

        Direction facing = target.getValue(DoorBlock.FACING);

        if(target.getValue(DoorBlock.OPEN)) facing = target.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT ? facing.getCounterClockWise() : facing.getClockWise();

        BlockState newState = block.defaultBlockState().setValue(TrapDoorBlock.OPEN, true).setValue(TrapDoorBlock.FACING, facing);

        world.setBlockAndUpdate(pos, newState);
        world.setBlockAndUpdate(otherPos, newState);
            DecayInventoryHelper.transferOrDrop(world, pos, contents);

        return entropy;
    } else if(target.getBlock() instanceof BedBlock) {
        BlockPos otherPos = pos.relative(BedBlock.getConnectedDirection(target));
        BlockState newState = block.defaultBlockState();

        world.setBlockAndUpdate(pos, newState);
        world.setBlockAndUpdate(otherPos, newState);
            DecayInventoryHelper.transferOrDrop(world, pos, contents);
            return entropy;
    }

        DecayInventoryHelper.drop(world, pos, contents);
    return 0;
    }

    @Override
    public List<Result> produces() {
        return List.of(new Result(block, 2));
    }
}
