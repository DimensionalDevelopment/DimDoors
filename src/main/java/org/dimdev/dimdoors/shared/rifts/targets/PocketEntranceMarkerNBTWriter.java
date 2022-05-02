package org.dimdev.dimdoors.shared.rifts.targets;

import net.minecraft.nbt.*;

public final class PocketEntranceMarkerNBTWriter {

    public static void writeToNBT(org.dimdev.dimdoors.shared.rifts.targets.PocketEntranceMarker obj, NBTTagCompound nbt) {
        // Write field float weight
        nbt.setFloat("weight", obj.weight);
    }

    @SuppressWarnings({"OverlyStrongTypeCast", "RedundantSuppression"})
    public static void readFromNBT(org.dimdev.dimdoors.shared.rifts.targets.PocketEntranceMarker obj, NBTTagCompound nbt) {
        // Read field float weight
        obj.weight = nbt.getFloat("weight");
    }
}
