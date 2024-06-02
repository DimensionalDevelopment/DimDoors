package org.dimdev.dimdoors.forge.item.door;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.forge.item.door.data.RiftDataList;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
		Map<ResourceLocation, Resource> resources = manager.listResources("door/data", id -> id.getPath().endsWith(".json"));
		resources.forEach((id, resource) -> {
			String name = id.getPath().substring(id.getPath().lastIndexOf('/') + 1, id.getPath().lastIndexOf('.'));
			ResourceLocation itemId = new ResourceLocation(id.getNamespace(), name);
			if (!Registry.ITEM.containsKey(itemId)) {
				LOGGER.error("Could not find item " + itemId + " for door data " + id);
				return;
			}
			Item item = Registry.ITEM.get(itemId);
			try {
				JsonArray json = GSON.fromJson(resource.openAsReader(), JsonArray.class);
				RiftDataList dataList = RiftDataList.fromJson(json);
				itemRiftData.put(item, dataList);
			} catch (IOException e) {
				LOGGER.error("Could not read door data " + id, e);
			}
		});
	}
}
