package com.zixiken.dimdoors.shared.pockets;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.*;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;

import java.util.Random;

public class PocketGenerator {

    public static Pocket generatePocketFromTemplate(int dimID, int depth, PocketTemplate pocketTemplate, VirtualLocation virtualLocation) {
        DimDoors.log("depth = " + depth + " originalDim = " + virtualLocation);

        PocketRegistry registry = PocketRegistry.getForDim(dimID);
        Pocket pocket = registry.newPocket(depth);
        pocketTemplate.place(pocket, 10); // Sky starts getting dark (because of void) below y = 10 TODO: config option for yBase or maybe param?
        pocket.setVirtualLocation(virtualLocation);
        return pocket;
    }

    public static Pocket generatePrivatePocket(VirtualLocation virtualLocation) {
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getPersonalPocketTemplate();
        return generatePocketFromTemplate(DimDoorDimensions.getPrivateDimID(), 0, pocketTemplate, virtualLocation);
    }

    public static Pocket generatePublicPocket(VirtualLocation virtualLocation) {
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getPublicPocketTemplate();
        return generatePocketFromTemplate(DimDoorDimensions.getPublicDimID(), 0, pocketTemplate, virtualLocation);
    }

    /**
     * Create a dungeon pocket at a certain depth.
     *
     * @param depth The depth of the dungeon
     * @param virtualLocation The virtual location of the pocket
     * @return The newly-generated dungeon pocket
     */
    public Pocket generateDungeonPocket(int depth, VirtualLocation virtualLocation) { // TODO: Add rift for linking!
        float netherProbability = virtualLocation.getDimID() == -1 ? 1 : (float) depth / 50; // TODO: improve nether probability
        Random random = new Random();
        String group = random.nextFloat() < netherProbability ? "nether" : "ruins";
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getRandomTemplate(group, depth, DDConfig.getMaxPocketSize(), false);

        return generatePocketFromTemplate(DimDoorDimensions.getDungeonDimID(), depth, pocketTemplate, virtualLocation);
    }
}
