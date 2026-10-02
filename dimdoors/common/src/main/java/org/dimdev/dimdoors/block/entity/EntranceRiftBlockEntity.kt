package org.dimdev.dimdoors.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Rotations
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.castOrNull
import org.dimdev.dimcore.api.util.EntityUtils.chat
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.DimensionalDoors.Companion.config
import org.dimdev.dimdoors.api.rift.target.EntityTarget
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.Location.Companion.ofWorld
import org.dimdev.dimdoors.api.util.TeleportUtil
import org.dimdev.dimdoors.api.util.horizontalFacing
import org.dimdev.dimdoors.api.util.math.transform
import org.dimdev.dimdoors.api.util.math.transformDirection
import org.dimdev.dimdoors.block.CoordinateTransformerBlock
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.block.RiftProvider
import org.dimdev.dimdoors.block.TraversableRiftBlock
import org.dimdev.dimdoors.pockets.DefaultDungeonDestinations
import org.dimdev.dimdoors.rift.RiftUtils
import org.dimdev.dimdoors.rift.targets.EscapeTarget
import org.dimdev.dimdoors.rift.targets.LocationProvider
import org.dimdev.dimdoors.rift.targets.Targets
import org.dimdev.dimdoors.util.LevelSpaceHelper
import org.dimdev.dimdoors.world.ModDimensions.isLimboDimension
import org.dimdev.dimdoors.world.pocket.VirtualLocation.Companion.fromLocation
import org.joml.Matrix4d

