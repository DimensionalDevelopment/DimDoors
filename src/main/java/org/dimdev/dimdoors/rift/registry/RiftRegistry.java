package org.dimdev.dimdoors.rift.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.util.GraphUtils;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.world.level.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class RiftRegistry {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String DATA_NAME = "rifts";

	protected DefaultDirectedGraph<RegistryVertex, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
	protected Map<Location, Rift> locationMap = new HashMap<>();
	protected Map<Pocket, PocketEntrancePointer> pocketEntranceMap = new HashMap<>();
	protected Map<UUID, RegistryVertex> uuidMap = new HashMap<>();

	protected Map<UUID, PlayerRiftPointer> lastPrivatePocketEntrances = new HashMap<>(); // Player UUID -> last rift used to exit pocket
	protected Map<UUID, PlayerRiftPointer> lastPrivatePocketExits = new HashMap<>(); // Player UUID -> last rift used to enter pocket
	protected Map<UUID, PlayerRiftPointer> overworldRifts = new HashMap<>(); // Player UUID -> rift used to exit the overworld

	// TODO: async
	public static RiftRegistry fromTag(Map<RegistryKey<World>, PocketDirectory> pocketRegistry, CompoundTag nbt) {
		// Read rifts in this dimension

		RiftRegistry riftRegistry = new RiftRegistry();

		ListTag riftsNBT = (ListTag) nbt.get("rifts");
		for (Tag riftNBT : riftsNBT) {
			Rift rift = Rift.fromTag((CompoundTag) riftNBT);
			riftRegistry.graph.addVertex(rift);
			riftRegistry.uuidMap.put(rift.id, rift);
			riftRegistry.locationMap.put(rift.location, rift);
		}

		ListTag pocketsNBT = (ListTag) nbt.get("pockets");
		for (Tag pocketNBT : pocketsNBT) {
			PocketEntrancePointer pocket = PocketEntrancePointer.fromTag((CompoundTag) pocketNBT);
			riftRegistry.graph.addVertex(pocket);
			riftRegistry.uuidMap.put(pocket.id, pocket);
			riftRegistry.pocketEntranceMap.put(pocketRegistry.get(pocket.world).getPocket(pocket.pocketId), pocket);
		}

		// Read the connections between links that have a source or destination in this dimension
		ListTag linksNBT = (ListTag) nbt.get("links");
		for (Tag linkNBT : linksNBT) {
			RegistryVertex from = riftRegistry.uuidMap.get(((CompoundTag) linkNBT).getUuid("from"));
			RegistryVertex to = riftRegistry.uuidMap.get(((CompoundTag) linkNBT).getUuid("to"));
			if (from != null && to != null) {
				riftRegistry.graph.addEdge(from, to);
				// We need a system for detecting links that are incomplete after processing them in the other subregistry too
			}
		}

		riftRegistry.lastPrivatePocketEntrances = riftRegistry.readPlayerRiftPointers((ListTag) nbt.get("lastPrivatePocketEntrances"));
		riftRegistry.lastPrivatePocketExits = riftRegistry.readPlayerRiftPointers((ListTag) nbt.get("lastPrivatePocketExits"));
		riftRegistry.overworldRifts = riftRegistry.readPlayerRiftPointers((ListTag) nbt.get("overworldRifts"));
		return riftRegistry;
	}

	// TODO: async
	public CompoundTag toTag() {
		CompoundTag tag = new CompoundTag();
		// Write rifts in this dimension
		ListTag riftsNBT = new ListTag();
		ListTag pocketsNBT = new ListTag();
		for (RegistryVertex vertex : this.graph.vertexSet()) {
			CompoundTag vertexNBT = RegistryVertex.toTag(vertex);
			if (vertex instanceof Rift) {
				riftsNBT.add(vertexNBT);
			} else if (vertex instanceof PocketEntrancePointer) {
				pocketsNBT.add(vertexNBT);
			} else if (!(vertex instanceof PlayerRiftPointer)) {
				throw new RuntimeException("Unsupported registry vertex type " + vertex.getClass().getName());
			}
		}
		tag.put("rifts", riftsNBT);
		tag.put("pockets", pocketsNBT);

		// Write the connections between links that have a source or destination in this dimension
		ListTag linksNBT = new ListTag();
		for (DefaultEdge edge : this.graph.edgeSet()) {
			RegistryVertex from = this.graph.getEdgeSource(edge);
			RegistryVertex to = this.graph.getEdgeTarget(edge);
			CompoundTag linkNBT = new CompoundTag();
			linkNBT.putUuid("from", from.id);
			linkNBT.putUuid("to", to.id);
			linksNBT.add(linkNBT);
		}
		tag.put("links", linksNBT);

		// Subregistries are written automatically when the worlds are saved.
		tag.put("lastPrivatePocketEntrances", this.writePlayerRiftPointers(this.lastPrivatePocketEntrances));
		tag.put("lastPrivatePocketExits", this.writePlayerRiftPointers(this.lastPrivatePocketExits));
		tag.put("overworldRifts", this.writePlayerRiftPointers(this.overworldRifts));

		return tag;
	}

	private Map<UUID, PlayerRiftPointer> readPlayerRiftPointers(ListTag tag) {
		Map<UUID, PlayerRiftPointer> pointerMap = new HashMap<>();
		for (Tag entryNBT : tag) {
			UUID player = ((CompoundTag) entryNBT).getUuid("player");
			UUID rift = ((CompoundTag) entryNBT).getUuid("rift");
			PlayerRiftPointer pointer = new PlayerRiftPointer(player);
			pointerMap.put(player, pointer);
			this.uuidMap.put(pointer.id, pointer);
			this.graph.addVertex(pointer);
			this.graph.addEdge(pointer, this.uuidMap.get(rift));
		}
		return pointerMap;
	}

	private ListTag writePlayerRiftPointers(Map<UUID, PlayerRiftPointer> playerRiftPointerMap) {
		ListTag pointers = new ListTag();
		for (Map.Entry<UUID, PlayerRiftPointer> entry : playerRiftPointerMap.entrySet()) {
			CompoundTag entryNBT = new CompoundTag();
			entryNBT.putUuid("player", entry.getKey());
			int count = 0;
			for (DefaultEdge edge : this.graph.outgoingEdgesOf(entry.getValue())) {
				entryNBT.putUuid("rift", this.graph.getEdgeTarget(edge).id);
				count++;
			}
			if (count != 1) throw new RuntimeException("PlayerRiftPointer points to more than one rift");
			pointers.add(entryNBT);
		}
		return pointers;
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

	private Rift getRiftOrPlaceholder(Location location) {
		Rift rift = this.locationMap.get(location);
		if (rift == null) {
			LOGGER.debug("Creating a rift placeholder at " + location);
			rift = new RiftPlaceholder();
			rift.world = location.world;
			rift.location = location;
			this.locationMap.put(location, rift);
			this.uuidMap.put(rift.id, rift);
			this.graph.addVertex(rift);
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
			rift.world = location.world;
			rift.id = currentRift.id;
			GraphUtils.replaceVertex(this.graph, currentRift, rift);
		} else if (currentRift == null) {
			rift = new Rift(location);
			rift.world = location.world;
			this.graph.addVertex(rift);
		} else {
			throw new IllegalArgumentException("There is already a rift registered at " + location);
		}
		this.uuidMap.put(rift.id, rift);
		this.locationMap.put(location, rift);
		rift.markDirty();
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
	}

	public void removeLink(Location locationFrom, Location locationTo) {
		LOGGER.debug("Removing link " + locationFrom + " -> " + locationTo);

		Rift from = this.getRift(locationFrom);
		Rift to = this.getRift(locationTo);

		this.removeEdge(from, to);

		// Notify the linked vertices of the change
		from.targetGone(to);
		to.sourceGone(from);
	}

	public void setProperties(Location location, LinkProperties properties) {
		LOGGER.debug("Setting DungeonLinkProperties for rift at " + location + " to " + properties);
		Rift rift = this.getRift(location);
		rift.properties = properties;
		rift.markDirty();
	}

	public Set<Location> getPocketEntrances(Pocket pocket) {
		PocketEntrancePointer pointer = this.pocketEntranceMap.get(pocket);
		if (pointer == null) {
			return Collections.emptySet();
		} else {
			return this.graph.outgoingEdgesOf(pointer).stream()
					.map(this.graph::getEdgeTarget)
					.map(Rift.class::cast)
					.map(rift -> rift.location)
					.collect(Collectors.toSet());
		}
	}

	public Location getPocketEntrance(Pocket pocket) {
		Set<Location> entrances = this.getPocketEntrances(pocket);
		return entrances.stream()
				.findFirst()
				.orElse(null);
	}

	public void addPocketEntrance(Pocket pocket, Location location) {
		LOGGER.debug("Adding pocket entrance for pocket " + pocket.getId() + " in dimension " + pocket.getWorld() + " at " + location);

//		PocketEntrancePointer pointer = this.pocketEntranceMap.get(pocket);
//		if (pointer == null) {
//			pointer = new PocketEntrancePointer(pocket.world, pocket.id);
//			pointer.world = pocket.world;
//			this.graph.addVertex(pointer);
//			this.pocketEntranceMap.put(pocket, pointer);
//			this.uuidMap.put(pointer.id, pointer);
//		}
		this.addEdge(
				this.pocketEntranceMap.computeIfAbsent(pocket, p -> {
					PocketEntrancePointer pointer = new PocketEntrancePointer(pocket.getWorld(), pocket.getId());
					pointer.world = pocket.getWorld();
					this.graph.addVertex(pointer);
					this.uuidMap.put(pointer.id, pointer);
					return pointer;
				}),
				this.getRift(location)
		);
	}

	public Location getPrivatePocketEntrance(UUID playerUUID) {
		// Try to get the last used entrance
		PlayerRiftPointer entrancePointer = this.lastPrivatePocketEntrances.get(playerUUID);
		Rift entrance = (Rift) GraphUtils.followPointer(this.graph, entrancePointer);
		if (entrance != null) return entrance.location;

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
			pointer = new PlayerRiftPointer(playerUUID);
			this.graph.addVertex(pointer);
			map.put(playerUUID, pointer);
			this.uuidMap.put(pointer.id, pointer);
			this.addEdge(pointer, this.getRift(rift));
		}
	}

	public void setLastPrivatePocketEntrance(UUID playerUUID, Location rift) {
		LOGGER.debug("Setting last used private pocket entrance for " + playerUUID + " at " + rift);
		this.setPlayerRiftPointer(playerUUID, rift, this.lastPrivatePocketEntrances);
	}

	public Location getPrivatePocketExit(UUID playerUUID) {
		PlayerRiftPointer entrancePointer = this.lastPrivatePocketExits.get(playerUUID);
		Rift entrance = (Rift) GraphUtils.followPointer(this.graph, entrancePointer);
		return entrance != null ? entrance.location : null;
	}

	public void setLastPrivatePocketExit(UUID playerUUID, Location rift) {
		LOGGER.debug("Setting last used private pocket exit for " + playerUUID + " at " + rift);
		this.setPlayerRiftPointer(playerUUID, rift, this.lastPrivatePocketExits);
	}

	public Location getOverworldRift(UUID playerUUID) {
		PlayerRiftPointer entrancePointer = this.overworldRifts.get(playerUUID);
		Rift rift = (Rift) GraphUtils.followPointer(this.graph, entrancePointer);
		return rift != null ? rift.location : null;
	}

	public void setOverworldRift(UUID playerUUID, Location rift) {
		LOGGER.debug("Setting last used overworld rift for " + playerUUID + " at " + rift);
		this.setPlayerRiftPointer(playerUUID, rift, this.overworldRifts);
	}

	public Collection<Rift> getRifts() {
		return this.locationMap.values();
	}

	public Set<Location> getTargets(Location location) {
		return this.graph.outgoingEdgesOf(this.getRift(location)).stream()
				.map(this.graph::getEdgeTarget)
				.map(Rift.class::cast)
				.map(rift -> rift.location)
				.collect(Collectors.toSet());
	}

	public Set<Location> getSources(Location location) {
		return this.graph.incomingEdgesOf(this.getRift(location)).stream()
				.map(this.graph::getEdgeTarget)
				.map(Rift.class::cast)
				.map(rift -> rift.location)
				.collect(Collectors.toSet());
	}
}
