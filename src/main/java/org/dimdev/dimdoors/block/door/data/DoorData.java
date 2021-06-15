package org.dimdev.dimdoors.block.door.data;

import java.util.*;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.item.ItemGroup;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.item.DimensionalDoorItem;
import org.dimdev.dimdoors.item.ItemExtensions;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.util.TriState;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.mixin.accessor.ItemGroupAccessor;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class DoorData implements AutoCloseable {
	private static final Map<String, ItemGroup> itemGroupCache = new HashMap<>();
	public static final Set<Block> PARENT_BLOCKS = new HashSet<>();
	public static final Set<Item> PARENT_ITEMS = new HashSet<>();
	public static final List<Block> DOORS = new ArrayList<>();
	private final String id;
	private final UnbakedItemSettings itemSettings;
	private final Optional<String> itemGroup;
	private final UnbakedBlockSettings blockSettings;
	private final RiftDataList riftDataList;
	private final boolean hasToolTip;
	private boolean closed = false;

	public static DoorData fromJson(JsonObject json) {
		try {
			String id = json.get("id").getAsString();
			UnbakedItemSettings itemSettings = UnbakedItemSettings.fromJson(json.getAsJsonObject("itemSettings"));
			Optional<String> itemGroup = Optional.ofNullable(json.getAsJsonPrimitive("itemGroup")).map(JsonPrimitive::getAsString);
			UnbakedBlockSettings blockSettings = UnbakedBlockSettings.fromJson(json.getAsJsonObject("blockSettings"));
			RiftDataList riftDataList = RiftDataList.fromJson(json.getAsJsonArray("riftData"));
			boolean hasToolTip = json.has("hasToolTip") && json.getAsJsonPrimitive("hasToolTip").getAsBoolean();
			return new DoorData(id, itemSettings, itemGroup, blockSettings, riftDataList, hasToolTip);
		} catch (RuntimeException e) {
			throw new RuntimeException("Caught exception while deserializing " + json.toString(), e);
		}
	}

	public DoorData(String id, UnbakedItemSettings itemSettings, UnbakedBlockSettings blockSettings, RiftDataList riftDataList) {
		this(id, itemSettings, Optional.empty(), blockSettings, riftDataList, false);
	}

	public DoorData(String id, UnbakedItemSettings itemSettings, UnbakedBlockSettings blockSettings, RiftDataList riftDataList, boolean hasToolTip) {
		this(id, itemSettings, Optional.empty(), blockSettings, riftDataList, hasToolTip);
	}

	public DoorData(String id, UnbakedItemSettings itemSettings, Optional<String> itemGroup, UnbakedBlockSettings blockSettings, RiftDataList riftDataList, boolean hasToolTip) {
		this.id = id;
		this.itemSettings = itemSettings;
		this.itemGroup = itemGroup;
		this.blockSettings = blockSettings;
		this.riftDataList = riftDataList;
		this.hasToolTip = hasToolTip;
	}

	public JsonObject toJson(JsonObject json) {
		json.addProperty("id", this.id);
		json.add("itemSettings", this.itemSettings.toJson(new JsonObject()));
		itemGroup.ifPresent(s -> json.add("itemGroup", new JsonPrimitive(s)));
		json.add("blockSettings", this.blockSettings.toJson(new JsonObject()));
		json.add("riftData", this.riftDataList.toJson());
		json.addProperty("hasToolTip", this.hasToolTip);
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

	public boolean hasToolTip() {
		return hasToolTip;
	}

	@Override
	public void close() {
		if (closed) {
			throw new UnsupportedOperationException("Already Closed");
		}

		Item parentItem = Registry.ITEM.get(new Identifier(this.itemSettings.parent));
		PARENT_ITEMS.add(parentItem);
		Item.Settings itemSettings = ItemExtensions.getSettings(parentItem);
		this.itemSettings.maxCount.ifPresent(itemSettings::maxCount);
		this.itemSettings.maxDamage.ifPresent(itemSettings::maxDamageIfAbsent);
		this.itemSettings.rarity.ifPresent(itemSettings::rarity);
		this.itemSettings.fireproof.map(b -> {
			if (!b) return false;
			itemSettings.fireproof();
			return false;
		});
		ItemGroup group = null;
		if (itemGroup.isPresent()) {
			String groupString = itemGroup.get();
			if (itemGroupCache.containsKey(groupString)) {
				group = itemGroupCache.get(groupString);
			} else {
				for (ItemGroup g : ItemGroup.GROUPS) {
					if (((ItemGroupAccessor) g).getId().equals(groupString)) {
						group = g;
						itemGroupCache.put(groupString, group);
						break;
					}
				}
			}
		}
		itemSettings.group(group != null ? group : ModItems.DIMENSIONAL_DOORS);

		Block parentBlock = Registry.BLOCK.get(new Identifier(this.blockSettings.parent));
		PARENT_BLOCKS.add(parentBlock);
		FabricBlockSettings blockSettings = FabricBlockSettings.copyOf(parentBlock);
		this.blockSettings.luminance.ifPresent(blockSettings::luminance);
		Identifier id = new Identifier(this.id);
		Block doorBlock = new DimensionalDoorBlock(blockSettings);
		Item doorItem = new DimensionalDoorItem(doorBlock, itemSettings, createSetupFunction(), hasToolTip);
		Registry.register(Registry.BLOCK, id, doorBlock);
		Registry.register(Registry.ITEM, id, doorItem);
		DOORS.add(doorBlock);
		Item.BLOCK_ITEMS.put(doorBlock, doorItem);
		this.closed = true;
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static final class UnbakedItemSettings {
		private static final Map<String, Rarity> RARITIES = Util.make(ImmutableMap.<String, Rarity>builder(), b -> {
			for (Rarity rarity : Rarity.values()) {
				b.put(rarity.name().toLowerCase(), rarity);
			}
		}).build();
		private final String parent;
		private final OptionalInt maxCount;
		private final OptionalInt maxDamage;
		private final Optional<Rarity> rarity;
		private final TriState fireproof;

		public static UnbakedItemSettings fromJson(JsonObject json) {
			String parent = Optional.ofNullable(json.get("parent")).map(JsonElement::getAsString).orElseThrow(() -> new RuntimeException("Missing parent key in " + json.toString()));
			OptionalInt maxCount = Optional.ofNullable(json.get("maxCount")).map(JsonElement::getAsInt).map(OptionalInt::of).orElse(OptionalInt.empty());
			OptionalInt maxDamage = Optional.ofNullable(json.get("maxDamage")).map(JsonElement::getAsInt).map(OptionalInt::of).orElse(OptionalInt.empty());
			Optional<Rarity> rarity = Optional.ofNullable(json.get("rarity")).map(JsonElement::getAsString).map(String::toLowerCase).map(RARITIES::get).map(Objects::requireNonNull);
			TriState fireproof = Optional.ofNullable(json.get("fireproof")).map(JsonElement::getAsBoolean).map(TriState::of).orElse(TriState.DEFAULT);
			return new UnbakedItemSettings(parent, maxCount, maxDamage, rarity, fireproof);
		}

		public UnbakedItemSettings(String parent, OptionalInt maxCount, OptionalInt maxDamage, Optional<Rarity> rarity, TriState fireproof) {
			this.parent = parent;
			this.maxCount = maxCount;
			this.maxDamage = maxDamage;
			this.rarity = rarity;
			this.fireproof = fireproof;
		}

		public JsonObject toJson(JsonObject json) {
			json.addProperty("parent", parent);
			maxCount.ifPresent(s -> json.addProperty("maxCount", s));
			maxDamage.ifPresent(s -> json.addProperty("maxDamage", s));
			rarity.ifPresent(s -> json.addProperty("rarity", s.name().toLowerCase()));
			fireproof.map(b -> {
				json.addProperty("fireproof", b);
				return b;
			});
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
