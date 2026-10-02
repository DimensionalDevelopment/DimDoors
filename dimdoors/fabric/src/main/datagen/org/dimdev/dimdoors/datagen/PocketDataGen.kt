package org.dimdev.dimdoors.datagen

import com.mojang.serialization.JsonOps
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimdoors.pockets.generator.PocketGenerator
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

abstract class PocketDataGen(packOutput: PackOutput) : DataProvider {
    private val resolver: PackOutput.PathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "pockets/generators")

    override fun run(cache: CachedOutput): CompletableFuture<*> {
        val list = mutableListOf<CompletableFuture<*>>()

        val consumer = BiConsumer<ResourceLocation, PocketGenerator<*>> { id, generator ->
            val element = JsonOps.INSTANCE.withEncoder(PocketGenerator.CODEC).apply(generator).getOrThrow()
            val outputPath = resolver.json(id)
            list.add(DataProvider.saveStable(cache, element, outputPath))
        }

        generateGenerators(consumer)

        return CompletableFuture.allOf(*list.toTypedArray())
    }

    private fun generateGenerators(consumer: BiConsumer<ResourceLocation, PocketGenerator<*>>) {

    }
}
