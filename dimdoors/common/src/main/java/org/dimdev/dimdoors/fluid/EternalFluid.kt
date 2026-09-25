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
import net.minecraft.world.level.material.Fluids
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.item.ModItems

abstract class EternalFluid : FlowingFluid() {
    override fun getFlowing(): Fluid = ModFluids.FLOWING_ETERNAL_FLUID.value()

    override fun getSource(falling: Boolean): FluidState = ModFluids.ETERNAL_FLUID.value().defaultFluidState().setValue(FALLING, falling)

    override fun getBucket(): Item = ModItems.ETERNAL_FLUID_BUCKET.value()

    override fun randomTick(level: Level, blockPos: BlockPos, fluidState: FluidState, randomSource: RandomSource) {}

    override fun animateTick(level: Level, blockPos: BlockPos, fluidState: FluidState, randomSource: RandomSource) {}

    override fun beforeDestroyingBlock(levelAccessor: LevelAccessor, blockPos: BlockPos, blockState: BlockState) {
    }

    override fun getDropOff(levelReader: LevelReader) = if (levelReader.dimensionType().ultraWarm()) 4 else 2

    override fun createLegacyBlock(fluidState: FluidState): BlockState =
        ModBlocks.ETERNAL_FLUID.value().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(fluidState))

    override fun isSame(fluid: Fluid): Boolean = fluid === ModFluids.ETERNAL_FLUID || fluid === ModFluids.FLOWING_ETERNAL_FLUID

    override fun getSlopeFindDistance(levelReader: LevelReader) = if (levelReader.dimensionType().ultraWarm()) 1 else 2

    override fun canBeReplacedWith(
        fluidState: FluidState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        fluid: Fluid,
        direction: Direction
    ) = fluidState.getHeight(blockGetter, blockPos) >= 0.44444445f && fluid.isSame(Fluids.WATER)

    override fun getTickDelay(levelReader: LevelReader) = if (levelReader.dimensionType().ultraWarm()) 10 else 30

    override fun getSpreadDelay(
        level: Level,
        blockPos: BlockPos,
        fluidState: FluidState,
        fluidState2: FluidState
    ): Int {
        var tickDelay = this.getTickDelay(level)

        if (!fluidState.isEmpty && !fluidState2.isEmpty && !fluidState.getValue(FALLING) && !fluidState2.getValue(FALLING) && fluidState2.getHeight(level, blockPos) > fluidState.getHeight(level, blockPos) && level.getRandom().nextInt(4) != 0) {
            tickDelay *= 4
        }

        return tickDelay
    }

    override fun canConvertToSource(level: Level): Boolean = false

    override fun spreadTo(
        levelAccessor: LevelAccessor,
        blockPos: BlockPos,
        blockState: BlockState,
        direction: Direction,
        fluidState: FluidState
    ) {
        if (direction == Direction.DOWN) {
            if (levelAccessor.getFluidState(blockPos).`is`(FluidTags.WATER)) {
                if (blockState.block is LiquidBlock) {
                    levelAccessor.setBlock(blockPos, ModBlocks.BLACK_ANCIENT_FABRIC.value().defaultBlockState(), 3)
                }

                return
            }
        }

        super.spreadTo(levelAccessor, blockPos, blockState, direction, fluidState)
    }

    override fun isRandomlyTicking() = true


    override fun getExplosionResistance() = 100000f

    open class Flowing : EternalFluid() {
        override fun createFluidStateDefinition(builder: StateDefinition.Builder<Fluid, FluidState>) {
            super.createFluidStateDefinition(builder)
            builder.add(LEVEL)
        }

        override fun getSource() = ModFluids.ETERNAL_FLUID.value()

        override fun isSource(fluidState: FluidState): Boolean = false

        override fun getAmount(fluidState: FluidState): Int = fluidState.getValue(LEVEL)
    }

    open class Still : EternalFluid() {
        override fun getSource() = ModFluids.ETERNAL_FLUID.value().defaultFluidState().type

        override fun isSource(fluidState: FluidState) = true

        override fun getAmount(fluidState: FluidState) = 8
    }
}
