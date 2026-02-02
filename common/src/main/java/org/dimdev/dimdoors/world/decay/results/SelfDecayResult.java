package org.dimdev.dimdoors.world.decay.results;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.Decay;
import org.dimdev.dimdoors.world.decay.DecaySource;

import java.util.List;

import static org.dimdev.dimdoors.world.decay.results.DecayResultType.SELF_RESULT_TYPE;

public class SelfDecayResult implements DecayResult {
    public static final String KEY = "self";

    private static final SelfDecayResult instance = new SelfDecayResult();

    public static SelfDecayResult instance() {
        return instance;
    }

    @Override
    public DecayResultType<SelfDecayResult> getType() {
        return SELF_RESULT_TYPE.get();
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
