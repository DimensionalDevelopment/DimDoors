package org.dimdev.dimdoors.pockets

import net.minecraft.core.Holder
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.rift.registry.LinkProperties
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.world.pocket.VirtualLocation
import org.dimdev.dimdoors.world.pocket.type.Pocket
import kotlin.jvm.optionals.getOrNull

fun <T> ResourceKey<T>.holder() = DimensionalDoors.server.registryAccess().lookupOrThrow(this.registryKey()).get(this).getOrNull()
private fun ResourceKey<Level>.world() = DimensionalDoors.getWorld(this)

object PocketGenerationHelper {
    private val LOGGER: Logger = LogManager.getLogger()

    @JvmField val ALL_DUNGEONS: ResourceKey<VirtualPocket> = group("dungeon")
    @JvmField val PUBLIC: ResourceKey<VirtualPocket> = group("public")
    @JvmField val PRIVATE: ResourceKey<VirtualPocket> = group("private")
    @JvmField val DIALING: ResourceKey<VirtualPocket> = group("dialing")
    @JvmField val HUB: ResourceKey<VirtualPocket> = group("hub")
    @JvmField val NETHER_DUNGEONS: ResourceKey<VirtualPocket> = group("nether")
    @JvmField val MYTH_DUNGEONS: ResourceKey<VirtualPocket> = group("myth")
    @JvmField val RUINS_DUNGEONS: ResourceKey<VirtualPocket> = group("ruins")
    @JvmField val ATLANTIS_DUNGEONS: ResourceKey<VirtualPocket> = group("atlantis")
    @JvmField val JUNGLE_DUNGEONS: ResourceKey<VirtualPocket> = group("jungle")
    @JvmField val SNOW_DUNGEONS: ResourceKey<VirtualPocket> = group("snow")
    @JvmField val PYRAMID_DUNGEONS: ResourceKey<VirtualPocket> = group("pyramid")
    @JvmField val END_DUNGEONS: ResourceKey<VirtualPocket> = group("end")

    private fun group(name: String): ResourceKey<VirtualPocket> = ResourceKey.create(ModRegistryKeys.POCKET_GROUPS, DimensionalDoors.id(name))

    fun generateFromVirtualPocket(key: ResourceKey<VirtualPocket>, virtualLocation: VirtualLocation, linkTo: VirtualTarget<*>? = null, linkProperties: LinkProperties? = null): Pocket<*, *>? {
        return generateFromVirtualPocket(key.holder() ?: return null, virtualLocation, linkTo, linkProperties)
    }
    fun generateFromVirtualPocket(id: Holder<VirtualPocket>, virtualLocation: VirtualLocation, linkTo: VirtualTarget<*>?, linkProperties: LinkProperties?): Pocket<*, *>? = generateFromVirtualPocket(id.value(), virtualLocation, linkTo, linkProperties)

    @JvmStatic
    fun generateFromVirtualPocket(id: VirtualPocket, virtualLocation: VirtualLocation, linkTo: VirtualTarget<*>?, linkProperties: LinkProperties?): Pocket<*, *>? {
        val world = id.level.world()

        if (world == null) {
            LOGGER.error("Cannot generate virtual pocket because the target world is unavailable.",)
            return null
        }

        val context = PocketGenerationContext(world, virtualLocation, linkTo, linkProperties, world.registryAccess())
        LOGGER.info("Generating virtual target: $id")

        val pocketGeneratorReference = id.getNextPocketGeneratorReference(context)


        return PocketCreator.create(pocketGeneratorReference, context)
    }

}

fun ResourceKey<VirtualPocket>.generatePocket(
    virtualLocation: VirtualLocation,
    linkTo: VirtualTarget<*>? = null,
    linkProperties: LinkProperties? = null
): Pocket<*, *>? = PocketGenerationHelper.generateFromVirtualPocket(this, virtualLocation, linkTo, linkProperties)
