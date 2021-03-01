package org.dimdev.dimdoors.block.door;

import java.util.LinkedList;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import org.dimdev.dimdoors.block.door.condition.Condition;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Pair;

public class RiftDataList {
	private final LinkedList<Pair<OptRiftData, Condition>> riftDataConditions;

	public static RiftDataList fromJson(JsonArray jsonArray) {
		LinkedList<Pair<OptRiftData, Condition>> riftDataConditions = new LinkedList<>();
		for (JsonElement json : jsonArray) {
			JsonObject jsonObject = json.getAsJsonObject();
			OptRiftData riftData = OptRiftData.fromJson(jsonObject.getAsJsonObject("data"));
			Condition condition = Condition.fromJson(jsonObject.getAsJsonObject("data"));
		}
		return new RiftDataList(riftDataConditions);
	}

	public RiftDataList(LinkedList<Pair<OptRiftData, Condition>> riftDataConditions) {
		this.riftDataConditions = riftDataConditions;
	}

	public OptRiftData getRiftData(EntranceRiftBlockEntity rift) {
		return riftDataConditions.stream().filter(pair -> pair.getRight().matches(rift)).findFirst().orElseThrow(() -> new RuntimeException("Could not find any matching rift data")).getLeft();
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static class OptRiftData {
		private final Optional<VirtualTarget> destination;
		private final Optional<LinkProperties> linkProperties;

		public static OptRiftData fromJson(JsonObject json) {
			Optional<VirtualTarget> destination = Optional.ofNullable(json.get("destination")).map(JsonElement::getAsJsonObject).map(j -> JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, j)).map(CompoundTag.class::cast).map(VirtualTarget::fromTag);
			Optional<LinkProperties> linkProperties = Optional.ofNullable(json.get("destination")).map(JsonElement::getAsJsonObject).map(j -> JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, j)).map(CompoundTag.class::cast).map(LinkProperties::fromTag);
			return new OptRiftData(destination, linkProperties);
		}

		public OptRiftData(Optional<VirtualTarget> destination, Optional<LinkProperties> linkProperties) {
			this.destination = destination;
			this.linkProperties = linkProperties;
		}

		public Optional<LinkProperties> getProperties() {
			return linkProperties;
		}

		public Optional<VirtualTarget> getDestination() {
			return destination;
		}
	}
}
