package org.dimdev.dimdoors.shared.rifts;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;
import lombok.*;
import lombok.experimental.Wither;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.WorldUtils;
import org.dimdev.ddutils.nbt.INBTStorable;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.VirtualLocation;
import org.dimdev.dimdoors.shared.rifts.RiftRegistry.RiftInfo.AvailableLinkInfo;
import org.dimdev.dimdoors.shared.world.ModDimensions;

import java.util.*;

@NBTSerializable public class RiftRegistry extends WorldSavedData {

    private static final String DATA_NAME = DimDoors.MODID + "_rifts";
    @Getter private static final int DATA_VERSION = 0; // IMPORTANT: Update this and upgradeRegistry when making changes.

    @Saved @Getter protected /*final*/ Map<Location, RiftInfo> rifts = new HashMap<>(); // TODO: convert to a static directed graph, but store links per-world
    @Saved @Getter protected /*final*/ Map<String, Location> privatePocketEntrances = new HashMap<>(); // Player UUID -> last rift used to exit pocket TODO: split into PrivatePocketRiftRegistry subclass
    @Saved @Getter protected /*final*/ Map<String, List<Location>> privatePocketEntranceLists = new HashMap<>(); // Player UUID -> private pocket entrances TODO: split into PrivatePocketRiftRegistry subclass
    @Saved @Getter protected /*final*/ Map<String, Location> privatePocketExits = new HashMap<>(); // Player UUID -> last rift used to enter pocket
    @Saved @Getter protected /*final*/ Map<String, Location> overworldRifts = new HashMap<>();

    @Getter private int dim;
    private World world;

    @AllArgsConstructor @EqualsAndHashCode @Builder(toBuilder = true)
    @NBTSerializable public static class RiftInfo implements INBTStorable {
        // IntelliJ warnings are wrong, Builder needs these initializers!
        @Saved @SuppressWarnings({"UnusedAssignment", "RedundantSuppression"}) @Builder.Default @Getter /*package-private*/ Set<AvailableLinkInfo> availableLinks = new HashSet<>(); // TODO: multiset?
        @Saved @SuppressWarnings({"UnusedAssignment", "RedundantSuppression"}) @Builder.Default @Getter /*package-private*/ Multiset<Location> sources = ConcurrentHashMultiset.create();
        @Saved @SuppressWarnings({"UnusedAssignment", "RedundantSuppression"}) @Builder.Default @Getter /*package-private*/ Multiset<Location> destinations = ConcurrentHashMultiset.create();

        @NBTSerializable @AllArgsConstructor @NoArgsConstructor @EqualsAndHashCode @Builder(toBuilder = true)
        public static class AvailableLinkInfo implements INBTStorable {
            @Saved @Getter @Setter /*package-private*/ float weight;
            @Saved @Getter /*package-private*/ VirtualLocation virtualLocation;
            @Saved @Getter @Wither /*package-private*/ Location location;
            @Saved @Getter /*package-private*/ UUID uuid;

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

    public static RiftRegistry getForDim(int dim) {
        MapStorage storage = WorldUtils.getWorld(dim).getPerWorldStorage();
        RiftRegistry instance = (RiftRegistry) storage.getOrLoadData(RiftRegistry.class, DATA_NAME);

        if (instance == null) {
            instance = new RiftRegistry();
            instance.initNewRegistry();
            storage.setData(DATA_NAME, instance);
        }

        instance.world = WorldUtils.getWorld(dim);
        instance.dim = dim;
        return instance;
    }

    public void initNewRegistry() {
        // Nothing to do
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
        DimDoors.log.info("Rift added at " + rift);
        RiftRegistry registry = getRegistry(rift);
        registry.rifts.computeIfAbsent(rift, k -> new RiftInfo());
        registry.markDirty();
    }

