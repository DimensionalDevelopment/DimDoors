package org.dimdev.dimdoors.shared.tileentities;

import net.minecraft.nbt.*;

public final class TileEntityEntranceRiftNBTWriter {

    public static void writeToNBT(org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift obj, NBTTagCompound nbt) {
        // Write field boolean leaveRiftOnBreak
        nbt.setBoolean("leaveRiftOnBreak", obj.leaveRiftOnBreak);

        // Write field boolean closeAfterPassThrough
        nbt.setBoolean("closeAfterPassThrough", obj.closeAfterPassThrough);

        // Write field net.minecraft.util.EnumFacing orientation
        if (obj.orientation != null) {
            NBTTagInt tag = new NBTTagInt(obj.orientation.ordinal());
            nbt.setTag("orientation", tag);
        }
    }

    @SuppressWarnings({"OverlyStrongTypeCast", "RedundantSuppression"})
    public static void readFromNBT(org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift obj, NBTTagCompound nbt) {
        // Read field boolean leaveRiftOnBreak
        obj.leaveRiftOnBreak = nbt.getBoolean("leaveRiftOnBreak");

        // Read field boolean closeAfterPassThrough
        obj.closeAfterPassThrough = nbt.getBoolean("closeAfterPassThrough");

        // Read field net.minecraft.util.EnumFacing orientation
        if (nbt.hasKey("orientation")) {
            NBTBase tag = nbt.getTag("orientation");
            net.minecraft.util.EnumFacing arr = net.minecraft.util.EnumFacing.values()[((NBTTagInt) tag).getInt()];
            obj.orientation = arr;
        }
    }
}
