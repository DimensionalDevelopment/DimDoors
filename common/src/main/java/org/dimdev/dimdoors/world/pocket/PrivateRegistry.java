package org.dimdev.dimdoors.world.pocket;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.PrivatePocket;

import java.util.UUID;

public class PrivateRegistry {
    private static final Logger LOGGER = LogManager.getLogger();

    protected record PocketInfo(ResourceKey<Level> world, int id) {

        public static CompoundTag toNbt(PocketInfo info) {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("world", info.world.location().toString());
            nbt.putInt("id", info.id);
            return nbt;
        }

        public static PocketInfo fromNbt(CompoundTag nbt) {
            return new PocketInfo(
                    ResourceKey.create(Registries.DIMENSION, Identifier.parse(nbt.getString("world"))),
                    nbt.getInt("id")
            );
        }
    }

    private static final String DATA_NAME = "dimdoors_private_pockets";

    protected BiMap<UUID, PocketInfo> privatePocketMap = HashBiMap.create(); // Player UUID -> Pocket Info TODO: fix AnnotatedNBT and use UUID rather than String

    public PrivateRegistry() {
    }

    public void fromNbt(CompoundTag nbt) {
    privatePocketMap.clear();
    CompoundTag privatePocketMapNbt = nbt.getCompound("private_pocket_map");

        for (var key : privatePocketMapNbt.getAllKeys()) {
        try {
        CompoundTag pocketInfoNbt = privatePocketMapNbt.getCompound(key);
        UUID uuidKey = UUID.fromString(key);
        PocketInfo pocketInfo = PocketInfo.fromNbt(pocketInfoNbt);

        if (this.privatePocketMap.containsKey(uuidKey)) {
            LOGGER.warn("Skipping duplicate private pocket owner mapping for {}.", uuidKey);
            continue;
        }
        if (this.privatePocketMap.containsValue(pocketInfo)) {
            LOGGER.warn("Skipping duplicate private pocket assignment {}:{} for {}.", pocketInfo.world().location(), pocketInfo.id(), uuidKey);
            continue;
        }

        this.privatePocketMap.put(uuidKey, pocketInfo);
        } catch (RuntimeException e) {
        LOGGER.warn("Skipping invalid private pocket mapping for {}.", key, e);
        }
        }
    }

    public CompoundTag toNbt(CompoundTag nbt) {
        var pocketMapNbt = new CompoundTag();

        for(var pair : this.privatePocketMap.entrySet()) {
            pocketMapNbt.put(pair.getKey().toString(), PocketInfo.toNbt(pair.getValue()));
        }

    nbt.put("private_pocket_map", pocketMapNbt);

    return nbt;
    }

    public PrivatePocket getPrivatePocket(UUID playerUUID) {
    PocketInfo pocket = this.privatePocketMap.get(playerUUID);
    if (pocket == null) return null;
    PocketDirectory directory = DimensionalRegistry.peekPocketDirectory(pocket.world);
    if (directory == null) {
        removeStaleMapping(playerUUID, pocket, "missing pocket directory");
        return null;
    }

    PrivatePocket privatePocket = directory.getPocket(pocket.id, PrivatePocket.class);
    if (privatePocket == null) {
        removeStaleMapping(playerUUID, pocket, "missing private pocket");
        return null;
    }

    return privatePocket;
    }

    public void setPrivatePocketID(UUID playerUUID, Pocket pocket) {
    this.privatePocketMap.forcePut(playerUUID, new PocketInfo(pocket.getWorld(), pocket.getId()));
        DimensionalRegistry.setDirty();
    }

    public boolean removePrivatePocket(Pocket pocket) {
    return removePrivatePocket(pocket.getWorld(), pocket.getId());
    }

    public boolean removePrivatePocket(ResourceKey<Level> world, int id) {
    UUID removedOwner = this.privatePocketMap.inverse().remove(new PocketInfo(world, id));
    if (removedOwner != null) {
        DimensionalRegistry.setDirty();
        return true;
    }
    return false;
    }

    public UUID getPrivatePocketOwner(Pocket pocket) {
    return this.privatePocketMap.inverse().get(new PocketInfo(pocket.getWorld(), pocket.getId()));
    }

    private void removeStaleMapping(UUID playerUUID, PocketInfo pocket, String reason) {
    if (this.privatePocketMap.remove(playerUUID) != null) {
        LOGGER.warn("Removing stale private pocket mapping {} -> {}:{} ({})", playerUUID, pocket.world().location(), pocket.id(), reason);
        DimensionalRegistry.setDirty();
    }
    }
}
