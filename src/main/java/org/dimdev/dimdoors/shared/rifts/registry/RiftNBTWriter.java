package org.dimdev.dimdoors.shared.rifts.registry;

import net.minecraft.nbt.*;
import org.dimdev.ddutils.Location;

import java.util.Objects;

public final class RiftNBTWriter {

    public static void writeToNBT(org.dimdev.dimdoors.shared.rifts.registry.Rift obj, NBTTagCompound nbt) {
        // Write field org.dimdev.ddutils.Location location
        if (obj.location != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("dim", obj.location.getDim());
            tag.setInteger("x", obj.location.getX());
            tag.setInteger("y", obj.location.getY());
            tag.setInteger("z", obj.location.getZ());
            nbt.setTag("location", tag);
        }

        // Write field boolean isFloating
        nbt.setBoolean("isFloating", obj.isFloating);

        // Write field org.dimdev.dimdoors.shared.rifts.registry.LinkProperties properties
        if (Objects.nonNull(obj.properties))
            nbt.setTag("properties", obj.properties.writeToNBT(new NBTTagCompound()));
    }

    @SuppressWarnings({"OverlyStrongTypeCast", "RedundantSuppression"})
    public static void readFromNBT(org.dimdev.dimdoors.shared.rifts.registry.Rift obj, NBTTagCompound nbt) {
        // Read field org.dimdev.ddutils.Location location
        if (nbt.hasKey("location")) {
            NBTBase tag = nbt.getTag("location");
            obj.location = new Location(((NBTTagCompound) tag).getInteger("dim"), ((NBTTagCompound) tag).getInteger("x"), ((NBTTagCompound) tag).getInteger("y"), ((NBTTagCompound) tag).getInteger("z"));
        }

        // Read field boolean isFloating
        obj.isFloating = nbt.getBoolean("isFloating");

        // Read field org.dimdev.dimdoors.shared.rifts.registry.LinkProperties properties
        if (nbt.hasKey("properties")) {
            obj.properties = new org.dimdev.dimdoors.shared.rifts.registry.LinkProperties();
            obj.properties.readFromNBT(nbt.getCompoundTag("properties"));
        }
    }
}
