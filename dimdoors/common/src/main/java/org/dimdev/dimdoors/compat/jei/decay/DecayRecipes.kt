package org.dimdev.dimdoors.compat.jei.decay

import org.dimdev.dimdoors.DimensionalDoors.Companion.server
import org.dimdev.dimdoors.compat.decay.DecayDisplayData
import org.dimdev.dimdoors.world.decay.Decay.DecayLoader.getPatterns
import kotlin.streams.asSequence

object DecayRecipes {
    @JvmStatic
    val decays: List<DecayDisplayData>
        get() = getPatterns().values
            .flatten()
            .distinct()
            .flatMap { DecayDisplayData.list(it, server.registryAccess()).asSequence() }
            .filter { DecayJeiUtil.supports(it) }
}
