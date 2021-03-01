package org.dimdev.dimdoors.block.door;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.item.DimensionalDoorItem;
import org.dimdev.dimdoors.item.ItemExtensions;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.util.OptionalBool;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

public final class DoorData implements AutoCloseable {
	public static final List<Block> DOORS = new ArrayList<>();
	private final String id;
	private final UnbakedItemSettings itemSettings;
	private final UnbakedBlockSettings blockSettings;
	private final RiftDataList riftDataList;
	private boolean closed = false;

	public static DoorData fromJson(JsonObject json) {
		String id = json.get("id").getAsString();
		UnbakedItemSettings itemSettings = UnbakedItemSettings.fromJson(json.getAsJsonObject("itemSettings"));
		UnbakedBlockSettings blockSettings = UnbakedBlockSettings.fromJson(json.getAsJsonObject("blockSettings"));
		RiftDataList riftDataList = RiftDataList.fromJson(json.getAsJsonArray("riftData"));
		return new DoorData(id, itemSettings, blockSettings, riftDataList);
	}

	public DoorData(String id, UnbakedItemSettings itemSettings, UnbakedBlockSettings blockSettings, RiftDataList riftDataList) {
		this.id = id;
		this.itemSettings = itemSettings;
		this.blockSettings = blockSettings;
		this.riftDataList = riftDataList;
	}

	public JsonObject toJson(JsonObject json) {
		json.addProperty("id", this.id);
		json.add("itemSettings", this.itemSettings.toJson(new JsonObject()));
		json.add("blockSettings", this.blockSettings.toJson(new JsonObject()));
		json.add("riftData", this.riftDataList.toJson());
		return json;
	}

	private Consumer<? super EntranceRiftBlockEntity> createSetupFunction() {
		RiftDataList riftDataList = this.riftDataList;

		return rift -> {
			RiftDataList.OptRiftData riftData = riftDataList.getRiftData(rift);
			riftData.getDestination().ifPresent(rift::setDestination);
			riftData.getProperties().ifPresent(rift::setProperties);
		};
	}

	public String getId() {
		return id;
	}

	public UnbakedItemSettings getItemSettings() {
		return itemSettings;
	}

	public UnbakedBlockSettings getBlockSettings() {
		return blockSettings;
	}

	public RiftDataList getRiftDataList() {
		return riftDataList;
	}

	@Override
	public void close() {
		if (closed) {
			throw new UnsupportedOperationException("Already Closed");
		}

		Item.Settings itemSettings;
		if (this.itemSettings.parent.isPresent()) {
			itemSettings = ItemExtensions.getSettings(Registry.ITEM.get(new Identifier(this.itemSettings.parent.get())));
		} else {
			itemSettings = new Item.Settings().group(ModItems.DIMENSIONAL_DOORS);
		}
		this.itemSettings.maxCount.ifPresent(itemSettings::maxCount);
		this.itemSettings.maxDamage.ifPresent(itemSettings::maxDamageIfAbsent);
		this.itemSettings.rarity.ifPresent(itemSettings::rarity);
		this.itemSettings.fireproof.ifPresentAndTrue(itemSettings::fireproof);

		FabricBlockSettings blockSettings = FabricBlockSettings.copyOf(Registry.BLOCK.get(new Identifier(this.blockSettings.parent)));
		this.blockSettings.luminance.ifPresent(blockSettings::luminance);
		Identifier id = new Identifier(this.id);
		Block doorBlock = new DimensionalDoorBlock(blockSettings);
		Item doorItem = new DimensionalDoorItem(doorBlock, itemSettings, createSetupFunction());
		Registry.register(Registry.BLOCK, id, doorBlock);
		Registry.register(Registry.ITEM, id, doorItem);
		DOORS.add(doorBlock);
		this.closed = true;
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static final class UnbakedItemSettings {
		private static final Map<String, Rarity> RARITIES = Util.make(ImmutableMap.<String, Rarity>builder(), b -> {
			for (Rarity rarity : Rarity.values()) {
				b.put(rarity.name().toLowerCase(), rarity);
			}
		}).build();
		private final Optional<String> parent;
		private final OptionalInt maxCount;
		private final OptionalInt maxDamage;
		private final Optional<Rarity> rarity;
		private final OptionalBool fireproof;

		public static UnbakedItemSettings fromJson(JsonObject json) {
			Optional<String> parent = Optional.ofNullable(json.get("parent")).map(JsonElement::getAsString);
			OptionalInt maxCount = Optional.ofNullable(json.get("maxCount")).map(JsonElement::getAsInt).map(OptionalInt::of).orElse(OptionalInt.empty());
			OptionalInt maxDamage = Optional.ofNullable(json.get("maxDamage")).map(JsonElement::getAsInt).map(OptionalInt::of).orElse(OptionalInt.empty());
			Optional<Rarity> rarity = Optional.ofNullable(json.get("rarity")).map(JsonElement::getAsString).map(String::toLowerCase).map(RARITIES::get).map(Objects::requireNonNull);
			OptionalBool fireproof = Optional.ofNullable(json.get("fireproof")).map(JsonElement::getAsBoolean).map(OptionalBool::of).orElse(OptionalBool.empty());
			return new UnbakedItemSettings(parent, maxCount, maxDamage, rarity, fireproof);
		}

		public UnbakedItemSettings(Optional<String> parent, OptionalInt maxCount, OptionalInt maxDamage, Optional<Rarity> rarity, OptionalBool fireproof) {
			this.parent = parent;
			this.maxCount = maxCount;
			this.maxDamage = maxDamage;
			this.rarity = rarity;
			this.fireproof = fireproof;
		}

		public JsonObject toJson(JsonObject json) {
			parent.ifPresent(s -> json.addProperty("parent", s));
			maxCount.ifPresent(s -> json.addProperty("maxCount", s));
			maxDamage.ifPresent(s -> json.addProperty("maxDamage", s));
			rarity.ifPresent(s -> json.addProperty("rarity", s.name().toLowerCase()));
			fireproof.ifPresent(s -> json.addProperty("fireproof", s));
			return json;
		}
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static final class UnbakedBlockSettings {
		private final String parent;
		private final OptionalInt luminance;

		public static UnbakedBlockSettings fromJson(JsonObject json) {
			String parent = Optional.ofNullable(json.get("parent")).map(JsonElement::getAsString).orElseThrow(() -> new RuntimeException("Missing parent block"));
			OptionalInt luminance = Optional.ofNullable(json.get("luminance")).map(JsonElement::getAsInt).map(OptionalInt::of).orElse(OptionalInt.empty());
			return new UnbakedBlockSettings(parent, luminance);
		}

		public UnbakedBlockSettings(String parent, OptionalInt luminance) {
			this.parent = parent;
			this.luminance = luminance;
		}

		public JsonObject toJson(JsonObject json) {
			json.addProperty("parent", parent);
			luminance.ifPresent(s -> json.addProperty("luminance", s));
			return json;
		}
	}
}
