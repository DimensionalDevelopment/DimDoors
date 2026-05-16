package org.dimdev.dimdoors.item.door;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.ModRegistries;
import org.dimdev.dimdoors.ModRegistryKeys;
import org.dimdev.dimdoors.item.door.data.RiftDataList;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// TODO: make it async?
public final class DoorRiftDataLoader {
    private static final Logger LOGGER = LogManager.getLogger("DoorRiftDataLoader");
    private static final Gson GSON = new GsonBuilder().create();
//    private static final Map<Item, RiftDataList> itemRiftData = new HashMap<>();

    public static Optional<RiftDataList> getRiftData(RegistryAccess access, Item item) {
        return access.registry(ModRegistryKeys.DOOR_DATA).map(registry -> registry.get(item.builtInRegistryHolder().key().location()));
    }

    public static void reload(HolderLookup.Provider provider, ResourceManager manager) {
//        itemRiftData.clear();
        Map<ResourceLocation, Resource> resources = manager.listResources("door/data", id -> id.getPath().endsWith(".json"));
        resources.forEach((id, resource) -> {
            String name = id.getPath().substring(id.getPath().lastIndexOf('/') + 1, id.getPath().lastIndexOf('.'));
            ResourceLocation itemId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), name);
            if (!BuiltInRegistries.ITEM.containsKey(itemId)) {
                LOGGER.error("Could not find item " + itemId + " for door data " + id);
                return;
            }
            Item item = BuiltInRegistries.ITEM.get(itemId);
            try {
                JsonElement json = GSON.fromJson(resource.openAsReader(), JsonElement.class);
                RiftDataList dataList = RiftDataList.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
//                itemRiftData.put(item, dataList);
            } catch (Exception e) {
                LOGGER.error("Could not read door data " + id, e);
            }
        });
    }
}