package org.dimdev.dimdoors.forge.item.door.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.forge.item.door.data.condition.Condition;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;

import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RiftDataList {
	private final LinkedList<Pair<OptRiftData, Condition>> riftDataConditions;

	public static RiftDataList fromJson(JsonArray jsonArray) {
		LinkedList<Pair<JsonObject, Condition>> riftDataConditions = new LinkedList<>();
		for (JsonElement json : jsonArray) {
			JsonObject jsonObject = json.getAsJsonObject();
			//OptRiftData riftData = OptRiftData.fromJson(jsonObject.getAsJsonObject("data"));
			JsonObject unbakedRiftData = jsonObject.getAsJsonObject("data");
			Condition condition = Condition.fromJson(jsonObject.getAsJsonObject("condition"));
			riftDataConditions.add(new Pair<>(unbakedRiftData, condition));
		}
		return new RiftDataList(riftDataConditions);
	}

	public RiftDataList(LinkedList<Pair<JsonObject, Condition>> riftDataConditions) {
		this.riftDataConditions = riftDataConditions.stream().map(pair -> new Pair<>(OptRiftData.fromJson(pair.getFirst()), pair.getSecond())).collect(Collectors.toCollection(LinkedList::new));
	}

	public OptRiftData getRiftData(EntranceRiftBlockEntity rift) {
		return riftDataConditions.stream().filter(pair -> pair.getSecond().matches(rift)).findFirst().orElseThrow(() -> new RuntimeException("Could not find any matching rift data")).getFirst();
	}

	public JsonArray toJson() {
		JsonArray jsonArray = new JsonArray();
		for (Map.Entry<OptRiftData, Condition> entry : this.riftDataConditions.stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)).entrySet()) {
			JsonObject unbakedRiftData = entry.getKey().toJson(new JsonObject());
			Condition condition = entry.getValue();
			JsonObject jsonInner = new JsonObject();
			jsonInner.add("data", unbakedRiftData);
			jsonInner.add("condition", condition.toJson(new JsonObject()));
			jsonArray.add(jsonInner);
		}
		return jsonArray;
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static class OptRiftData {
		private final VirtualTarget destination;
		private final Optional<LinkProperties> linkProperties;

		public static OptRiftData fromJson(JsonObject json) {
			VirtualTarget destination = Optional.of(json.get("destination")).map(JsonElement::getAsJsonObject).map(j -> JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, j)).map(CompoundTag.class::cast).map(VirtualTarget::fromNbt).get();
			Optional<LinkProperties> linkProperties = Optional.ofNullable(json.get("properties")).map(JsonElement::getAsJsonObject).map(j -> JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, j)).map(CompoundTag.class::cast).map(LinkProperties::fromNbt);
			return new OptRiftData(destination, linkProperties);
		}

		public OptRiftData(VirtualTarget destination, Optional<LinkProperties> linkProperties) {
			this.destination = destination;
			this.linkProperties = linkProperties;
		}

		public JsonObject toJson(JsonObject json) {
			Optional.of(this.destination).ifPresent(s -> json.add("destination", NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, VirtualTarget.toNbt(s))));
			this.linkProperties.ifPresent(s -> json.add("properties", NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, LinkProperties.toNbt(s))));
			return json;
		}

		public Optional<LinkProperties> getProperties() {
			return linkProperties;
		}

		public VirtualTarget getDestination() {
			return destination.copy();
		}
	}
}
