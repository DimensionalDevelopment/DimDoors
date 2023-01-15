package org.dimdev.dimdoors.shared.rifts.targets;

import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;

public final class LocalReferenceNBTWriter {

    public static void writeToNBT(org.dimdev.dimdoors.shared.rifts.targets.LocalReference obj, NBTTagCompound nbt) {
        // Write field net.minecraft.util.math.BlockPos target
        if (obj.target != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("x", obj.target.getX());
            tag.setInteger("y", obj.target.getY());
            tag.setInteger("z", obj.target.getZ());
            nbt.setTag("target", tag);
        }
    }

    @SuppressWarnings({"OverlyStrongTypeCast", "RedundantSuppression"})
    public static void readFromNBT(org.dimdev.dimdoors.shared.rifts.targets.LocalReference obj, NBTTagCompound nbt) {
        // Read field net.minecraft.util.math.BlockPos target
        if (nbt.hasKey("target")) {
            NBTBase tag = nbt.getTag("target");
            obj.target = new BlockPos(((NBTTagCompound) tag).getInteger("x"), ((NBTTagCompound) tag).getInteger("y"), ((NBTTagCompound) tag).getInteger("z"));
        }
    }
}
