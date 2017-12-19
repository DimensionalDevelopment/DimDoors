package com.zixiken.dimdoors.shared.pockets;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.*;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;

import java.util.Random;

public class PocketGenerator {

    public static Pocket generatePocketFromTemplate(int dimID, PocketTemplate pocketTemplate, VirtualLocation virtualLocation) {
        DimDoors.log.info("Generating pocket from template " + pocketTemplate.getName() + " at virtual location " + virtualLocation);

        PocketRegistry registry = PocketRegistry.getForDim(dimID);
        Pocket pocket = registry.newPocket();
        pocketTemplate.place(pocket, 0); // TODO: config option for yBase
        pocket.setVirtualLocation(virtualLocation);
        return pocket;
    }

    public static Pocket generatePrivatePocket(VirtualLocation virtualLocation) {
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getPersonalPocketTemplate();
        return generatePocketFromTemplate(DimDoorDimensions.getPrivateDimID(), pocketTemplate, virtualLocation);
    }

    public static Pocket generatePublicPocket(VirtualLocation virtualLocation) {
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getPublicPocketTemplate();
        return generatePocketFromTemplate(DimDoorDimensions.getPublicDimID(), pocketTemplate, virtualLocation);
    }

    /**
     * Create a dungeon pocket at a certain depth.
     *
     * @param virtualLocation The virtual location of the pocket
     * @return The newly-generated dungeon pocket
     */
    public Pocket generateDungeonPocket(VirtualLocation virtualLocation) {
        int depth = virtualLocation.getDepth();
        float netherProbability = virtualLocation.getDimID() == -1 ? 1 : (float) depth / 50; // TODO: improve nether probability
        Random random = new Random();
        String group = random.nextFloat() < netherProbability ? "nether" : "ruins";
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getRandomTemplate(group, depth, DDConfig.getMaxPocketSize(), false);

        return generatePocketFromTemplate(DimDoorDimensions.getDungeonDimID(), pocketTemplate, virtualLocation);
    }
}
