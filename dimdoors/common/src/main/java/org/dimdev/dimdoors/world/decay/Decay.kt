package org.dimdev.dimdoors.world.decay

import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.decoration.Painting
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.phys.AABB
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimdoors.DimensionalDoors.Companion.config
import org.dimdev.dimdoors.api.util.ResourceUtil
import org.dimdev.dimdoors.network.ServerPacketHandler
import org.dimdev.dimdoors.network.packet.s2c.RenderBreakBlockS2CPacket
import org.dimdev.dimdoors.sound.ModSoundEvents
import org.dimdev.dimdoors.world.decay.pattern.DecayPattern
import kotlin.jvm.optionals.getOrNull

/**
 * Provides methods for applying decay. Decay refers to the effect that most blocks placed next to certain blocks like unraveled fabric
 * change into simpler forms ultimately becoming in most cases unraveled Fabric as time passes.
 */
object Decay {
    private val LOGGER: Logger = LogManager.getLogger()
    private val DECAY_QUEUE: MutableMap<ResourceKey<Level>, MutableSet<DecayTask>> = HashMap()
    private const val BREAK_BLOCK_STAGE = 5
    private const val BREAK_BLOCK_RENDER_DISTANCE = 100.0

    /**
     * Checks the blocks orthogonally around a given location (presumably the location of an Unraveled Fabric block)
     * and applies Limbo decay to them. This gives the impression that decay spreads outward from Unraveled Fabric.
     */
    @JvmStatic
    fun applySpreadDecay(world: ServerLevel, pos: BlockPos, random: RandomSource, source: DecaySource) {
        //Check if we randomly apply decay spread or not. This can be used to moderate the frequency of
        //full spread decay checks, which can also shift its performance impact on the game.
        if (random.nextDouble() < config.decayConfig.decaySpreadChance) {
            val origin = world.getBlockState(pos)

            //Apply decay to the blocks above, below, and on all four sides.
            // TODO: make max amount configurable
            val decayAmount = random.nextInt(Direction.values().size - 1) + 1
            val directions = Direction.values().toMutableList()
            for (i in 0 until decayAmount) {
                val direction = directions.removeAt(random.nextInt(directions.size))
                decayBlock(world, pos, origin, direction, source)
            }
        }
    }

    @JvmStatic
    fun decayBlock(world: ServerLevel, originPos: BlockPos, originBlockState: BlockState, direction: Direction, source: DecaySource) {
        val targetPos = originPos.relative(direction)
        decayBlock(world, originPos, originBlockState, targetPos, world.getBlockState(targetPos), source)
    }

    /**
     * Checks if a block can be decayed and, if so, changes it to the next block ID along the decay sequence.
     */
    @JvmStatic
    fun decayBlock(world: ServerLevel, originPos: BlockPos, originBlockState: BlockState, targetPos: BlockPos, targetBlockState: BlockState, source: DecaySource) {
        val context = DecayContext.create(world, originPos, originBlockState, targetPos, targetBlockState, source)

        val patterns = DecayLoader.getPatterns(context)

        for (pattern in patterns) {
            if (pattern.value.test(context)) {
                sendBreakBlockProgress(world, context.targetBlockPos, BREAK_BLOCK_STAGE)
                world.playSound(null, context.targetBlockPos, ModSoundEvents.TEARING, SoundSource.BLOCKS, 0.5f, 1f)
                queueDecay(context, pattern, config.decayConfig.decayDelay)
                break
            }
        }
    }

    @JvmStatic
    fun queueDecay(context: DecayContext, pattern: DecayPatternHolder, delay: Int) {
        val task = DecayTask(context, pattern, delay)
        if (delay <= 0) {
            task.process()
        } else {
            DECAY_QUEUE.computeIfAbsent(context.world.dimension()) { HashSet() }.add(task)
        }
    }

    @JvmStatic
    fun clearQueue() {
        DECAY_QUEUE.clear()
    }

    @JvmStatic
    fun tick(world: ServerLevel) {
        val key = world.dimension()
        val tasks = DECAY_QUEUE[key] ?: return
        val tasksToRun = tasks.filter { it.reduceDelayIsDone() }.toSet()
        tasks.removeAll(tasksToRun)
        tasksToRun.forEach { task -> task.process() }
    }

