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
        public static final Codec<OptRiftData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                VirtualTarget.CODEC.fieldOf("destination").forGetter(OptRiftData::destination),
                LinkProperties.CODEC.optionalFieldOf("properties").forGetter(OptRiftData::linkProperties)
        ).apply(instance, OptRiftData::new));

        public Optional<LinkProperties> getProperties() {
            return linkProperties;
        }

        public VirtualTarget<?> getDestination() {
            return destination.copy();
        }
    }
}
