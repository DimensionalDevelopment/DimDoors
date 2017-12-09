package com.zixiken.dimdoors.shared.pockets;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.*;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;

import java.util.Random;

public class PocketGenerator {

    public static Pocket generatePocketFromTemplate(int dimID, int depth, PocketTemplate pocketTemplate, int originalDim) {
        DimDoors.log("depth = " + depth + " originalDim = " + originalDim);

        PocketRegistry registry = PocketRegistry.getForDim(dimID);
        Pocket pocket = registry.newPocket(depth);
        pocketTemplate.place(pocket, 0); // TODO: config option for yBase or maybe param?
        pocket.setOriginalDim(originalDim);
        return pocket;
    }

    public Pocket generatePrivatePocket(int originalDim) {
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getPersonalPocketTemplate();
        return generatePocketFromTemplate(DimDoorDimensions.getPrivateDimID(), 0, pocketTemplate, originalDim);
    }

    public Pocket generatePublicPocket(int originalDim) {
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getPersonalPocketTemplate();
        return generatePocketFromTemplate(DimDoorDimensions.getPrivateDimID(), 0, pocketTemplate, originalDim);
    }

    /**
     * Create a dungeon pocket at a certain depth.
     *
     * @param depth The depth of the dungeon
     * @param originalDim The non-pocket dimension from which this dungeon was created
     * @return The newly-generated dungeon pocket
     */
    public Pocket generateDungeonPocket(int depth, int originalDim) { // TODO: Add rift for linking!
        float netherProbability = originalDim == -1 ? 1 : (float) depth / 50; // TODO: improve nether probability
        Random random = new Random();
        String group = random.nextFloat() < netherProbability ? "nether" : "ruins";
        PocketTemplate pocketTemplate = SchematicHandler.INSTANCE.getRandomTemplate(group, depth, DDConfig.getMaxPocketSize(), false);

        return generatePocketFromTemplate(DimDoorDimensions.getDungeonDimID(), depth, pocketTemplate, originalDim);
    }
}
