package com.zixiken.dimdoors.shared.rifts;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import ddutils.Location;
import ddutils.nbt.INBTStorable;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import lombok.*;  // Don't change import order! (Gradle bug): https://stackoverflow.com/questions/26557133/

import java.lang.reflect.InvocationTargetException;

@Getter @ToString @EqualsAndHashCode
public abstract class RiftDestination implements INBTStorable {

    /*private*/ public static final BiMap<String, Class<? extends RiftDestination>> destinationRegistry = HashBiMap.create(); // TODO: move to RiftDestinationRegistry
    //private String type;
    /*package-private*/ WeightedRiftDestination weightedDestination;

    public RiftDestination() {
      //type = destinationRegistry.inverse().get(getClass());
    }

    public static RiftDestination readDestinationNBT(NBTTagCompound nbt) {
        String type = nbt.getString("type");
        Class<? extends RiftDestination> destinationClass = destinationRegistry.get(type);
        if (destinationClass == null) throw new RuntimeException("Unknown type '" + type + "'.");
        try {
            RiftDestination destination = destinationClass.getConstructor().newInstance();
            destination.readFromNBT(nbt);
            //destination.type = type;
            return destination;
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("The class registered for type " + type + " must have a public no-args constructor.", e);
        } catch (InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) { }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        String type = destinationRegistry.inverse().get(getClass());
        if (type == null) throw new RuntimeException("No type has been registered for class" + getClass().getName() + " yet!");
        nbt.setString("type", type);
        return nbt;
    }

    public Location getReferencedRift(Location rift) { // TODO: change to getReferencedRifts
        return null;
    }

    public void register(TileEntityRift rift) {
        Location loc = getReferencedRift(rift.getLocation());
        if (loc != null) RiftRegistry.addLink(rift.getLocation(), loc);
    }

    public void unregister(TileEntityRift rift) {
        Location loc = getReferencedRift(rift.getLocation());
        if (loc != null) RiftRegistry.removeLink(rift.getLocation(), loc);
    }

    public abstract boolean teleport(TileEntityRift rift, Entity entity);
}
