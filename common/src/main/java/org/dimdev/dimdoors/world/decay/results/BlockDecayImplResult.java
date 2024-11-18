package org.dimdev.dimdoors.world.decay.results;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.DecayResultType;
import org.dimdev.dimdoors.world.decay.DecaySource;

import static org.dimdev.dimdoors.block.UnravelUtil.copyState;

public class BlockDecayImplResult extends BlockDecayResult<BlockDecayImplResult> {

    public static final MapCodec<BlockDecayImplResult> CODEC = RecordCodecBuilder.mapCodec(instance -> blockDecayCodec(instance).apply(instance, BlockDecayImplResult::new));
    public static final String KEY = "block";

    public BlockDecayImplResult(int entropy, float worldThreadChance, Block block) {
        super(entropy, worldThreadChance, block);
    }

    @Override
    public DecayResultType<BlockDecayImplResult> getType() {
        return DecayResultType.BLOCK_RESULT_TYPE.get();
    }

    @Override
    public int process(Level world, BlockPos pos, BlockState origin, BlockState target, FluidState targetFluid, DecaySource source) {
        BlockState newState = copyState(block, target);

        if(target.getBlock() instanceof DoublePlantBlock) pos = target.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? pos.above() : pos;

        world.setBlockAndUpdate(pos, newState);
        return entropy;
    }
}
