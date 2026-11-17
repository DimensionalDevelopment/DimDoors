package org.dimdev.dimdoors.decay


import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.Painting
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.phys.AABB
import net.minecraft.world.ticks.ScheduledTick
import net.minecraft.world.ticks.TickPriority
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.DimensionalDoors.Companion.config
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.decay.pattern.DecayPattern
import org.dimdev.dimdoors.sound.ModSoundEvents
import org.dimdev.dimdoors.util.ServerLevelExt
import java.util.*
import kotlin.jvm.optionals.getOrNull

/**
 * Provides methods for applying decay. Decay refers to the effect that most blocks placed next to certain blocks like unraveled fabric
 * change into simpler forms ultimately becoming in most cases unraveled Fabric as time passes.
 */
object Decay {
    private const val BREAK_BLOCK_STAGE = 5
    val levelEventId = "dimdoors:decay".hashCode()

    /**
     * Checks if a block can be decayed and, if so, changes it to the next block ID along the decay sequence.
     */
    @JvmStatic
    fun decayBlock(world: ServerLevel, source: DecaySource, blockPos: BlockPos) {
        val snapshot = Snapshot.create(world, blockPos)

        val context = DecayContext.create(world, source, blockPos)

        val patterns = DecayLoader.getPatterns(world, snapshot)

        for (pattern in patterns) {
            if (pattern.value().test(world, snapshot, source)) {
                val pos = context.target.pos
                world.levelEvent(levelEventId, pos, BREAK_BLOCK_STAGE)
                world.playSound(null, pos, ModSoundEvents.TEARING, SoundSource.BLOCKS, 0.5f, 1f)
                val task = DecayTask(source, pattern)

                world.castOrNull<ServerLevelExt>()?.`dimensionalDoors$getDecayTicks`()?.schedule(ScheduledTick(task, pos, world.gameTime + config.decayConfig.decayDelay, TickPriority.NORMAL, world.nextSubTickCount()))

                break
            }
        }
    }

    object DecayLoader {
        private val patterns: Reference2ObjectOpenHashMap<ResourceKey<*>, MutableList<Holder<DecayPattern>>> = Reference2ObjectOpenHashMap()

        @JvmStatic
        fun reload(registryAccess: RegistryAccess) {
            patterns.clear()

            registryAccess.registry(ModRegistryKeys.DECAY_PATTERN).ifPresent {
                it.holders().forEach { pattern ->
                    pattern.value().constructApplicable(registryAccess)
                        .forEach { resourceKey -> patterns.computeIfAbsent(resourceKey) { ArrayList() }.add(pattern) }
                }
            }
        }


        @JvmStatic
        fun getPatterns(level: ServerLevel, obj: Any?): Collection<Holder<DecayPattern>> {
            if (obj != null) {
                return when (obj) {
                    is FluidState -> getPatterns(level, obj.holder())
                    is BlockState -> getPatterns(level, obj.blockHolder)
                    is ResourceKey<*> -> patterns.getOrDefault(obj, emptyList())
                    is Holder<*> -> obj.unwrapKey().getOrNull()?.let { patterns[it] } ?: emptyList()
                    is Snapshot -> {
                        var patterns = getPatterns(level, obj.blockState)
                        if (patterns.isEmpty()) patterns = getPatterns(level, obj.fluidState)
                        if (patterns.isEmpty()) patterns = getPatterns(level, obj.targetEntity(level))
                        if (patterns.isEmpty()) patterns = emptyList()
                        patterns
                    }

                    is Entity -> {
                        when (obj) {
                            is Painting -> getPatterns(level, obj.variant)
                            else -> emptyList()
                        }
                    }

                    else -> emptyList()
                }
            }

            return emptyList()
        }

        @JvmStatic
        fun getPatterns(): Map<ResourceKey<*>, List<Holder<DecayPattern>>> {
            return patterns
        }
    }

    data class DecayTask(private val source: DecaySource, private val processor: Holder<DecayPattern>) {
        fun process(level: ServerLevel, pos: BlockPos) {
            level.levelEvent(levelEventId, pos, -1)

            val snapshot = Snapshot.create(level, pos)

            if (!processor.value().test(level, snapshot, source)) return

            if (source.decayIntoWorldThread() && config.decayConfig.decaysIntoAir) {
                    val contents = DecayInventoryHelper.takeContents(level, pos)
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
                    DecayInventoryHelper.drop(level, pos, contents)
            } else {
                processor.value().applyPattern(level, snapshot, source)
            }
        }

        fun encode(): String = "${source.serializedName}|${processor.unwrapKey().orElseThrow().location()}"

        companion object {
            @JvmStatic
            fun decode(level: ServerLevel, encoded: String): Optional<DecayTask> {
                val (sourceName, patternId) = encoded.split("|").takeIf { it.size == 2 } ?: return Optional.empty()
                val source = DecaySource.entries.firstOrNull { it.serializedName == sourceName } ?: return Optional.empty()
                val id = ResourceLocation.tryParse(patternId) ?: return Optional.empty()
                return level.registryAccess().registryOrThrow(ModRegistryKeys.DECAY_PATTERN).getHolder(id).map { DecayTask(source, it) }
            }
        }
    }

    data class Snapshot(val pos: BlockPos, val blockState: BlockState, val fluidState: FluidState) {
        fun targetEntity(level: ServerLevel): Entity? = level.getEntitiesOfClass(Entity::class.java, AABB.encapsulatingFullBlocks(pos, pos)).firstOrNull()

        companion object {
            fun create(
                world: ServerLevel,
                pos: BlockPos
            ): Snapshot = world.getBlockState(pos).let { Snapshot(pos, it, it.fluidState) }
        }
    }

    data class DecayContext(
        val target: Snapshot,
        val source: DecaySource
    ) {
        companion object {
            fun create(world: ServerLevel, source: DecaySource, blockPos: BlockPos): DecayContext {
                val snapshot = Snapshot.create(world, blockPos)

                return DecayContext(snapshot, source)
            }
        }
    }
}
