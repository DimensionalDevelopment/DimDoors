package org.dimdev.dimdoors.rift.registry;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.PersistentState;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.DimensionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.pocketlib.Pocket;
import org.dimdev.pocketlib.PocketRegistry;
import org.dimdev.pocketlib.PrivatePocketData;
import org.dimdev.util.GraphUtils;
import org.dimdev.util.Location;
import org.dimdev.util.WorldUtils;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;
import java.util.stream.Collectors;

public class RiftRegistry extends PersistentState {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String DATA_NAME = "dimdoors_global_rifts";
    private static final String SUBREGISTRY_DATA_NAME = "dimdoors_rifts";

    protected Map<Integer, RiftSubregistry> subregistries = new HashMap<>();
    private static RiftRegistry riftRegistry = null; // For use by RiftSubregistry only
    private static int currentDim; // For use by RiftSubregistry only
    protected DefaultDirectedGraph<RegistryVertex, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    // TODO: add methods that automatically add vertices/edges and mark appropriate subregistries as dirty

    // Caches to avoid looping through vertices to find specific vertices
    protected Map<Location, Rift> locationMap = new HashMap<>();
    protected Map<Pocket, PocketEntrancePointer> pocketEntranceMap = new HashMap<>();
    protected Map<UUID, RegistryVertex> uuidMap = new HashMap<>();

    // These are stored in the main registry
    protected Map<UUID, PlayerRiftPointer> lastPrivatePocketEntrances = new HashMap<>(); // Player UUID -> last rift used to exit pocket
    protected Map<UUID, PlayerRiftPointer> lastPrivatePocketExits = new HashMap<>(); // Player UUID -> last rift used to enter pocket
    protected Map<UUID, PlayerRiftPointer> overworldRifts = new HashMap<>(); // Player UUID -> rift used to exit the overworld

    // <editor-fold defaultstate="collapsed" desc="Code for reading/writing/getting the registry">

    public static class RiftSubregistry extends PersistentState {
        private int dim;

        public RiftSubregistry() {
            super(SUBREGISTRY_DATA_NAME);
        }

        public RiftSubregistry(String s) {
            super(s);
        }

        @Override
        public void fromTag(CompoundTag nbt) {
            dim = currentDim;
            if (riftRegistry == null || riftRegistry.subregistries.get(dim) != null) return;

            // Read rifts in this dimension
            ListTag riftsNBT = (ListTag) nbt.getTag("rifts");
            for (Tag riftNBT : riftsNBT) {
                Rift rift = new Rift();
                rift.fromTag((CompoundTag) riftNBT);
                rift.dim = dim;
                riftRegistry.graph.addVertex(rift);
                riftRegistry.uuidMap.put(rift.id, rift);
                riftRegistry.locationMap.put(rift.location, rift);
            }

            ListTag pocketsNBT = (ListTag) nbt.getTag("pockets");
            for (Tag pocketNBT : pocketsNBT) {
                PocketEntrancePointer pocket = new PocketEntrancePointer();
                pocket.fromTag((CompoundTag) pocketNBT);
                pocket.dim = dim;
                riftRegistry.graph.addVertex(pocket);
                riftRegistry.uuidMap.put(pocket.id, pocket);
                riftRegistry.pocketEntranceMap.put(PocketRegistry.instance(pocket.dim).getPocket(pocket.pocketId), pocket);
            }

            // Read the connections between links that have a source or destination in this dimension
            ListTag linksNBT = (ListTag) nbt.getTag("links");
            for (Tag linkNBT : linksNBT) {
                RegistryVertex from = riftRegistry.uuidMap.get(((CompoundTag) linkNBT).getUuid("from"));
                RegistryVertex to = riftRegistry.uuidMap.get(((CompoundTag) linkNBT).getUuid("to"));
                if (from != null && to != null) {
                    riftRegistry.graph.addEdge(from, to);
                    // We need a system for detecting links that are incomplete after processing them in the other subregistry too
                }
            }
        }

