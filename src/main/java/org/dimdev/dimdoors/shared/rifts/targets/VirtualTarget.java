package org.dimdev.dimdoors.shared.rifts.targets;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.RGBA;
import org.dimdev.ddutils.nbt.INBTStorable;

import java.util.Objects;

/**
 * A target that is not an actual object in the game such as a block or a tile
 * entity. Only virtual targets can be saved to NBT.
 */
@EqualsAndHashCode @ToString
public abstract class VirtualTarget implements ITarget, INBTStorable {

    public static final BiMap<String, Class<? extends VirtualTarget>> registry = HashBiMap.create();
    @Setter protected Location location;

    @SuppressWarnings("deprecation")
    public static VirtualTarget readVirtualTargetNBT(NBTTagCompound nbt) {
        String type = nbt.getString("type");
        Class<? extends VirtualTarget> destinationClass = registry.get(type);
        if (Objects.isNull(destinationClass)) throw new RuntimeException("Unknown type '" + type + "'.");
        try {
            VirtualTarget destination = destinationClass.newInstance();
            destination.readFromNBT(nbt);
            return destination;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("The class registered for virtual target " + type + " must have a public no-args constructor and must not be abstract", e);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {}

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        String type = registry.inverse().get(getClass());
        if (type == null) throw new RuntimeException("No type has been registered for class" + getClass().getName());
        nbt.setString("type", type);
        return nbt;
    }

    public void register() {}

    public void unregister() {}

    public boolean shouldInvalidate(Location riftDeleted) { return false; }

    public RGBA getColor() { return new RGBA(1, 0, 0, 1); }
}
