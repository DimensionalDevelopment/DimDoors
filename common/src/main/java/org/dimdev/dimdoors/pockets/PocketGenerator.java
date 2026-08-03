package org.dimdev.dimdoors.pockets;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.ModRegistryKeys;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.rift.registry.DialingAddress;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.DialingPocket;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.Objects;

public final class PocketGenerator {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final ResourceKey<VirtualPocket> ALL_DUNGEONS = ResourceKey.create(ModRegistryKeys.POCKET_GROUPS, DimensionalDoors.id("dungeon"));
    public static final ResourceKey<VirtualPocket> PUBLIC = ResourceKey.create(ModRegistryKeys.POCKET_GROUPS, DimensionalDoors.id("public"));
    public static final ResourceKey<VirtualPocket> PRIVATE = ResourceKey.create(ModRegistryKeys.POCKET_GROUPS, DimensionalDoors.id("private"));
    public static final ResourceKey<VirtualPocket> DIALING = ResourceKey.create(ModRegistryKeys.POCKET_GROUPS, DimensionalDoors.id("dialing"));
    public static final ResourceKey<VirtualPocket> NETHER_DUNGEONS = ResourceKey.create(ModRegistryKeys.POCKET_GROUPS, DimensionalDoors.id("nether"));
    public static final ResourceKey<VirtualPocket> MYTH_DUNGEONS = ResourceKey.create(ModRegistryKeys.POCKET_GROUPS, DimensionalDoors.id("myth"));
    public static final ResourceKey<VirtualPocket> RUINS_DUNGEONS = ResourceKey.create(ModRegistryKeys.POCKET_GROUPS, DimensionalDoors.id("ruins"));
    public static final ResourceKey<VirtualPocket> ATLANTIS_DUNGEONS = ResourceKey.create(ModRegistryKeys.POCKET_GROUPS, DimensionalDoors.id("atlantis"));
    public static final ResourceKey<VirtualPocket> JUNGLE_DUNGEONS = ResourceKey.create(ModRegistryKeys.POCKET_GROUPS, DimensionalDoors.id("jungle"));
    public static final ResourceKey<VirtualPocket> SNOW_DUNGEONS = ResourceKey.create(ModRegistryKeys.POCKET_GROUPS, DimensionalDoors.id("snow"));
    public static final ResourceKey<VirtualPocket> PYRAMID_DUNGEONS = ResourceKey.create(ModRegistryKeys.POCKET_GROUPS, DimensionalDoors.id("pyramid"));
    public static final ResourceKey<VirtualPocket> END_DUNGEONS = ResourceKey.create(ModRegistryKeys.POCKET_GROUPS, DimensionalDoors.id("end"));

    public static Pocket<?,?> generateDialingPocket(VirtualLocation virtualLocation, DialingAddress address) {
        Objects.requireNonNull(address, "address");

        Pocket<?, ?> pocket = generateFromPocketGroupV2(DimensionalDoors.getWorld(ModDimensions.PUBLIC), DIALING, virtualLocation, null, null);
        if (pocket instanceof DialingPocket dialingPocket) {
            dialingPocket.setAddress(address);
        }
        return pocket;
    }


    public static Pocket<?, ?> generatePrivatePocketV2(VirtualLocation virtualLocation) {
        return generateFromPocketGroupV2(DimensionalDoors.getWorld(ModDimensions.PERSONAL), PRIVATE, virtualLocation, null, null);
    }

    public static Pocket<?, ?> generatePublicPocketV2(VirtualLocation virtualLocation, VirtualTarget<?> linkTo, LinkProperties linkProperties) {
        return generateFromPocketGroupV2(DimensionalDoors.getWorld(ModDimensions.PUBLIC), PUBLIC, virtualLocation, linkTo, linkProperties);
    }

    public static Pocket<?, ?> generateFromPocketGroupV2(ServerLevel world, ResourceKey<VirtualPocket> group, VirtualLocation virtualLocation, VirtualTarget<?> linkTo, LinkProperties linkProperties) {
        if (world == null) {
            LOGGER.error("Cannot generate pocket group {} because the target world is unavailable.", group);
            return null;
        }

        VirtualPocket virtualPocket = world.registryAccess().registry(group.registryKey()).map(a -> a.get(group)).orElse(null);
        if (virtualPocket == null) {
            LOGGER.error("Cannot generate pocket group {} because it is not loaded.", group);
            return null;
        }

        PocketGenerationContext context = new PocketGenerationContext(world, virtualLocation, linkTo, linkProperties, world.registryAccess());
        return generatePocketV2(virtualPocket.getNextPocketGeneratorReference(context), context);
    }

    public static Pocket<?, ?> generateFromVirtualPocket(ServerLevel world, Holder<VirtualPocket> id, VirtualLocation virtualLocation, VirtualTarget<?> linkTo, LinkProperties linkProperties) {
        if (world == null) {
            LOGGER.error("Cannot generate virtual pocket {} because the target world is unavailable.", id.getRegisteredName());
            return null;
        }

        VirtualPocket virtualPocket = id.value();

        PocketGenerationContext context = new PocketGenerationContext(world, virtualLocation, linkTo, linkProperties, world.registryAccess());
        LOGGER.info("Generating virtual target: " + id);
        return generatePocketV2(virtualPocket.getNextPocketGeneratorReference(context), context);
    }

    public static Pocket<?, ?> generatePocketV2(PocketGeneratorReference pocketGeneratorReference, PocketGenerationContext context) {
        if (context == null || context.world() == null) {
            LOGGER.error("Cannot generate pocket because the generation context has no world.");
            return null;
        }
        if (pocketGeneratorReference == null) {
            LOGGER.error("Cannot generate pocket at {} because no pocket generator reference resolved.", context.sourceVirtualLocation());
            return null;
        }

        return PocketCreator.create(pocketGeneratorReference, context);
    }

    public static Pocket<?, ?> generateDungeonPocketV2(VirtualLocation virtualLocation, VirtualTarget<?> linkTo, LinkProperties linkProperties) {
        return generateFromPocketGroupV2(DimensionalDoors.getWorld(ModDimensions.DUNGEON), ALL_DUNGEONS, virtualLocation, linkTo, linkProperties);
    }

    public static Pocket<?, ?> generateDungeonPocketV2(VirtualLocation virtualLocation, VirtualTarget<?> linkTo, LinkProperties linkProperties, ResourceKey<VirtualPocket> group) {
        return generateFromPocketGroupV2(DimensionalDoors.getWorld(ModDimensions.DUNGEON), group, virtualLocation, linkTo, linkProperties);
    }

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
}
