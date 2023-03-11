package org.dimdev.dimdoors.pockets;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public final class PocketGenerator {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final ResourceLocation ALL_DUNGEONS = DimensionalDoors.id("dungeon");
    public static final ResourceLocation NETHER_DUNGEONS = DimensionalDoors.id("nether");
	public static final ResourceLocation RUINS_DUNGEONS = DimensionalDoors.id("ruins");
	public static final ResourceLocation ATLANTIS_DUNGEONS = DimensionalDoors.id("atlantis");
	public static final ResourceLocation JUNGLE_DUNGEONS = DimensionalDoors.id("jungle");
	public static final ResourceLocation SNOW_DUNGEONS = DimensionalDoors.id("snow");
	public static final ResourceLocation PYRAMID_DUNGEONS = DimensionalDoors.id("pyramid");
	public static final ResourceLocation END_DUNGEONS = DimensionalDoors.id("end");

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
		return generateFromPocketGroupV2(DimensionalDoors.getWorld(ModDimensions.PERSONAL), DimensionalDoors.id("private"), virtualLocation, null, null);
    }

    public static Pocket generatePublicPocketV2(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
        return generateFromPocketGroupV2(DimensionalDoors.getWorld(ModDimensions.PUBLIC), DimensionalDoors.id("public"), virtualLocation, linkTo, linkProperties);
    }

    public static Pocket generateFromPocketGroupV2(ServerLevel world, ResourceLocation group, VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
    	PocketGenerationContext context = new PocketGenerationContext(world, virtualLocation, linkTo, linkProperties);
    	return generatePocketV2(PocketLoader.getInstance().getGroup(group).getNextPocketGeneratorReference(context), context);
	}

	public static Pocket generatePocketV2(PocketGeneratorReference pocketGeneratorReference, PocketGenerationContext context) {
    	return pocketGeneratorReference.prepareAndPlacePocket(context);
	}

	public static Pocket generateDungeonPocketV2(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
		return generateFromPocketGroupV2(DimensionalDoors.getWorld(ModDimensions.DUNGEON), DimensionalDoors.id("dungeon"), virtualLocation, linkTo, linkProperties);
	}

	public static Pocket generateDungeonPocketV2(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties, ResourceLocation group) {
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
