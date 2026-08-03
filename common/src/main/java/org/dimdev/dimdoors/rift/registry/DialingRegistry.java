package org.dimdev.dimdoors.rift.registry;

import com.google.common.collect.HashBiMap;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.util.CodecUtils;
import org.dimdev.dimdoors.world.pocket.DialingPocket;
import org.dimdev.dimdoors.world.pocket.PocketInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class DialingRegistry extends PlayerTrackingSubSystem<DialingRegistry> {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final MapCodec<DialingRegistry> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
            .and(CodecUtils.unboundedMap(DialingAddress.STRING_CODEC, PocketInfo.CODEC, HashBiMap::create).fieldOf("dialing_pockets").<DialingRegistry>forGetter(a -> a.dialingPockets))
            .and(CodecUtils.unboundedMap(UUIDUtil.STRING_CODEC, DialingAddress.CODEC).fieldOf("player_to_address").<DialingRegistry>forGetter(a -> a.playertoAddress)
            ).apply(instance, DialingRegistry::new));

    protected HashBiMap<DialingAddress, PocketInfo> dialingPockets;
    protected Map<UUID, DialingAddress> playertoAddress;

    public DialingRegistry() {
        this(new HashMap<>(), HashBiMap.create(), new HashMap<>());
    }

    public DialingRegistry(Map<UUID, PlayerRiftConnection> locations, HashBiMap<DialingAddress, PocketInfo> dialingPockets, Map<UUID, DialingAddress> playertoAddress) {
        super(locations);
        this.dialingPockets = dialingPockets;
        this.playertoAddress = playertoAddress;
    }

    public static DialingRegistry getInstance() {
        return getInstance(SubsystemTypes.DIALING);
    }


    public DialingPocket getDialingPocket(UUID uuid) {
        var address = playertoAddress.get(uuid);
        if (address == null) return null;
        return getDialingPocket(address);
    }

    public DialingPocket getDialingPocket(DialingAddress address) {
        var pocket = this.dialingPockets.get(address);
        if (pocket == null) return null;
        return PocketRegistry.getInstance().getPocket(pocket, DialingPocket.class);
    }

    public void setPlayerAddress(UUID uuid, DialingAddress address) {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(address, "address");

        DialingAddress previous = this.playertoAddress.put(uuid, address);
        if (!address.equals(previous)) {
            this.setDirty();
        }
    }

    @Override
    public Type<DialingRegistry> type() {
        return SubsystemTypes.DIALING;
    }

    public void setDialingPocketAddress(DialingAddress address, DialingPocket pocket) {
        Objects.requireNonNull(address, "address");
        Objects.requireNonNull(pocket, "pocket");

        PocketInfo info = new PocketInfo(pocket.getWorld(), pocket.getId());
        DialingAddress pocketAddress = pocket.getAddress();
        if (!address.equals(pocketAddress)) {
            throw new IllegalStateException("Dialing pocket " + info.world().location() + ":" + info.id()
                    + " has address " + pocketAddress
                    + ", cannot assign registry address " + address);
        }

        DialingAddress existingAddress = this.dialingPockets.inverse().get(info);
        if (existingAddress != null && !existingAddress.equals(address)) {
            throw new IllegalStateException("Dialing pocket " + info.world().location() + ":" + info.id()
                    + " is already assigned to " + existingAddress
                    + ", cannot assign to " + address);
        }

        PocketInfo previous = this.dialingPockets.put(address, info);
        LOGGER.info("Tracking dialing pocket address {} -> {}:{} (previous={}, pocketAddress={})",
                address, info.world().location(), info.id(), previous, pocketAddress);

        if (!info.equals(previous)) {
            this.setDirty();
        }
    }

    public Location resolveEntrance(UUID playerUUID) {
        Objects.requireNonNull(playerUUID, "playerUUID");

        DialingAddress address = playertoAddress.get(playerUUID);
        if (address == null) {
            LOGGER.warn("Cannot resolve dialing entrance for {} because no active dialing address is tracked.", playerUUID);
            return null;
        }

        DialingPocket pocket = this.getDialingPocket(address);
        if (pocket == null) {
            LOGGER.warn("Cannot resolve dialing entrance for {} at {} because no dialing pocket is tracked.", playerUUID, address);
            return null;
        }

        Location entrance = this.getEntranceLocation(playerUUID);
        if (entrance != null && PocketRegistry.getInstance().getPocketEntrances(pocket).contains(entrance)) {
            return entrance;
        }

        return PocketRegistry.getInstance().getPocketEntrance(pocket);
    }
}
