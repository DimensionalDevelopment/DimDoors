package org.dimdev.dimdoors.world.decay.results

import com.mojang.datafixers.Products.P3
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import java.util.function.Supplier

abstract class BlockDecayResult(protected var entropy: Int, private val worldThreadChance: Float, protected var block: Block) : DecayResult {
    override fun entropy(): Int = entropy

    override fun worldThreadChance(): Float = worldThreadChance

    override fun produces(): List<DecayResult.Result> = listOf(DecayResult.Result(block, 1))

    companion object {
        @JvmStatic
        fun <T : BlockDecayResult> blockDecayCodec(instance: RecordCodecBuilder.Instance<T>): P3<RecordCodecBuilder.Mu<T>, Int, Float, Block> {
            return DecayResult.entropyCodec(instance).and(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter { blockDecayResult -> blockDecayResult.block })
        }

        @JvmStatic
        @JvmOverloads
        fun single(block: Block, entropy: Int = 1): SingleBlockDecayResult = SingleBlockDecayResult(entropy, 0.0f, block)

        @JvmStatic
        @JvmOverloads
        fun single(block: Supplier<out Block>, entropy: Int = 1): SingleBlockDecayResult = single(block.get(), entropy)

        @JvmStatic
        @JvmOverloads
        fun doubly(block: Block, entropy: Int = 1): DoubleBlockDecayResult = DoubleBlockDecayResult(entropy, 0.0f, block)

        @JvmStatic
        @JvmOverloads
        fun doubly(block: Supplier<Block>, entropy: Int = 1): DoubleBlockDecayResult = doubly(block.get(), entropy)
    }
}
