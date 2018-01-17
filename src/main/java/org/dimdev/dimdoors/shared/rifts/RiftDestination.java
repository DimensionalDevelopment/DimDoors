package org.dimdev.dimdoors.shared.rifts;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.RGBA;
import org.dimdev.ddutils.RotatedLocation;
import org.dimdev.ddutils.nbt.INBTStorable;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import lombok.*;  // Don't change import order! (Gradle bug): https://stackoverflow.com/questions/26557133/
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

@EqualsAndHashCode @ToString
public abstract class RiftDestination implements INBTStorable {

    public static final BiMap<String, Class<? extends RiftDestination>> destinationRegistry = HashBiMap.create(); // TODO: move to RiftDestinationRegistry

    public RiftDestination() {}

    public static RiftDestination readDestinationNBT(NBTTagCompound nbt) {
        String type = nbt.getString("type");
        Class<? extends RiftDestination> destinationClass = destinationRegistry.get(type);
        if (destinationClass == null) throw new RuntimeException("Unknown type '" + type + "'.");
        try {
            RiftDestination destination = destinationClass.getConstructor().newInstance();
            destination.readFromNBT(nbt);
            return destination;
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("The class registered for type " + type + " must have a public no-args constructor.", e);
        } catch (InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {}

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        String type = destinationRegistry.inverse().get(getClass());
        if (type == null) throw new RuntimeException("No type has been registered for class" + getClass().getName() + " yet!");
        nbt.setString("type", type);
        return nbt;
    }

    public abstract boolean teleport(RotatedLocation rift, Entity entity);

    public Location getFixedTarget(Location location) { // TODO: this should only be available for local/global/relative destinations, maybe make a superclass for them
        return null;
    }

    public void register(Location location) {
        Location fixedTarget = getFixedTarget(location);
        if (fixedTarget != null) RiftRegistry.instance().addLink(location, getFixedTarget(location));
    }

    public void unregister(Location location) {
        Location fixedTarget = getFixedTarget(location);
        if (fixedTarget != null) RiftRegistry.instance().removeLink(location, fixedTarget);
    }

    public boolean keepAfterTargetGone(Location location, Location target) {
        return !target.equals(getFixedTarget(location));
    }

    public RGBA getColor(Location location) {
        Location target = getFixedTarget(location);
        if (target != null && RiftRegistry.instance().isRiftAt(target)) {
            Set<Location> otherRiftTargets = RiftRegistry.instance().getTargets(target);
            if (otherRiftTargets.size() == 1 && otherRiftTargets.contains(location)) return new RGBA(0, 1, 0, 1);
        }
        return new RGBA(1, 0, 0, 1);
    }
}