        // Even though it seems like we could loop only once over the vertices and edges (in the RiftRegistry's toTag
        // method rather than RiftSubregistry) and save each in the appropriate registry, we can't do this because it is not
        // always the case that all worlds will be saved at once.
        @Override
        public CompoundTag toTag(CompoundTag nbt) {
            if (riftRegistry == null) riftRegistry = RiftRegistry.instance();
            // Write rifts in this dimension
            ListTag riftsNBT = new ListTag();
            ListTag pocketsNBT = new ListTag();
            for (RegistryVertex vertex : riftRegistry.graph.vertexSet()) {
                if (vertex.dim == dim) {
                    CompoundTag vertexNBT = vertex.toTag(new CompoundTag());
                    if (vertex instanceof Rift) {
                        riftsNBT.appendTag(vertexNBT);
                    } else if (vertex instanceof PocketEntrancePointer) {
                        pocketsNBT.appendTag(vertexNBT);
                    } else if (!(vertex instanceof PlayerRiftPointer)) {
                        throw new RuntimeException("Unsupported registry vertex type " + vertex.getClass().getName());
                    }
                }
            }
            nbt.put("rifts", riftsNBT);
            nbt.put("pockets", pocketsNBT);

            // Write the connections between links that have a source or destination in this dimension
            ListTag linksNBT = new ListTag();
            for (DefaultEdge edge : riftRegistry.graph.edgeSet()) {
                RegistryVertex from = riftRegistry.graph.getEdgeSource(edge);
                RegistryVertex to = riftRegistry.graph.getEdgeTarget(edge);
                if (from.dim == dim || to.dim == dim && !(from instanceof PlayerRiftPointer)) {
                    CompoundTag linkNBT = new CompoundTag();
                    linkNBT.putUuid("from", from.id);
                    linkNBT.putUuid("to", to.id);
                    linksNBT.appendTag(linkNBT);
                }
            }
            nbt.put("links", linksNBT);

            return nbt;
        }
    }

    public RiftRegistry() {
        super(DATA_NAME);
    }

    public RiftRegistry(String s) {
        super(s);
    }

    public static RiftRegistry instance() {
        MapStorage storage = WorldUtils.getWorld(0).getMapStorage();
        RiftRegistry instance = (RiftRegistry) storage.getOrLoadData(RiftRegistry.class, DATA_NAME);

        if (instance == null) {
            instance = new RiftRegistry();
            storage.setData(DATA_NAME, instance);
        }

        return instance;
    }

    @Override
    public void fromTag(CompoundTag nbt) {

        // Trigger the subregistry reading code for all dimensions. It would be better if there was some way of forcing
        // them to be read from somewhere else, since this is technically more than just reading the NBT and can cause
        // problems with recursion without riftRegistry. This has to be done first since links are only
        // in the subregistries.
        // TODO: If non-dirty but new WorldSavedDatas aren't automatically saved, then create the subregistries here
        // TODO: rather then in the markSubregistryDirty method.
        // TODO: try to get rid of this code:
        riftRegistry = this;
        for (int dim : DimensionManager.getStaticDimensionIDs()) {
            MapStorage storage = WorldUtils.getWorld(dim).getPerWorldStorage();
            currentDim = dim;
            RiftSubregistry instance = (RiftSubregistry) storage.getOrLoadData(RiftSubregistry.class, SUBREGISTRY_DATA_NAME);
            if (instance != null) {
                instance.dim = dim;
                subregistries.put(dim, instance);
            }
        }
        riftRegistry = null;

        // Read player to rift maps (this has to be done after the uuidMap has been filled by the subregistry code)
        lastPrivatePocketEntrances = readPlayerRiftPointers((ListTag) nbt.getTag("lastPrivatePocketEntrances"));
        lastPrivatePocketExits = readPlayerRiftPointers((ListTag) nbt.getTag("lastPrivatePocketExits"));
        overworldRifts = readPlayerRiftPointers((ListTag) nbt.getTag("overworldRifts"));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        // Subregistries are written automatically when the worlds are saved.
        tag.put("lastPrivatePocketEntrances", writePlayerRiftPointers(lastPrivatePocketEntrances));
        tag.put("lastPrivatePocketExits", writePlayerRiftPointers(lastPrivatePocketExits));
        tag.put("overworldRifts", writePlayerRiftPointers(overworldRifts));
        return tag;
    }

    private Map<UUID, PlayerRiftPointer> readPlayerRiftPointers(ListTag tag) {
        Map<UUID, PlayerRiftPointer> pointerMap = new HashMap<>();
        for (Tag entryNBT : tag) {
            UUID player = ((CompoundTag) entryNBT).getUuid("player");
            UUID rift = ((CompoundTag) entryNBT).getUuid("rift");
            PlayerRiftPointer pointer = new PlayerRiftPointer(player);
            pointerMap.put(player, pointer);
            uuidMap.put(pointer.id, pointer);
            graph.addVertex(pointer);
            graph.addEdge(pointer, uuidMap.get(rift));
        }
        return pointerMap;
    }

    private ListTag writePlayerRiftPointers(Map<UUID, PlayerRiftPointer> playerRiftPointerMap) {
        ListTag pointers = new ListTag();
        for (Map.Entry<UUID, PlayerRiftPointer> entry : playerRiftPointerMap.entrySet()) {
            CompoundTag entryNBT = new CompoundTag();
            entryNBT.putUuid("player", entry.getKey());
            int count = 0;
            for (DefaultEdge edge : graph.outgoingEdgesOf(entry.getValue())) {
                entryNBT.putUuid("rift", graph.getEdgeTarget(edge).id);
                count++;
            }
            if (count != 1) throw new RuntimeException("PlayerRiftPointer points to more than one rift");
            pointers.add(entryNBT);
        }
        return pointers;
    }