open class EntranceRiftBlockEntity<T : EntranceRiftBlockEntity<T>> protected constructor(
    type: BlockEntityType<T>,
    pos: BlockPos,
    state: BlockState
) : RiftBlockEntity<T>(type, pos, state), EntityTarget {
    var renderBlockState: BlockState? = null
        protected set
    private var plane: RiftUtils.PortalPlane? = null

    init {
        updateState(pos, state)
    }

    private fun updateState(pos: BlockPos, state: BlockState) {
        val block = state.block

        this.renderBlockState = state
        plane = null

        when (block) {
            is TraversableRiftBlock<*> -> {
                this.renderBlockState = block.getVisualBlockState(state)
                plane = block.getPortalPlane(state, pos)
            }
        }
    }


    override fun setBlockState(state: BlockState) {
        super.setBlockState(state)
        this.renderBlockState = state.block.castOrNull<TraversableRiftBlock<*>>()?.getVisualBlockState(state) ?: state
    }

    fun teleport(entity: Entity): Boolean {
        val status: Boolean = attemptTeleport(entity, this)

        if (this.isStateDirty && !this.data.alwaysDelete) this.setChanged()

        return status
    }

    override fun receiveEntity(
        entity: Entity,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3,
        location: Location?
    ): Boolean {
        val level = this.level?.castOrNull<ServerLevel>() ?: return false
        return receiveEntityAt(
            level,
            this.blockPos,
            level.getBlockState(this.blockPos),
            entity,
            relativePos,
            relativeAngle,
            relativeVelocity,
            location
        )
    }

    val orientation: Direction get() = level?.getBlockState(this.worldPosition)?.horizontalFacing ?: Direction.NORTH

    fun hasOrientation(): Boolean = this.level?.getBlockState(this.worldPosition)?.horizontalFacing != null

    val isTall: Boolean
        /**
         * Specifies if the portal should be rendered two blocks tall
         */
        get() {
            val state = this.blockState
            return state.block.castOrNull<RiftProvider<*>>()?.isTall(state) ?: false
        }

    fun setPortalDestination(world: ServerLevel) {
        if (isLimboDimension(world)) {
            this.setDestination(ESCAPE_TARGET)
        } else {
            this.setDestination(DefaultDungeonDestinations.gateway)
            this.properties = DefaultDungeonDestinations.POCKET_LINK_PROPERTIES
        }
    }

    override fun unregister() {
        super.unregister()

        val level = level ?: return
        val pos = this.blockPos

        val state = level.getBlockState(pos)
        state.block.castOrNull<TraversableRiftBlock<*>>()?.closeRift(level, pos, state)
    }

    fun hasTraversed(level: Level?, previousPosition: Vec3, currentPosition: Vec3): Boolean {
        return plane == null || !plane!!.isTraversed(level, previousPosition, currentPosition)
    }

    override fun detach() {
        val level = level ?: return

        val waterlogged = blockState.getOptionalValue(BlockStateProperties.WATERLOGGED).orElse(false)
        level.setBlockAndUpdate(
            worldPosition,
            ModBlocks.DETACHED_RIFT.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, waterlogged)
        )
        level.getBlockEntity(worldPosition, ModBlockEntityTypes.DETACHED_RIFT).ifPresent { a -> a.data = this.data }
    }

    class Impl(pos: BlockPos, state: BlockState) : EntranceRiftBlockEntity<Impl>(ModBlockEntityTypes.ENTRANCE_RIFT, pos, state)

    companion object {
        private val ESCAPE_TARGET = EscapeTarget(true)
        @JvmStatic
        fun attemptTeleport(entity: Entity, rift: Rift): Boolean {
            rift.isStateDirty = false

            // Attempt a teleport
            try {
                var relativePos = Vec3(0.0, 0.0, 0.0)
                var relativeAngle = Rotations(entity.xRot, entity.yRot, 0f)
                var relativeVelocity = entity.deltaMovement

                val target = rift.target
                val location = if (target is LocationProvider) target.location else null

                val state = rift.riftLevel.getBlockState(rift.riftBlockPos)
                val block = state.block
                if (block is CoordinateTransformerBlock) {
                    val blockPos = rift.riftBlockPos
                    val sourceFrame = LevelSpaceHelper.INSTANCE.sourceTeleportFrame(rift.riftLevel as ServerLevel, blockPos, entity, entity.position(), relativeAngle, relativeVelocity)
                    val transformationBuilder = block.transformation(state, blockPos)
                    val rotatorBuilder = block.rotator(state, blockPos)
                    relativePos = block.transformTo(transformationBuilder, sourceFrame.pos)
                    relativeAngle = block.rotateTo(rotatorBuilder, sourceFrame.angle)
                    relativeVelocity = block.rotateTo(rotatorBuilder, sourceFrame.velocity)
                }

                val entityTarget = target.`as`(Targets.ENTITY)
                if (entityTarget!!.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, location)) {
                    val vLoc = fromLocation(ofWorld(entity.level() as ServerLevel, entity.blockPosition()))
                    if (config.generalConfig.enableDebugMessages) chat(
                        entity,
                        Component.literal("You are at x = " + vLoc.x + ", y = ?, z = " + vLoc.z + ", w = " + vLoc.depth)
                    )
                    return true
                }
            } catch (e: Exception) {
                chat(
                    entity,
                    Component.literal("Something went wrong while trying to teleport you, please report this bug.")
                )
                DimensionalDoors.LOGGER.error("Teleporting failed with the following exception: ", e)
            }

            return false
        }

        fun receiveEntityAt(
            level: ServerLevel,
            blockPos: BlockPos,
            state: BlockState,
            entity: Entity,
            relativePos: Vec3,
            relativeAngle: Rotations,
            relativeVelocity: Vec3,
            location: Location?
        ): Boolean {
            var relativePos = relativePos
            var relativeAngle = relativeAngle
            var relativeVelocity = relativeVelocity
            val block = state.block
            val direction: Direction = getOrientation(state).opposite

            // compute offset once — used whether or not it's a transformer block
            val offset = config.generalConfig.teleportOffset + 0.01
            val offsetVec = Vec3.atLowerCornerOf(direction.normal).scale(offset)

            var targetPos = Vec3.atCenterOf(blockPos).add(offsetVec)

            if (block is CoordinateTransformerBlock) {
                if (block.isExitFlipped()) {
                    val flipper = Matrix4d().rotateY(Math.PI)
                    relativePos = flipper.transform(relativePos)
                    relativeAngle = flipper.transform(relativeAngle)
                    relativeVelocity = flipper.transformDirection(relativeVelocity)
                }

                val transformation = block.transformation(state, blockPos)
                val rotator = block.rotator(state, blockPos)

                targetPos =
                    block.transformOut(transformation, relativePos).add(offsetVec) // offset reapplied here

                relativeAngle = block.rotateOut(rotator, relativeAngle)
                relativeVelocity = block.rotateOut(rotator, relativeVelocity)
            }

            targetPos = targetPos.add(
                direction.normal.x / 2.0,
                direction.normal.y / 2.0,
                direction.normal.z / 2.0
            )

            val frame = LevelSpaceHelper.INSTANCE.projectTeleportFrame(
                level,
                location,
                targetPos,
                relativeAngle,
                relativeVelocity
            )

            TeleportUtil.teleport(entity, level, frame.pos, frame.angle, frame.velocity)
            return true
        }

        private fun getOrientation(state: BlockState): Direction {
            return if (state.hasProperty(HorizontalDirectionalBlock.FACING)) state.getValue(
                HorizontalDirectionalBlock.FACING
            ) else Direction.NORTH
        }
    }
}
