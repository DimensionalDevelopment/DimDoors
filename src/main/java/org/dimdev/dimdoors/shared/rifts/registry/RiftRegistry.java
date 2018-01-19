package org.dimdev.dimdoors.shared.rifts.registry;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.WorldUtils;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.ddutils.GraphUtils;
import org.dimdev.dimdoors.shared.pockets.Pocket;
import org.dimdev.dimdoors.shared.pockets.PocketRegistry;
import org.dimdev.dimdoors.shared.world.ModDimensions;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;
import java.util.stream.Collectors;

public class RiftRegistry extends WorldSavedData {

    // Both are saved in the "data" folder, so we can't use the same name
    private static final String DATA_NAME = DimDoors.MODID + "_global_rifts";
    private static final String SUBREGISTRY_DATA_NAME = DimDoors.MODID + "_rifts";

    protected Map<Integer, RiftSubregistry> subregistries = new HashMap<>();
    protected DefaultDirectedGraph<RegistryVertex, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    // TODO: add methods that automatically add vertices/edges and mark appropriate subregistries as dirty

    // Caches to avoid looping through vertices to find specific vertices
    protected Map<Location, Rift> locationMap = new HashMap<>();
    protected Map<Pocket, PocketEntrancePointer> pocketEntranceMap = new HashMap<>(); // TODO: We're going to want to move all pocket entrance info to the rift registry later to make PocketLib independent of DimDoors.
    protected Map<UUID, RegistryVertex> uuidMap = new HashMap<>();

    // These are stored in the main registry
    protected Map<UUID, PlayerRiftPointer> lastPrivatePocketEntrances = new HashMap<>(); // Player UUID -> last rift used to exit pocket
    protected Map<UUID, PlayerRiftPointer> lastPrivatePocketExits = new HashMap<>(); // Player UUID -> last rift used to enter pocket
    protected Map<UUID, PlayerRiftPointer> overworldRifts = new HashMap<>(); // Player UUID -> rift used to exit the overworld

    // <editor-fold defaultstate="collapsed" desc="Code for reading/writing/getting the registry">

    public static class RiftSubregistry extends WorldSavedData {
        private int dim;
        RiftRegistry riftRegistry = (RiftRegistry) WorldUtils.getWorld(0).getMapStorage().getOrLoadData(RiftRegistry.class, DATA_NAME);

        public RiftSubregistry() {
            super(SUBREGISTRY_DATA_NAME);
        }

        public RiftSubregistry(String s) {
            super(s);
        }

        @Override public void readFromNBT(NBTTagCompound nbt) {
            // Registry is already loaded
            if (riftRegistry.subregistries.get(dim) != null) return;

            // Read rifts in this dimension
            NBTTagList riftsNBT = (NBTTagList) nbt.getTag("rifts");
            for (NBTBase riftNBT : riftsNBT) {
                Rift rift = new Rift();
                rift.readFromNBT((NBTTagCompound) riftNBT);
                rift.dim = dim;
                riftRegistry.graph.addVertex(rift);
                riftRegistry.uuidMap.put(rift.id, rift);
                riftRegistry.locationMap.put(rift.location, rift);
            }

            NBTTagList pocketsNBT = (NBTTagList) nbt.getTag("pockets");
            for (NBTBase pocketNBT : pocketsNBT) {
                PocketEntrancePointer pocket = new PocketEntrancePointer();
                pocket.readFromNBT((NBTTagCompound) pocketNBT);
                pocket.dim = dim;
                riftRegistry.graph.addVertex(pocket);
                riftRegistry.uuidMap.put(pocket.id, pocket);
                riftRegistry.pocketEntranceMap.put(PocketRegistry.instance(pocket.dim).getPocket(pocket.pocketId), pocket);
            }

            // Read the connections between links that have a source or destination in this dimension
            NBTTagList linksNBT = (NBTTagList) nbt.getTag("links");
            for (NBTBase linkNBT : linksNBT) {
                RegistryVertex from = riftRegistry.uuidMap.get(((NBTTagCompound) linkNBT).getUniqueId("from"));
                RegistryVertex to = riftRegistry.uuidMap.get(((NBTTagCompound) linkNBT).getUniqueId("to"));
                if (from != null && to != null) {
                    riftRegistry.graph.addEdge(from, to);
                    // We need a system for detecting links that are incomplete after processing them in the other subregistry too
                }
            }
        }

