package org.dimdev.dimdoors.block.door.data;

import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import org.dimdev.dimdoors.block.door.data.condition.Condition;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Pair;

public class RiftDataList {
	private final LinkedList<Pair<JsonObject, Condition>> riftDataConditions;

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
		this.riftDataConditions = riftDataConditions;
	}

	public OptRiftData getRiftData(EntranceRiftBlockEntity rift) {
		JsonObject unbakedRiftData = riftDataConditions.stream().filter(pair -> pair.getRight().matches(rift)).findFirst().orElseThrow(() -> new RuntimeException("Could not find any matching rift data")).getLeft();
		return OptRiftData.fromJson(unbakedRiftData);
	}

	public JsonArray toJson() {
		JsonArray jsonArray = new JsonArray();
		for (Map.Entry<JsonObject, Condition> entry : this.riftDataConditions.stream().collect(Collectors.toMap(Pair::getLeft, Pair::getRight)).entrySet()) {
			JsonObject unbakedRiftData = entry.getKey();
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
		private final Optional<VirtualTarget> destination;
		private final Optional<LinkProperties> linkProperties;

		public static OptRiftData fromJson(JsonObject json) {
			Optional<VirtualTarget> destination = Optional.ofNullable(json.get("destination")).map(JsonElement::getAsJsonObject).map(j -> JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, j)).map(NbtCompound.class::cast).map(VirtualTarget::fromNbt);
			Optional<LinkProperties> linkProperties = Optional.ofNullable(json.get("properties")).map(JsonElement::getAsJsonObject).map(j -> JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, j)).map(NbtCompound.class::cast).map(LinkProperties::fromNbt);
			return new OptRiftData(destination, linkProperties);
		}

		public OptRiftData(Optional<VirtualTarget> destination, Optional<LinkProperties> linkProperties) {
			this.destination = destination;
			this.linkProperties = linkProperties;
		}

		public JsonObject toJson(JsonObject json) {
			this.destination.ifPresent(s -> json.add("destination", NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, VirtualTarget.toNbt(s))));
			this.linkProperties.ifPresent(s -> json.add("properties", NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, LinkProperties.toNbt(s))));
			return json;
		}

		public Optional<LinkProperties> getProperties() {
			return linkProperties;
		}

		public Optional<VirtualTarget> getDestination() {
			return destination;
		}
	}
}
