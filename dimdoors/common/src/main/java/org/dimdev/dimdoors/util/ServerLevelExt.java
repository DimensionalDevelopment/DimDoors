package org.dimdev.dimdoors.util;

import net.minecraft.world.ticks.LevelTicks;
import org.dimdev.dimdoors.decay.Decay;

public interface ServerLevelExt {
    LevelTicks<Decay.DecayTask> dimensionalDoors$getDecayTicks();
}
