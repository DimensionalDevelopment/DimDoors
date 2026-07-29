package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.Edge;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;

import java.util.*;
import java.util.stream.Collectors;

public class RiftRegistry extends SubSystem<RiftRegistry> implements VertexProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String DATA_NAME = "rifts";
    public static final MapCodec<RiftRegistry> CODEC = RegistryVertex.CODEC.listOf().fieldOf(DATA_NAME).xmap(RiftRegistry::new, RiftRegistry::verticesForCodec);

    protected Map<Location, Rift> locationMap = new HashMap<>();

    public RiftRegistry() {
    }

    private RiftRegistry(Collection<RegistryVertex> vertices) {
        for (RegistryVertex vertex : vertices) {
            if (!(vertex instanceof Rift rift)) {
                throw new IllegalArgumentException("RiftRegistry cannot load non-rift vertex " + vertex);
            }
            if (rift.getLocation() == null) {
                throw new IllegalArgumentException("RiftRegistry cannot load rift without location " + rift.getId());
            }
            this.locationMap.put(rift.getLocation(), rift);
        }
    }

    public static RiftRegistry getInstance() {
        return SubSystem.getInstance(SubsystemTypes.RIFT);
    }

    @Override
    public List<? extends RegistryVertex> collectVertices() {
        return new ArrayList<>(this.locationMap.values());
    }

    @Override
    public Type<RiftRegistry> type() {
        return SubsystemTypes.RIFT;
    }

    private List<RegistryVertex> verticesForCodec() {
        return new ArrayList<>(this.locationMap.values());
    }

    public static RiftRegistry fromNbt(Map<ResourceKey<Level>, PocketDirectory> pocketRegistry, CompoundTag nbt) {
        RiftRegistry riftRegistry = new RiftRegistry();

        ListTag riftsNBT = nbt.getList("rifts", Tag.TAG_COMPOUND);
        String riftTypeId = RegistryVertex.REGISTRY.getKey(RegistryVertex.RegistryVertexType.RIFT).toString();

        for (Tag tag : riftsNBT) {
            CompoundTag compound = (CompoundTag) tag;
            if (compound.getString("type").equals(riftTypeId)) {
                Rift rift = Rift.fromNbt(compound);
                RiftGraph.getInstance().addVertex(rift);
                riftRegistry.locationMap.put(rift.getLocation(), rift);
            }
        }

        ListTag linksNBT = nbt.getList("links", Tag.TAG_COMPOUND);
        for (Tag linkNBT : linksNBT) {
            RiftGraph.getInstance().addEdge(((CompoundTag) linkNBT).getUUID("from"), ((CompoundTag) linkNBT).getUUID("to"));
        }

        return riftRegistry;
    }

    public CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();

        ListTag riftsNBT = new ListTag();

        for (Rift rift : this.locationMap.values()) {
            var vertexNbt = RegistryVertex.toNbt(rift);
            riftsNBT.add(vertexNbt);
        }



        ListTag linksNBT = new ListTag();
        for (Edge edge : RiftGraph.getInstance().edges()) {
            CompoundTag linkNBT = new CompoundTag();
            linkNBT.putUUID("from", edge.source());
            linkNBT.putUUID("to", edge.target());
            linksNBT.add(linkNBT);
        }

        nbt.put("rifts", riftsNBT);
        nbt.put("links", linksNBT);

        return nbt;
    }

    public boolean isRiftAt(Location location) {
        Rift possibleRift = this.locationMap.get(location);
        return possibleRift != null && !(possibleRift instanceof RiftPlaceholder);
    }

    public Rift getRift(Location location) {
        Rift rift = this.locationMap.get(location);
        if (rift == null) throw new IllegalArgumentException("There is no rift registered at " + location);
        return rift;
    }

    public Rift getRift(UUID id) {
        Rift rift = this.locationMap.values().stream()
                .filter(candidate -> candidate.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (rift == null || rift instanceof RiftPlaceholder) {
            throw new IllegalArgumentException("There is no rift registered with id " + id);
        }
        return rift;
    }

    public Optional<Rift> findRift(UUID id) {
        return this.locationMap.values().stream()
                .filter(rift -> rift.getId().equals(id))
                .filter(rift -> !(rift instanceof RiftPlaceholder))
                .findFirst();
    }

    Rift getRiftOrPlaceholder(Location location) {
        Rift rift = this.locationMap.get(location);
        if (rift == null) {
            LOGGER.debug("Creating a rift placeholder at " + location);
            rift = new RiftPlaceholder();
            rift.setWorld(location.world);
            rift.setLocation(location);
            this.locationMap.put(location, rift);
            RiftGraph.getInstance().addVertex(rift);

            setDirty();
        }
        return rift;
    }

    public void moveRift(Location oldLocation, Location newLocation) {
        this.moveRifts(Map.of(oldLocation, newLocation));
    }

    public void moveRifts(Map<Location, Location> movements) {
        Map<Location, Location> filteredMovements = new LinkedHashMap<>();
        for (Map.Entry<Location, Location> entry : movements.entrySet()) {
            if (!entry.getKey().equals(entry.getValue())) {
                filteredMovements.put(entry.getKey(), entry.getValue());
            }
        }

        if (filteredMovements.isEmpty()) {
            return;
        }

        LOGGER.debug("Moving rifts " + filteredMovements);

        Set<Location> oldLocations = filteredMovements.keySet();
        Set<Location> newLocations = new HashSet<>();
        for (Location newLocation : filteredMovements.values()) {
            if (!newLocations.add(newLocation)) {
                throw new IllegalArgumentException("Multiple rifts are moving to " + newLocation);
            }
            if (this.locationMap.containsKey(newLocation) && !oldLocations.contains(newLocation)) {
                throw new IllegalArgumentException("There is already a rift registered at " + newLocation);
            }
        }

        Map<Location, Rift> movedRifts = new LinkedHashMap<>();
        for (Location oldLocation : oldLocations) {
            movedRifts.put(oldLocation, this.getRift(oldLocation));
        }

        for (Location oldLocation : oldLocations) {
            this.locationMap.remove(oldLocation);
        }

        for (Map.Entry<Location, Rift> entry : movedRifts.entrySet()) {
            Location newLocation = filteredMovements.get(entry.getKey());
            Rift rift = entry.getValue();

            this.locationMap.put(newLocation, rift);
            rift.setWorld(newLocation.world);
            rift.setLocation(newLocation);
        }

        for (Rift rift : movedRifts.values()) {
            for (UUID sourceId : Set.copyOf(RiftGraph.getInstance().sources(rift))) {
                this.findRift(sourceId).ifPresent(source -> source.targetMoved(rift));
            }
            for (UUID targetId : Set.copyOf(RiftGraph.getInstance().targets(rift))) {
                this.findRift(targetId).ifPresent(target -> target.sourceMoved(rift));
            }

            rift.markDirty();
        }

        setDirty();
    }

    public void addRift(Location location) {
        LOGGER.debug("Adding rift at " + location);
        RegistryVertex currentRift = this.locationMap.get(location);
        Rift rift;
        if (currentRift instanceof RiftPlaceholder) {
            LOGGER.info("Converting a rift placeholder at " + location + " into a rift");
            rift = new Rift(location);
            rift.id = currentRift.id;
        } else if (currentRift == null) {
            rift = new Rift(location);
        } else {
            throw new IllegalArgumentException("There is already a rift registered at " + location);
        }
        RiftGraph.getInstance().addVertex(rift);
        this.locationMap.put(location, rift);
        rift.markDirty();

        setDirty();
    }

    public void removeRift(Location location) {
        LOGGER.debug("Removing rift at " + location);

        Rift rift = this.getRift(location);

        Set<UUID> sources = RiftGraph.getInstance().sources(rift);
        Set<UUID> targets = RiftGraph.getInstance().targets(rift);

        RiftGraph.getInstance().removeVertex(rift);
        this.locationMap.remove(location);

        // Notify the adjacent vertices of the change
        for (UUID sourceId : sources) this.findRift(sourceId).ifPresent(source -> source.targetGone(rift));
        for (UUID targetId : targets) this.findRift(targetId).ifPresent(target -> target.sourceGone(rift));

        setDirty();
    }


    private void addEdge(RegistryVertex from, RegistryVertex to) {
        RiftGraph.getInstance().addEdge(from, to);

        if (from instanceof Rift) {
            ((Rift) from).markDirty();
        }
        if (to instanceof Rift) {
            ((Rift) to).markDirty();
        }

    }

    private void removeEdge(RegistryVertex from, RegistryVertex to) {
        RiftGraph.getInstance().removeEdge(from, to);
        setDirty();
    }

    public void addLink(Location locationFrom, Location locationTo) {
        LOGGER.debug("Adding link " + locationFrom + " -> " + locationTo);

        Rift from = this.getRiftOrPlaceholder(locationFrom);
        Rift to = this.getRiftOrPlaceholder(locationTo);

        this.addEdge(from, to);

        // Notify the linked vertices of the change
        if (!(from instanceof RiftPlaceholder) && !(to instanceof RiftPlaceholder)) {
            from.targetAdded(to);
            to.sourceAdded(from);
        }

        setDirty();
    }

    public void removeLink(Location locationFrom, Location locationTo) {
        LOGGER.debug("Removing link " + locationFrom + " -> " + locationTo);

        Rift from = this.getRift(locationFrom);
        Rift to = this.getRift(locationTo);

        this.removeEdge(from, to);

        // Notify the linked vertices of the change
        from.targetGone(to);
        to.sourceGone(from);
        setDirty();
    }

    public void setProperties(Location location, LinkProperties properties) {
        LOGGER.debug("Setting DungeonLinkProperties for rift at " + location + " to " + properties);
        Rift rift = this.getRift(location);
        rift.setProperties(properties);
        rift.markDirty();
        setDirty();
    }

    public Collection<Rift> getRifts() {
        return this.locationMap.values();
    }

    public Set<Location> getTargets(Location location) {
        return RiftGraph.getInstance().targets(this.getRift(location)).stream()
                .map(this::findRift)
                .flatMap(Optional::stream)
                .map(Rift::getLocation)
                .collect(Collectors.toSet());
    }

    public Set<Location> getSources(Location location) {
        return RiftGraph.getInstance().sources(this.getRift(location)).stream()
                .map(this::findRift)
                .flatMap(Optional::stream)
                .map(Rift::getLocation)
                .collect(Collectors.toSet());
    }

}
