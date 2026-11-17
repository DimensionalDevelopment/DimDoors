package org.dimdev.dimdoors.compat.recipeviewer

import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.decay.pattern.CompoundDecayPattern
import org.dimdev.dimdoors.decay.pattern.DecayPattern
import org.dimdev.dimdoors.decay.results.DecayResult
import java.util.stream.Stream

data class DecayDisplayData(
    @JvmField val id: ResourceLocation,
    @JvmField val input: Any,
    @JvmField val outputs: List<DecayResult.Result>
) {
    companion object {
        @JvmStatic
        fun list(patternHolder: Holder<DecayPattern>, registryAccess: RegistryAccess): Stream<DecayDisplayData> {
            val outputs = patternHolder.value().castOrNull<CompoundDecayPattern>()?.result?.produces() ?: listOf()

            if (outputs.isEmpty()) {
                return Stream.empty()
            }

            return patternHolder.value()
                .constructApplicable(registryAccess)
                .distinct()
                .map { input -> DecayDisplayData(patternHolder.unwrapKey().get().location(), input, outputs) }
        }
    }
}
