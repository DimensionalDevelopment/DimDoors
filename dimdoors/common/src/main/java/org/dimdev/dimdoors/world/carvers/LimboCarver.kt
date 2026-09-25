package org.dimdev.dimdoors.world.carvers

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.util.RandomSource
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.CarvingMask
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.levelgen.Aquifer
import net.minecraft.world.level.levelgen.carver.CarvingContext
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver
import org.apache.commons.lang3.mutable.MutableBoolean
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.fluid.ModFluids
import java.util.function.Function

class LimboCarver(codec: Codec<CaveCarverConfiguration>) : CaveWorldCarver(codec) {
    init {
        this.liquids = setOf(ModFluids.ETERNAL_FLUID.value())
    }

    override fun getCaveBound(): Int {
        return 10
    }

    override fun getThickness(random: RandomSource) = (random.nextFloat() * 2.0f + random.nextFloat()) * 2.0f

    override fun getYScale() = 5.0

    override fun carveBlock(
        carvingContext: CarvingContext,
        caveCarverConfiguration: CaveCarverConfiguration,
        chunkAccess: ChunkAccess,
        function: Function<BlockPos?, Holder<Biome>>,
        carvingMask: CarvingMask,
        mutableBlockPos: BlockPos.MutableBlockPos,
        mutableBlockPos2: BlockPos.MutableBlockPos,
        aquifer: Aquifer,
        mutableBoolean: MutableBoolean
    ): Boolean {
        if (this.canReplaceBlock(caveCarverConfiguration, chunkAccess.getBlockState(mutableBlockPos))) {
            val blockState: BlockState =
                if (mutableBlockPos.y <= carvingContext.minGenY + 31) ModBlocks.ETERNAL_FLUID.value().defaultBlockState() else ModBlocks.LIMBO_AIR.value().defaultBlockState()
            chunkAccess.setBlockState(mutableBlockPos, blockState, false)
            if (!blockState.fluidState.isEmpty) {
                chunkAccess.markPosForPostprocessing(mutableBlockPos)
            }
            return true
        }
        return false
    }
}
