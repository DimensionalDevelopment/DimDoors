package org.dimdev.dimdoors.item.door.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.item.door.data.condition.Condition;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public record RiftDataList(List<Entry> entries) {
    private record Entry(OptRiftData data, Condition condition) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                OptRiftData.CODEC.fieldOf("data").forGetter(Entry::data),
                Condition.CODEC.fieldOf("condition").forGetter(Entry::condition)
        ).apply(instance, Entry::new));
    }

    public static final Codec<RiftDataList> CODEC = Entry.CODEC.listOf().xmap(RiftDataList::new, RiftDataList::entries);

    public OptRiftData getRiftData(EntranceRiftBlockEntity rift) {
        return this.entries().stream().filter(pair -> pair.condition().matches(rift)).findFirst().orElseThrow(() -> new IllegalStateException("Could not find any matching rift data")).data();
    }

    public static record OptRiftData(VirtualTarget<?> destination, Optional<LinkProperties> linkProperties) {
        public OptRiftData(VirtualTarget<?> destination) {
            this(destination, Optional.empty());
        }
        public static final Codec<OptRiftData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                VirtualTarget.CODEC.fieldOf("destination").forGetter(OptRiftData::destination),
                LinkProperties.CODEC.optionalFieldOf("properties").forGetter(OptRiftData::linkProperties)
        ).apply(instance, OptRiftData::new));

        public OptRiftData(VirtualTarget<?> destination, LinkProperties linkProperties) {
            this(destination, Optional.of(linkProperties));
        }

        public Optional<LinkProperties> getProperties() {
            return linkProperties;
        }

        public VirtualTarget<?> getDestination() {
            return destination.copy();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static RiftDataList of(OptRiftData data, Condition condition) {
        return new RiftDataList(List.of(new Entry(data, condition)));
    }

    public static RiftDataList of(VirtualTarget<?> target, LinkProperties properties, Condition condition) {
        return of(new OptRiftData(target, properties), condition);
    }

    public static RiftDataList of(VirtualTarget<?> target, Condition condition) {
        return of(new OptRiftData(target), condition);
    }

    public static class Builder {
        private List<Entry> entries = new ArrayList<>();

        public Builder add(OptRiftData data, Condition condition) {
            entries.add(new Entry(data, condition));
            return this;
        }

        public Builder add(VirtualTarget<?> target, Condition condition) {
            return add(new OptRiftData(target), condition);
        }

        public RiftDataList builder() {
            return new RiftDataList(entries);
        }
    }
}
