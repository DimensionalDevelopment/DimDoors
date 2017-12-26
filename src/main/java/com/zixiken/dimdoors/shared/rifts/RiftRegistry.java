package com.zixiken.dimdoors.shared.rifts;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import ddutils.nbt.INBTStorable; // Don't change imports order! (Gradle bug): https://stackoverflow.com/questions/26557133/
import ddutils.Location;
import ddutils.WorldUtils;
import lombok.*;
import lombok.experimental.Wither;
import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.VirtualLocation;
import com.zixiken.dimdoors.shared.rifts.RiftRegistry.RiftInfo.AvailableLinkInfo;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

import java.util.*;

public class RiftRegistry extends WorldSavedData {

    private static final String DATA_NAME = DimDoors.MODID + "_rifts";
    @Getter private static final int DATA_VERSION = 0; // IMPORTANT: Update this and upgradeRegistry when making changes.

    @Getter private final Map<Location, RiftInfo> rifts = new HashMap<>(); // TODO: store relative locations too (better location class supporting relative, etc)
    @Getter private final Map<String, Location> privatePocketEntrances = new HashMap<>(); // Player UUID -> last rift used to exit pocket TODO: split into PrivatePocketRiftRegistry subclass
    @Getter private final Map<String, List<Location>> privatePocketEntranceLists = new HashMap<>(); // Player UUID -> private pocket entrances TODO: split into PrivatePocketRiftRegistry subclass
    @Getter private final Map<String, Location> privatePocketExits = new HashMap<>(); // Player UUID -> last rift used to enter pocket
    @Getter private final Map<String, Location> overworldRifts = new HashMap<>();

    @Getter private int dim;
    private World world;

    @AllArgsConstructor @EqualsAndHashCode @Builder(toBuilder = true)
    public static class RiftInfo implements INBTStorable {
        // IntelliJ warnings are wrong, Builder needs these initializers!
        @SuppressWarnings({"UnusedAssignment", "RedundantSuppression"}) @Builder.Default @Getter private Set<AvailableLinkInfo> availableLinks = new HashSet<>(); // TODO: multiset?
        @SuppressWarnings({"UnusedAssignment", "RedundantSuppression"}) @Builder.Default @Getter private Multiset<Location> sources = ConcurrentHashMultiset.create();
        @SuppressWarnings({"UnusedAssignment", "RedundantSuppression"}) @Builder.Default @Getter private Multiset<Location> destinations = ConcurrentHashMultiset.create();

        @AllArgsConstructor @NoArgsConstructor @EqualsAndHashCode @Builder(toBuilder = true)
        public static class AvailableLinkInfo implements INBTStorable {
            @Getter @Setter private float weight;
            @Getter private VirtualLocation virtualLocation;
            @Getter @Wither private Location location;
            @Getter private UUID uuid;

            @Override
            public void readFromNBT(NBTTagCompound nbt) {
                weight = nbt.getFloat("weight");
                virtualLocation = VirtualLocation.readFromNBT(nbt.getCompoundTag("virtualLocation"));
                uuid = nbt.getUniqueId("uuid");
            }

            @Override
            public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
                nbt.setFloat("weight", weight);
                nbt.setTag("virtualLocation", virtualLocation.writeToNBT());
                nbt.setUniqueId("uuid", uuid);
                return nbt;
            }
        }

