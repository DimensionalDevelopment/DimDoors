package com.zixiken.dimdoors.shared.rifts;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.VirtualLocation;
import com.zixiken.dimdoors.shared.rifts.RiftRegistry.RiftInfo.AvailableLinkInfo;
import com.zixiken.dimdoors.shared.util.INBTStorable;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.util.WorldUtils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;
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

    @Getter private Map<Location, RiftInfo> rifts = new HashMap<>(); // TODO: store relative locations too (better location class supporting relative, etc)
    @Getter private Map<String, Location> privatePocketEntrances = new HashMap<>(); // TODO: more general group-group linking
    @Getter private Map<String, Location> escapeRift = new HashMap<>();

    @Getter private int dim;
    private World world;

    @lombok.AllArgsConstructor @lombok.NoArgsConstructor @lombok.EqualsAndHashCode
    public static class RiftInfo implements com.zixiken.dimdoors.shared.util.INBTStorable {
        @Getter private List<AvailableLinkInfo> availableLinks = new LinkedList<>();
        @Getter private Set<Location> sources = new HashSet<>();
        @Getter private Set<Location> destinations = new HashSet<>();

        @AllArgsConstructor @NoArgsConstructor @EqualsAndHashCode
        public static class AvailableLinkInfo implements INBTStorable {
            @Getter private float weight;
            @Getter private VirtualLocation virtualLocation;
            @Getter @Wither private Location location;

            @Override
            public void readFromNBT(NBTTagCompound nbt) {
                weight = nbt.getFloat("weight");
                virtualLocation = VirtualLocation.readFromNBT(nbt.getCompoundTag("virtualLocation"));
            }

            @Override
            public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
                nbt.setFloat("weight", weight);
                nbt.setTag("virtualLocation", virtualLocation.writeToNBT());
                return nbt;
            }
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            NBTTagList availableLinksNBT = (NBTTagList) nbt.getTag("availableLinks");
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

    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        Integer version = nbt.getInteger("version");
        if (version == null || version != DATA_VERSION) {
            if (upgradeRegistry(nbt, version == null ? -1 : version)) {
                markDirty();
            } else {
                DimDoors.warn("Failed to upgrade the pocket registry, you'll have to recreate your world!");
                throw new RuntimeException("Couldn't upgrade registry"); // TODO: better exceptions
            }
        }

        NBTTagList riftsNBT = (NBTTagList) nbt.getTag("rifts");
        for (NBTBase riftNBT : riftsNBT) {
            NBTTagCompound riftNBTC = (NBTTagCompound) riftNBT;
            Location location = Location.readFromNBT(riftNBTC.getCompoundTag("location"));
            RiftInfo riftInfo = new RiftInfo();
            riftInfo.readFromNBT(nbt);
            rifts.put(location, riftInfo);
        }
    }

    private static boolean upgradeRegistry(NBTTagCompound nbt, int oldVersion) {
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

    public static void registerNewLink(Location from, Location to) {
        RiftRegistry registryFrom = getRegistry(from);
        RiftRegistry registryTo = getRegistry(to);
        registryFrom.rifts.get(from).destinations.add(to);
        registryTo.rifts.get(to).destinations.add(from);
        registryFrom.markDirty();
        registryTo.markDirty();
    }

    public static void deleteLink(Location from, Location to) {
        RiftRegistry registryFrom = getRegistry(from);
        RiftRegistry registryTo = getRegistry(to);
        registryFrom.rifts.get(from).destinations.remove(to);
        registryTo.rifts.get(to).destinations.remove(from);
        registryFrom.markDirty();
        registryTo.markDirty();
    }

    public static void deleteRift(Location rift) {
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
            TileEntityRift riftEntity = (TileEntityRift) destinationRegistry.world.getTileEntity(destination.getPos());
            riftEntity.checkIfNeeded();
        }
        registry.markDirty();
    }

    public static void addAvailableLink(Location rift, AvailableLinkInfo link) { // TODO cache rifts with availableLinks
        RiftRegistry registry = getRegistry(rift);
        registry.rifts.get(rift).availableLinks.add(link);
        registry.markDirty();
    }

    public void removeAvailableLink(Location rift, AvailableLinkInfo link) {
        RiftRegistry registry = getRegistry(rift);
        registry.rifts.get(rift).availableLinks.remove(link);
        registry.markDirty();
    }

    public static RiftRegistry getRegistry(Location rift) {
        return getForDim(rift.getDimID());
    }

    public Location getPrivatePocketEntrance(String playerUUID) {
        return privatePocketEntrances.get(playerUUID);
    }

    public void setPrivatePocketEntrance(String playerUUID, Location rift) {
        privatePocketEntrances.put(playerUUID, rift);
    }

    public static Location getEscapeRift(String playerUUID) { // TODO: since this is per-world, move to different registry?
        return getForDim(0).escapeRift.get(playerUUID); // store in overworld, since that's where per-world player data is stored
    }

    public static void setEscapeRift(String playerUUID, Location rift) {
        getForDim(0).escapeRift.put(playerUUID, rift);
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
