package org.dimdev.dimdoors.pockets;

import java.util.Random;

import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.world.level.DimensionalRegistry;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import net.minecraft.server.world.ServerWorld;

public final class PocketGenerator {
    private static final Logger LOGGER = LogManager.getLogger();

    private static Pocket prepareAndPlacePocket(ServerWorld world, PocketTemplate pocketTemplate, VirtualLocation virtualLocation, boolean setup) {
        LOGGER.info("Generating pocket from template " + pocketTemplate.getId() + " at virtual location " + virtualLocation);

        Pocket pocket = DimensionalRegistry.getPocketDirectory(world.getRegistryKey()).newPocket(Pocket.builder().expand(new Vec3i(1, 1, 1)));
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
        return generatePocketFromTemplate(DimensionalDoorsInitializer.getWorld(ModDimensions.PERSONAL), pocketTemplate, virtualLocation, true);
    }

    public static Pocket generatePrivatePocketV2(VirtualLocation virtualLocation) {
		return generateFromPocketGroupV2(DimensionalDoorsInitializer.getWorld(ModDimensions.PERSONAL), "private", virtualLocation, null, null);
    }

    // TODO: size of public pockets should increase with depth
    public static Pocket generatePublicPocket(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getPublicPocketTemplate();
        return generatePocketFromTemplate(DimensionalDoorsInitializer.getWorld(ModDimensions.PUBLIC), pocketTemplate, virtualLocation, linkTo, linkProperties);
    }

    public static Pocket generatePublicPocketV2(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
        return generateFromPocketGroupV2(DimensionalDoorsInitializer.getWorld(ModDimensions.PUBLIC), "public", virtualLocation, linkTo, linkProperties);
    }

    public static Pocket generateFromPocketGroupV2(ServerWorld world, String group, VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
    	PocketGenerationParameters parameters = new PocketGenerationParameters(world, virtualLocation, linkTo, linkProperties);
    	return generatePocketV2(SchematicV2Handler.getInstance().getGroup(group).getNextPocketGeneratorReference(parameters), parameters);
	}

	public static Pocket generatePocketV2(PocketGeneratorReference pocketGeneratorReference, PocketGenerationParameters parameters) {
    	return pocketGeneratorReference.prepareAndPlacePocket(parameters);
	}

	public static Pocket generateDungeonPocketV2(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
		return generateFromPocketGroupV2(DimensionalDoorsInitializer.getWorld(ModDimensions.DUNGEON), "dungeon", virtualLocation, linkTo, linkProperties);
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
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getRandomTemplate(group, depth, DimensionalDoorsInitializer.getConfig().getPocketsConfig().maxPocketSize, false);

        return generatePocketFromTemplate(DimensionalDoorsInitializer.getWorld(ModDimensions.DUNGEON), pocketTemplate, virtualLocation, linkTo, linkProperties);
    }
}
