package org.dimdev.dimdoors.tileentities;

import net.minecraft.tileentity.TileEntity;

import java.util.Random;

public abstract class DDTileEntityBase extends TileEntity {
    /**
     * @return an array of floats representing RGBA color where 1.0 = 255.
     */
    public abstract float[] getRenderColor(Random rand);

}
