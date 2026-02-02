package org.dimdev.dimdoors.world.decay.results;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.Decay;
import org.dimdev.dimdoors.world.decay.DecaySource;

import java.util.List;

public class NoneDecayResult implements DecayResult {
    public static final String KEY = "none";
    private static final NoneDecayResult INSTANCE = new NoneDecayResult();

    private NoneDecayResult() {}

    public static NoneDecayResult instance() {
        return INSTANCE;
    }

    @Override
    public DecayResultType<NoneDecayResult> getType() {
        return DecayResultType.NONE_PROCESSOR_TYPE.get();
    }

    @Override
    public int process(Decay.DecayContext context) {
        return 0;
    }

    @Override
    public List<Result> produces() {
        return List.of();
    }
}