    private fun sendBreakBlockProgress(world: ServerLevel, pos: BlockPos, stage: Int) {
        val packet = RenderBreakBlockS2CPacket(pos, stage)
        world.getPlayers(EntitySelector.withinDistance(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), BREAK_BLOCK_RENDER_DISTANCE))
            .forEach { player -> ServerPacketHandler.sendPacket(player, packet) }
    }

    object DecayLoader {
        private val LOGGER: Logger = LogManager.getLogger()

        private val patterns: MutableMap<ResourceKey<*>, MutableList<DecayPatternHolder>> = HashMap()

        private var undiffernitatedPatterns: List<DecayPatternHolder> = ArrayList()

        @JvmStatic
        fun reload(provider: HolderLookup.Provider, manager: ResourceManager) {
            undiffernitatedPatterns = ArrayList(ResourceUtil.loadResources(manager, "decay_patterns", ".json") { id, stream -> loadPattern(ResourceUtil.readJson(stream), id) }.values)
        }

        private fun loadPattern(json: JsonElement, id: ResourceLocation): DecayPatternHolder {
            return DecayPatternHolder(id, JsonOps.INSTANCE.withDecoder(DecayPattern.CODEC).apply(json).getOrThrow().first)
        }

        @JvmStatic
        fun getPatterns(obj: Any?): Collection<DecayPatternHolder> {
            if (obj != null) {
                return when (obj) {
                    is FluidState -> getPatterns(obj.holder())
                    is BlockState -> getPatterns(obj.blockHolder)
                    is ResourceKey<*> -> patterns.getOrDefault(obj, emptyList())
                    is Holder<*> -> obj.unwrapKey().getOrNull()?.let { patterns[it] } ?: emptyList()
                    is DecayContext -> {
                        var patterns = getPatterns(obj.targetBlockState)
                        if (patterns.isEmpty()) patterns = getPatterns(obj.targetFluidState)
                        if (patterns.isEmpty()) patterns = getPatterns(obj.targetEntity)
                        if (patterns.isEmpty()) patterns = emptyList()
                        patterns
                    }
                    is Painting -> getPatterns(obj.variant)
                    else -> emptyList()
                }
            }

            return emptyList()
        }

//    public static Collection<DecayPatternHolder> getPatterns(ResourceKey<Fluid> fluid) {
//        return fluidPatterns.getOrDefault(fluid, Collections.emptyList());
//    }

        @JvmStatic
        fun getPatterns(): Map<ResourceKey<*>, List<DecayPatternHolder>> {
            return patterns
        }

        @JvmStatic
        fun populate(server: MinecraftServer) {
            patterns.clear()
            val registry = server.registryAccess()

            for (pattern in undiffernitatedPatterns) {
                pattern.value.constructApplicable(registry).forEach { resourceKey -> patterns.computeIfAbsent(resourceKey) { ArrayList() }.add(pattern) }
            }
        }
    }

    private class DecayTask(private val context: DecayContext, private val processor: DecayPatternHolder, private var delay: Int) {
        fun reduceDelayIsDone(): Boolean {
            return --delay <= 0
        }

        fun process() {
            sendBreakBlockProgress(context.world, context.targetBlockPos, -1)
            val currentContext = DecayContext.create(
                context.world,
                context.originBlockPos,
                context.world.getBlockState(context.originBlockPos),
                context.targetBlockPos,
                context.world.getBlockState(context.targetBlockPos),
                context.source
            )

            if (!processor.value.test(currentContext)) {
                return
            }

            if (currentContext.source.decayIntoWorldThread()) {
                if (config.decayConfig.decaysIntoAir) {
                    val contents = DecayInventoryHelper.takeContents(currentContext.world, currentContext.targetBlockPos)
                    currentContext.world.setBlockAndUpdate(currentContext.targetBlockPos, Blocks.AIR.defaultBlockState())
                    DecayInventoryHelper.drop(currentContext.world, currentContext.targetBlockPos, contents)
                } else processor.value.applyPattern(currentContext)
            } else {
                processor.value.applyPattern(currentContext)
            }
        }
    }

    data class DecayContext(
        val world: ServerLevel,
        val originBlockPos: BlockPos,
        val originBlockState: BlockState,
        val targetBlockPos: BlockPos,
        val targetBlockState: BlockState,
        val targetFluidState: FluidState,
        val targetEntity: Entity?,
        val source: DecaySource
    ) {
        companion object {
            @JvmStatic
            fun create(world: ServerLevel, blockPos: BlockPos, blockState: BlockState, source: DecaySource): DecayContext {
                return create(world, blockPos, blockState, blockPos, blockState, source)
            }

            @JvmStatic
            fun create(world: ServerLevel, originBlockPos: BlockPos, originBlockState: BlockState, targetBlockPos: BlockPos, targetBlockState: BlockState, source: DecaySource): DecayContext {
                val targetFluidState = world.getFluidState(targetBlockPos)

                val entity = world.getEntitiesOfClass(Painting::class.java, AABB.encapsulatingFullBlocks(targetBlockPos, targetBlockPos)).firstOrNull()

                return DecayContext(world, originBlockPos, originBlockState, targetBlockPos, targetBlockState, targetFluidState, entity, source)
            }
        }
    }
}