    public void markSubregistryDirty(int dim) {
        RiftSubregistry subregistry = subregistries.get(dim);
        if (subregistry != null) {
            subregistry.markDirty();
        } else {
            // Create the subregistry
            MapStorage storage = WorldUtils.getWorld(dim).getPerWorldStorage();
            RiftSubregistry instance = new RiftSubregistry();
            instance.dim = dim;
            instance.markDirty();
            storage.setData(SUBREGISTRY_DATA_NAME, instance);
            subregistries.put(dim, instance);
        }
    }

    // </editor-fold>

    public boolean isRiftAt(Location location) {
        Rift possibleRift = locationMap.get(location);
        return possibleRift != null && !(possibleRift instanceof RiftPlaceholder);
    }

    public Rift getRift(Location location) {
        Rift rift = locationMap.get(location);
        if (rift == null) throw new IllegalArgumentException("There is no rift registered at " + location);
        return rift;
    }

    private Rift getRiftOrPlaceholder(Location location) {
        Rift rift = locationMap.get(location);
        if (rift == null) {
            LOGGER.debug("Creating a rift placeholder at " + location);
            rift = new RiftPlaceholder();
            rift.dim = location.getDim();
            rift.location = location;
            locationMap.put(location, rift);
            uuidMap.put(rift.id, rift);
            graph.addVertex(rift);
            markSubregistryDirty(rift.dim);
        }
        return rift;
    }

    public void addRift(Location location) {
        LOGGER.debug("Adding rift at " + location);
        RegistryVertex currentRift = locationMap.get(location);
        Rift rift;
        if (currentRift instanceof RiftPlaceholder) {
            LOGGER.info("Converting a rift placeholder at " + location + " into a rift");
            rift = new Rift(location);
            rift.dim = location.getDim();
            rift.id = currentRift.id;
            GraphUtils.replaceVertex(graph, currentRift, rift);
        } else if (currentRift == null) {
            rift = new Rift(location);
            rift.dim = location.getDim();
            graph.addVertex(rift);
        } else {
            throw new IllegalArgumentException("There is already a rift registered at " + location);
        }
        uuidMap.put(rift.id, rift);
        locationMap.put(location, rift);
        rift.markDirty();
    }

    public void removeRift(Location location) {
        LOGGER.debug("Removing rift at " + location);

        Rift rift = getRift(location);

        Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(rift);
        Set<DefaultEdge> outgoingEdges = graph.outgoingEdgesOf(rift);

        graph.removeVertex(rift);
        locationMap.remove(location);
        uuidMap.remove(rift.id);
        markSubregistryDirty(rift.dim);

        // Notify the adjacent vertices of the change
        for (DefaultEdge edge : incomingEdges) graph.getEdgeSource(edge).targetGone(rift);
        for (DefaultEdge edge : outgoingEdges) graph.getEdgeTarget(edge).sourceGone(rift);
    }

    private void addEdge(RegistryVertex from, RegistryVertex to) {
        graph.addEdge(from, to);
        if (from instanceof PlayerRiftPointer) {
            markDirty();
        } else if (from instanceof Rift) {
            ((Rift) from).markDirty();
        } else {
            markSubregistryDirty(from.dim);
        }
        if (to instanceof Rift) {
            ((Rift) to).markDirty();
        } else {
            markSubregistryDirty(to.dim);
        }
    }

    private void removeEdge(RegistryVertex from, RegistryVertex to) {
        graph.removeEdge(from, to);
        if (from instanceof PlayerRiftPointer) {
            markDirty();
        } else {
            markSubregistryDirty(from.dim);
        }
        markSubregistryDirty(to.dim);
    }

    public void addLink(Location locationFrom, Location locationTo) {
        LOGGER.debug("Adding link " + locationFrom + " -> " + locationTo);

        Rift from = getRiftOrPlaceholder(locationFrom);
        Rift to = getRiftOrPlaceholder(locationTo);

        addEdge(from, to);

        // Notify the linked vertices of the change
        if (!(from instanceof RiftPlaceholder) && !(to instanceof RiftPlaceholder)) {
            from.targetAdded(to);
            to.sourceAdded(from);
        }
    }

    public void removeLink(Location locationFrom, Location locationTo) {
        LOGGER.debug("Removing link " + locationFrom + " -> " + locationTo);

        Rift from = getRift(locationFrom);
        Rift to = getRift(locationTo);

        removeEdge(from, to);

        // Notify the linked vertices of the change
        from.targetGone(to);
        to.sourceGone(from);
    }