    public static void removeRift(Location rift) {
        DimDoors.log.info("Rift removed at " + rift);
        RiftRegistry registry = getRegistry(rift);
        RiftInfo oldRift = registry.rifts.remove(rift);
        if (oldRift == null) return;
        List<TileEntityRift> updateQueue = new ArrayList<>();
        for (Location source : oldRift.sources) {
            RiftRegistry sourceRegistry = getRegistry(source);
            sourceRegistry.rifts.get(source).destinations.remove(rift);
            sourceRegistry.markDirty();
            TileEntityRift riftEntity = (TileEntityRift) sourceRegistry.world.getTileEntity(source.getPos());
            riftEntity.destinationGone(rift);
            updateQueue.add(riftEntity);
        }
        for (Location destination : oldRift.destinations) {
            RiftRegistry destinationRegistry = getRegistry(destination);
            destinationRegistry.rifts.get(destination).sources.remove(rift);
            destinationRegistry.markDirty();
            TileEntityRift riftEntity = (TileEntityRift) destinationRegistry.world.getTileEntity(destination.getPos());
            updateQueue.add(riftEntity);
            //riftEntity.allSourcesGone(); // TODO
        }
        for (TileEntityRift riftEntity : updateQueue) {
            //riftEntity.updateColor();
            riftEntity.markDirty();
        }
        getForDim(ModDimensions.getPrivateDim()).privatePocketEntrances.entrySet().removeIf(e -> e.getValue().equals(rift));
        getForDim(0).overworldRifts.entrySet().removeIf(e -> e.getValue().equals(rift));
        registry.markDirty();
    }

    public static void addLink(Location from, Location to) {
        DimDoors.log.info("Link added " + from + " -> " + to);
        RiftRegistry registryFrom = getRegistry(from);
        RiftRegistry registryTo = getRegistry(to);
        RiftInfo riftInfoFrom = registryFrom.rifts.computeIfAbsent(from, k -> new RiftInfo());
        RiftInfo riftInfoTo = registryTo.rifts.computeIfAbsent(to, k -> new RiftInfo());
        riftInfoFrom.destinations.add(to);
        registryFrom.markDirty();
        riftInfoTo.sources.add(from);
        registryTo.markDirty();
        if (to.getTileEntity() instanceof TileEntityRift) ((TileEntityRift) to.getTileEntity()).updateColor();
        if (from.getTileEntity() instanceof TileEntityRift) ((TileEntityRift) from.getTileEntity()).updateColor();
    }

    public static void removeLink(Location from, Location to) {
        DimDoors.log.info("Link removed " + from + " -> " + to);
        RiftRegistry registryFrom = getRegistry(from);
        RiftRegistry registryTo = getRegistry(to);
        registryFrom.rifts.get(from).destinations.remove(to);
        registryTo.rifts.get(to).sources.remove(from);
        registryFrom.markDirty();
        registryTo.markDirty();
        if (to.getTileEntity() instanceof TileEntityRift) ((TileEntityRift) to.getTileEntity()).updateColor();
        if (from.getTileEntity() instanceof TileEntityRift) ((TileEntityRift) from.getTileEntity()).updateColor();
    }

    public static void addAvailableLink(Location rift, AvailableLinkInfo link) { // TODO cache rifts with availableLinks
        DimDoors.log.info("AvailableLink added at " + rift);
        RiftRegistry registry = getRegistry(rift);
        registry.rifts.get(rift).availableLinks.add(link);
        registry.markDirty();
    }

    public static void removeAvailableLink(Location rift, AvailableLinkInfo link) {
        DimDoors.log.info("AvailableLink removed at " + rift);
        RiftRegistry registry = getRegistry(rift);
        registry.rifts.get(rift).availableLinks.remove(link);
        registry.markDirty();
    }

    public static void clearAvailableLinks(Location rift) {
        DimDoors.log.info("AvailableLink cleared at " + rift);
        RiftRegistry registry = getRegistry(rift);
        registry.rifts.get(rift).availableLinks.clear();
        registry.markDirty();
    }

    public static void removeAvailableLinkByUUID(Location rift, UUID uuid) {
        DimDoors.log.info("AvailableLink with uuid " + uuid + " removed at " + rift);
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
        DimDoors.log.info("Private pocket entrance added for " + playerUUID + " at " + rift);
        privatePocketEntranceLists.computeIfAbsent(playerUUID, k -> new ArrayList<>()).add(rift);
    }

    public void setPrivatePocketEntrance(String playerUUID, Location rift) {
        DimDoors.log.info("Last private pocket entrance set for " + playerUUID + " at " + rift);
        privatePocketEntrances.put(playerUUID, rift);
        markDirty();
    }

    public Location getPrivatePocketExit(String playerUUID) {
        return privatePocketExits.get(playerUUID);
    }

    public void setPrivatePocketExit(String playerUUID, Location rift) {
        DimDoors.log.info("Last private pocket exit set for " + playerUUID + " at " + rift);
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
        DimDoors.log.info("Overworld rift set for " + playerUUID + " at " + rift);
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
