package org.dimdev.dimdoors.datagen

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mojang.serialization.JsonOps
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.decoration.PaintingVariant
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.material.Fluid
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.world.decay.DecayPatternHolder
import org.dimdev.dimdoors.world.decay.conditions.BlockDecayCondition
import org.dimdev.dimdoors.world.decay.conditions.DecayCondition
import org.dimdev.dimdoors.world.decay.conditions.DimensionDecayCondition
import org.dimdev.dimdoors.world.decay.conditions.FluidDecayCondition
import org.dimdev.dimdoors.world.decay.pattern.CompoundDecayPattern
import org.dimdev.dimdoors.world.decay.pattern.DecayPattern
import org.dimdev.dimdoors.world.decay.pattern.PaintingDecayPattern
import org.dimdev.dimdoors.world.decay.results.DecayResult
import org.dimdev.dimdoors.world.decay.results.DoubleBlockDecayResult
import org.dimdev.dimdoors.world.decay.results.FluidDecayResult
import org.dimdev.dimdoors.world.decay.results.NoneDecayResult
import org.dimdev.dimdoors.world.decay.results.SelfDecayResult
import org.dimdev.dimdoors.world.decay.results.SingleBlockDecayResult
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Supplier

abstract class LimboDecayProvider(output: PackOutput, private val registries: CompletableFuture<HolderLookup.Provider?>?) : DataProvider {
    private val decayPatternPathResolver: PackOutput.PathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "decay_patterns")

    override fun run(cache: CachedOutput): CompletableFuture<*> {
        return registries!!.thenCompose { provider ->
            val list = mutableListOf<CompletableFuture<*>>()

            val consumer = Consumer<DecayPatternHolder> { patternHolder ->
                val element = JsonOps.INSTANCE.withEncoder(DecayPattern.CODEC).apply(patternHolder.value).getOrThrow()
                val outputPath = decayPatternPathResolver.json(patternHolder.id)
                list.add(DataProvider.saveStable(cache, element, outputPath))
            }

            generatePatterns(provider!!, consumer)

            CompletableFuture.allOf(*list.toTypedArray())
        }
    }

    protected abstract fun generatePatterns(provider: HolderLookup.Provider, consumer: Consumer<DecayPatternHolder>)

    protected fun addPaintingPattern(key: ResourceKey<PaintingVariant>, decaysInto: TagKey<PaintingVariant>): DecayPatternHolder.Builder =
        DecayPatternHolder.builder(key.location()).pattern(PaintingDecayPattern.builder().from(decaysInto).to(key))

    protected fun addPattern(to: Any, from: Any): DecayPatternHolder.Builder = addPattern(DimensionalDoors.id(getId(to)!!), to, from)

    protected fun addPattern(id: ResourceLocation, to: Any, from: Any): DecayPatternHolder.Builder = createPatterData(id, from, to)

    protected fun getPredicate(obj: Any): DecayCondition = when (obj) {
        is TagKey<*> -> obj.cast(Registries.BLOCK).map { BlockDecayCondition.of(it) }.orElse(null)
            ?: obj.cast(Registries.FLUID).map { FluidDecayCondition.of(it) }.orElse(null)
            ?: obj.cast(Registries.DIMENSION_TYPE).map { DimensionDecayCondition.of(it) }.orElse(null)
        is ResourceKey<*> -> obj.cast(Registries.BLOCK).map { BlockDecayCondition.of(it) }.orElse(null)
            ?: obj.cast(Registries.FLUID).map { FluidDecayCondition.of(it) }.orElse(null)
            ?: obj.cast(Registries.DIMENSION_TYPE).map { DimensionDecayCondition.of(it) }.orElse(null)
        is Supplier<*> -> when (val supplied = obj.get()) {
            is Block -> BlockDecayCondition.of(supplied)
            is Fluid -> FluidDecayCondition.of(supplied)
            else -> null
        }
        is Block -> BlockDecayCondition.of(obj)
        is Fluid -> FluidDecayCondition.of(obj)
        else -> null
    } ?: DecayCondition.NONE

    protected fun getId(obj: Any): String? = when (obj) {
        is ResourceKey<*> -> obj.registryKey().location().path
        is Block -> obj.builtInRegistryHolder().key().location().path
        is Fluid -> obj.builtInRegistryHolder().key().location().path
        else -> null
    }

    protected fun getProcessor(obj: Any, entropy: Int = 1): DecayResult = when (val resolved = if (obj is Supplier<*>) obj.get() else obj) {
        is Block -> SingleBlockDecayResult(entropy, 0.0f, resolved)
        is Fluid -> FluidDecayResult(entropy, 0.0f, resolved)
        else -> NoneDecayResult.instance()
    }

    protected fun createOxidizationChain(consumer: Consumer<DecayPatternHolder>, provider: HolderLookup.Provider, vararg blocks: Block) {
        for (i in 0 until blocks.size - 2 step 2) {
            val from = blocks[i]
            val fromWaxed = blocks[i + 1]
            val to = blocks[i + 2]
            val toWaxed = blocks[i + 3]

            addPattern(to, from).accept(consumer, provider)
            addPattern(DimensionalDoors.id("dewaxed_" + getId(from)), from, fromWaxed).accept(consumer, provider)
            addPattern(DimensionalDoors.id("dewaxed_" + getId(to)), to, toWaxed).accept(consumer, provider)
        }
    }

    protected fun getBlock(id: ResourceLocation): Block = BuiltInRegistries.BLOCK.get(id)

    protected fun getBlockId(block: Block): ResourceLocation = BuiltInRegistries.BLOCK.getKey(block)

    protected fun turnIntoSelf(resourceLocation: ResourceLocation, before: Any): DecayPatternHolder =
        DecayPatternHolder(resourceLocation, CompoundDecayPattern(listOf(getPredicate(before)), SelfDecayResult.instance()))

    protected fun createPatterData(id: ResourceLocation, before: Any, after: Any): DecayPatternHolder.Builder =
        DecayPatternHolder.builder(id).pattern(CompoundDecayPattern.builder().condition(getPredicate(before)).result(getProcessor(after)))

    protected fun addDoublePattern(before: Any, after: Block) {
        addDoublePattern(DimensionalDoors.id(getId(after)!!), after, before)
    }

    fun addDoublePattern(id: ResourceLocation, after: Any, before: Any): DecayPatternHolder.Builder =
        DecayPatternHolder.builder(id).pattern(CompoundDecayPattern.builder().condition(getPredicate(before)).result(DoubleBlockDecayResult(1, 0.0f, after as Block)))

    fun createDoublePattern(id: ResourceLocation, before: Any, after: Block): DecayPatternHolder =
        DecayPatternHolder(id, CompoundDecayPattern(listOf(getPredicate(before)), DoubleBlockDecayResult(1, 0.0f, after)))

    companion object {
        private val LOGGER: Logger = LogManager.getLogger()
        private val GSON: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

        @JvmStatic
        protected fun getOutput(rootOutput: Path, lootTableId: ResourceLocation): Path =
            rootOutput.resolve("data/" + lootTableId.namespace + "/decay_patterns/" + lootTableId.path + ".json")
    }
}
