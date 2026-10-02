package org.dimdev.dimdoors.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import org.dimdev.dimdoors.world.decay.DecayPatternHolder
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

//TODO: Populate and Connect
class DisintegrationDecayProvider(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider?>?) : LimboDecayProvider(output, registries) {
    override fun generatePatterns(provider: HolderLookup.Provider, consumer: Consumer<DecayPatternHolder>) {}

    override fun getName(): String = "Disintegration Decay Patterns"
}
