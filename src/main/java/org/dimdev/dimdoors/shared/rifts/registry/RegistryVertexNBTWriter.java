package org.dimdev.dimdoors.shared.rifts.registry;

import net.minecraft.nbt.*;

public final class RegistryVertexNBTWriter {

    public static void writeToNBT(org.dimdev.dimdoors.shared.rifts.registry.RegistryVertex obj, NBTTagCompound nbt) {
        // Write field java.util.UUID id
        nbt.setUniqueId("id", obj.id);
    }

    @SuppressWarnings({"OverlyStrongTypeCast", "RedundantSuppression"})
    public static void readFromNBT(org.dimdev.dimdoors.shared.rifts.registry.RegistryVertex obj, NBTTagCompound nbt) {
        // Read field java.util.UUID id
        obj.id = nbt.getUniqueId("id");
    }
}
