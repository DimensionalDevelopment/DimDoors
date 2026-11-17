package org.dimdev.dimdoors.util;

import net.minecraft.world.ticks.LevelChunkTicks;
import org.dimdev.dimdoors.decay.Decay;

public interface LevelChunkExt {
    LevelChunkTicks<Decay.DecayTask> dimensionalDoors$getDecayTicks();

    void dimensionalDoors$setDecayTicks(LevelChunkTicks<Decay.DecayTask> ticks);
}