        public RiftInfo() {
            availableLinks = new HashSet<>();
            sources = ConcurrentHashMultiset.create();
            destinations = ConcurrentHashMultiset.create();
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            NBTTagList availableLinksNBT = (NBTTagList) nbt.getTag("availableLinks"); // TODO: figure out why this is sometimes null
            for (NBTBase availableLinkNBT : availableLinksNBT) {
                AvailableLinkInfo link = new AvailableLinkInfo();
                link.readFromNBT((NBTTagCompound) availableLinkNBT);
                availableLinks.add(link);
            }

            NBTTagList sourcesNBT = (NBTTagList) nbt.getTag("sources");
            for (NBTBase sourceNBT : sourcesNBT) {
                sources.add(Location.readFromNBT((NBTTagCompound) sourceNBT));
            }

            NBTTagList destinationsNBT = (NBTTagList) nbt.getTag("destinations");
            for (NBTBase destinationNBT : destinationsNBT) {
                destinations.add(Location.readFromNBT((NBTTagCompound) destinationNBT));
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            NBTTagList availableLinksNBT = new NBTTagList();
            for (AvailableLinkInfo availableLink : availableLinks) {
                availableLinksNBT.appendTag(availableLink.writeToNBT(new NBTTagCompound()));
            }
            nbt.setTag("availableLinks", availableLinksNBT);

            NBTTagList sourcesNBT = new NBTTagList();
            for (Location source : sources) {
                sourcesNBT.appendTag(Location.writeToNBT(source));
            }
            nbt.setTag("sources", sourcesNBT);

            NBTTagList destinationsNBT = new NBTTagList();
            for (Location destination : destinations) {
                destinationsNBT.appendTag(Location.writeToNBT(destination));
            }
            nbt.setTag("destinations", sourcesNBT);

            return nbt;
        }
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

        NBTTagList riftsNBT = (NBTTagList) nbt.getTag("rifts");
        for (NBTBase riftNBT : riftsNBT) {
            NBTTagCompound riftNBTC = (NBTTagCompound) riftNBT;
            Location location = Location.readFromNBT(riftNBTC.getCompoundTag("location"));
            RiftInfo riftInfo = new RiftInfo();
            riftInfo.readFromNBT(riftNBTC);
            rifts.put(location, riftInfo);
        }

        NBTTagList privatePocketEntrancesNBT = (NBTTagList) nbt.getTag("privatePocketEntrances");
        for (NBTBase privatePocketEntranceNBT : privatePocketEntrancesNBT) { // TODO: move to NBTUtils
            NBTTagCompound privatePocketEntranceNBTC = (NBTTagCompound) privatePocketEntranceNBT;
            String uuid = privatePocketEntranceNBTC.getString("uuid");
            Location rift = Location.readFromNBT(privatePocketEntranceNBTC.getCompoundTag("location"));
            privatePocketEntrances.put(uuid, rift);
        }
        
        NBTTagList privatePocketEntranceListsNBT = (NBTTagList) nbt.getTag("privatePocketEntranceLists");
        for (NBTBase privatePocketEntranceListNBT : privatePocketEntranceListsNBT) { // TODO: move to NBTUtils
            NBTTagCompound privatePocketEntranceListNBTC = (NBTTagCompound) privatePocketEntranceListNBT;
            String uuid = privatePocketEntranceListNBTC.getString("uuid");
            NBTTagList entrancesNBT = (NBTTagList) privatePocketEntranceListNBTC.getTag("locationList");
            for (NBTBase entranceNBT : entrancesNBT) {
                NBTTagCompound entranceNBTC = (NBTTagCompound) entranceNBT;
                Location rift = Location.readFromNBT(entranceNBTC);
                privatePocketEntranceLists.get(uuid).add(rift);
            }
        }

        NBTTagList privatePocketExitsNBT = (NBTTagList) nbt.getTag("privatePocketExits");
        for (NBTBase privatePocketExitNBT : privatePocketExitsNBT) { // TODO: move to NBTUtils
            NBTTagCompound privatePocketExitNBTC = (NBTTagCompound) privatePocketExitNBT;
            String uuid = privatePocketExitNBTC.getString("uuid");
            Location rift = Location.readFromNBT(privatePocketExitNBTC.getCompoundTag("location"));
            privatePocketExits.put(uuid, rift);
        }

        NBTTagList overworldRiftsNBT = (NBTTagList) nbt.getTag("overworldRifts");
        for (NBTBase overworldRiftNBT : overworldRiftsNBT) { // TODO: move to NBTUtils
            NBTTagCompound overworldRiftNBTC = (NBTTagCompound) overworldRiftNBT;
            String uuid = overworldRiftNBTC.getString("uuid");
            Location rift = Location.readFromNBT(overworldRiftNBTC.getCompoundTag("location"));
            overworldRifts.put(uuid, rift);
        }
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

        NBTTagList riftsNBT = new NBTTagList();
        for (HashMap.Entry<Location, RiftInfo> rift : rifts.entrySet()) {
            NBTTagCompound riftNBT = new NBTTagCompound();
            riftNBT.setTag("location", Location.writeToNBT(rift.getKey()));
            riftNBT = rift.getValue().writeToNBT(riftNBT);
            riftsNBT.appendTag(riftNBT);
        }
        nbt.setTag("rifts", riftsNBT);

        NBTTagList privatePocketEntrancesNBT = new NBTTagList();
        for (HashMap.Entry<String, Location> privatePocketEntrance : privatePocketEntrances.entrySet()) { // TODO: move to NBTUtils
            if (privatePocketEntrance.getValue() == null) continue;
            NBTTagCompound privatePocketEntranceNBT = new NBTTagCompound();
            privatePocketEntranceNBT.setString("uuid", privatePocketEntrance.getKey());
            privatePocketEntranceNBT.setTag("location", Location.writeToNBT(privatePocketEntrance.getValue()));
            privatePocketEntrancesNBT.appendTag(privatePocketEntranceNBT);
        }
        nbt.setTag("privatePocketEntrances", privatePocketEntrancesNBT);
        
        NBTTagList privatePocketEntranceListsNBT = new NBTTagList();
        for (HashMap.Entry<String, List<Location>> privatePocketEntranceList : privatePocketEntranceLists.entrySet()) { // TODO: move to NBTUtils
            if (privatePocketEntranceList.getValue() == null) continue;
            NBTTagCompound privatePocketEntranceListNBT = new NBTTagCompound();
            privatePocketEntranceListNBT.setString("uuid", privatePocketEntranceList.getKey());
            NBTTagList entranceListNBT = new NBTTagList();
            for (Location entrance : privatePocketEntranceList.getValue()) {
                entranceListNBT.appendTag(Location.writeToNBT(entrance));
            }
            privatePocketEntranceListNBT.setTag("locationList", entranceListNBT);
            privatePocketEntranceListsNBT.appendTag(privatePocketEntranceListNBT);
        }
        nbt.setTag("privatePocketEntranceLists", privatePocketEntranceListsNBT);
        
        NBTTagList privatePocketExitsNBT = new NBTTagList();
        for (HashMap.Entry<String, Location> privatePocketExit : privatePocketExits.entrySet()) { // TODO: move to NBTUtils
            if (privatePocketExit.getValue() == null) continue;
            NBTTagCompound privatePocketExitNBT = new NBTTagCompound();
            privatePocketExitNBT.setString("uuid", privatePocketExit.getKey());
            privatePocketExitNBT.setTag("location", Location.writeToNBT(privatePocketExit.getValue()));
            privatePocketExitsNBT.appendTag(privatePocketExitNBT);
        }
        nbt.setTag("privatePocketExits", privatePocketExitsNBT);
        
        NBTTagList overworldRiftsNBT = new NBTTagList();
        for (HashMap.Entry<String, Location> overworldRift : overworldRifts.entrySet()) {
            if (overworldRift.getValue() == null) continue;
            NBTTagCompound overworldRiftNBT = new NBTTagCompound();
            overworldRiftNBT.setString("uuid", overworldRift.getKey());
            overworldRiftNBT.setTag("location", Location.writeToNBT(overworldRift.getValue()));
            overworldRiftsNBT.appendTag(overworldRiftNBT);
        }
        nbt.setTag("overworldRifts", overworldRiftsNBT);

        return nbt;
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
        registryTo.rifts.get(to).destinations.add(from);
        registryFrom.markDirty();
        registryTo.markDirty();
    }

    public static void removeLink(Location from, Location to) {
        RiftRegistry registryFrom = getRegistry(from);
        RiftRegistry registryTo = getRegistry(to);
        registryFrom.rifts.get(from).destinations.remove(to);
        registryTo.rifts.get(to).destinations.remove(from);
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
            entrance = entrances.get(0);
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
