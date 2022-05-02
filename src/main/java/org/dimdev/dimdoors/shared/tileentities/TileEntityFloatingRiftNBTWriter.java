package org.dimdev.dimdoors.shared.tileentities;

import net.minecraft.nbt.*;

public final class TileEntityFloatingRiftNBTWriter {

    public static void writeToNBT(org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift obj, NBTTagCompound nbt) {
        // Write field boolean closing
        nbt.setBoolean("closing", obj.closing);

        // Write field boolean stabilized
        nbt.setBoolean("stabilized", obj.stabilized);

        // Write field int spawnedEndermenID
        nbt.setInteger("spawnedEndermenID", obj.spawnedEndermenID);

        // Write field float size
        nbt.setFloat("size", obj.size);

        // Write field float riftYaw
        nbt.setFloat("riftYaw", obj.riftYaw);

        // Write field float teleportTargetPitch
        nbt.setFloat("teleportTargetPitch", obj.teleportTargetPitch);

        // Write field int curveId
        nbt.setInteger("curveId", obj.curveId);
    }

    @SuppressWarnings({"OverlyStrongTypeCast", "RedundantSuppression"})
    public static void readFromNBT(org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift obj, NBTTagCompound nbt) {
        // Read field boolean closing
        obj.closing = nbt.getBoolean("closing");

        // Read field boolean stabilized
        obj.stabilized = nbt.getBoolean("stabilized");

        // Read field int spawnedEndermenID
        obj.spawnedEndermenID = nbt.getInteger("spawnedEndermenID");

        // Read field float size
        obj.size = nbt.getFloat("size");

        // Read field float riftYaw
        obj.riftYaw = nbt.getFloat("riftYaw");

        // Read field float teleportTargetPitch
        obj.teleportTargetPitch = nbt.getFloat("teleportTargetPitch");

        // Read field int curveId
        obj.curveId = nbt.getInteger("curveId");
    }
}
