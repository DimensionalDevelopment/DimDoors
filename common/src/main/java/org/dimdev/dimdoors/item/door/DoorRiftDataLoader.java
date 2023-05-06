package org.dimdev.dimdoors.item.door;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.item.door.data.RiftDataList;

// TODO: make it async?
public final class DoorRiftDataLoader implements ResourceManagerReloadListener {
	private static final DoorRiftDataLoader INSTANCE = new DoorRiftDataLoader();
	private static final Logger LOGGER = LogManager.getLogger("DoorRiftDataLoader");
	private static final Gson GSON = new GsonBuilder().create();
	private final Map<Item, RiftDataList> itemRiftData = new HashMap<>();

	public static DoorRiftDataLoader getInstance() {
		return INSTANCE;
	}

	private DoorRiftDataLoader() {
	}

	public RiftDataList getRiftData(Item item) {
		return itemRiftData.get(item);
	}

	@Override
	public void onResourceManagerReload(ResourceManager manager) {
		itemRiftData.clear();
		Map<Identifier, Resource> resources = manager.findResources("door/data", id -> id.getPath().endsWith(".json"));
		resources.forEach((id, resource) -> {
			String name = id.getPath().substring(id.getPath().lastIndexOf('/') + 1, id.getPath().lastIndexOf('.'));
			Identifier itemId = new Identifier(id.getNamespace(), name);
			if (!Registries.ITEM.containsId(itemId)) {
				LOGGER.error("Could not find item " + itemId + " for door data " + id);
				return;
			}
			Item item = Registries.ITEM.get(itemId);
			try {
				JsonArray json = GSON.fromJson(resource.getReader(), JsonArray.class);
				RiftDataList dataList = RiftDataList.fromJson(json);
				itemRiftData.put(item, dataList);
			} catch (IOException e) {
				LOGGER.error("Could not read door data " + id, e);
			}
		});
	}
}
