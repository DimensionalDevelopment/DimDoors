package org.dimdev.dimdoors.pockets;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.util.WorldUtil;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.Pocket;
import org.dimdev.dimdoors.world.pocket.PocketRegistry;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import net.minecraft.server.world.ServerWorld;

public final class PocketGenerator {
    private static final Logger LOGGER = LogManager.getLogger();

    private static Pocket prepareAndPlacePocket(ServerWorld world, PocketTemplate pocketTemplate, VirtualLocation virtualLocation, boolean setup) {
        LOGGER.info("Generating pocket from template " + pocketTemplate.getId() + " at virtual location " + virtualLocation);

        Pocket pocket = PocketRegistry.instance(world.getRegistryKey()).newPocket();
        pocketTemplate.place(pocket, setup);
        pocket.virtualLocation = virtualLocation;
        return pocket;
    }

    public static Pocket generatePocketFromTemplate(ServerWorld world, PocketTemplate pocketTemplate, VirtualLocation virtualLocation, boolean setup) {
        Pocket pocket = prepareAndPlacePocket(world, pocketTemplate, virtualLocation, setup);
        if (setup) pocketTemplate.setup(pocket, null, null);
        return pocket;
    }

    public static Pocket generatePocketFromTemplate(ServerWorld world, PocketTemplate pocketTemplate, VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
        Pocket pocket = prepareAndPlacePocket(world, pocketTemplate, virtualLocation, true);
        pocketTemplate.setup(pocket, linkTo, linkProperties);
        return pocket;
    }

    public static Pocket generatePrivatePocket(VirtualLocation virtualLocation) {
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getPersonalPocketTemplate();
        return generatePocketFromTemplate(WorldUtil.getWorld(ModDimensions.PERSONAL), pocketTemplate, virtualLocation, true);
    }

    // TODO: size of public pockets should increase with depth
    public static Pocket generatePublicPocket(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getPublicPocketTemplate();
        return generatePocketFromTemplate(WorldUtil.getWorld(ModDimensions.PUBLIC), pocketTemplate, virtualLocation, linkTo, linkProperties);
    }

    /**
     * Create a dungeon pockets at a certain depth.
     *
     * @param virtualLocation The virtual location of the pockets
     * @return The newly-generated dungeon pockets
     */
    public static Pocket generateDungeonPocket(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
        int depth = virtualLocation.depth;
        float netherProbability = WorldUtil.getWorld(virtualLocation.world).getDimension().isUltrawarm() ? 1 : (float) depth / 200; // TODO: improve nether probability
        Random random = new Random();
        String group = random.nextFloat() < netherProbability ? "nether" : "ruins";
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getRandomTemplate(group, depth, ModConfig.POCKETS.maxPocketSize, false);

        return generatePocketFromTemplate(WorldUtil.getWorld(ModDimensions.DUNGEON), pocketTemplate, virtualLocation, linkTo, linkProperties);
    }
}
