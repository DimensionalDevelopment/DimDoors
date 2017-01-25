package com.zixiken.dimdoors.shared.tileentities;

import java.util.Random;
import net.minecraft.entity.Entity;

public class TileEntityTransTrapdoor extends DDTileEntityBase {

    @Override
    public float[] getRenderColor(Random rand) {
        float[] rgbaColor = {1, 1, 1, 1};
        if (this.world.provider.getDimension() == -1) {
            rgbaColor[0] = world.rand.nextFloat() * 0.5F + 0.4F;
            rgbaColor[1] = world.rand.nextFloat() * 0.05F;
            rgbaColor[2] = world.rand.nextFloat() * 0.05F;
        } else {
            rgbaColor[0] = world.rand.nextFloat() * 0.5F + 0.1F;
            rgbaColor[1] = world.rand.nextFloat() * 0.4F + 0.4F;
            rgbaColor[2] = world.rand.nextFloat() * 0.6F + 0.5F;
        }
        return rgbaColor;
    }

    @Override
    public boolean tryTeleport(Entity entity) {
        //@todo teleport the player somewhere to the Overworld?
        return false;
    }
}
