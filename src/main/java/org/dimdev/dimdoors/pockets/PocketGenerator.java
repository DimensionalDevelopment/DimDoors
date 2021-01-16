package org.dimdev.dimdoors.pockets;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.util.DimensionalRegistry;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.Pocket;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import net.minecraft.server.world.ServerWorld;

public final class PocketGenerator {
    private static final Logger LOGGER = LogManager.getLogger();

    private static Pocket prepareAndPlacePocket(ServerWorld world, PocketTemplate pocketTemplate, VirtualLocation virtualLocation, boolean setup) {
        LOGGER.info("Generating pocket from template " + pocketTemplate.getId() + " at virtual location " + virtualLocation);

        Pocket pocket = DimensionalRegistry.getPocketDirectory(world.getRegistryKey()).newPocket();
        pocketTemplate.place(pocket, setup);
        pocket.virtualLocation = virtualLocation;
        return pocket;
    }

    private static Pocket prepareAndPlaceV2Pocket(ServerWorld world, PocketTemplateV2 pocketTemplate, VirtualLocation virtualLocation, boolean setup) {
        LOGGER.info("Generating pocket from template " + pocketTemplate.getId() + " at virtual location " + virtualLocation);

        Pocket pocket = DimensionalRegistry.getPocketDirectory(world.getRegistryKey()).newPocket();
        pocketTemplate.place(pocket);
        pocket.virtualLocation = virtualLocation;
        return pocket;
    }

    public static Pocket generatePocketFromTemplate(ServerWorld world, PocketTemplate pocketTemplate, VirtualLocation virtualLocation, boolean setup) {
        Pocket pocket = prepareAndPlacePocket(world, pocketTemplate, virtualLocation, setup);
        if (setup) pocketTemplate.setup(pocket, null, null);
        return pocket;
    }

    public static Pocket generateV2PocketFromTemplate(ServerWorld world, PocketTemplateV2 pocketTemplate, VirtualLocation virtualLocation, boolean setup) {
        Pocket pocket = prepareAndPlaceV2Pocket(world, pocketTemplate, virtualLocation, setup);
        if (setup) {
            pocketTemplate.setup(pocket, null, null);
        }
        return pocket;
    }


    public static Pocket generatePocketFromTemplate(ServerWorld world, PocketTemplate pocketTemplate, VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
        Pocket pocket = prepareAndPlacePocket(world, pocketTemplate, virtualLocation, true);
        pocketTemplate.setup(pocket, linkTo, linkProperties);
        return pocket;
    }

    public static Pocket generateV2PocketFromTemplate(ServerWorld world, PocketTemplateV2 pocketTemplate, VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
        Pocket pocket = prepareAndPlaceV2Pocket(world, pocketTemplate, virtualLocation, true);
        pocketTemplate.setup(pocket, linkTo, linkProperties);
        return pocket;
    }

    public static Pocket generatePrivatePocket(VirtualLocation virtualLocation) {
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getPersonalPocketTemplate();
        return generatePocketFromTemplate(DimensionalDoorsInitializer.getWorld(ModDimensions.PERSONAL), pocketTemplate, virtualLocation, true);
    }

    public static Pocket generatePrivatePocketV2(VirtualLocation virtualLocation) {
        PocketTemplateV2 pocketTemplate = SchematicV2Handler.getInstance().getRandomPrivatePocket();
        return generateV2PocketFromTemplate(DimensionalDoorsInitializer.getWorld(ModDimensions.PERSONAL), pocketTemplate, virtualLocation, true);
    }

    // TODO: size of public pockets should increase with depth
    public static Pocket generatePublicPocket(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getPublicPocketTemplate();
        return generatePocketFromTemplate(DimensionalDoorsInitializer.getWorld(ModDimensions.PUBLIC), pocketTemplate, virtualLocation, linkTo, linkProperties);
    }

    public static Pocket generatePublicPocketV2(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
        PocketTemplateV2 pocketTemplate = SchematicV2Handler.getInstance().getRandomPublicPocket();
        return generateV2PocketFromTemplate(DimensionalDoorsInitializer.getWorld(ModDimensions.PUBLIC), pocketTemplate, virtualLocation, linkTo, linkProperties);
    }

    /**
     * Create a dungeon pockets at a certain depth.
     *
     * @param virtualLocation The virtual location of the pockets
     * @return The newly-generated dungeon pockets
     */
    public static Pocket generateDungeonPocket(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
        int depth = virtualLocation.getDepth();
        float netherProbability = DimensionalDoorsInitializer.getWorld(virtualLocation.getWorld()).getDimension().isUltrawarm() ? 1 : (float) depth / 200; // TODO: improve nether probability
        Random random = new Random();
        String group = random.nextFloat() < netherProbability ? "nether" : "ruins";
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getRandomTemplate(group, depth, ModConfig.INSTANCE.getPocketsConfig().maxPocketSize, false);

        return generatePocketFromTemplate(DimensionalDoorsInitializer.getWorld(ModDimensions.DUNGEON), pocketTemplate, virtualLocation, linkTo, linkProperties);
    }
}
