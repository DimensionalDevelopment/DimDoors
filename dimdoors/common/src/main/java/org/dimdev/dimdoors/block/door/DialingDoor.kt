package org.dimdev.dimdoors.block.door

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockSetType
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.level.storage.loot.BuiltInLootTables
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.block.entity.DialingDoorBlockEntity
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes
import org.dimdev.dimdoors.rift.registry.DialingAddress
import org.dimdev.dimdoors.sound.ModSoundEvents
import org.dimdev.dimdoors.util.MathUtils
import kotlin.math.abs
import kotlin.math.floor

class DialingDoor(settings: Properties, blockSetType: BlockSetType) : DimensionalDoorBlock<DialingDoorBlockEntity>(settings, blockSetType, true) {
    override val riftBlockEnityType get() = ModBlockEntityTypes.DIALING_DOOR

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun useWithoutItem(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        val type = getType(state, pos, hitResult)
        if (!isOpen(state) && type != null) {
            if (!world.isClientSide()) {
                getRift(world, pos, state)!!.turnDial(type)
                world.playSound(null, pos, ModSoundEvents.KEY_UNLOCKED, SoundSource.BLOCKS, 1.0f, 1.0f)
            }

            return InteractionResult.SUCCESS
        }

        return super.useWithoutItem(state, world, pos, player, hitResult)
    }

    private fun getType(state: BlockState, pos: BlockPos, hitResult: HitResult): DialingAddress.DialType? {
        val direction = state.getValue(FACING)
        val lower = if (state.getValue(HALF) == DoubleBlockHalf.LOWER) pos else pos.below()
        val upper = lower.above()

        val coordiantes = getVoxelCoord(hitResult, direction, lower, upper)

        for (i in BUTTONS.indices) {
            val button: InteractionShape = BUTTONS[i]
            if (button.intersects(coordiantes)) {
                return DialingAddress.DialType.values()[i]
            }
        }

        return null
    }

    private fun getVoxelCoord(hit: HitResult, facing: Direction, min: BlockPos, max: BlockPos): Vec3 {
        val p = hit.getLocation().subtract(Vec3.atLowerCornerOf(min))
        val size = Vec3.atLowerCornerOf(max.subtract(min).offset(1, 1, 1))
        val c = p.subtract(size.scale(0.5))

        val front = Vec3.atLowerCornerOf(facing.getNormal())
        val right = Vec3.atLowerCornerOf(facing.getClockWise().getNormal())

        return Vec3(
            c.dot(right) + abs(size.dot(right)) * 0.5,
            p.y,
            abs(-c.dot(front) + abs(size.dot(front)) * 0.5 - 1)
        ).scale(16.0)
    }

    private fun getCoord(
        hitResult: HitResult,
        direction: Direction,
        lowerCorner: BlockPos,
        upperCorner: BlockPos
    ): Vec3 {
        val size = lowerCorner.subtract(upperCorner)
        val local = hitResult.getLocation().subtract(Vec3.atLowerCornerOf(lowerCorner))

        val xCenter = size.getX() * 0.5
        val zCenter = size.getZ() * 0.5

        val centered = local.subtract(xCenter, 0.0, zCenter)

        val front = direction
        val right = front.clockWise

        var correctedX = (centered.x * right.getStepX() + centered.z * right.getStepZ())
        var correctedY = local.y()
        var correctedZ = (size.getZ() * 16) - 1 - (centered.x * front.getStepX() + centered.z * front.getStepZ())
        correctedX += xCenter
        correctedY += 0.0
        correctedZ += zCenter
        correctedX *= 16.0
        correctedY *= 16.0
        correctedZ *= 16.0

        return Vec3(correctedX, correctedY, correctedZ)
    }

    private fun getCoord(hitResult: HitResult, state: BlockState, pos: BlockPos): BlockPos {
        val lowerPos = if (state.getValue<DoubleBlockHalf?>(HALF) == DoubleBlockHalf.LOWER) pos else pos.below()
        val local = hitResult.getLocation().subtract(Vec3.atLowerCornerOf(lowerPos))
        val centered = local.subtract(0.5, 0.0, 0.5)

        val front = state.getValue<Direction?>(FACING).getOpposite()
        val right = front.getCounterClockWise()
        val correctedX = centered.x * right.getStepX() + centered.z * right.getStepZ()
        val correctedY = local.y()
        val correctedZ = 15 - (centered.x * front.getStepX() + centered.z * front.getStepZ())

        return BlockPos(
            Mth.clamp(floor((correctedX + 0.5) * 16.0).toInt(), 0, 15),
            Mth.clamp(floor(local.y * 16.0).toInt(), 0, 31),
            15 - Mth.clamp(
                floor((correctedZ + 0.5) * 16.0).toInt(), 0, 15
            )
        )
    }


    override fun getDrops(state: BlockState, params: LootParams.Builder): MutableList<ItemStack?> {
        val resourcekey = this.getLootTable()
        if (resourcekey === BuiltInLootTables.EMPTY) {
            return mutableListOf()
        } else {
            val lootparams = params.withParameter(LootContextParams.BLOCK_STATE, state).create(LootContextParamSets.BLOCK
            )
            val serverlevel = lootparams.level
            val loottable = serverlevel.server.reloadableRegistries().getLootTable(resourcekey)
            return loottable.getRandomItems(lootparams)
        }
    }

    data class InteractionShape(
        val minX: Double,
        val minY: Double,
        val minZ: Double,
        val maxX: Double,
        val maxY: Double,
        val maxZ: Double
    ) {
        fun intersects(point: Vec3): Boolean {
            return intersects(point.x, point.y, point.z)
        }

        fun intersects(x: Double, y: Double, z: Double): Boolean {
            return MathUtils.betewen(x, minX, maxX) &&
                    MathUtils.betewen(y, minY, maxY) &&
                    MathUtils.betewen(z, minZ, maxZ)
        }
    }

    companion object {
        private val BUTTONS = arrayOf<InteractionShape>(
            InteractionShape(4.0, 21.0, 0.0, 12.0, 28.0, 1.0),
            InteractionShape(4.0, 14.0, 0.0, 12.0, 21.0, 1.0),
            InteractionShape(4.0, 6.0, 0.0, 12.0, 14.0, 1.0)
        )
    }
}
