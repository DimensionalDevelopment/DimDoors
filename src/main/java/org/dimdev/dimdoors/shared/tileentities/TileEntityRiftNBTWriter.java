package org.dimdev.dimdoors.shared.tileentities;

import net.minecraft.nbt.*;
import org.dimdev.ddutils.RGBA;

import java.util.Objects;

public final class TileEntityRiftNBTWriter {

    public static void writeToNBT(org.dimdev.dimdoors.shared.tileentities.TileEntityRift obj, NBTTagCompound nbt) {
        // Write field org.dimdev.dimdoors.shared.rifts.registry.LinkProperties properties
        if (Objects.nonNull(obj.properties))
            nbt.setTag("properties", obj.properties.writeToNBT(new NBTTagCompound()));

        // Write field boolean relativeRotation
        nbt.setBoolean("relativeRotation", obj.relativeRotation);

        // Write field boolean alwaysDelete
        nbt.setBoolean("alwaysDelete", obj.alwaysDelete);

        // Write field boolean forcedColor
        nbt.setBoolean("forcedColor", obj.forcedColor);

        // Write field org.dimdev.ddutils.RGBA color
        if (obj.color != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setFloat("red", obj.color.getRed());
            tag.setFloat("green", obj.color.getGreen());
            tag.setFloat("blue", obj.color.getBlue());
            tag.setFloat("alpha", obj.color.getAlpha());
            nbt.setTag("color", tag);
        }
    }

    @SuppressWarnings({"OverlyStrongTypeCast", "RedundantSuppression"})
    public static void readFromNBT(org.dimdev.dimdoors.shared.tileentities.TileEntityRift obj, NBTTagCompound nbt) {
        // Read field org.dimdev.dimdoors.shared.rifts.registry.LinkProperties properties
        if (nbt.hasKey("properties")) {
            obj.properties = new org.dimdev.dimdoors.shared.rifts.registry.LinkProperties();
            obj.properties.readFromNBT(nbt.getCompoundTag("properties"));
        }

        // Read field boolean relativeRotation
        obj.relativeRotation = nbt.getBoolean("relativeRotation");

        // Read field boolean alwaysDelete
        obj.alwaysDelete = nbt.getBoolean("alwaysDelete");

        // Read field boolean forcedColor
        obj.forcedColor = nbt.getBoolean("forcedColor");

        // Read field org.dimdev.ddutils.RGBA color
        if (nbt.hasKey("color")) {
            NBTBase tag = nbt.getTag("color");
            obj.color = new RGBA(((NBTTagCompound) tag).getFloat("red"), ((NBTTagCompound) tag).getFloat("green"), ((NBTTagCompound) tag).getFloat("blue"), ((NBTTagCompound) tag).getFloat("alpha"));
        }
    }
}