    public void setProperties(Location location, LinkProperties properties) {
        LOGGER.debug("Setting DungeonLinkProperties for rift at " + location + " to " + properties);
        Rift rift = getRift(location);
        rift.properties = properties;
        rift.markDirty();
    }

    public Set<Location> getPocketEntrances(Pocket pocket) {
        PocketEntrancePointer pointer = pocketEntranceMap.get(pocket);
        if (pointer == null) {
            return Collections.emptySet();
        } else {
            return graph.outgoingEdgesOf(pointer).stream()
                        .map(graph::getEdgeTarget)
                        .map(Rift.class::cast)
                        .map(rift -> rift.location)
                        .collect(Collectors.toSet());
        }
    }

    public Location getPocketEntrance(Pocket pocket) {
        return getPocketEntrances(pocket).stream().findFirst().orElse(null);
    }

    public void addPocketEntrance(Pocket pocket, Location location) {
        LOGGER.debug("Adding pocket entrance for pocket " + pocket.id + " in dimension " + pocket.world + " at " + location);
        PocketEntrancePointer pointer = pocketEntranceMap.get(pocket);
        if (pointer == null) {
            pointer = new PocketEntrancePointer(pocket.world, pocket.id);
            pointer.dim = pocket.world;
            graph.addVertex(pointer);
            pocketEntranceMap.put(pocket, pointer);
            uuidMap.put(pointer.id, pointer);
        }
        Rift rift = getRift(location);
        addEdge(pointer, rift);
    }

    public Location getPrivatePocketEntrance(UUID playerUUID) {
        // Try to get the last used entrance
        PlayerRiftPointer entrancePointer = lastPrivatePocketEntrances.get(playerUUID);
        Rift entrance = (Rift) GraphUtils.followPointer(graph, entrancePointer);
        if (entrance != null) return entrance.location;

        // If there was no last used private entrance, get the first player's private pocket entrance
        return getPocketEntrance(PrivatePocketData.instance().getPrivatePocket(playerUUID));
    }

    private void setPlayerRiftPointer(UUID playerUUID, Location rift, Map<UUID, PlayerRiftPointer> map) {
        PlayerRiftPointer pointer = map.get(playerUUID);
        if (pointer != null) {
            graph.removeVertex(pointer);
            map.remove(playerUUID);
            uuidMap.remove(pointer.id);
        }
        if (rift != null) {
            pointer = new PlayerRiftPointer(playerUUID);
            graph.addVertex(pointer);
            map.put(playerUUID, pointer);
            uuidMap.put(pointer.id, pointer);
            addEdge(pointer, getRift(rift));
        }
    }

    public void setLastPrivatePocketEntrance(UUID playerUUID, Location rift) {
        LOGGER.debug("Setting last used private pocket entrance for " + playerUUID + " at " + rift);
        setPlayerRiftPointer(playerUUID, rift, lastPrivatePocketEntrances);
    }

    public Location getPrivatePocketExit(UUID playerUUID) {
        PlayerRiftPointer entrancePointer = lastPrivatePocketExits.get(playerUUID);
        Rift entrance = (Rift) GraphUtils.followPointer(graph, entrancePointer);
        return entrance != null ? entrance.location : null;
    }

    public void setLastPrivatePocketExit(UUID playerUUID, Location rift) {
        LOGGER.debug("Setting last used private pocket exit for " + playerUUID + " at " + rift);
        setPlayerRiftPointer(playerUUID, rift, lastPrivatePocketExits);
    }

    public Location getOverworldRift(UUID playerUUID) {
        PlayerRiftPointer entrancePointer = overworldRifts.get(playerUUID);
        Rift rift = (Rift) GraphUtils.followPointer(graph, entrancePointer);
        return rift != null ? rift.location : null;
    }

    public void setOverworldRift(UUID playerUUID, Location rift) {
        LOGGER.debug("Setting last used overworld rift for " + playerUUID + " at " + rift);
        setPlayerRiftPointer(playerUUID, rift, overworldRifts);
    }

    public Collection<Rift> getRifts() {
        return locationMap.values();
    }

    public Set<Location> getTargets(Location location) {
        return graph.outgoingEdgesOf(getRift(location)).stream()
                    .map(graph::getEdgeTarget)
                    .map(Rift.class::cast)
                    .map(rift -> rift.location)
                    .collect(Collectors.toSet());
    }

    public Set<Location> getSources(Location location) {
        return graph.incomingEdgesOf(getRift(location)).stream()
                    .map(graph::getEdgeTarget)
                    .map(Rift.class::cast)
                    .map(rift -> rift.location)
                    .collect(Collectors.toSet());
    }
}
