package org.dimdev.dimdoors.shared.pockets;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.*;
import org.dimdev.dimdoors.shared.rifts.RiftDestination;
import org.dimdev.dimdoors.shared.rifts.registry.LinkProperties;
import org.dimdev.dimdoors.shared.world.ModDimensions;
import org.dimdev.pocketlib.Pocket;
import org.dimdev.pocketlib.PocketRegistry;
import org.dimdev.pocketlib.VirtualLocation;

import java.util.Random;

public final class PocketGenerator {

    public static Pocket generatePocketFromTemplate(int dim, PocketTemplate pocketTemplate, VirtualLocation virtualLocation, boolean setup) {
        DimDoors.log.info("Generating pocket from template " + pocketTemplate.getId() + " at virtual location " + virtualLocation);

        PocketRegistry registry = PocketRegistry.instance(dim);
        Pocket pocket = registry.newPocket();
        pocketTemplate.place(pocket);
        pocket.setVirtualLocation(virtualLocation);
        if (setup) pocketTemplate.setup(pocket, null, null);
        return pocket;
    }

    public static Pocket generatePrivatePocket(VirtualLocation virtualLocation) {
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getPersonalPocketTemplate();
        return generatePocketFromTemplate(ModDimensions.getPrivateDim(), pocketTemplate, virtualLocation, true);
    }

    // TODO: size of public pocket should increase with depth
    public static Pocket generatePublicPocket(VirtualLocation virtualLocation, RiftDestination linkTo, LinkProperties linkProperties) {
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getPublicPocketTemplate();
        Pocket pocket = generatePocketFromTemplate(ModDimensions.getPublicDim(), pocketTemplate, virtualLocation, false);
        pocketTemplate.setup(pocket, linkTo, linkProperties);
        return pocket;
    }

    /**
     * Create a dungeon pocket at a certain depth.
     *
     * @param virtualLocation The virtual location of the pocket
     * @return The newly-generated dungeon pocket
     */
    public static Pocket generateDungeonPocket(VirtualLocation virtualLocation, RiftDestination linkTo, LinkProperties linkProperties) {
        int depth = virtualLocation.getDepth();
        float netherProbability = virtualLocation.getDim() == -1 ? 1 : (float) depth / 200; // TODO: improve nether probability
        Random random = new Random();
        String group = random.nextFloat() < netherProbability ? "nether" : "ruins";
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getRandomTemplate(group, depth, ModConfig.pocket.maxPocketSize, false);

        Pocket pocket = generatePocketFromTemplate(ModDimensions.getDungeonDim(), pocketTemplate, virtualLocation, false);
        pocketTemplate.setup(pocket, linkTo, linkProperties);
        return pocket;
    }
}
