package org.dimdev.dimdoors.shared.rifts;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.ddutils.nbt.SavedToNBT;
import org.dimdev.dimdoors.shared.world.DimDoorDimensions;
import org.dimdev.ddutils.nbt.INBTStorable; // Don't change imports order! (Gradle bug): https://stackoverflow.com/questions/26557133/
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.WorldUtils;
import lombok.*;
import lombok.experimental.Wither;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.VirtualLocation;
import org.dimdev.dimdoors.shared.rifts.RiftRegistry.RiftInfo.AvailableLinkInfo;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

import java.util.*;

@SavedToNBT public class RiftRegistry extends WorldSavedData {

    private static final String DATA_NAME = DimDoors.MODID + "_rifts";
    @Getter private static final int DATA_VERSION = 0; // IMPORTANT: Update this and upgradeRegistry when making changes.

    @SavedToNBT @Getter /*package-private*/ /*final*/ Map<Location, RiftInfo> rifts = new HashMap<>(); // TODO: store relative locations too (better location class supporting relative, etc)
    @SavedToNBT @Getter /*package-private*/ /*final*/ Map<String, Location> privatePocketEntrances = new HashMap<>(); // Player UUID -> last rift used to exit pocket TODO: split into PrivatePocketRiftRegistry subclass
    @SavedToNBT @Getter /*package-private*/ /*final*/ Map<String, List<Location>> privatePocketEntranceLists = new HashMap<>(); // Player UUID -> private pocket entrances TODO: split into PrivatePocketRiftRegistry subclass
    @SavedToNBT @Getter /*package-private*/ /*final*/ Map<String, Location> privatePocketExits = new HashMap<>(); // Player UUID -> last rift used to enter pocket
    @SavedToNBT @Getter /*package-private*/ /*final*/ Map<String, Location> overworldRifts = new HashMap<>();

    @Getter private int dim;
    private World world;

    @AllArgsConstructor @EqualsAndHashCode @Builder(toBuilder = true)
    @SavedToNBT public static class RiftInfo implements INBTStorable {
        // IntelliJ warnings are wrong, Builder needs these initializers!
        @SavedToNBT @SuppressWarnings({"UnusedAssignment", "RedundantSuppression"}) @Builder.Default @Getter /*package-private*/ Set<AvailableLinkInfo> availableLinks = new HashSet<>(); // TODO: multiset?
        @SavedToNBT @SuppressWarnings({"UnusedAssignment", "RedundantSuppression"}) @Builder.Default @Getter /*package-private*/ Multiset<Location> sources = ConcurrentHashMultiset.create();
        @SavedToNBT @SuppressWarnings({"UnusedAssignment", "RedundantSuppression"}) @Builder.Default @Getter /*package-private*/ Multiset<Location> destinations = ConcurrentHashMultiset.create();

        @AllArgsConstructor @NoArgsConstructor @EqualsAndHashCode @Builder(toBuilder = true)
        public static class AvailableLinkInfo implements INBTStorable {
            @SavedToNBT @Getter @Setter /*package-private*/ float weight;
            @SavedToNBT @Getter /*package-private*/ VirtualLocation virtualLocation;
            @SavedToNBT @Getter @Wither /*package-private*/ Location location;
            @SavedToNBT @Getter /*package-private*/ UUID uuid;

            @Override public void readFromNBT(NBTTagCompound nbt) { NBTUtils.readFromNBT(this, nbt); }
            @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { return NBTUtils.writeToNBT(this, nbt); }
        }

        public RiftInfo() {
            availableLinks = new HashSet<>();
            sources = ConcurrentHashMultiset.create();
            destinations = ConcurrentHashMultiset.create();
        }

        @Override public void readFromNBT(NBTTagCompound nbt) { NBTUtils.readFromNBT(this, nbt); }
        @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { return NBTUtils.writeToNBT(this, nbt); }
    }

    public RiftRegistry() {
        super(DATA_NAME);
    }

    public RiftRegistry(String s) {
        super(s);
    }

    public static RiftRegistry getForDim(int dimID) {
        MapStorage storage = WorldUtils.getWorld(dimID).getPerWorldStorage();
        RiftRegistry instance = (RiftRegistry) storage.getOrLoadData(RiftRegistry.class, DATA_NAME);

        if (instance == null) {
            instance = new RiftRegistry();
            instance.initNewRegistry();
            storage.setData(DATA_NAME, instance);
        }

        instance.world = WorldUtils.getWorld(dimID);
        instance.dim = dimID;
        return instance;
    }

    public void initNewRegistry() {
        // TODO
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        Integer version = nbt.getInteger("version");
        if (version == null || version != DATA_VERSION) {
            if (upgradeRegistry(nbt, version == null ? -1 : version)) {
                markDirty();
            } else {
                DimDoors.log.warn("Failed to upgrade the pocket registry, you'll have to recreate your world!");
                throw new RuntimeException("Couldn't upgrade registry"); // TODO: better exceptions
            }
        }

        NBTUtils.readFromNBT(this, nbt);
    }

    private static boolean upgradeRegistry(@SuppressWarnings("unused") NBTTagCompound nbt, int oldVersion) {
        if (oldVersion > DATA_VERSION) throw new RuntimeException("Upgrade the mod!"); // TODO: better exceptions
        switch (oldVersion) {
            case -1: // No version tag
                return false;
            case 0:
                // Upgrade to 1 or return false
            case 1:
                // Upgrade to 2 or return false
            case 2:
                // Upgrade to 3 or return false
            // ...
        }
        return true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("version", DATA_VERSION);
        return NBTUtils.writeToNBT(this, nbt);
    }

    public static RiftInfo getRiftInfo(Location rift) {
        return getRegistry(rift).rifts.get(rift);
    }

