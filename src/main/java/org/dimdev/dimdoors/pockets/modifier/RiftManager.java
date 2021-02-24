package org.dimdev.dimdoors.pockets.modifier;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.rift.targets.IdMarker;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class RiftManager {
	private final Map<Integer, RiftBlockEntity> map;
	private final List<RiftBlockEntity> rifts;
	private final Pocket pocket;
	private int maxId;

	public RiftManager(Pocket pocket, boolean skipGatheringRifts) {
		this.pocket = pocket;
		if (skipGatheringRifts) {
			map = new HashMap<>();
			rifts = new ArrayList<>();
			return;
		}
		rifts = pocket.getBlockEntities().values().stream()
				.filter(RiftBlockEntity.class::isInstance).map(RiftBlockEntity.class::cast).collect(Collectors.toList());
		map = rifts.stream()
				.filter(a -> a.getData().getDestination() instanceof IdMarker)
				.filter(a -> ((IdMarker) a.getData().getDestination()).getId() >= 0)
				.collect(Collectors.toMap(rift -> ((IdMarker) rift.getData().getDestination()).getId(), rift -> rift));
		maxId = map.keySet().stream()
				.mapToInt(a -> a)
				.max()
				.orElse(-1);
	}

	public RiftManager(Pocket pocket) {
		this(pocket, false);
	}

	//TODO add javadocs
	public boolean add(RiftBlockEntity rift) {
		rifts.add(rift);
		if(rift.getData().getDestination() instanceof IdMarker) {
			int id = ((IdMarker) rift.getData().getDestination()).getId();

			if(id < 0) return false;

			map.put(id, rift);

			maxId = Math.max(id, maxId);

			return true;
		}
		return false;
	}

	public boolean consume(int id, Predicate<RiftBlockEntity> consumer) {
		if (map.containsKey(id) && consumer.test(map.get(id))) {
			map.remove(id);
			return true;
		}
		return false;
	}

	public Pocket getPocket() {
		return pocket;
	}

	public int nextId() {
		return maxId + 1;
	}

	public boolean available(int id) { // TODO: remove? method is likely redundant
		return !map.containsKey(id);
	}

	public void foreachConsume(BiPredicate<Integer, RiftBlockEntity> consumer) {
		for(int id : new HashSet<>(map.keySet())) {
			if(consumer.test(id, map.get(id))) {
				map.remove(id);
			}
		}
	}

	public Optional<RiftBlockEntity> get(int id) {
		return Optional.ofNullable(map.get(id));
	}

	public List<RiftBlockEntity> getRifts() {
		return rifts;
	}

	public boolean isPocketLazy() {
		return pocket instanceof LazyGenerationPocket;
	}
}
