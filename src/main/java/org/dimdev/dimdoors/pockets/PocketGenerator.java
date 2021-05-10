package org.dimdev.dimdoors.pockets;

import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import net.minecraft.server.world.ServerWorld;

public final class PocketGenerator {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final Identifier ALL_DUNGEONS = new Identifier("dimdoors", "dungeon");
    public static final Identifier NETHER_DUNGEONS = new Identifier("dimdoors", "nether");
	public static final Identifier RUINS_DUNGEONS = new Identifier("dimdoors", "ruins");
	public static final Identifier ATLANTIS_DUNGEONS = new Identifier("dimdoors", "atlantis");
	public static final Identifier JUNGLE_DUNGEONS = new Identifier("dimdoors", "jungle");
	public static final Identifier SNOW_DUNGEONS = new Identifier("dimdoors", "snow");
	public static final Identifier PYRAMID_DUNGEONS = new Identifier("dimdoors", "pyramid");
	public static final Identifier END_DUNGEONS = new Identifier("dimdoors", "end");

    /*
    private static Pocket prepareAndPlacePocket(ServerWorld world, PocketTemplate pocketTemplate, VirtualLocation virtualLocation, boolean setup) {
        LOGGER.info("Generating pocket from template " + pocketTemplate.getId() + " at virtual location " + virtualLocation);

        Pocket pocket = DimensionalRegistry.getPocketDirectory(world.getRegistryKey()).newPocket(Pocket.builder().expand(new Vec3i(1, 1, 1)));
        pocketTemplate.place(pocket, setup);
        pocket.virtualLocation = virtualLocation;
        return pocket;
    }
	*/


    public static Pocket generatePrivatePocketV2(VirtualLocation virtualLocation) {
		return generateFromPocketGroupV2(DimensionalDoorsInitializer.getWorld(ModDimensions.PERSONAL), new Identifier("dimdoors", "private"), virtualLocation, null, null);
    }

    public static Pocket generatePublicPocketV2(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
        return generateFromPocketGroupV2(DimensionalDoorsInitializer.getWorld(ModDimensions.PUBLIC), new Identifier("dimdoors", "public"), virtualLocation, linkTo, linkProperties);
    }

    public static Pocket generateFromPocketGroupV2(ServerWorld world, Identifier group, VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
    	PocketGenerationContext context = new PocketGenerationContext(world, virtualLocation, linkTo, linkProperties);
    	return generatePocketV2(PocketLoader.getInstance().getGroup(group).getNextPocketGeneratorReference(context), context);
	}

	public static Pocket generatePocketV2(PocketGeneratorReference pocketGeneratorReference, PocketGenerationContext context) {
    	return pocketGeneratorReference.prepareAndPlacePocket(context);
	}

	public static Pocket generateDungeonPocketV2(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
		return generateFromPocketGroupV2(DimensionalDoorsInitializer.getWorld(ModDimensions.DUNGEON), new Identifier("dimdoors", "dungeon"), virtualLocation, linkTo, linkProperties);
	}

	public static Pocket generateDungeonPocketV2(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties, Identifier group) {
		return generateFromPocketGroupV2(DimensionalDoorsInitializer.getWorld(ModDimensions.DUNGEON), group, virtualLocation, linkTo, linkProperties);
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
        Random random = new Random();
        String group = random.nextFloat() < netherProbability ? "nether" : "ruins";
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getRandomTemplate(group, depth, DimensionalDoorsInitializer.getConfig().getPocketsConfig().maxPocketSize, false);

        return generatePocketFromTemplate(DimensionalDoorsInitializer.getWorld(ModDimensions.DUNGEON), pocketTemplate, virtualLocation, linkTo, linkProperties);
    }
    */
}
