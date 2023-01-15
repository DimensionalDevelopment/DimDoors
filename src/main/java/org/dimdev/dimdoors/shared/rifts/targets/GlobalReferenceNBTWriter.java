package org.dimdev.dimdoors.shared.rifts.targets;

import net.minecraft.nbt.*;
import org.dimdev.ddutils.Location;

public final class GlobalReferenceNBTWriter {

    public static void writeToNBT(org.dimdev.dimdoors.shared.rifts.targets.GlobalReference obj, NBTTagCompound nbt) {
        // Write field org.dimdev.ddutils.Location target
        if (obj.target != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("dim", obj.target.getDim());
            tag.setInteger("x", obj.target.getX());
            tag.setInteger("y", obj.target.getY());
            tag.setInteger("z", obj.target.getZ());
            nbt.setTag("target", tag);
        }
    }

    @SuppressWarnings({"OverlyStrongTypeCast", "RedundantSuppression"})
    public static void readFromNBT(org.dimdev.dimdoors.shared.rifts.targets.GlobalReference obj, NBTTagCompound nbt) {
        // Read field org.dimdev.ddutils.Location target
        if (nbt.hasKey("target")) {
            NBTBase tag = nbt.getTag("target");
            obj.target = new Location(((NBTTagCompound) tag).getInteger("dim"), ((NBTTagCompound) tag).getInteger("x"), ((NBTTagCompound) tag).getInteger("y"), ((NBTTagCompound) tag).getInteger("z"));
        }
    }
}
