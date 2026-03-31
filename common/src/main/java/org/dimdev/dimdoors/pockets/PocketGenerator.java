package org.dimdev.dimdoors.pockets;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.ModRegistries;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.PocketRegistry;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import java.util.UUID;

public final class PocketGenerator {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final ResourceKey<VirtualPocket> ALL_DUNGEONS = ResourceKey.create(ModRegistries.POCKET_GROUP, DimensionalDoors.id("dungeon"));
	public static final ResourceKey<VirtualPocket> NETHER_DUNGEONS = ResourceKey.create(ModRegistries.POCKET_GROUP, DimensionalDoors.id("nether"));
	public static final ResourceKey<VirtualPocket> RUINS_DUNGEONS = ResourceKey.create(ModRegistries.POCKET_GROUP, DimensionalDoors.id("ruins"));
	public static final ResourceKey<VirtualPocket> ATLANTIS_DUNGEONS = ResourceKey.create(ModRegistries.POCKET_GROUP, DimensionalDoors.id("atlantis"));
	public static final ResourceKey<VirtualPocket> JUNGLE_DUNGEONS = ResourceKey.create(ModRegistries.POCKET_GROUP, DimensionalDoors.id("jungle"));
	public static final ResourceKey<VirtualPocket> SNOW_DUNGEONS = ResourceKey.create(ModRegistries.POCKET_GROUP, DimensionalDoors.id("snow"));
	public static final ResourceKey<VirtualPocket> PYRAMID_DUNGEONS = ResourceKey.create(ModRegistries.POCKET_GROUP, DimensionalDoors.id("pyramid"));
	public static final ResourceKey<VirtualPocket> END_DUNGEONS = ResourceKey.create(ModRegistries.POCKET_GROUP, DimensionalDoors.id("end"));

    /*
    private static Pocket prepareAndPlacePocket(ServerWorld world, PocketTemplate pocketTemplate, VirtualLocation virtualLocation, boolean setup) {
        LOGGER.info("Generating pocket from template " + pocketTemplate.getId() + " at virtual location " + virtualLocation);

        Pocket pocket = DimensionalRegistry.getPocketDirectory(world.getRegistryKey()).newPocket(Pocket.builder().expand(new Vec3i(1, 1, 1)));
        pocketTemplate.place(pocket, setup);
        pocket.virtualLocation = virtualLocation;
        return pocket;
    }
	*/


	public static UUID generatePrivatePocketV2(VirtualLocation virtualLocation) {
		return generateFromPocketGroupV2(ResourceKey.create(ModRegistries.POCKET_GROUP, DimensionalDoors.id("private")), virtualLocation, null, null);
	}

	public static UUID generatePublicPocketV2(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
		return generateFromPocketGroupV2(ResourceKey.create(ModRegistries.POCKET_GROUP, DimensionalDoors.id("public")), virtualLocation, linkTo, linkProperties);
	}

	public static UUID generateFromPocketGroupV2(ResourceKey<VirtualPocket> group, VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
		PocketGenerationContext context = new PocketGenerationContext(virtualLocation, linkTo, linkProperties, DimensionalDoors.getServer().registryAccess());
		return generatePocketV2(context.lookup(group).getNextPocketGeneratorReference(context), context);
	}

	public static UUID generateFromVirtualPocket(ResourceKey<VirtualPocket> id, VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
		PocketGenerationContext context = new PocketGenerationContext(virtualLocation, linkTo, linkProperties, DimensionalDoors.getServer().registryAccess());
		LOGGER.info("Generating virtual target: " + id);
		return generatePocketV2(context.lookup(id).getNextPocketGeneratorReference(context), context);
	}

	public static UUID generatePocketV2(PocketGeneratorReference pocketGeneratorReference, PocketGenerationContext context) {
		return pocketGeneratorReference.prepareAndPlacePocket(context);
	}

	public static UUID generateDungeonPocketV2(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
		return generateFromPocketGroupV2(ALL_DUNGEONS, virtualLocation, linkTo, linkProperties);
	}

	public static UUID generateDungeonPocketV2(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties, ResourceKey<VirtualPocket> group) {
		return generateFromPocketGroupV2(group, virtualLocation, linkTo, linkProperties);
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