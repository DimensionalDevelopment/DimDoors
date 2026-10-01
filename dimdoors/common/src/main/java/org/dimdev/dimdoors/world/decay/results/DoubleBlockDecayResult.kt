package org.dimdev.dimdoors.world.decay.results

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.world.level.block.BedBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.TrapDoorBlock
import net.minecraft.world.level.block.state.properties.DoorHingeSide
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import org.dimdev.dimdoors.world.decay.Decay
import org.dimdev.dimdoors.world.decay.DecayInventoryHelper

class DoubleBlockDecayResult(entropy: Int, worldThreadChance: Float, block: Block) : BlockDecayResult(entropy, worldThreadChance, block) {
    override val type get() = DecayResults.DOUBLE_BLOCK

    override fun process(context: Decay.DecayContext): Int {
        val target = context.targetBlockState
        val pos = context.targetBlockPos
        val world = context.world
        val contents = DecayInventoryHelper.takeContents(world, pos)

        if (target.block is DoorBlock) {
            val otherPos = if (target.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) pos.below() else pos.above()

            var facing = target.getValue(DoorBlock.FACING)

            if (target.getValue(DoorBlock.OPEN)) facing = if (target.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT) facing.counterClockWise else facing.clockWise

            val newState = block.defaultBlockState().setValue(TrapDoorBlock.OPEN, true).setValue(TrapDoorBlock.FACING, facing)

            world.setBlockAndUpdate(pos, newState)
            world.setBlockAndUpdate(otherPos, newState)
            DecayInventoryHelper.transferOrDrop(world, pos, contents)

            return entropy
        } else if (target.block is BedBlock) {
            val otherPos = pos.relative(BedBlock.getConnectedDirection(target))
            val newState = block.defaultBlockState()

            world.setBlockAndUpdate(pos, newState)
            world.setBlockAndUpdate(otherPos, newState)
            DecayInventoryHelper.transferOrDrop(world, pos, contents)
            return entropy
        }

        DecayInventoryHelper.drop(world, pos, contents)
        return 0
    }

    override fun produces(): List<DecayResult.Result> = listOf(DecayResult.Result(block, 2))

    companion object {
        @JvmField
        val CODEC: MapCodec<DoubleBlockDecayResult> = RecordCodecBuilder.mapCodec { instance -> BlockDecayResult.blockDecayCodec(instance).apply(instance, ::DoubleBlockDecayResult) }

        const val KEY: String = "double_block"
    }
}
