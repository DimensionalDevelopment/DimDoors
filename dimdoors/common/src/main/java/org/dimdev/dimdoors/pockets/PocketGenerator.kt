package org.dimdev.dimdoors.pockets

import net.minecraft.core.Holder
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference
import org.dimdev.dimdoors.rift.registry.DialingAddress
import org.dimdev.dimdoors.rift.registry.LinkProperties
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.world.ModDimensions
import org.dimdev.dimdoors.world.pocket.DialingPocket
import org.dimdev.dimdoors.world.pocket.VirtualLocation
import org.dimdev.dimdoors.world.pocket.type.Pocket

object PocketGenerator {
    private val LOGGER: Logger = LogManager.getLogger()

    @JvmField val ALL_DUNGEONS: ResourceKey<VirtualPocket> = group("dungeon")
    @JvmField val PUBLIC: ResourceKey<VirtualPocket> = group("public")
    @JvmField val PRIVATE: ResourceKey<VirtualPocket> = group("private")
    @JvmField val DIALING: ResourceKey<VirtualPocket> = group("dialing")
    @JvmField val NETHER_DUNGEONS: ResourceKey<VirtualPocket> = group("nether")
    @JvmField val MYTH_DUNGEONS: ResourceKey<VirtualPocket> = group("myth")
    @JvmField val RUINS_DUNGEONS: ResourceKey<VirtualPocket> = group("ruins")
    @JvmField val ATLANTIS_DUNGEONS: ResourceKey<VirtualPocket> = group("atlantis")
    @JvmField val JUNGLE_DUNGEONS: ResourceKey<VirtualPocket> = group("jungle")
    @JvmField val SNOW_DUNGEONS: ResourceKey<VirtualPocket> = group("snow")
    @JvmField val PYRAMID_DUNGEONS: ResourceKey<VirtualPocket> = group("pyramid")
    @JvmField val END_DUNGEONS: ResourceKey<VirtualPocket> = group("end")

    private fun group(name: String): ResourceKey<VirtualPocket> = ResourceKey.create(ModRegistryKeys.POCKET_GROUPS, DimensionalDoors.id(name))

    @JvmStatic
    fun generateDialingPocket(virtualLocation: VirtualLocation, address: DialingAddress): Pocket<*, *>? {
        val pocket = generateFromPocketGroupV2(DimensionalDoors.getWorld(ModDimensions.PUBLIC), DIALING, virtualLocation, null, null)
        if (pocket is DialingPocket) {
            pocket.address = address
        }
        return pocket
    }

    @JvmStatic
    fun generatePrivatePocketV2(virtualLocation: VirtualLocation): Pocket<*, *>? =
        generateFromPocketGroupV2(DimensionalDoors.getWorld(ModDimensions.PERSONAL), PRIVATE, virtualLocation, null, null)

    @JvmStatic
    fun generatePublicPocketV2(virtualLocation: VirtualLocation, linkTo: VirtualTarget<*>?, linkProperties: LinkProperties?): Pocket<*, *>? =
        generateFromPocketGroupV2(DimensionalDoors.getWorld(ModDimensions.PUBLIC), PUBLIC, virtualLocation, linkTo, linkProperties)

    @JvmStatic
    fun generateFromPocketGroupV2(world: ServerLevel?, group: ResourceKey<VirtualPocket>, virtualLocation: VirtualLocation, linkTo: VirtualTarget<*>?, linkProperties: LinkProperties?): Pocket<*, *>? {
        if (world == null) {
            LOGGER.error("Cannot generate pocket group {} because the target world is unavailable.", group)
            return null
        }

        val virtualPocket = world.registryAccess().registry(group.registryKey()).map { it.get(group) }.orElse(null)
        if (virtualPocket == null) {
            LOGGER.error("Cannot generate pocket group {} because it is not loaded.", group)
            return null
        }

        val context = PocketGenerationContext(world, virtualLocation, linkTo, linkProperties, world.registryAccess())
        return generatePocketV2(virtualPocket.getNextPocketGeneratorReference(context), context)
    }

    @JvmStatic
    fun generateFromVirtualPocket(world: ServerLevel?, id: Holder<VirtualPocket>, virtualLocation: VirtualLocation, linkTo: VirtualTarget<*>?, linkProperties: LinkProperties?): Pocket<*, *>? {
        if (world == null) {
            LOGGER.error("Cannot generate virtual pocket {} because the target world is unavailable.", id.registeredName)
            return null
        }

        val virtualPocket = id.value()

        val context = PocketGenerationContext(world, virtualLocation, linkTo, linkProperties, world.registryAccess())
        LOGGER.info("Generating virtual target: $id")
        return generatePocketV2(virtualPocket.getNextPocketGeneratorReference(context), context)
    }

    @JvmStatic
    fun generatePocketV2(pocketGeneratorReference: PocketGeneratorReference<*>?, context: PocketGenerationContext): Pocket<*, *>? {
        if (pocketGeneratorReference == null) {
            LOGGER.error("Cannot generate pocket at {} because no pocket generator reference resolved.", context.sourceVirtualLocation)
            return null
        }

        return PocketCreator.create(pocketGeneratorReference, context)
    }

    @JvmStatic
    fun generateDungeonPocketV2(virtualLocation: VirtualLocation, linkTo: VirtualTarget<*>?, linkProperties: LinkProperties?): Pocket<*, *>? =
        generateFromPocketGroupV2(DimensionalDoors.getWorld(ModDimensions.DUNGEON), ALL_DUNGEONS, virtualLocation, linkTo, linkProperties)

    @JvmStatic
    fun generateDungeonPocketV2(virtualLocation: VirtualLocation, linkTo: VirtualTarget<*>?, linkProperties: LinkProperties?, group: ResourceKey<VirtualPocket>): Pocket<*, *>? =
        generateFromPocketGroupV2(DimensionalDoors.getWorld(ModDimensions.DUNGEON), group, virtualLocation, linkTo, linkProperties)

    /*
    /**
     * Create a dungeon pockets at a certain depth.
     *
     * @param virtualLocation The virtual location of the pockets
     * @return The newly-generated dungeon pockets
     */
    /*
    public static Pocket generateDungeonPocket(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
        int depth = virtualLocation.getDepth();
        float netherProbability = DimensionalDoorsInitializer.getWorld(virtualLocation.getWorld()).getDimension().isUltrawarm() ? 1 : (float) depth / 200; // TODO: improve nether probability
        Random random = Random.create();
        String group = random.nextFloat() < netherProbability ? "nether" : "ruins";
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getRandomTemplate(group, depth, DimensionalDoorsInitializer.getConfig().getPocketsConfig().maxPocketSize, false);

        return generatePocketFromTemplate(DimensionalDoorsInitializer.getWorld(ModDimensions.DUNGEON), pocketTemplate, virtualLocation, linkTo, linkProperties);
    }
    */
     */
}
