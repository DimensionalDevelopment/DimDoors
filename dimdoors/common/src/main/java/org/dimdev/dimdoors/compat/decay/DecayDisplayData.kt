package org.dimdev.dimdoors.compat.decay

import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.world.decay.DecayPatternHolder
import org.dimdev.dimdoors.world.decay.pattern.CompoundDecayPattern
import org.dimdev.dimdoors.world.decay.results.DecayResult
import java.util.stream.Stream

data class DecayDisplayData(
    @JvmField val id: ResourceLocation,
    @JvmField val input: Any,
    @JvmField val outputs: List<DecayResult.Result>
) {
    companion object {
        @JvmStatic
        fun list(patternHolder: DecayPatternHolder, registryAccess: RegistryAccess): Stream<DecayDisplayData> {
            val outputs = patternHolder.value.castOrNull<CompoundDecayPattern>()?.result?.produces() ?: listOf()

            if (outputs.isEmpty()) {
                return Stream.empty()
            }

            return patternHolder.value
                .constructApplicable(registryAccess)
                .distinct()
                .map { input -> DecayDisplayData(patternHolder.id, input, outputs) }
        }
    }
}