    public static void addRift(Location rift) {
        RiftRegistry registry = getRegistry(rift);
        registry.rifts.put(rift, new RiftInfo());
        registry.markDirty();
    }

    public static void removeRift(Location rift) {
        RiftRegistry registry = getRegistry(rift);
        RiftInfo oldRift = registry.rifts.remove(rift);
        if (oldRift == null) return;
        for (Location source : oldRift.sources) {
            RiftRegistry sourceRegistry = getRegistry(source);
            sourceRegistry.rifts.get(source).destinations.remove(rift);
            sourceRegistry.markDirty();
            TileEntityRift riftEntity = (TileEntityRift) sourceRegistry.world.getTileEntity(source.getPos());
            riftEntity.destinationGone(rift);
        }
        for (Location destination : oldRift.destinations) {
            RiftRegistry destinationRegistry = getRegistry(destination);
            destinationRegistry.rifts.get(destination).sources.remove(rift);
            destinationRegistry.markDirty();
            //TileEntityRift riftEntity = (TileEntityRift) destinationRegistry.world.getTileEntity(destination.getPos());
            //riftEntity.allSourcesGone(); // TODO
        }
        getForDim(DimDoorDimensions.getPrivateDimID()).privatePocketEntrances.entrySet().removeIf(e -> e.getValue().equals(rift));
        getForDim(0).overworldRifts.entrySet().removeIf(e -> e.getValue().equals(rift));
        registry.markDirty();
    }

    public static void addLink(Location from, Location to) {
        RiftRegistry registryFrom = getRegistry(from);
        RiftRegistry registryTo = getRegistry(to);
        registryFrom.rifts.get(from).destinations.add(to);
        registryTo.rifts.get(to).sources.add(from);
        registryFrom.markDirty();
        registryTo.markDirty();
    }

    public static void removeLink(Location from, Location to) {
        RiftRegistry registryFrom = getRegistry(from);
        RiftRegistry registryTo = getRegistry(to);
        registryFrom.rifts.get(from).destinations.remove(to);
        registryTo.rifts.get(to).sources.remove(from);
        registryFrom.markDirty();
        registryTo.markDirty();
    }

    public static void addAvailableLink(Location rift, AvailableLinkInfo link) { // TODO cache rifts with availableLinks
        RiftRegistry registry = getRegistry(rift);
        registry.rifts.get(rift).availableLinks.add(link);
        registry.markDirty();
    }

    public static void removeAvailableLink(Location rift, AvailableLinkInfo link) {
        RiftRegistry registry = getRegistry(rift);
        registry.rifts.get(rift).availableLinks.remove(link);
        registry.markDirty();
    }

    public static void clearAvailableLinks(Location rift) {
        RiftRegistry registry = getRegistry(rift);
        registry.rifts.get(rift).availableLinks.clear();
        registry.markDirty();
    }

    public static void removeAvailableLinkByUUID(Location rift, UUID uuid) {
        RiftRegistry registry = getRegistry(rift);
        for (AvailableLinkInfo link : registry.rifts.get(rift).availableLinks) {
            if (link.uuid.equals(uuid)) {
                removeAvailableLink(rift, link);
                return;
            }
        }
    }

    public static RiftRegistry getRegistry(Location rift) {
        return getForDim(rift.getDim());
    }

    public Location getPrivatePocketEntrance(String playerUUID) {
        Location entrance = privatePocketEntrances.get(playerUUID);
        List<Location> entrances = privatePocketEntranceLists.computeIfAbsent(playerUUID, k -> new ArrayList<>());
        while ((entrance == null || !(entrance.getTileEntity() instanceof TileEntityRift)) && entrances.size() > 0) {
            if (entrance != null) entrances.remove(entrance);
            if (entrances.size() > 0) entrance = entrances.get(0);
        }
        privatePocketEntrances.put(playerUUID, entrance);
        return entrance;
    }

    public void addPrivatePocketEntrance(String playerUUID, Location rift) {
        privatePocketEntranceLists.computeIfAbsent(playerUUID, k -> new ArrayList<>()).add(rift);
    }

    public void setPrivatePocketEntrance(String playerUUID, Location rift) {
        privatePocketEntrances.put(playerUUID, rift);
        markDirty();
    }

    public Location getPrivatePocketExit(String playerUUID) {
        return privatePocketExits.get(playerUUID);
    }

    public void setPrivatePocketExit(String playerUUID, Location rift) {
        if (rift != null) {
            privatePocketExits.put(playerUUID, rift);
        } else {
            privatePocketExits.remove(playerUUID);
        }
        markDirty();
    }

    public static Location getOverworldRift(String playerUUID) { // TODO: since this is per-world, move to different registry?
        return getForDim(0).overworldRifts.get(playerUUID); // store in overworld, since that's where per-world player data is stored
    }

    public static void setOverworldRift(String playerUUID, Location rift) {
        if (rift != null) {
            getForDim(0).overworldRifts.put(playerUUID, rift);
        } else {
            getForDim(0).overworldRifts.remove(playerUUID);
        }
        getForDim(0).markDirty();
    }

    public static List<AvailableLinkInfo> getAvailableLinks() { // TODO: cache this
        List<AvailableLinkInfo> availableLinks = new ArrayList<>();
        for (World world : DimensionManager.getWorlds()) {
            RiftRegistry registry = getForDim(WorldUtils.getDim(world));
            for (Map.Entry<Location, RiftInfo> rift : registry.rifts.entrySet()) {
                for (AvailableLinkInfo availableLink : rift.getValue().availableLinks) {
                    availableLinks.add(availableLink.withLocation(rift.getKey()));
                }
            }
        }
        return availableLinks;
    }

    // TODO: rebuildRifts() function that scans the world and rebuilds the rift regestry
}
