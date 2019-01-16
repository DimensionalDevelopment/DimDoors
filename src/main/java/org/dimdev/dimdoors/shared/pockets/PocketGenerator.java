package org.dimdev.dimdoors.shared.pockets;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.ModConfig;
import org.dimdev.dimdoors.shared.rifts.targets.VirtualTarget;
import org.dimdev.dimdoors.shared.rifts.registry.LinkProperties;
import org.dimdev.dimdoors.shared.world.ModDimensions;
import org.dimdev.pocketlib.Pocket;
import org.dimdev.pocketlib.PocketRegistry;
import org.dimdev.pocketlib.VirtualLocation;

import java.util.Random;

public final class PocketGenerator {

    private static Pocket prepareAndPlacePocket(int dim, PocketTemplate pocketTemplate, VirtualLocation virtualLocation, boolean setup) {
        DimDoors.log.info("Generating pocket from template " + pocketTemplate.getId() + " at virtual location " + virtualLocation);

        Pocket pocket = PocketRegistry.instance(dim).newPocket();
	pocketTemplate.place(pocket, setup);
	pocket.setVirtualLocation(virtualLocation);
	pocket.setTemplateGroup(pocketTemplate.getGroup());
	pocket.setTemplateId(pocketTemplate.getId());
	return pocket;
    }

    public static Pocket generatePocketFromTemplate(int dim, PocketTemplate pocketTemplate, VirtualLocation virtualLocation, boolean setup) {
        Pocket pocket = prepareAndPlacePocket(dim, pocketTemplate, virtualLocation, setup);
        if (setup) pocketTemplate.setup(pocket, null, null);
        return pocket;
    }

    public static Pocket generatePocketFromTemplate(int dim, PocketTemplate pocketTemplate, VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
        Pocket pocket = prepareAndPlacePocket(dim, pocketTemplate, virtualLocation, true);
        pocketTemplate.setup(pocket, linkTo, linkProperties);
        return pocket;
    }

    public static Pocket generatePrivatePocket(VirtualLocation virtualLocation) {
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getPersonalPocketTemplate();
        return generatePocketFromTemplate(ModDimensions.getPrivateDim(), pocketTemplate, virtualLocation, true);
    }

    // TODO: size of public pockets should increase with depth
    public static Pocket generatePublicPocket(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getPublicPocketTemplate();
        return generatePocketFromTemplate(ModDimensions.getPublicDim(), pocketTemplate, virtualLocation, linkTo, linkProperties);
    }

    /**
     * Create a dungeon pockets at a certain depth.
     *
     * @param virtualLocation The virtual location of the pockets
     * @return The newly-generated dungeon pockets
     */
    public static Pocket generateDungeonPocket(VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
        int depth = virtualLocation.getDepth();
        float netherProbability = virtualLocation.getDim() == -1 ? 1 : (float) depth / 200; // TODO: improve nether probability
        Random random = new Random();
        String group = random.nextFloat() < netherProbability ? "nether" : "ruins";
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getRandomTemplate(group, depth, ModConfig.pockets.maxPocketSize, false);

        return generatePocketFromTemplate(ModDimensions.getDungeonDim(), pocketTemplate, virtualLocation, linkTo, linkProperties);
    }
}
