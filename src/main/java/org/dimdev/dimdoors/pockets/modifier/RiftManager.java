package org.dimdev.dimdoors.pockets.modifier;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.rift.targets.IdMarker;
import org.dimdev.dimdoors.world.pocket.Pocket;

public class RiftManager {
	private final Map<Integer, RiftBlockEntity> map;
	private final Pocket pocket;
	private int maxId;

	public RiftManager(Pocket pocket) {
		this.pocket = pocket;
		map = pocket.getBlockEntities().values().stream()
				.filter(RiftBlockEntity.class::isInstance).map(RiftBlockEntity.class::cast)
				.filter(a -> a.getData().getDestination() instanceof IdMarker)
				.filter(a -> ((IdMarker) a.getData().getDestination()).getId() < 0)
				.collect(Collectors.toMap(rift -> ((IdMarker) rift.getData().getDestination()).getId(), rift -> rift));
		maxId = map.keySet().stream()
				.mapToInt(a -> a)
				.max()
				.orElse(-1);
	}

	//TODO add javadocs
	public boolean add(RiftBlockEntity rift) {
		if(rift.getData().getDestination() instanceof IdMarker) {
			int id = ((IdMarker) rift.getData().getDestination()).getId();

			if(id < 0) return false;

			map.put(id, rift);

			maxId = Math.max(id, maxId);

			return true;
		}

		return false;
	}

	public boolean consume(int id, Function<RiftBlockEntity, Boolean> consumer) {
		if (map.containsKey(id) && consumer.apply(map.get(id))) {
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
}
