package com.zixiken.dimdoors.shared.rifts;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.VirtualLocation;
import com.zixiken.dimdoors.shared.rifts.RiftRegistry.RiftInfo.AvailableLinkInfo;
import com.zixiken.dimdoors.shared.util.INBTStorable;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.util.WorldUtils;
import lombok.*;
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
    @Getter private Map<String, Location> escapeRifts = new HashMap<>();

    @Getter private int dim;
    private World world;

    @lombok.AllArgsConstructor @lombok.EqualsAndHashCode @lombok.Builder(toBuilder = true)
    public static class RiftInfo implements com.zixiken.dimdoors.shared.util.INBTStorable {
        @Builder.Default @Getter private Set<AvailableLinkInfo> availableLinks = new HashSet<>(); // ignore intellij warnings, builder needs these
        @Builder.Default @Getter private Set<Location> sources = new HashSet<>();
        @Builder.Default @Getter private Set<Location> destinations = new HashSet<>();

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
            sources = new HashSet<>();
            destinations = new HashSet<>();
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

        NBTTagList escapeRiftsNBT = (NBTTagList) nbt.getTag("escapeRifts");
        for (NBTBase escapeRiftNBT : escapeRiftsNBT) { // TODO: move to NBTUtils
            NBTTagCompound escapeRiftNBTC = (NBTTagCompound) escapeRiftNBT;
            String uuid = escapeRiftNBTC.getString("uuid");
            Location rift = Location.readFromNBT(escapeRiftNBTC.getCompoundTag("location"));
            escapeRifts.put(uuid, rift);
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

        NBTTagList privatePocketEntrancesNBT = new NBTTagList();
        for (HashMap.Entry<String, Location> privatePocketEntrance : privatePocketEntrances.entrySet()) { // TODO: move to NBTUtils
            if (privatePocketEntrance.getValue() == null) continue;
            NBTTagCompound privatePocketEntranceNBT = new NBTTagCompound();
            privatePocketEntranceNBT.setString("uuid", privatePocketEntrance.getKey());
            privatePocketEntranceNBT.setTag("location", Location.writeToNBT(privatePocketEntrance.getValue()));
            riftsNBT.appendTag(privatePocketEntranceNBT);
        }
        nbt.setTag("privatePocketEntrances", privatePocketEntrancesNBT);

        NBTTagList escapeRiftsNBT = new NBTTagList();
        for (HashMap.Entry<String, Location> escapeRift : escapeRifts.entrySet()) {
            if (escapeRift.getValue() == null) continue;
            NBTTagCompound escapeRiftNBT = new NBTTagCompound();
            escapeRiftNBT.setString("uuid", escapeRift.getKey());
            escapeRiftNBT.setTag("location", Location.writeToNBT(escapeRift.getValue()));
            riftsNBT.appendTag(escapeRiftNBT);
        }
        nbt.setTag("escapeRifts", escapeRiftsNBT);

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
            TileEntityRift riftEntity = (TileEntityRift) destinationRegistry.world.getTileEntity(destination.getPos());
            riftEntity.allSourcesGone();
        }
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
        return getForDim(rift.getDimID());
    }

    public Location getPrivatePocketEntrance(String playerUUID) {
        return privatePocketEntrances.get(playerUUID);
    }

    public void setPrivatePocketEntrance(String playerUUID, Location rift) {
        privatePocketEntrances.put(playerUUID, rift);
    }

    public static Location getEscapeRift(String playerUUID) { // TODO: since this is per-world, move to different registry?
        return getForDim(0).escapeRifts.get(playerUUID); // store in overworld, since that's where per-world player data is stored
    }

    public static void setEscapeRift(String playerUUID, Location rift) {
        getForDim(0).escapeRifts.put(playerUUID, rift);
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
