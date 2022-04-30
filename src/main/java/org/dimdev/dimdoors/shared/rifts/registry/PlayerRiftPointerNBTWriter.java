package org.dimdev.dimdoors.shared.rifts.registry;

import net.minecraft.nbt.*;

public final class PlayerRiftPointerNBTWriter {

    public static void writeToNBT(org.dimdev.dimdoors.shared.rifts.registry.PlayerRiftPointer obj, NBTTagCompound nbt) {
        // Write field java.util.UUID player
        nbt.setUniqueId("player", obj.player);
    }

    @SuppressWarnings({"OverlyStrongTypeCast", "RedundantSuppression"})
    public static void readFromNBT(org.dimdev.dimdoors.shared.rifts.registry.PlayerRiftPointer obj, NBTTagCompound nbt) {
        // Read field java.util.UUID player
        obj.player = nbt.getUniqueId("player");
    }
}
