package org.dimdev.dimdoors.item.door.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.item.door.data.condition.Condition;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;

import java.util.List;
import java.util.Optional;

public class RiftDataList {
    public static final Codec<RiftDataList> CODEC = Codec.pair(OptRiftData.CODEC, Condition.CODEC).listOf().xmap(RiftDataList::new, riftDataList -> riftDataList.riftDataConditions);

	private final List<Pair<OptRiftData, Condition>> riftDataConditions;

    public RiftDataList(List<Pair<OptRiftData, Condition>> riftDataConditions) {
		this.riftDataConditions = riftDataConditions;
	}

	public OptRiftData getRiftData(EntranceRiftBlockEntity rift) {
		return riftDataConditions.stream().filter(pair -> pair.getSecond().matches(rift)).findFirst().orElseThrow(() -> new RuntimeException("Could not find any matching rift data")).getFirst();
	}

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static class OptRiftData {
        public static final Codec<OptRiftData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                VirtualTarget.CODEC.fieldOf("destination").forGetter(OptRiftData::getDestination),
                LinkProperties.CODEC.optionalFieldOf("properties").forGetter(OptRiftData::getProperties)
        ).apply(instance, OptRiftData::new));

		private final VirtualTarget destination;
		private final Optional<LinkProperties> linkProperties;

        public OptRiftData(VirtualTarget destination, Optional<LinkProperties> linkProperties) {
			this.destination = destination;
			this.linkProperties = linkProperties;
		}

        public Optional<LinkProperties> getProperties() {
			return linkProperties;
		}

		public VirtualTarget getDestination() {
			return destination.copy();
		}
	}
}
