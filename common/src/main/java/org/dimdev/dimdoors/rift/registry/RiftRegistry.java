package org.dimdev.dimdoors.rift.registry;

import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.GraphUtils;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.StreamUtils;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

    public static RiftRegistry fromNbt(Map<ResourceKey<Level>, PocketDirectory> pocketRegistry, CompoundTag nbt) {
        RiftRegistry riftRegistry = new RiftRegistry();

        ListTag riftsNBT = nbt.getList("rifts", Tag.TAG_COMPOUND);
        String riftTypeId = RegistryVertex.REGISTRY.getId(RegistryVertex.RegistryVertexType.RIFT.get()).toString();

        for (Tag tag : riftsNBT) {
            CompoundTag compound = (CompoundTag) tag;
            if (compound.getString("type").equals(riftTypeId)) {
                Rift rift = Rift.fromNbt(compound);
                riftRegistry.graph.addVertex(rift);
                riftRegistry.uuidMap.put(rift.id, rift);
                riftRegistry.locationMap.put(rift.getLocation(), rift);
            }
        }

        ListTag pocketsNBT = nbt.getList("pockets", Tag.TAG_COMPOUND);
        for (Tag tag : pocketsNBT) {
            PocketEntrancePointer pocket = PocketEntrancePointer.fromNbt((CompoundTag) tag);
            riftRegistry.graph.addVertex(pocket);
            riftRegistry.uuidMap.put(pocket.id, pocket);

            PocketDirectory directory = pocketRegistry.get(pocket.getWorld());
            if (directory != null) {
                Pocket pocketObj = directory.getPocket(pocket.getPocketId());
                if (pocketObj != null) {
                    riftRegistry.pocketEntranceMap.put(pocketObj, pocket);
                }
            }
        }

        ListTag linksNBT = nbt.getList("links", Tag.TAG_COMPOUND);
        for (Tag linkNBT : linksNBT) {
            RegistryVertex from = riftRegistry.uuidMap.get(((CompoundTag) linkNBT).getUUID("from"));
            RegistryVertex to = riftRegistry.uuidMap.get(((CompoundTag) linkNBT).getUUID("to"));
            if (from != null && to != null) {
                riftRegistry.graph.addEdge(from, to);
            }
        }

        riftRegistry.lastPrivatePocketEntrances = riftRegistry.readPlayerRiftPointers(nbt.getList("last_private_pocket_entrances", Tag.TAG_COMPOUND));
        riftRegistry.lastPrivatePocketExits = riftRegistry.readPlayerRiftPointers(nbt.getList("last_private_pocket_exits", Tag.TAG_COMPOUND));
        riftRegistry.overworldRifts = riftRegistry.readPlayerRiftPointers(nbt.getList("overworld_rifts", Tag.TAG_COMPOUND));
        riftRegistry.overworldLocations = riftRegistry.readLocations(nbt.getList("overworld_locations", Tag.TAG_COMPOUND));

        return riftRegistry;
    }

    public CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();

        ListTag riftsNBT = new ListTag();
        ListTag pocketsNBT = new ListTag();

        for (RegistryVertex vertex : this.graph.vertexSet()) {
            var vertexNbt = RegistryVertex.toNbt(vertex);
            if (vertex instanceof Rift) riftsNBT.add(vertexNbt);
            else pocketsNBT.add(vertexNbt);
        }

        ListTag linksNBT = new ListTag();
        for (DefaultEdge edge : this.graph.edgeSet()) {
            RegistryVertex from = this.graph.getEdgeSource(edge);
            RegistryVertex to = this.graph.getEdgeTarget(edge);
            CompoundTag linkNBT = new CompoundTag();
            linkNBT.putUUID("from", from.id);
            linkNBT.putUUID("to", to.id);
            linksNBT.add(linkNBT);
        }

        nbt.put("last_private_pocket_entrances", this.writePlayerRiftPointers(this.lastPrivatePocketEntrances));
        nbt.put("last_private_pocket_exits", this.writePlayerRiftPointers(this.lastPrivatePocketExits));
        nbt.put("overworld_rifts", this.writePlayerRiftPointers(this.overworldRifts));
        nbt.put("overworld_locations", this.writeLocations(this.overworldLocations));

        nbt.put("rifts", riftsNBT);
        nbt.put("pockets", pocketsNBT);
        nbt.put("links", linksNBT);

        return nbt;
    }

    private Map<UUID, PlayerRiftPointer> readPlayerRiftPointers(ListTag nbt) {
        Map<UUID, PlayerRiftPointer> pointerMap = new HashMap<>();
        for (Tag entryNBT : nbt) {
            UUID player = ((CompoundTag) entryNBT).getUUID("player");
            UUID riftId = ((CompoundTag) entryNBT).getUUID("rift");
            RegistryVertex riftVertex = this.uuidMap.get(riftId);

            if (riftVertex != null) {
                PlayerRiftPointer pointer = new PlayerRiftPointer(player);
                pointerMap.put(player, pointer);
                this.uuidMap.put(pointer.id, pointer);
                this.graph.addVertex(pointer);
                this.graph.addEdge(pointer, riftVertex);
            }
        }
        return pointerMap;
    }

    private ListTag writePlayerRiftPointers(Map<UUID, PlayerRiftPointer> playerRiftPointerMap) {
        ListTag pointers = new ListTag();
        for (Map.Entry<UUID, PlayerRiftPointer> entry : playerRiftPointerMap.entrySet()) {
			PlayerRiftPointer pointer = entry.getValue();
			if (!this.graph.containsVertex(pointer)) {
				LOGGER.warn("Skipping player rift pointer for {} because it is no longer in the graph.", entry.getKey());
				continue;
			}

			Set<DefaultEdge> edges = this.graph.outgoingEdgesOf(pointer);
			if (edges.size() != 1) {
				LOGGER.warn("Skipping player rift pointer for {} because it points to {} targets.", entry.getKey(), edges.size());
				continue;
			}

			RegistryVertex target = this.graph.getEdgeTarget(edges.iterator().next());
			if (!(target instanceof Rift rift)) {
				LOGGER.warn("Skipping player rift pointer for {} because it does not target a rift.", entry.getKey());
				continue;
			}

            CompoundTag entryNBT = new CompoundTag();
            entryNBT.putUUID("player", entry.getKey());
            entryNBT.putUUID("rift", rift.id);
            pointers.add(entryNBT);
        }
        return pointers;
    }

    private Map<UUID, Location> readLocations(ListTag nbt) {
        Map<UUID, Location> map = new HashMap<>();
        for (Tag tag : nbt) {
            CompoundTag entry = (CompoundTag) tag;
            map.put(entry.getUUID("player"), Location.fromNbt(entry.getCompound("location")));
        }
        return map;
    }

    private ListTag writeLocations(Map<UUID, Location> map) {
        ListTag list = new ListTag();
        for (Map.Entry<UUID, Location> entry : map.entrySet()) {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("player", entry.getKey());
            tag.put("location", Location.toNbt(entry.getValue()));
            list.add(tag);
        }
        return list;
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

        DimensionalRegistry.setDirty();
    }

	public boolean removePocketReferences(Pocket pocket) {
		return this.removePocketReferences(pocket.getWorld(), pocket.getId());
	}

	public boolean removePocketReferences(ResourceKey<Level> world, int pocketId) {
		Set<PocketEntrancePointer> pointers = this.pocketEntranceMap.entrySet().stream()
				.filter(entry -> referencesPocket(entry.getKey(), world, pocketId))
				.map(Map.Entry::getValue)
				.collect(Collectors.toCollection(LinkedHashSet::new));

		boolean removed = this.pocketEntranceMap.entrySet().removeIf(entry -> referencesPocket(entry.getKey(), world, pocketId));

		this.graph.vertexSet().stream()
				.filter(PocketEntrancePointer.class::isInstance)
				.map(PocketEntrancePointer.class::cast)
				.filter(pointer -> referencesPocket(pointer, world, pocketId))
				.forEach(pointers::add);

		Set<Rift> affectedRifts = new HashSet<>();
		for (PocketEntrancePointer pointer : pointers) {
			if (this.graph.containsVertex(pointer)) {
				for (DefaultEdge edge : this.graph.incomingEdgesOf(pointer)) {
					RegistryVertex source = this.graph.getEdgeSource(edge);
					if (source instanceof Rift rift) affectedRifts.add(rift);
				}
				for (DefaultEdge edge : this.graph.outgoingEdgesOf(pointer)) {
					RegistryVertex target = this.graph.getEdgeTarget(edge);
					if (target instanceof Rift rift) affectedRifts.add(rift);
				}

				this.graph.removeVertex(pointer);
				removed = true;
			}

			if (this.uuidMap.remove(pointer.id) != null) {
				removed = true;
			}
		}

		affectedRifts.stream()
				.filter(this.graph::containsVertex)
				.forEach(Rift::markDirty);

		if (removed) {
			DimensionalRegistry.setDirty();
		}
		return removed;
	}

	private boolean referencesPocket(Pocket pocket, ResourceKey<Level> world, int pocketId) {
		return pocket.getId() == pocketId && Objects.equals(pocket.getWorld(), world);
	}

	private boolean referencesPocket(PocketEntrancePointer pointer, ResourceKey<Level> world, int pocketId) {
		return pointer.getPocketId() == pocketId && Objects.equals(pointer.getWorld(), world);
	}

	public Location getPrivatePocketEntrance(UUID playerUUID) {
		Pocket pocket = DimensionalRegistry.getPrivateRegistry().getPrivatePocket(playerUUID);
		if (pocket == null) return null;

		Rift entrance = this.getPointedRift(this.lastPrivatePocketEntrances.get(playerUUID));
		if (entrance != null && this.getPocketEntrances(pocket).contains(entrance.getLocation())) {
			return entrance.getLocation();
		}

		return this.getPocketEntrance(pocket);
	}

	private void setPlayerRiftPointer(UUID playerUUID, Location rift, Map<UUID, PlayerRiftPointer> map) {
		PlayerRiftPointer pointer = map.get(playerUUID);
		if (pointer != null) {
			this.graph.removeVertex(pointer);
			map.remove(playerUUID);
			this.uuidMap.remove(pointer.id);
		}
		if (rift != null) {
			RegistryVertex target = this.locationMap.get(rift);
			if (!(target instanceof Rift)) {
				LOGGER.warn("Skipping player rift pointer update for {} because {} is not registered.", playerUUID, rift);
				return;
			}
			if (target instanceof RiftPlaceholder) {
				LOGGER.warn("Skipping player rift pointer update for {} because {} only resolves to a placeholder.", playerUUID, rift);
				return;
			}

			pointer = new PlayerRiftPointer(playerUUID);
			this.graph.addVertex(pointer);
			map.put(playerUUID, pointer);
			this.uuidMap.put(pointer.id, pointer);
			this.addEdge(pointer, (Rift) target);
		}
	}

	public void setLastPrivatePocketEntrance(UUID playerUUID, Location rift) {
		LOGGER.debug("Setting last used private pocket entrance for " + playerUUID + " at " + rift);
		this.setPlayerRiftPointer(playerUUID, rift, this.lastPrivatePocketEntrances);
        DimensionalRegistry.setDirty();
    }

	public Location getPrivatePocketExit(UUID playerUUID) {
		Rift entrance = this.getPointedRift(this.lastPrivatePocketExits.get(playerUUID));
		return entrance != null ? entrance.getLocation() : null;
	}

	public void setLastPrivatePocketExit(UUID playerUUID, Location rift) {
		LOGGER.debug("Setting last used private pocket exit for " + playerUUID + " at " + rift);
		this.setPlayerRiftPointer(playerUUID, rift, this.lastPrivatePocketExits);
        DimensionalRegistry.setDirty();
    }

	public Location getOverworldRift(UUID playerUUID) {
		Rift rift = this.getPointedRift(this.overworldRifts.get(playerUUID));
		return rift != null ? rift.getLocation() : null;
	}

	public void setOverworldRift(UUID playerUUID, Location rift) {

        this.setPlayerRiftPointer(playerUUID, rift, this.overworldRifts);
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

	private Rift getPointedRift(PlayerRiftPointer pointer) {
		RegistryVertex target = GraphUtils.followPointer(this.graph, pointer);
		return target instanceof Rift rift ? rift : null;
	}
}
