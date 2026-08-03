package org.dimdev.dimdoors.world.pocket;

import com.google.common.collect.HashBiMap;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.rift.registry.PlayerTrackingSubSystem;
import org.dimdev.dimdoors.rift.registry.PocketRegistry;
import org.dimdev.dimdoors.rift.registry.SubsystemTypes;
import org.dimdev.dimdoors.util.CodecUtils;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.PrivatePocket;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PrivateRegistry extends PlayerTrackingSubSystem<UUID, PrivatePocket, PrivateRegistry> {

    public static final MapCodec<PrivateRegistry> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
            .and(CodecUtils.unboundedMap(UUIDUtil.STRING_CODEC, PocketInfo.CODEC, HashBiMap::create)
                    .fieldOf("private_pockets").forGetter(a -> a.privatePockets))
            .apply(instance, PrivateRegistry::new));

    protected HashBiMap<UUID, PocketInfo> privatePockets;

    public PrivateRegistry() {
        this(new HashMap<>(), HashBiMap.create());
    }

    public PrivateRegistry(Map<UUID, PlayerRiftConnection> locations, HashBiMap<UUID, PocketInfo> privatePockets) {
        super(locations);
        this.privatePockets = privatePockets;
    }

    @Override
    public void setEntrance(UUID uuid, Location entrance) {
        LOGGER.debug("Setting private pocket entrance for {} to {}.", uuid, entrance);
        super.setEntrance(uuid, entrance);
    }

    @Override
    public void setExit(UUID uuid, Location exit) {
        LOGGER.debug("Setting private pocket exit for {} to {}.", uuid, exit);
        super.setExit(uuid, exit);
    }

    @Override
    public UUID getKeyFromPlayer(UUID playerUUID) {
        return playerUUID;
    }

    @Override
    public Type<PrivateRegistry> type() {
        return SubsystemTypes.PRIVATE;
    }

    public PrivatePocket getPocketFromPlayer(UUID playerUUID) {
        Objects.requireNonNull(playerUUID, "playerUUID");

        PocketInfo pocket = this.privatePockets.get(playerUUID);
        if (pocket == null) return null;

        return PocketRegistry.getInstance().getPocket(pocket, PrivatePocket.class);
    }

    @Override
    public PrivatePocket getPocketFromKey(UUID uuid) {
        return getPocketFromPlayer(uuid);
    }

    public void setPrivatePocketID(UUID playerUUID, PrivatePocket pocket) {
        Objects.requireNonNull(playerUUID, "playerUUID");
        Objects.requireNonNull(pocket, "pocket");

        PocketInfo info = new PocketInfo(pocket.getWorld(), pocket.getId());

        UUID existingOwner = this.privatePockets.inverse().get(info);
        if (existingOwner != null && !existingOwner.equals(playerUUID)) {
            throw new IllegalStateException("Private pocket " + info.world().location() + ":" + info.id()
                    + " is already assigned to " + existingOwner
                    + ", cannot assign to " + playerUUID);
        }

        PocketInfo previous = this.privatePockets.put(playerUUID, info);
        if (!info.equals(previous)) {
            this.setDirty();
        }
    }

    public void setPrivatePocketID(UUID playerUUID, Pocket<?,?> pocket) {
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

        UUID removedOwner = this.privatePockets.inverse().remove(new PocketInfo(world, id));
        if (removedOwner != null) {
            this.setDirty();
            return true;
        }

        return false;
    }

    public UUID getPrivatePocketOwner(Pocket pocket) {
        Objects.requireNonNull(pocket, "pocket");
        return this.privatePockets.inverse().get(new PocketInfo(pocket.getWorld(), pocket.getId()));
    }

    public boolean removePrivatePocketOwner(UUID playerUUID) {
        Objects.requireNonNull(playerUUID, "playerUUID");

        if (this.privatePockets.remove(playerUUID) != null) {
            this.setDirty();
            return true;
        }

        return false;
    }

    public boolean removeStalePrivatePocketMapping(UUID playerUUID) {
        Objects.requireNonNull(playerUUID, "playerUUID");

        PocketInfo pocket = this.privatePockets.get(playerUUID);
        if (pocket == null) return false;

        if (PocketRegistry.getInstance().getPocket(pocket, PrivatePocket.class) != null) return false;

        this.privatePockets.remove(playerUUID);
        this.setDirty();
        LOGGER.warn("Removed stale private pocket mapping {} -> {}:{}", playerUUID, pocket.world().location(), pocket.id());
        return true;
    }

    public static PrivateRegistry getInstance() {
        return getInstance(SubsystemTypes.PRIVATE);
    }

    @Override
    public String invalidKeyErrorMessage() {
        return "Cannot resolve private entrance for {} because their uuid isn't being tracked.";
    }

    @Override
    public String invalidPocketErrorMessage() {
        return "Cannot resolve private entrance for {} at {} because no private pocket is tracked.";
    }

    @Override
    public void setNewPocket(UUID uuid, UUID key, PrivatePocket pocket) {
        setPrivatePocketID(uuid, pocket);
    }

    @Override
    public boolean isCorrectDimensionForPocket(ServerLevel world) {
        return ModDimensions.isPrivatePocketDimension(world);
    }

    @Override
    public void setCurrentKey(UUID uuid, UUID key) {

    }
}
