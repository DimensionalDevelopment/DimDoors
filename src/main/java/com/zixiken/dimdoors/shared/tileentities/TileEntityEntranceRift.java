package com.zixiken.dimdoors.shared.tileentities;

import java.util.Random;

public abstract class TileEntityEntranceRift extends TileEntityRift {
    // TODO: merge horizontal and vertical entrances' render code into one, and support custom sizes

    public float[] getEntranceRenderColor(Random rand) { // Use RGBA instead? Would that be slow because of object creation?
        if (world.provider.getDimension() != -1) {
            float red = rand.nextFloat() * 0.5F + 0.4F;
            float green = rand.nextFloat() * 0.05F;
            float blue = rand.nextFloat() * 0.05F;
            return new float[] {red, green, blue, 1};
        } else {
            float red = rand.nextFloat() * 0.5F + 0.1F;
            float green = rand.nextFloat() * 0.4F + 0.4F;
            float blue = rand.nextFloat() * 0.6F + 0.5F;
            return new float[] {red, green, blue, 1};
        }
    }
}
