package org.dimdev.dimdoors.decay


import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.decoration.Painting
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.phys.AABB
import org.dimdev.dimdoors.DimensionalDoors.Companion.config
import org.dimdev.dimdoors.decay.pattern.DecayPattern
import org.dimdev.dimdoors.network.ServerPacketHandler
import org.dimdev.dimdoors.network.s2c.RenderBreakBlockS2CPacket
import org.dimdev.dimdoors.sound.ModSoundEvents
import org.dimdev.dimdoors.util.ResourceUtil
import kotlin.jvm.optionals.getOrNull

/**
 * Provides methods for applying decay. Decay refers to the effect that most blocks placed next to certain blocks like unraveled fabric
 * change into simpler forms ultimately becoming in most cases unraveled Fabric as time passes.
 */
object Decay {
    private val DECAY_QUEUE: MutableMap<ResourceKey<Level>, MutableMap<BlockPos, DecayTask>> = HashMap()
    private const val BREAK_BLOCK_STAGE = 5
    private const val BREAK_BLOCK_RENDER_DISTANCE = 100.0

    /**
     * Checks if a block can be decayed and, if so, changes it to the next block ID along the decay sequence.
     */
    @JvmStatic
    fun decayBlock(world: ServerLevel, source: DecaySource, originPos: BlockPos, targetPos: BlockPos? = null) {
        val context = DecayContext.create(world, source, originPos, targetPos)

        val patterns = DecayLoader.getPatterns(context)

        for (pattern in patterns) {
            if (pattern.value.test(context)) {
                val pos = context.target.pos
                sendBreakBlockProgress(world, pos, BREAK_BLOCK_STAGE)
                world.playSound(null, pos, ModSoundEvents.TEARING, SoundSource.BLOCKS, 0.5f, 1f)
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
            DECAY_QUEUE.computeIfAbsent(context.world.dimension()) { HashMap() }.putIfAbsent(context.target.pos, task)
        }
    }

    @JvmStatic
    fun clearQueue() {
        DECAY_QUEUE.clear()
    }

    @JvmStatic
    fun tick(world: ServerLevel) {
        val key = world.dimension()
        val tasks = DECAY_QUEUE[key]?.takeUnless { it.isEmpty() } ?: return
        val ready = ArrayList<DecayTask>()
        tasks.values.removeIf { it.reduceDelayIsDone() && ready.add(it) }
        ready.forEach { it.process() }
    }

    private fun sendBreakBlockProgress(world: ServerLevel, pos: BlockPos, stage: Int) {
        val packet = RenderBreakBlockS2CPacket(pos, stage)
        world.getPlayers(EntitySelector.withinDistance(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), BREAK_BLOCK_RENDER_DISTANCE))
            .forEach { player -> ServerPacketHandler.sendPacket(player, packet) }
    }

    object DecayLoader {
        private val patterns: MutableMap<ResourceKey<*>, MutableList<DecayPatternHolder>> = HashMap()

        private var undiffernitatedPatterns: List<DecayPatternHolder> = ArrayList()

        @JvmStatic
        fun reload(manager: ResourceManager) {
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
                        var patterns = getPatterns(obj.target.blockState)
                        if (patterns.isEmpty()) patterns = getPatterns(obj.target.fluidState)
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
            sendBreakBlockProgress(context.world, context.target.pos, -1)

            val currentContext = context

            if (!processor.value.test(currentContext)) {
                return
            }

            if (currentContext.source.decayIntoWorldThread()) {
                if (config.decayConfig.decaysIntoAir) {
                    val pos = currentContext.target.pos
                    val contents = DecayInventoryHelper.takeContents(currentContext.world, pos)
                    currentContext.world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
                    DecayInventoryHelper.drop(currentContext.world, pos, contents)
                } else processor.value.applyPattern(currentContext)
            } else {
                processor.value.applyPattern(currentContext)
            }
        }
    }

    data class Snapshot(val pos: BlockPos, val blockState: BlockState, val fluidState: FluidState) {
        companion object {
            fun create(
                world: ServerLevel,
                pos: BlockPos
            ): Snapshot {
                val blockState = world.getBlockState(pos)
                val fluidState = blockState.fluidState
                return Snapshot(pos, blockState, fluidState)
            }
        }
    }

    data class DecayContext(
        val world: ServerLevel,
        val origin: Snapshot,
        val target: Snapshot,
        val source: DecaySource
    ) {
        val targetEntity: Entity? by lazy { world.getEntitiesOfClass(Painting::class.java, AABB.encapsulatingFullBlocks(target.pos, target.pos)).firstOrNull() }

        companion object {
            fun create(world: ServerLevel, source: DecaySource, originBlockPos: BlockPos, targetBlockPos: BlockPos? = null): DecayContext {
                val origin = Snapshot.create(world, originBlockPos)
                val target = if(targetBlockPos == null) origin else Snapshot.create(world, targetBlockPos)

                return DecayContext(world, origin, target, source)
            }
        }
    }
}
