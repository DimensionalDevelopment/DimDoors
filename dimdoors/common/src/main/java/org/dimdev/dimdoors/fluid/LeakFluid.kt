package org.dimdev.dimdoors.fluid

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.tags.FluidTags
import net.minecraft.util.RandomSource
import net.minecraft.world.item.Item
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.FlowingFluid
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.item.ModItems

abstract class LeakFluid : FlowingFluid() {
    override fun getFlowing(): Fluid {
        return ModFluids.FLOWING_LEAK.value()
    }

    override fun getSource(falling: Boolean) = ModFluids.LEAK.value().defaultFluidState().setValue(FALLING, falling)

    override fun getBucket(): Item {
        return ModItems.LEAK_BUCKET
    }

    override fun randomTick(level: Level, blockPos: BlockPos, fluidState: FluidState, randomSource: RandomSource) {
    }

    override fun animateTick(level: Level, blockPos: BlockPos, fluidState: FluidState, randomSource: RandomSource) {
    }

    override fun beforeDestroyingBlock(levelAccessor: LevelAccessor, blockPos: BlockPos, blockState: BlockState) {
    }

    override fun getDropOff(levelReader: LevelReader) = if (levelReader.dimensionType().ultraWarm()) 4 else 2

    override fun createLegacyBlock(fluidState: FluidState) = ModBlocks.LEAK.value().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(fluidState))

    override fun isSame(fluid: Fluid): Boolean {
        return fluid === ModFluids.LEAK || fluid === ModFluids.FLOWING_LEAK
    }

    override fun getSlopeFindDistance(levelReader: LevelReader) = if (levelReader.dimensionType().ultraWarm()) 1 else 2

    override fun canBeReplacedWith(fluidState: FluidState, blockGetter: BlockGetter, blockPos: BlockPos, fluid: Fluid, direction: Direction) = direction == Direction.DOWN && !fluid.`is`(FluidTags.WATER)

    override fun getTickDelay(levelReader: LevelReader): Int {
        return if (levelReader.dimensionType().ultraWarm()) 10 else 30
    }

    override fun getSpreadDelay(
        level: Level,
        blockPos: BlockPos,
        fluidState: FluidState,
        fluidState2: FluidState
    ): Int {
        var tickDelay = this.getTickDelay(level)

        if (!fluidState.isEmpty && !fluidState2.isEmpty && !fluidState.getValue(FALLING) && !fluidState2.getValue(FALLING) && fluidState2.getHeight(level, blockPos) > fluidState.getHeight(level, blockPos) && level.getRandom().nextInt(4) != 0) tickDelay *= 4

        return tickDelay
    }

    override fun canConvertToSource(level: Level) = false

    override fun isRandomlyTicking() = true


    override fun getExplosionResistance() = 100000f

    open class Flowing : LeakFluid() {
        override fun createFluidStateDefinition(builder: StateDefinition.Builder<Fluid, FluidState>) {
            super.createFluidStateDefinition(builder)
            builder.add(LEVEL)
        }

        override fun getSource() = ModFluids.LEAK.value()

        override fun isSource(fluidState: FluidState) = false

        override fun getAmount(fluidState: FluidState) = fluidState.getValue(LEVEL)
    }

    open class Still : LeakFluid() {
        override fun getSource() = ModFluids.LEAK.value().defaultFluidState().type

        override fun isSource(fluidState: FluidState) = true

        override fun getAmount(fluidState: FluidState) = 8
    }
}
