package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.GraphUtils;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RiftRegistry {
	private static final Logger LOGGER = LogManager.getLogger();

	protected DefaultDirectedGraph<RegistryVertex, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
	protected Map<Location, Rift> locationMap = new HashMap<>();
	protected Map<UUID, PocketEntrancePointer> pocketEntranceMap = new HashMap<>();
	protected Map<UUID, RegistryVertex> uuidMap = new HashMap<>();

	protected Map<UUID, PlayerRiftPointer> lastPrivatePocketEntrances = new HashMap<>(); // Player UUID -> last rift used to exit pocket
	protected Map<UUID, PlayerRiftPointer> lastPrivatePocketExits = new HashMap<>(); // Player UUID -> last rift used to enter pocket

    public record Link(UUID from, UUID to) {
        public static final Codec<Link> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("from").forGetter(Link::from),
                UUIDUtil.CODEC.fieldOf("to").forGetter(Link::to)
        ).apply(instance, Link::new));
    }

    public record PlayerPrivateData(UUID player, UUID rift) {
        public static final Codec<PlayerPrivateData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("player").forGetter(PlayerPrivateData::player),
            UUIDUtil.CODEC.fieldOf("rift").forGetter(PlayerPrivateData::rift)
        ).apply(instance, PlayerPrivateData::new));
    }

    public record Serialized(List<Link> links, List<Rift> rifts, List<PocketEntrancePointer> pockets, List<PlayerPrivateData> privateEntrances, List<PlayerPrivateData> privateExits) {
        public static final Codec<Serialized> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Link.CODEC.listOf().fieldOf("links").forGetter(Serialized::links),
                RegistryVertex.CODEC.xmap(Rift.class::cast, Function.identity()).listOf().fieldOf("rifts").forGetter(Serialized::rifts),
                PocketEntrancePointer.CODEC.codec().listOf().fieldOf("pockets").forGetter(Serialized::pockets),
                PlayerPrivateData.CODEC.listOf().fieldOf("last_private_pocket_entrances").forGetter(Serialized::privateEntrances),
                PlayerPrivateData.CODEC.listOf().fieldOf("last_private_pocket_exits").forGetter(Serialized::privateExits)
        ).apply(instance, Serialized::new));
    }

    public static final Codec<RiftRegistry> CODEC = Serialized.CODEC.xmap(RiftRegistry::new, RiftRegistry::toSerialized);

    public RiftRegistry() {

    }

    public RiftRegistry(Serialized serialized) {

        for(var rift : serialized.rifts()) {
            graph.addVertex(rift);
            uuidMap.put(rift.id, rift);
            locationMap.put(rift.getLocation(), rift);
        }

        for (var pocket : serialized.pockets()) {
            graph.addVertex(pocket);
            uuidMap.put(pocket.id, pocket);

            pocketEntranceMap.put(pocket.getId(), pocket);
        }

        for (var link : serialized.links()) {
            RegistryVertex from = uuidMap.get(link.from());
            RegistryVertex to = uuidMap.get(link.to());
            if (from != null && to != null) {
                graph.addEdge(from, to);
            }
        }

        readPlayerRiftPointers(lastPrivatePocketEntrances, serialized.privateEntrances());
        readPlayerRiftPointers(lastPrivatePocketExits, serialized.privateExits());
    }

    private Serialized toSerialized() {
        var rifts = new ArrayList<Rift>();
        var pockets = new ArrayList<PocketEntrancePointer>();

        for (RegistryVertex vertex : this.graph.vertexSet()) {
            if (vertex instanceof Rift rift) rifts.add(rift);
            else if(vertex instanceof PocketEntrancePointer entrance) pockets.add(entrance);
        }

        List<Link> links = new ArrayList<>();
        for (DefaultEdge edge : this.graph.edgeSet()) {
            RegistryVertex from = this.graph.getEdgeSource(edge);
            RegistryVertex to = this.graph.getEdgeTarget(edge);
            links.add(new Link(from.id, to.id));
        }

        var entrances = writePlayerRiftPointers(lastPrivatePocketEntrances);
        var exits = writePlayerRiftPointers(lastPrivatePocketExits);

        return new Serialized(links, rifts, pockets, entrances, exits);
    }

    private void readPlayerRiftPointers(Map<UUID, PlayerRiftPointer> map, List<PlayerPrivateData> data) {
        for(var d : data) {
            RegistryVertex riftVertex = this.uuidMap.get(d.rift());

            if (riftVertex != null) {
                PlayerRiftPointer pointer = new PlayerRiftPointer();
                map.put(d.player(), pointer);
                this.uuidMap.put(pointer.id, pointer);
                this.graph.addVertex(pointer);
                this.graph.addEdge(pointer, riftVertex);
            }
        }
    }

    private List<PlayerPrivateData> writePlayerRiftPointers(Map<UUID, PlayerRiftPointer> playerRiftPointerMap) {
        List<PlayerPrivateData> pointers = new ArrayList<>();

        for (Map.Entry<UUID, PlayerRiftPointer> entry : playerRiftPointerMap.entrySet()) {
            Set<DefaultEdge> edges = this.graph.outgoingEdgesOf(entry.getValue());
            if (edges.size() != 1) throw new RuntimeException("PlayerRiftPointer points to " + edges.size() + " rifts, expected 1");
            DefaultEdge edge = edges.iterator().next();
            pointers.add(new PlayerPrivateData(entry.getKey(), this.graph.getEdgeTarget(edge).id));
        }

        return pointers;
    }

    public boolean isRiftAt(Location location) {
		return this.locationMap.containsKey(location);
	}

	public Rift getRift(Location location) {
		Rift rift = this.locationMap.get(location);
		if (rift == null) throw new IllegalArgumentException("There is no rift registered at " + location);
		return rift;
	}

	private Rift getRiftOrPlaceholder(Location location) {
		Rift rift = this.locationMap.get(location);
		if (rift == null) {
			LOGGER.debug("Creating a rift placeholder at " + location);
			rift = new RiftPlaceholder(location);
			this.locationMap.put(location, rift);
			this.uuidMap.put(rift.id, rift);
			this.graph.addVertex(rift);

            DimensionalRegistry.setDirty();
		}
		return rift;
	}

	public void addRift(Location location) {
		LOGGER.debug("Adding rift at " + location);
		RegistryVertex currentRift = this.locationMap.get(location);
		Rift rift;
		if (currentRift instanceof RiftPlaceholder) {
			LOGGER.info("Converting a rift placeholder at " + location + " into a rift");
			rift = new Rift(location);
			rift.id = currentRift.id;
			GraphUtils.replaceVertex(this.graph, currentRift, rift);
		} else if (currentRift == null) {
			rift = new Rift(location);
			this.graph.addVertex(rift);
		} else {
			throw new IllegalArgumentException("There is already a rift registered at " + location);
		}
		this.uuidMap.put(rift.id, rift);
		this.locationMap.put(location, rift);
		rift.markDirty();

        DimensionalRegistry.setDirty();
    }

	public void removeRift(Location location) {
		LOGGER.debug("Removing rift at " + location);

		Rift rift = this.getRift(location);

		Set<DefaultEdge> incomingEdges = this.graph.incomingEdgesOf(rift);
		Set<DefaultEdge> outgoingEdges = this.graph.outgoingEdgesOf(rift);

		this.graph.removeVertex(rift);
		this.locationMap.remove(location);
		this.uuidMap.remove(rift.id);

		// Notify the adjacent vertices of the change
		for (DefaultEdge edge : incomingEdges) this.graph.getEdgeSource(edge).targetGone(rift);
		for (DefaultEdge edge : outgoingEdges) this.graph.getEdgeTarget(edge).sourceGone(rift);

        DimensionalRegistry.setDirty();
    }

	private void addEdge(RegistryVertex from, RegistryVertex to) {
		this.graph.addEdge(from, to);

		if (from instanceof Rift) {
			((Rift) from).markDirty();
		}
		if (to instanceof Rift) {
			((Rift) to).markDirty();
		}

	}

	private void removeEdge(RegistryVertex from, RegistryVertex to) {
		this.graph.removeEdge(from, to);
        DimensionalRegistry.setDirty();
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

        DimensionalRegistry.setDirty();
    }

	public void removeLink(Location locationFrom, Location locationTo) {
		LOGGER.debug("Removing link " + locationFrom + " -> " + locationTo);

		Rift from = this.getRift(locationFrom);
		Rift to = this.getRift(locationTo);

		this.removeEdge(from, to);

		// Notify the linked vertices of the change
		from.targetGone(to);
		to.sourceGone(from);

        DimensionalRegistry.setDirty();
    }

	public void setProperties(Location location, LinkProperties properties) {
		LOGGER.debug("Setting DungeonLinkProperties for rift at " + location + " to " + properties);
		Rift rift = this.getRift(location);
		rift.setProperties(properties);
		rift.markDirty();
        DimensionalRegistry.setDirty();
    }

	public Set<Location> getPocketEntrances(UUID pocket) {
		PocketEntrancePointer pointer = this.pocketEntranceMap.get(pocket);
		if (pointer == null) {
			return Collections.emptySet();
		} else {
			return this.graph.outgoingEdgesOf(pointer).stream()
					.map(this.graph::getEdgeTarget)
					.map(Rift.class::cast)
					.map(Rift::getLocation)
					.collect(Collectors.toSet());
		}
	}

	public Location getPocketEntrance(UUID pocket) {
		Set<Location> entrances = this.getPocketEntrances(pocket);
		return entrances.stream()
				.findFirst()
				.orElse(null);
	}

	public void addPocketEntrance(UUID pocket, Location location) {
		LOGGER.debug("Adding pocket entrance for pocket " + pocket + " at " + location);

		this.addEdge(
				this.pocketEntranceMap.computeIfAbsent(pocket, p -> {
					PocketEntrancePointer pointer = new PocketEntrancePointer(pocket);
					this.graph.addVertex(pointer);
					this.uuidMap.put(pointer.id, pointer);
					return pointer;
				}),
				this.getRift(location)
		);

        DimensionalRegistry.setDirty();
    }

	public Location getPrivatePocketEntrance(UUID playerUUID) {
		// Try to get the last used entrance
		PlayerRiftPointer entrancePointer = this.lastPrivatePocketEntrances.get(playerUUID);
		Rift entrance = (Rift) GraphUtils.followPointer(this.graph, entrancePointer);
		if (entrance != null) return entrance.getLocation();

		// If there was no last used private entrance, get the first player's private pocket entrance
		return this.getPocketEntrance(DimensionalRegistry.getPrivateRegistry().getPrivatePocket(playerUUID));
	}

	private void setPlayerRiftPointer(UUID playerUUID, Location rift, Map<UUID, PlayerRiftPointer> map) {
		PlayerRiftPointer pointer = map.get(playerUUID);
		if (pointer != null) {
			this.graph.removeVertex(pointer);
			map.remove(playerUUID);
			this.uuidMap.remove(pointer.id);
		}
		if (rift != null) {
			pointer = new PlayerRiftPointer();
			this.graph.addVertex(pointer);
			map.put(playerUUID, pointer);
			this.uuidMap.put(pointer.id, pointer);
			this.addEdge(pointer, this.getRift(rift));
		}
	}

	public void setLastPrivatePocketEntrance(UUID playerUUID, Location rift) {
		LOGGER.debug("Setting last used private pocket entrance for " + playerUUID + " at " + rift);
		this.setPlayerRiftPointer(playerUUID, rift, this.lastPrivatePocketEntrances);
        DimensionalRegistry.setDirty();
    }

	public Location getPrivatePocketExit(UUID playerUUID) {
		PlayerRiftPointer entrancePointer = this.lastPrivatePocketExits.get(playerUUID);
		Rift entrance = (Rift) GraphUtils.followPointer(this.graph, entrancePointer);
		return entrance != null ? entrance.getLocation() : null;
	}

	public void setLastPrivatePocketExit(UUID playerUUID, Location rift) {
		LOGGER.debug("Setting last used private pocket exit for " + playerUUID + " at " + rift);
		this.setPlayerRiftPointer(playerUUID, rift, this.lastPrivatePocketExits);
        DimensionalRegistry.setDirty();
    }

	public Collection<Rift> getRifts() {
		return this.locationMap.values();
	}

	public Set<Location> getTargets(Location location) {
		return this.graph.outgoingEdgesOf(this.getRift(location)).stream()
				.map(this.graph::getEdgeTarget)
				.map(Rift.class::cast)
				.map(Rift::getLocation)
				.collect(Collectors.toSet());
	}

	public Set<Location> getSources(Location location) {
		return this.graph.incomingEdgesOf(this.getRift(location)).stream()
				.map(this.graph::getEdgeSource)
				.map(Rift.class::cast)
				.map(Rift::getLocation)
				.collect(Collectors.toSet());
	}
}