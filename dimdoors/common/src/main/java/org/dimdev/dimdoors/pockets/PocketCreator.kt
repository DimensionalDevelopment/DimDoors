package org.dimdev.dimdoors.pockets

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.rift.targets.LocationProvider
import org.dimdev.dimdoors.world.pocket.type.Pocket
import java.util.concurrent.ConcurrentHashMap

interface PocketCreator {
    fun prepareAndPlacePocket(parameters: PocketGenerationContext): Pocket<*, *>?

    fun prepareAndPlacePocket(parameters: PocketGenerationContext, setupLoot: Boolean?): Pocket<*, *>?

    data class GenerationKey(val type: String, val world: ResourceKey<Level>, val pos: BlockPos) {
        companion object {
            fun from(parameters: PocketGenerationContext): GenerationKey {
                val location = parameters.linkTo?.castOrNull<LocationProvider>()?.providedLocation
                if (location != null) {
                    return GenerationKey("source", location.worldId, location.blockPos)
                }

                val virtualLocation = parameters.sourceVirtualLocation
                return GenerationKey(
                    "virtual",
                    virtualLocation.world,
                    BlockPos(virtualLocation.x, virtualLocation.depth, virtualLocation.z)
                )
            }
        }
    }

    companion object {
        private val LOGGER: Logger = LogManager.getLogger()
        private val ACTIVE_GENERATIONS: MutableSet<GenerationKey> = ConcurrentHashMap.newKeySet()

        @JvmStatic
        fun create(creator: PocketCreator, parameters: PocketGenerationContext): Pocket<*, *>? {
            val key = GenerationKey.from(parameters)
            if (!ACTIVE_GENERATIONS.add(key)) {
                LOGGER.warn("Skipping re-entrant pocket generation for {}.", key)
                return null
            }

            try {
                return creator.prepareAndPlacePocket(parameters)
            } finally {
                ACTIVE_GENERATIONS.remove(key)
            }
        }
    }
}
