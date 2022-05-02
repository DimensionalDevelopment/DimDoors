package org.dimdev.dimdoors.shared.rifts.targets;

import net.minecraft.nbt.*;

public final class RandomTargetNBTWriter {

    public static void writeToNBT(org.dimdev.dimdoors.shared.rifts.targets.RandomTarget obj, NBTTagCompound nbt) {
        // Write field float newRiftWeight
        nbt.setFloat("newRiftWeight", obj.newRiftWeight);

        // Write field double weightMaximum
        nbt.setDouble("weightMaximum", obj.weightMaximum);

        // Write field double coordFactor
        nbt.setDouble("coordFactor", obj.coordFactor);

        // Write field double positiveDepthFactor
        nbt.setDouble("positiveDepthFactor", obj.positiveDepthFactor);

        // Write field double negativeDepthFactor
        nbt.setDouble("negativeDepthFactor", obj.negativeDepthFactor);

        // Write field java.util.Set<java.lang.Integer> acceptedGroups
        if (obj.acceptedGroups != null) {
            NBTTagList tag = new NBTTagList();
            for (java.lang.Integer element : obj.acceptedGroups) {
                NBTTagInt elementNBT = new NBTTagInt(element);
                tag.appendTag(elementNBT);
            }
            nbt.setTag("acceptedGroups", tag);
        }

        // Write field boolean noLink
        nbt.setBoolean("noLink", obj.noLink);

        // Write field boolean noLinkBack
        nbt.setBoolean("noLinkBack", obj.noLinkBack);
    }

    @SuppressWarnings({"OverlyStrongTypeCast", "RedundantSuppression"})
    public static void readFromNBT(org.dimdev.dimdoors.shared.rifts.targets.RandomTarget obj, NBTTagCompound nbt) {
        // Read field float newRiftWeight
        obj.newRiftWeight = nbt.getFloat("newRiftWeight");

        // Read field double weightMaximum
        obj.weightMaximum = nbt.getDouble("weightMaximum");

        // Read field double coordFactor
        obj.coordFactor = nbt.getDouble("coordFactor");

        // Read field double positiveDepthFactor
        obj.positiveDepthFactor = nbt.getDouble("positiveDepthFactor");

        // Read field double negativeDepthFactor
        obj.negativeDepthFactor = nbt.getDouble("negativeDepthFactor");

        // Read field java.util.Set<java.lang.Integer> acceptedGroups
        if (nbt.hasKey("acceptedGroups")) {
            NBTBase tag = nbt.getTag("acceptedGroups");
            java.util.Set<java.lang.Integer> arr = new java.util.HashSet<>();
            for (NBTBase elementNBT : (NBTTagList) tag) {
                java.lang.Integer element = ((NBTTagInt) elementNBT).getInt();
                arr.add(element);
            }
            obj.acceptedGroups = arr;
        }

        // Read field boolean noLink
        obj.noLink = nbt.getBoolean("noLink");

        // Read field boolean noLinkBack
        obj.noLinkBack = nbt.getBoolean("noLinkBack");
    }
}
