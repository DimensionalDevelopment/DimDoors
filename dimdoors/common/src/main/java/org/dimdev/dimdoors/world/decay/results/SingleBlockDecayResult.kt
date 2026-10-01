package org.dimdev.dimdoors.world.decay.results

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.DoublePlantBlock
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import org.dimdev.dimdoors.world.decay.Decay
import org.dimdev.dimdoors.world.decay.DecayInventoryHelper

class SingleBlockDecayResult(entropy: Int, worldThreadChance: Float, block: Block) : BlockDecayResult(entropy, worldThreadChance, block) {
    override val type get() = DecayResults.SINGLE_BLOCK

    override fun process(context: Decay.DecayContext): Int {
        val target = context.targetBlockState
        var pos = context.targetBlockPos
        val newState = block.withPropertiesOf(target)
        val contents = DecayInventoryHelper.takeContents(context.world, pos)

        if (target.block is DoublePlantBlock || target.block is DoorBlock) pos = if (target.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) pos.above() else pos

        context.world.setBlockAndUpdate(pos, newState)
        DecayInventoryHelper.transferOrDrop(context.world, pos, contents)
        return entropy
    }

    override fun produces(): List<DecayResult.Result> = listOf(DecayResult.Result(block, 1))

    companion object {
        @JvmField
        val CODEC: MapCodec<SingleBlockDecayResult> = RecordCodecBuilder.mapCodec { instance -> BlockDecayResult.blockDecayCodec(instance).apply(instance, ::SingleBlockDecayResult) }

        const val KEY: String = "single_block"
    }
}