        // Even though it seems like we could loop only once over the vertices and edges (in the RiftRegistry's writeToNBT
        // method rather than RiftSubregistry) and save each in the appropriate registry, we can't do this because it is not
        // always the case that all worlds will be saved at once.
        @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            // Write rifts in this dimension
            NBTTagList riftsNBT = new NBTTagList();
            NBTTagList pocketsNBT = new NBTTagList();
            for (RegistryVertex vertex : riftRegistry.graph.vertexSet()) {
                if (vertex.dim == dim) {
                    NBTTagCompound vertexNBT = vertex.writeToNBT(new NBTTagCompound());
                    if (vertex instanceof Rift) {
                        riftsNBT.appendTag(vertexNBT);
                    } else if (vertex instanceof PocketEntrancePointer) {
                        pocketsNBT.appendTag(vertexNBT);
                    } else if (!(vertex instanceof PlayerRiftPointer)) {
                        throw new RuntimeException("Unsupported registry vertex type " + vertex.getClass().getName());
                    }
                }
            }
            nbt.setTag("rifts", riftsNBT);
            nbt.setTag("pockets", pocketsNBT);

            // Write the connections between links that have a source or destination in this dimension
            NBTTagList linksNBT = new NBTTagList();
            for (DefaultEdge edge : riftRegistry.graph.edgeSet()) {
                RegistryVertex from = riftRegistry.graph.getEdgeSource(edge);
                RegistryVertex to = riftRegistry.graph.getEdgeTarget(edge);
                if (from.dim == dim || to.dim == dim && !(from instanceof PlayerRiftPointer)) {
                    NBTTagCompound linkNBT = new NBTTagCompound();
                    linkNBT.setUniqueId("from", from.id);
                    linkNBT.setUniqueId("to", to.id);
                    linksNBT.appendTag(linkNBT);
                }
            }
            nbt.setTag("links", linksNBT);

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
    public void readFromNBT(NBTTagCompound nbt) {
        // Trigger the subregistry reading code for all dimensions. It would be better if there was some way of forcing
        // them to be read from somewhere else, since this is technically more than just reading the NBT. This has to be
        // done last since links are only in the subregistries.
        // TODO: If non-dirty but new WorldSavedDatas aren't automatically saved, then create the subregistries here
        // TODO: rather then in the markSubregistryDirty method.
        // TODO: try to get rid of this code:
        for (int dim : DimensionManager.getStaticDimensionIDs()) {
            MapStorage storage = WorldUtils.getWorld(dim).getPerWorldStorage();
            RiftSubregistry instance = (RiftSubregistry) storage.getOrLoadData(RiftSubregistry.class, SUBREGISTRY_DATA_NAME);
            if (instance != null) {
                instance.dim = dim;
                subregistries.put(dim, instance);
            }
        }

        // Read player to rift maps (this has to be done after the uuidMap has been filled by the subregistry code)
        lastPrivatePocketEntrances = readPlayerRiftPointers((NBTTagList) nbt.getTag("lastPrivatePocketEntrances"));
        lastPrivatePocketExits = readPlayerRiftPointers((NBTTagList) nbt.getTag("lastPrivatePocketExits"));
        overworldRifts = readPlayerRiftPointers((NBTTagList) nbt.getTag("overworldRifts"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        // Subregistries are written automatically when the worlds are saved.
        nbt.setTag("lastPrivatePocketEntrances", writePlayerRiftPointers(lastPrivatePocketEntrances));
        nbt.setTag("lastPrivatePocketExits", writePlayerRiftPointers(lastPrivatePocketExits));
        nbt.setTag("overworldRifts", writePlayerRiftPointers(overworldRifts));
        return nbt;
    }

    private Map<UUID, PlayerRiftPointer> readPlayerRiftPointers(NBTTagList playerRiftPointersNBT) {
        Map<UUID, PlayerRiftPointer> pointerMap = new HashMap<>();
        for (NBTBase entryNBT : playerRiftPointersNBT) {
            UUID player = ((NBTTagCompound) entryNBT).getUniqueId("player");
            UUID rift = ((NBTTagCompound) entryNBT).getUniqueId("rift");
            PlayerRiftPointer pointer = new PlayerRiftPointer(player);
            pointerMap.put(player, pointer);
            uuidMap.put(pointer.id, pointer);
            graph.addVertex(pointer);
            graph.addEdge(pointer, uuidMap.get(rift));
        }
        return pointerMap;
    }

    private NBTTagList writePlayerRiftPointers(Map<UUID, PlayerRiftPointer> playerRiftPointerMap) {
        NBTTagList pointers = new NBTTagList();
        for (Map.Entry<UUID, PlayerRiftPointer> entry : playerRiftPointerMap.entrySet()) {
            NBTTagCompound entryNBT = new NBTTagCompound();
            entryNBT.setUniqueId("player", entry.getKey());
            int count = 0;
            for (DefaultEdge edge : graph.outgoingEdgesOf(entry.getValue())) {
                entryNBT.setUniqueId("rift", graph.getEdgeTarget(edge).id);
                count++;
            }
            if (count != 1) throw new RuntimeException("PlayerRiftPointer points to more than one rift");
            pointers.appendTag(entryNBT);
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
            DimDoors.log.info("Creating a rift placeholder at " + location);
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
        DimDoors.log.info("Adding rift at " + location);
        RegistryVertex currentRift = locationMap.get(location);
        Rift rift;
        if (currentRift instanceof RiftPlaceholder) {
            DimDoors.log.info("Converting a rift placeholder at " + location + " into a rift");
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
        DimDoors.log.info("Removing rift at " + location);

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
        DimDoors.log.info("Adding link " + locationFrom + " -> " + locationTo);

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
        DimDoors.log.info("Removing link " + locationFrom + " -> " + locationTo);

        Rift from = getRift(locationFrom);
        Rift to = getRift(locationTo);

        removeEdge(from, to);

        // Notify the linked vertices of the change
        from.targetGone(to);
        to.sourceGone(from);
    }

    public void setProperties(Location location, LinkProperties properties) {
        DimDoors.log.info("Setting DungeonLinkProperties for rift at " + location + " to " + properties);
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

    public void addPocketEntrance(Pocket pocket, Location location) {
        DimDoors.log.info("Adding pocket entrance for pocket " + pocket.getId() + " in dimension " + pocket.getDim() + " at " + location);
        PocketEntrancePointer pointer = pocketEntranceMap.get(pocket);
        if (pointer == null) {
            pointer = new PocketEntrancePointer(pocket.getDim(), pocket.getId());
            pointer.dim = pocket.getDim();
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

        // If there was no last used private entrance, get one of the player's private pocket entrances
        PocketRegistry privatePocketRegistry = PocketRegistry.instance(ModDimensions.getPrivateDim());
        Pocket pocket = privatePocketRegistry.getPocket(privatePocketRegistry.getPrivatePocketID(playerUUID));
        return getPocketEntrances(pocket).stream().findFirst().orElse(null);
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
        DimDoors.log.info("Setting last used private pocket entrance for " + playerUUID + " at " + rift);
        setPlayerRiftPointer(playerUUID, rift, lastPrivatePocketEntrances);
    }

    public Location getPrivatePocketExit(UUID playerUUID) {
        PlayerRiftPointer entrancePointer = lastPrivatePocketExits.get(playerUUID);
        Rift entrance = (Rift) GraphUtils.followPointer(graph, entrancePointer);
        return entrance != null ? entrance.location : null;
    }

    public void setLastPrivatePocketExit(UUID playerUUID, Location rift) {
        DimDoors.log.info("Setting last used private pocket entrance for " + playerUUID + " at " + rift);
        setPlayerRiftPointer(playerUUID, rift, lastPrivatePocketExits);
    }

    public Location getOverworldRift(UUID playerUUID) {
        PlayerRiftPointer entrancePointer = overworldRifts.get(playerUUID);
        Rift entrance = (Rift) GraphUtils.followPointer(graph, entrancePointer);
        return entrance.location;
    }

    public void setOverworldRift(UUID playerUUID, Location rift) {
        DimDoors.log.info("Setting last used private pocket entrance for " + playerUUID + " at " + rift);
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
