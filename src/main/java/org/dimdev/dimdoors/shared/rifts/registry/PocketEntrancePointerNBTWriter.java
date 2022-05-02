package org.dimdev.dimdoors.shared.rifts.registry;

import net.minecraft.nbt.*;

public final class PocketEntrancePointerNBTWriter {

    public static void writeToNBT(org.dimdev.dimdoors.shared.rifts.registry.PocketEntrancePointer obj, NBTTagCompound nbt) {
        // Write field int pocketDim
        nbt.setInteger("pocketDim", obj.pocketDim);

        // Write field int pocketId
        nbt.setInteger("pocketId", obj.pocketId);
    }

    @SuppressWarnings({"OverlyStrongTypeCast", "RedundantSuppression"})
    public static void readFromNBT(org.dimdev.dimdoors.shared.rifts.registry.PocketEntrancePointer obj, NBTTagCompound nbt) {
        // Read field int pocketDim
        obj.pocketDim = nbt.getInteger("pocketDim");

        // Read field int pocketId
        obj.pocketId = nbt.getInteger("pocketId");
    }
}
