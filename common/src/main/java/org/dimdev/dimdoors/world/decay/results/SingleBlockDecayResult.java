package org.dimdev.dimdoors.world.decay.results;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.dimdev.dimdoors.world.decay.Decay;
import org.dimdev.dimdoors.world.decay.DecayInventoryHelper;

import java.util.List;

import static org.dimdev.dimdoors.block.UnravelUtil.copyState;

public class SingleBlockDecayResult extends BlockDecayResult<SingleBlockDecayResult> {

    public static final MapCodec<SingleBlockDecayResult> CODEC = RecordCodecBuilder.mapCodec(instance -> blockDecayCodec(instance).apply(instance, SingleBlockDecayResult::new));
    public static final String KEY = "single_block";

    public SingleBlockDecayResult(int entropy, float worldThreadChance, Block block) {
        super(entropy, worldThreadChance, block);
    }

    @Override
    public DecayResultType<SingleBlockDecayResult> getType() {
        return DecayResultType.BLOCK_RESULT_TYPE;
    }

    @Override
    public int process(Decay.DecayContext context) {
        var target = context.targetBlockState();
        var pos = context.targetBlockPos();
        BlockState newState = copyState(block, target);
        List<ItemStack> contents = DecayInventoryHelper.takeContents(context.world(), pos);

        if(target.getBlock() instanceof DoublePlantBlock || target.getBlock() instanceof DoorBlock) pos = target.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? pos.above() : pos;

        context.world().setBlockAndUpdate(pos, newState);
        DecayInventoryHelper.transferOrDrop(context.world(), pos, contents);
        return entropy;
    }

    @Override
    public List<Result> produces() {
        return List.of(new Result(block, 1));
    }
}
