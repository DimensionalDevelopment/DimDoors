package org.dimdev.dimdoors.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.dimdev.dimdoors.api.rift.target.RedstoneTarget
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.block.DimensionalPortalBlock.Companion.checkType
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes
import org.dimdev.dimdoors.entity.FarShotEnderPearlEntity
import org.dimdev.dimdoors.particle.client.RiftParticleOptions
import org.dimdev.dimdoors.world.ModDimensions.isPocketDimension
import kotlin.jvm.optionals.getOrNull

class DetachedRiftBlock(settings: Properties) : WaterLoggableBlockWithEntity(settings), RedstoneTarget, RiftProvider<DetachedRiftBlockEntity> {
    init {
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.POWER, 0))
    }

    override fun codec(): MapCodec<out BaseEntityBlock> = CODEC

    override fun getRift(world: Level, pos: BlockPos, state: BlockState): DetachedRiftBlockEntity? = world.getBlockEntity(pos, ModBlockEntityTypes.DETACHED_RIFT.value()).getOrNull()

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder)
        builder.add(BlockStateProperties.POWER)
    }

    override fun getSignal(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Int {
        return if (state.block === this) state.getValue(BlockStateProperties.POWER) else 0
    }

    override fun isSignalSource(state: BlockState): Boolean {
        return true
    }

    override fun recieveSignal(strength: Int, location: Location?): Boolean {
        if(location == null) return false

        val level = location.world
        val pos = location.blockPos

        val state = level.getBlockState(pos).setValue(BlockStateProperties.POWER, strength)

        level.setBlockAndUpdate(pos, state)

        return true
    }

    override fun animateTick(state: BlockState, world: Level, pos: BlockPos, rand: RandomSource) {
        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity !is DetachedRiftBlockEntity) return

        val outsidePocket = !isPocketDimension(world)
        val speed = 0.1

        if (blockEntity.getWeight() < 0) {
            world.addParticle(
                RiftParticleOptions.of(outsidePocket),
                pos.x + .5,
                pos.y + .5,
                pos.z + .5,
                rand.nextGaussian() * speed,
                rand.nextGaussian() * speed,
                rand.nextGaussian() * speed
            )
        }

        world.addParticle(
            RiftParticleOptions.of(outsidePocket, blockEntity.getWeight() == 0),
            pos.x + .5,
            pos.y + .5,
            pos.z + .5,
            rand.nextGaussian() * speed,
            rand.nextGaussian() * speed,
            rand.nextGaussian() * speed
        )
    }

    public override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.ENTITYBLOCK_ANIMATED

    public override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape = Shapes.empty()

    override fun getVisualShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape = Shapes.empty()

    public override fun getOcclusionShape(state: BlockState, level: BlockGetter, pos: BlockPos): VoxelShape {
        return Shapes.empty()
    }

    public override fun getCollisionShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape = Shapes.empty()

    override fun <T : BlockEntity> getTicker(
        world: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return checkType(
            type,
            ModBlockEntityTypes.DETACHED_RIFT.value()
        ) { _, blockPos, blockState, blockEntity -> blockEntity.tick(world, blockPos, blockState) }
    }

    override val riftBlockEnityType: BlockEntityType<DetachedRiftBlockEntity> get() = ModBlockEntityTypes.DETACHED_RIFT.value()

    override fun onProjectileHit(level: Level, state: BlockState, hit: BlockHitResult, projectile: Projectile) {
        if (!level.isClientSide() && projectile is FarShotEnderPearlEntity) {
            val entity = projectile.owner ?: return

            val rift = getRift(level, hit.blockPos, state)

            if (EntranceRiftBlockEntity.attemptTeleport(entity, rift!!)) projectile.discard()
        }
    }

    override fun providerType(): String {
        return "Detached rift"
    }

    companion object {
        val CODEC = simpleCodec(::DetachedRiftBlock)

        const val ID: String = "rift"
    }
}
