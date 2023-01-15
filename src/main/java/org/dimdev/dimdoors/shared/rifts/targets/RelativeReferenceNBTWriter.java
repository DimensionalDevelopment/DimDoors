package org.dimdev.dimdoors.shared.rifts.targets;

import net.minecraft.nbt.*;
import net.minecraft.util.math.Vec3i;

public final class RelativeReferenceNBTWriter {

    public static void writeToNBT(org.dimdev.dimdoors.shared.rifts.targets.RelativeReference obj, NBTTagCompound nbt) {
        // Write field net.minecraft.util.math.Vec3i offset
        if (obj.offset != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("x", obj.offset.getX());
            tag.setInteger("y", obj.offset.getY());
            tag.setInteger("z", obj.offset.getZ());
            nbt.setTag("offset", tag);
        }
    }

    @SuppressWarnings({"OverlyStrongTypeCast", "RedundantSuppression"})
    public static void readFromNBT(org.dimdev.dimdoors.shared.rifts.targets.RelativeReference obj, NBTTagCompound nbt) {
        // Read field net.minecraft.util.math.Vec3i offset
        if (nbt.hasKey("offset")) {
            NBTBase tag = nbt.getTag("offset");
            obj.offset = new Vec3i(((NBTTagCompound) tag).getInteger("x"), ((NBTTagCompound) tag).getInteger("y"), ((NBTTagCompound) tag).getInteger("z"));
        }
    }
}
