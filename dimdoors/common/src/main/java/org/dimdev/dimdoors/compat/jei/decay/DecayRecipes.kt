package org.dimdev.dimdoors.compat.jei.decay

import org.dimdev.dimcore.DimCore
import org.dimdev.dimdoors.compat.recipeviewer.DecayDisplayData
import org.dimdev.dimdoors.decay.Decay.DecayLoader.getPatterns
import kotlin.streams.asSequence

object DecayRecipes {
    @JvmStatic
    val decays: List<DecayDisplayData>
        get() = getPatterns().values
            .flatten()
            .distinct()
            .flatMap { DecayDisplayData.list(it, DimCore.platform.server.registryAccess()).asSequence() }
            .filter { DecayJeiUtil.supports(it) }
}
