package org.dimdev.dimdoors.world.pocket;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.PrivatePocket;

import java.util.Map;
import java.util.Objects;
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
                    ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(nbt.getString("world"))),
                    nbt.getInt("id")
            );
        }
    }

    protected BiMap<UUID, PocketInfo> privatePocketMap = HashBiMap.create();

    public PrivateRegistry() {
    }

    public void fromNbt(CompoundTag nbt) {
        this.privatePocketMap.clear();

        CompoundTag privatePocketMapNbt = nbt.getCompound("private_pocket_map");

        for (String key : privatePocketMapNbt.getAllKeys()) {
            try {
                CompoundTag pocketInfoNbt = privatePocketMapNbt.getCompound(key);
                UUID uuidKey = UUID.fromString(key);
                PocketInfo pocketInfo = PocketInfo.fromNbt(pocketInfoNbt);

                if (this.privatePocketMap.containsKey(uuidKey)) {
                    LOGGER.warn("Skipping duplicate private pocket owner mapping for {}.", uuidKey);
                    continue;
                }

                if (this.privatePocketMap.containsValue(pocketInfo)) {
                    LOGGER.warn("Skipping duplicate private pocket assignment {}:{} for {}.",
                            pocketInfo.world().location(), pocketInfo.id(), uuidKey);
                    continue;
                }

                this.privatePocketMap.put(uuidKey, pocketInfo);
            } catch (RuntimeException e) {
                LOGGER.warn("Skipping invalid private pocket mapping for {}.", key, e);
            }
        }
    }

    public CompoundTag toNbt(CompoundTag nbt) {
        CompoundTag pocketMapNbt = new CompoundTag();

        for (Map.Entry<UUID, PocketInfo> pair : this.privatePocketMap.entrySet()) {
            pocketMapNbt.put(pair.getKey().toString(), PocketInfo.toNbt(pair.getValue()));
        }

        nbt.put("private_pocket_map", pocketMapNbt);
        return nbt;
    }

    public PrivatePocket getPrivatePocket(UUID playerUUID) {
        Objects.requireNonNull(playerUUID, "playerUUID");

        PocketInfo pocket = this.privatePocketMap.get(playerUUID);
        if (pocket == null) return null;

        PocketDirectory directory = DimensionalRegistry.peekPocketDirectory(pocket.world);
        if (directory == null) return null;

        return directory.getPocket(pocket.id, PrivatePocket.class);
    }

    public void setPrivatePocketID(UUID playerUUID, PrivatePocket pocket) {
        Objects.requireNonNull(playerUUID, "playerUUID");
        Objects.requireNonNull(pocket, "pocket");

        PocketInfo info = new PocketInfo(pocket.getWorld(), pocket.getId());

        UUID existingOwner = this.privatePocketMap.inverse().get(info);
        if (existingOwner != null && !existingOwner.equals(playerUUID)) {
            throw new IllegalStateException("Private pocket " + info.world().location() + ":" + info.id()
                    + " is already assigned to " + existingOwner
                    + ", cannot assign to " + playerUUID);
        }

        PocketInfo previous = this.privatePocketMap.put(playerUUID, info);
        if (!info.equals(previous)) {
            DimensionalRegistry.setIsDirty();
        }
    }

    public void setPrivatePocketID(UUID playerUUID, Pocket pocket) {
        Objects.requireNonNull(pocket, "pocket");

        if (!(pocket instanceof PrivatePocket privatePocket)) {
            throw new IllegalArgumentException("Cannot assign non-private pocket as private pocket: " + pocket);
        }

        setPrivatePocketID(playerUUID, privatePocket);
    }

    public boolean removePrivatePocket(Pocket pocket) {
        Objects.requireNonNull(pocket, "pocket");
        return removePrivatePocket(pocket.getWorld(), pocket.getId());
    }

    public boolean removePrivatePocket(ResourceKey<Level> world, int id) {
        Objects.requireNonNull(world, "world");

        UUID removedOwner = this.privatePocketMap.inverse().remove(new PocketInfo(world, id));
        if (removedOwner != null) {
            DimensionalRegistry.setIsDirty();
            return true;
        }

        return false;
    }

    public UUID getPrivatePocketOwner(Pocket pocket) {
        Objects.requireNonNull(pocket, "pocket");
        return this.privatePocketMap.inverse().get(new PocketInfo(pocket.getWorld(), pocket.getId()));
    }

    public boolean removePrivatePocketOwner(UUID playerUUID) {
        Objects.requireNonNull(playerUUID, "playerUUID");

        if (this.privatePocketMap.remove(playerUUID) != null) {
            DimensionalRegistry.setIsDirty();
            return true;
        }

        return false;
    }

    public boolean removeStalePrivatePocketMapping(UUID playerUUID) {
        Objects.requireNonNull(playerUUID, "playerUUID");

        PocketInfo pocket = this.privatePocketMap.get(playerUUID);
        if (pocket == null) return false;

        PocketDirectory directory = DimensionalRegistry.peekPocketDirectory(pocket.world);
        if (directory != null && directory.getPocket(pocket.id, PrivatePocket.class) != null) {
            return false;
        }

        this.privatePocketMap.remove(playerUUID);
        LOGGER.warn("Removed stale private pocket mapping {} -> {}:{}",
                playerUUID, pocket.world().location(), pocket.id());
        DimensionalRegistry.setIsDirty();
        return true;
    }
}