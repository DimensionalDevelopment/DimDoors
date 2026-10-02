package org.dimdev.dimdoors.world.structure

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.RandomSource
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.StructureManager
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.state.properties.Half
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes
import org.dimdev.dimdoors.pockets.DefaultDungeonDestinations
import org.dimdev.dimdoors.pockets.PocketGenerator
import org.dimdev.dimdoors.world.ModStructuresPieces
import java.util.function.Consumer

class NetherGatewayPiece : NetherFortressPieces.NetherBridgePiece {
    constructor(
        genDepth: Int,
        boundingBox: BoundingBox,
        orientation: Direction?
    ) : super(ModStructuresPieces.NETHER_GATEWAY, genDepth, boundingBox) {
        setOrientation(orientation)
    }

    constructor(tag: CompoundTag) : super(ModStructuresPieces.NETHER_GATEWAY, tag)

    override fun postProcess(
        level: WorldGenLevel,
        structureManager: StructureManager,
        generator: ChunkGenerator,
        random: RandomSource,
        box: BoundingBox,
        chunkPos: ChunkPos,
        pos: BlockPos
    ) {

        val airstate = Blocks.AIR.defaultBlockState()
        val netherBrickFenceState = Blocks.NETHER_BRICK_FENCE.defaultBlockState()
        val netherBrickState = Blocks.NETHER_BRICKS.defaultBlockState()
        val netherSlabState = Blocks.NETHER_BRICK_SLAB.defaultBlockState()

        // Set all the blocks in the area of the room to air
        generateBox(level, box, 0, 2, 0, 6, 6, 6, airstate, airstate, false)
        // Set up the platform under the gateway
        generateBox(level, box, 0, 0, 0, 6, 1, 6, netherBrickState, netherBrickState, false)

        // Build the fence at the back of the room
        generateBox(level, box, 1, 2, 6, 5, 2, 6, netherBrickState, netherBrickState, false)
        generateBox(level, box, 1, 3, 6, 5, 3, 6, netherBrickFenceState, netherBrickFenceState, false)

        // Build the fences at the sides of the room
        generateBox(level, box, 0, 2, 0, 0, 2, 6, netherBrickState, netherBrickState, false)
        generateBox(level, box, 0, 3, 0, 0, 3, 6, netherBrickFenceState, netherBrickFenceState, false)

        generateBox(level, box, 6, 2, 0, 6, 2, 6, netherBrickState, netherBrickState, false)
        generateBox(level, box, 6, 3, 0, 6, 3, 6, netherBrickFenceState, netherBrickFenceState, false)

        // Build the fence portions closest to the entrance
        placeBlock(level, netherBrickState, 1, 2, 0, box)
        placeBlock(level, netherBrickFenceState, 1, 3, 0, box)

        placeBlock(level, netherBrickState, 5, 2, 0, box)
        placeBlock(level, netherBrickFenceState, 5, 3, 0, box)

        // Build the first layer of the gateway
        generateBox(level, box, 1, 2, 2, 5, 2, 5, netherBrickState, netherBrickState, false)
        generateBox(level, box, 1, 2, 1, 5, 2, 1, netherSlabState, netherSlabState, false)

        placeBlock(level, netherSlabState, 1, 2, 2, box)
        placeBlock(level, netherSlabState, 5, 2, 2, box)

        // Build the second layer of the gateway
        val stairs = Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH)
        generateBox(level, box, 2, 3, 3, 2, 3, 4, netherBrickState, netherBrickState, false)
        generateBox(level, box, 4, 3, 3, 4, 3, 4, netherBrickState, netherBrickState, false)
        placeBlock(level, netherBrickState, 3, 3, 4, box)
        placeBlock(level, stairs, 3, 3, 5, box)

        // Build the third layer of the gateway
        // We add 4 to get the rotated metadata for upside-down stairs
        // because Minecraft only supports metadata rotations for normal stairs -_-
        generateBox(level, box, 2, 4, 4, 4, 4, 4, stairs, stairs, false)

        placeBlock(level, Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.FACING, Direction.SOUTH), 2, 4, 3, box)
        placeBlock(level, Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue<Half?, Half?>(StairBlock.HALF, Half.TOP).setValue(StairBlock.FACING, Direction.WEST), 4, 4, 3, box)

        // Build the fourth layer of the gateway
        placeBlock(level, netherBrickState, 3, 5, 3, box)
        placeBlock(level, Blocks.NETHERRACK.defaultBlockState(), 2, 5, 3, box)
        placeBlock(level, Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.FACING, Direction.EAST), 1, 5, 3, box)
        placeBlock(level, Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.FACING, Direction.NORTH), 2, 5, 2, box)
        placeBlock(level, Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.FACING, Direction.SOUTH), 2, 5, 4, box)

        placeBlock(level, Blocks.NETHERRACK.defaultBlockState(), 4, 5, 3, box)
        placeBlock(level, Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.FACING, Direction.WEST), 5, 5, 3, box)
        placeBlock(level, Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.FACING, Direction.NORTH), 4, 5, 2, box)
        placeBlock(level, Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.FACING, Direction.SOUTH), 4, 5, 4, box)

        // Build the top layer of the gateway
        placeBlock(level, netherBrickFenceState, 3, 6, 3, box)

        placeBlock(level, Blocks.FIRE.defaultBlockState(), 2, 6, 3, box)
        placeBlock(level, netherBrickFenceState, 1, 6, 3, box)
        placeBlock(level, netherBrickFenceState, 2, 6, 2, box)
        placeBlock(level, netherBrickFenceState, 2, 6, 4, box)

        placeBlock(level, Blocks.FIRE.defaultBlockState(), 4, 6, 3, box)
        placeBlock(level, netherBrickFenceState, 5, 6, 3, box)
        placeBlock(level, netherBrickFenceState, 4, 6, 2, box)
        placeBlock(level, netherBrickFenceState, 4, 6, 4, box)

        // Place the transient door
        placeBlock(level, ModBlocks.DIMENSIONAL_PORTAL.defaultBlockState(), 3, 3, 3, box)
        level.getBlockEntity<EntranceRiftBlockEntity.Impl?>(getWorldPos(3, 3, 3), ModBlockEntityTypes.ENTRANCE_RIFT)
            .ifPresent(
                Consumer { rift: EntranceRiftBlockEntity.Impl? ->
                    rift!!.properties = DefaultDungeonDestinations.OVERWORLD_LINK_PROPERTIES
                    rift.setDestination(DefaultDungeonDestinations.getGateway(PocketGenerator.NETHER_DUNGEONS))
                })

        for (x in 0..6) {
            for (z in 0..6) {
                this.fillColumnDown(level, netherBrickState, x, -1, z, box)
            }
        }
    }

    companion object {
        @JvmStatic
        fun createPieces(
            pieces: StructurePieceAccessor,
            x: Int,
            y: Int,
            z: Int,
            genDepth: Int,
            orientation: Direction
        ): NetherGatewayPiece? {
            val boundingBox = BoundingBox.orientBox(x, y, z, -2, 0, 0, 7, 8, 7, orientation)
            if (!isOkBox(boundingBox) || pieces.findCollisionPiece(boundingBox) != null) return null
            else return NetherGatewayPiece(genDepth, boundingBox, orientation)
        }
    }
}
