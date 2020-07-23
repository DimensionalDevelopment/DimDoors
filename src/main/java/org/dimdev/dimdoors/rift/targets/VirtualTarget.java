package org.dimdev.dimdoors.rift.targets;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.dimdev.util.Location;

import net.minecraft.nbt.CompoundTag;

/**
 * A target that is not an actual object in the game such as a block or a tile
 * entity. Only virtual targets can be saved to NBT.
 */
public abstract class VirtualTarget implements Target {

    public static final BiMap<String, Class<? extends VirtualTarget>> registry = HashBiMap.create();
    protected Location location;

    public static VirtualTarget readVirtualTargetNBT(CompoundTag nbt) {
        String type = nbt.getString("type");
        Class<? extends VirtualTarget> destinationClass = registry.get(type);
        if (destinationClass == null) throw new RuntimeException("Unknown type '" + type + "'.");
        try {
            VirtualTarget destination = destinationClass.newInstance();
            destination.fromTag(nbt);
            return destination;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("The class registered for virtual target " + type + " must have a public no-args constructor and must not be abstract", e);
        }
    }

    public void fromTag(CompoundTag nbt) {
    }

    public CompoundTag toTag(CompoundTag nbt) {
        String type = registry.inverse().get(getClass());
        if (type == null) throw new RuntimeException("No type has been registered for class" + getClass().getName());
        nbt.putString("type", type);
        return nbt;
    }

    public void register() {
    }

    public void unregister() {
    }

    public boolean shouldInvalidate(Location riftDeleted) {
        return false;
    }

    public float[] getColor() {
        return new float[]{1, 0, 0, 1};
    }

    public boolean equals(Object o) {
        return o instanceof VirtualTarget &&
                ((VirtualTarget) o).canEqual(this) &&
                (location == null ? ((VirtualTarget) o).location == null : ((Object) location).equals(((VirtualTarget) o).location));
    }

    protected boolean canEqual(Object other) {
        return other instanceof VirtualTarget;
    }

    public int hashCode() {
        return 59 + (location == null ? 43 : location.hashCode());
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
