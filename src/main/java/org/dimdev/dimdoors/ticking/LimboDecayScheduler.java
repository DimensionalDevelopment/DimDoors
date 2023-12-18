package org.dimdev.dimdoors.ticking;

import org.dimdev.dimdoors.world.LimboDecay;

/**
 * Handles scheduling of periodic fast Limbo decay operations.
 */
public class LimboDecayScheduler implements IRegularTickReceiver {

    private static final int LIMBO_DECAY_INTERVAL = 10; //Apply fast decay every 10 ticks

    private final LimboDecay decay;

    public LimboDecayScheduler(IRegularTickSender tickSender, LimboDecay decay) {
        this.decay = decay;
        tickSender.registerReceiver(this, LIMBO_DECAY_INTERVAL, false);
    }

    /**
     * Applies fast Limbo decay periodically.
     */
    @Override
    public void notifyTick() {
        decay.applyRandomFastDecay();
    }
}
