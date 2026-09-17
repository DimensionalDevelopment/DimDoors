package org.dimdev.dimdoors.world.decay.results;

import org.dimdev.dimdoors.world.decay.Decay;

import java.util.List;


public class SelfDecayResult implements DecayResult {
    public static final String KEY = "self";

    private static final SelfDecayResult instance = new SelfDecayResult();

    public static SelfDecayResult instance() {
        return instance;
    }

    @Override
    public DecayResultType<SelfDecayResult> getType() {
        return DecayResultType.SELF_RESULT_TYPE;
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
