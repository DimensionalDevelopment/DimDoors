package org.dimdev.dimdoors.rift.registry;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.GraphUtils;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
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
	//I know this is sorta hacky, but overworldRifts can't be set for some reason it doesn't think that the rift location exists.
	//TODO: Fix this shit so that u can use overworldRifts instead of overworldLocations. NVM this is better cause we can teleport to locations that aren't rifts.
	protected Map<UUID, Location> overworldLocations = new HashMap<>();
	public static RiftRegistry fromNbt(Map<RegistryKey<World>, PocketDirectory> pocketRegistry, NbtCompound nbt) {
		// Read rifts in this dimension

		RiftRegistry riftRegistry = new RiftRegistry();

		NbtList riftsNBT = nbt.getList("rifts", NbtType.COMPOUND);
		String riftTypeId = RegistryVertex.registry.getId(RegistryVertex.RegistryVertexType.RIFT).toString();
		CompletableFuture<List<Rift>> futureRifts = CompletableFuture.supplyAsync(() -> riftsNBT.parallelStream().unordered().map(NbtCompound.class::cast).filter(nbtCompound -> nbtCompound.getString("type").equals(riftTypeId)).map(Rift::fromNbt).collect(Collectors.toList()));

		NbtList pocketsNBT = nbt.getList("pockets", NbtType.COMPOUND);
		CompletableFuture<List<PocketEntrancePointer>> futurePockets = CompletableFuture.supplyAsync(() -> pocketsNBT.stream().map(NbtCompound.class::cast).map(PocketEntrancePointer::fromNbt).collect(Collectors.toList()));

		futureRifts.join().forEach(rift -> {
			riftRegistry.graph.addVertex(rift);
			riftRegistry.uuidMap.put(rift.id, rift);
			riftRegistry.locationMap.put(rift.getLocation(), rift);
		});

		futurePockets.join().forEach(pocket -> {
			riftRegistry.graph.addVertex(pocket);
			riftRegistry.uuidMap.put(pocket.id, pocket);
			riftRegistry.pocketEntranceMap.put(pocketRegistry.get(pocket.getWorld()).getPocket(pocket.getPocketId()), pocket);
		});

		// Read the connections between links that have a source or destination in this dimension
		NbtList linksNBT = nbt.getList("links", NbtType.COMPOUND);
		for (NbtElement linkNBT : linksNBT) {
			RegistryVertex from = riftRegistry.uuidMap.get(((NbtCompound) linkNBT).getUuid("from"));
			RegistryVertex to = riftRegistry.uuidMap.get(((NbtCompound) linkNBT).getUuid("to"));
			if (from != null && to != null) {
				riftRegistry.graph.addEdge(from, to);
				// We need a system for detecting links that are incomplete after processing them in the other subregistry too
			}
		}

		riftRegistry.lastPrivatePocketEntrances = riftRegistry.readPlayerRiftPointers(nbt.getList("last_private_pocket_entrances", NbtType.COMPOUND));
		riftRegistry.lastPrivatePocketExits = riftRegistry.readPlayerRiftPointers(nbt.getList("last_private_pocket_exits", NbtType.COMPOUND));
		riftRegistry.overworldRifts = riftRegistry.readPlayerRiftPointers(nbt.getList("overworld_rifts", NbtType.COMPOUND));
		return riftRegistry;
	}

	public NbtCompound toNbt() {
		NbtCompound nbt = new NbtCompound();
		// Write rifts in this dimension
		CompletableFuture<Pair<NbtList, NbtList>> futureRiftsAndPocketsNBT = CompletableFuture.supplyAsync(() -> {
			Map<Boolean, List<RegistryVertex>> vertices = this.graph.vertexSet().parallelStream().unordered().filter(vertex -> vertex instanceof Rift || vertex instanceof PocketEntrancePointer)
					.collect(Collectors.partitioningBy(Rift.class::isInstance));

			CompletableFuture<List<NbtCompound>> futureRiftsNBT = CompletableFuture.supplyAsync(() -> vertices.get(true).parallelStream().map(RegistryVertex::toNbt).collect(Collectors.toList()));
			CompletableFuture<List<NbtCompound>> futurePocketsNBT = CompletableFuture.supplyAsync(() -> vertices.get(false).parallelStream().map(RegistryVertex::toNbt).collect(Collectors.toList()));

			NbtList riftsNBT = new NbtList();
			NbtList pocketsNBT = new NbtList();

			riftsNBT.addAll(futureRiftsNBT.join());
			pocketsNBT.addAll(futurePocketsNBT.join());

			return new Pair<>(riftsNBT, pocketsNBT);
		});


		// Write the connections between links that have a source or destination in this dimension
		CompletableFuture<NbtList> futureLinksNBT = CompletableFuture.supplyAsync(() -> {
			NbtList linksNBT = new NbtList();
			for (DefaultEdge edge : this.graph.edgeSet()) {
				RegistryVertex from = this.graph.getEdgeSource(edge);
				RegistryVertex to = this.graph.getEdgeTarget(edge);
				NbtCompound linkNBT = new NbtCompound();
				linkNBT.putUuid("from", from.id);
				linkNBT.putUuid("to", to.id);
				linksNBT.add(linkNBT);
			}
			return linksNBT;
		});


		// Subregistries are written automatically when the worlds are saved.
		nbt.put("last_private_pocket_entrances", this.writePlayerRiftPointers(this.lastPrivatePocketEntrances));
		nbt.put("last_private_pocket_exits", this.writePlayerRiftPointers(this.lastPrivatePocketExits));
		nbt.put("overworld_rifts", this.writePlayerRiftPointers(this.overworldRifts));

		Pair<NbtList, NbtList> riftsAndPocketsNBT = futureRiftsAndPocketsNBT.join();
		nbt.put("rifts", riftsAndPocketsNBT.getLeft());
		nbt.put("pockets", riftsAndPocketsNBT.getRight());

		nbt.put("links", futureLinksNBT.join());

		return nbt;
	}

	// TODO: parallelization
	private Map<UUID, PlayerRiftPointer> readPlayerRiftPointers(NbtList nbt) {
		Map<UUID, PlayerRiftPointer> pointerMap = new HashMap<>();
		for (NbtElement entryNBT : nbt) {
			UUID player = ((NbtCompound) entryNBT).getUuid("player");
			UUID rift = ((NbtCompound) entryNBT).getUuid("rift");
			PlayerRiftPointer pointer = new PlayerRiftPointer(player);
			pointerMap.put(player, pointer);
			this.uuidMap.put(pointer.id, pointer);
			this.graph.addVertex(pointer);
			this.graph.addEdge(pointer, this.uuidMap.get(rift));
		}
		return pointerMap;
	}

	// TODO: parallelization
	private NbtList writePlayerRiftPointers(Map<UUID, PlayerRiftPointer> playerRiftPointerMap) {
		NbtList pointers = new NbtList();
		for (Map.Entry<UUID, PlayerRiftPointer> entry : playerRiftPointerMap.entrySet()) {
			NbtCompound entryNBT = new NbtCompound();
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
			rift.setWorld(location.world);
			rift.setLocation(location);
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
		rift.setProperties(properties);
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
					.map(Rift::getLocation)
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
					pointer.setWorld(pocket.getWorld());
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
		return entrance != null ? entrance.getLocation() : null;
	}

	public void setLastPrivatePocketExit(UUID playerUUID, Location rift) {
		LOGGER.debug("Setting last used private pocket exit for " + playerUUID + " at " + rift);
		this.setPlayerRiftPointer(playerUUID, rift, this.lastPrivatePocketExits);
	}

	public Location getOverworldRift(UUID playerUUID) {
		/*
		PlayerRiftPointer entrancePointer = this.overworldRifts.get(playerUUID);
		Rift rift = (Rift) GraphUtils.followPointer(this.graph, entrancePointer);
		for (int i = 0; i < 10; i++) {
			if (rift == null) {
				LOGGER.log(Level.ERROR, "rift is null for getOverworldRift");
			} else {
				LOGGER.log(Level.INFO, "rift location " + rift.getLocation().getWorld() + " and pos " + rift.getLocation().pos.toString());
			}
		}
		return rift != null ? rift.getLocation() : null;
		 */

		return overworldLocations.get(playerUUID);
	}

	public void setOverworldRift(UUID playerUUID, Location rift) {
		/*
		LOGGER.log(Level.INFO, "Setting last used overworld rift for " + playerUUID + " at " + rift.getWorld() + " pos at " + rift.getBlockPos());
		this.setPlayerRiftPointer(playerUUID, rift, this.overworldRifts);
		 */
		overworldLocations.put(playerUUID, rift);
	}

	public Collection<Rift> getRifts() {
		return this.locationMap.values();
	}

	public Set<Location> getTargets(Location location) {
		return this.graph.outgoingEdgesOf(this.getRift(location)).stream()
				.map(this.graph::getEdgeTarget)
				.map(Rift.class::cast)
				.map(rift -> rift.getLocation())
				.collect(Collectors.toSet());
	}

	public Set<Location> getSources(Location location) {
		return this.graph.incomingEdgesOf(this.getRift(location)).stream()
				.map(this.graph::getEdgeTarget)
				.map(Rift.class::cast)
				.map(rift -> rift.getLocation())
				.collect(Collectors.toSet());
	}
}
