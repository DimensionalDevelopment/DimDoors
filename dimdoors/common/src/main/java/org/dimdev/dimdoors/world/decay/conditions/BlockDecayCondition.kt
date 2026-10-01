package org.dimdev.dimdoors.world.decay.conditions

import com.mojang.serialization.MapCodec
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import org.dimdev.dimdoors.util.CodecUtils
import org.dimdev.dimdoors.util.CodecUtils.createCodec
import org.dimdev.dimdoors.world.decay.Decay
import java.util.function.Supplier

class BlockDecayCondition(tagOrElementLocation: CodecUtils.TagOrElementLocation<Block>, invert: Boolean) : GenericDecayCondition<Block>(tagOrElementLocation, invert) {
    override val type get() = DecayConditions.BLOCK

    override fun getHolder(context: Decay.DecayContext): Holder<Block> = context.targetBlockState.blockHolder

    override fun registry(): ResourceKey<Registry<Block>> = Registries.BLOCK

    companion object {
        val CODEC: MapCodec<BlockDecayCondition> = createCodec(::BlockDecayCondition, Registries.BLOCK)

        const val KEY: String = "block"



        @JvmStatic @JvmOverloads
        fun of(tag: TagKey<Block>, invert: Boolean = false): BlockDecayCondition = BlockDecayCondition(CodecUtils.TagOrElementLocation.of(tag, Registries.BLOCK), invert)
        @JvmStatic @JvmOverloads
        fun of(key: ResourceKey<Block>, invert: Boolean = false): BlockDecayCondition = BlockDecayCondition(CodecUtils.TagOrElementLocation.of(key, Registries.BLOCK), invert)
        @JvmStatic @JvmOverloads
        fun of(block: Block, invert: Boolean = false): BlockDecayCondition = of(block.builtInRegistryHolder().key(), invert)
        @JvmStatic @JvmOverloads
        fun of(block: Supplier<Block?>, invert: Boolean = false): BlockDecayCondition = of(block.get()!!, invert)
    }
}
