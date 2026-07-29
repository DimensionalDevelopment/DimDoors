package org.dimdev.dimdoors.rift.registry;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.util.CodecUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public abstract class PlayerTrackingSubSystem<T extends PlayerTrackingSubSystem<T>> extends SubSystem<T> implements VertexProvider {
    protected static <T extends PlayerTrackingSubSystem<T>> Products.P1<RecordCodecBuilder.Mu<T>, Map<UUID, PlayerRiftConnection>> commonFields(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(PlayerRiftConnection.MAP_CODEC.fieldOf("locations").forGetter(PlayerTrackingSubSystem::getLocations));
    }

    protected Map<UUID, PlayerRiftConnection> locations;

    public PlayerTrackingSubSystem() {
        this(new HashMap<>());
    }

    public PlayerTrackingSubSystem(Map<UUID, PlayerRiftConnection> locations) {
        this.locations = locations;
    }

    public Map<UUID, PlayerRiftConnection> getLocations() {
        return locations;
    }

    @Override
    public List<? extends RegistryVertex> collectVertices() {
        Set<UUID> pointers = new LinkedHashSet<>();
        for (PlayerRiftConnection connection : this.locations.values()) {
            if (connection.getEntrance() != null) {
                pointers.add(connection.getEntrance());
            }
            if (connection.getExit() != null) {
                pointers.add(connection.getExit());
            }
        }

        List<PlayerRiftPointer> vertices = new ArrayList<>();
        for (UUID pointer : pointers) {
            vertices.add(new PlayerRiftPointer(pointer));
        }
        return vertices;
    }

    public UUID getEntrance(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");

        PlayerRiftConnection connection = this.locations.get(uuid);
        return connection == null ? null : connection.getEntrance();
    }

    public Location getEntranceLocation(UUID uuid) {
        return this.getPointedRiftLocation(this.getEntrance(uuid));
    }

    protected void setEntrance(UUID uuid, UUID entrance) {
        Objects.requireNonNull(uuid, "uuid");

        PlayerRiftConnection connection = this.locations.computeIfAbsent(uuid, ignored -> new PlayerRiftConnection());
        UUID previous = connection.getEntrance();
        connection.setEntrance(entrance);
        this.removeIfEmpty(uuid, connection);

        if (!Objects.equals(previous, entrance)) {
            this.setDirty();
        }
    }

    public void setEntrance(UUID uuid, Location entrance) {
        this.setEntrance(uuid, this.createPlayerRiftPointer(uuid, this.getEntrance(uuid), entrance));
    }

    public UUID getExit(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");

        PlayerRiftConnection connection = this.locations.get(uuid);
        return connection == null ? null : connection.getExit();
    }

    public Location getExitLocation(UUID uuid) {
        return this.getPointedRiftLocation(this.getExit(uuid));
    }

    protected void setExit(UUID uuid, UUID exit) {
        Objects.requireNonNull(uuid, "uuid");

        PlayerRiftConnection connection = this.locations.computeIfAbsent(uuid, ignored -> new PlayerRiftConnection());
        UUID previous = connection.getExit();
        connection.setExit(exit);
        this.removeIfEmpty(uuid, connection);

        if (!Objects.equals(previous, exit)) {
            this.setDirty();
        }
    }

    public void setExit(UUID uuid, Location exit) {
        this.setExit(uuid, this.createPlayerRiftPointer(uuid, this.getExit(uuid), exit));
    }

    private UUID createPlayerRiftPointer(UUID uuid, UUID currentPointer, Location location) {
        Objects.requireNonNull(uuid, "uuid");

        if (currentPointer != null) {
            RiftGraph.getInstance().removeVertex(currentPointer);
        }

        if (location == null) {
            return null;
        }

        Rift target = RiftRegistry.getInstance().getRiftOrPlaceholder(location);
        PlayerRiftPointer pointer = new PlayerRiftPointer();
        RiftGraph.getInstance().addVertex(pointer);
        RiftGraph.getInstance().addEdge(pointer, target);
        target.markDirty();
        return pointer.getId();
    }

    private Location getPointedRiftLocation(UUID pointer) {
        return pointer == null ? null : RiftRegistry.getInstance().findRift(RiftGraph.getInstance().followPointer(pointer))
                .map(Rift::getLocation)
                .orElse(null);
    }

    private void removeIfEmpty(UUID uuid, PlayerRiftConnection connection) {
        if (connection.getEntrance() == null && connection.getExit() == null) {
            this.locations.remove(uuid);
        }
    }

    public static class PlayerRiftConnection {
        public static final Codec<PlayerRiftConnection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        UUIDUtil.CODEC.optionalFieldOf("entranceId").forGetter(connection -> Optional.ofNullable(connection.getEntrance())),
                        UUIDUtil.CODEC.optionalFieldOf("exitId").forGetter(connection -> Optional.ofNullable(connection.getExit())))
                .apply(instance, PlayerRiftConnection::new));

        public static final Codec<Map<UUID, PlayerRiftConnection>> MAP_CODEC = CodecUtils.unboundedMap(UUIDUtil.STRING_CODEC, CODEC);

        public PlayerRiftConnection() {
            this((UUID) null, null);
        }

        private PlayerRiftConnection(Optional<UUID> entrance, Optional<UUID> exit) {
            this(entrance.orElse(null), exit.orElse(null));
        }

        public PlayerRiftConnection(UUID entrance, UUID exit) {
            this.entrance = entrance;
            this.exit = exit;
        }

        private UUID entrance;
        private UUID exit;

        public UUID getEntrance() {
            return entrance;
        }

        public void setEntrance(UUID entrance) {
            this.entrance = entrance;
        }

        public UUID getExit() {
            return exit;
        }

        public void setExit(UUID exit) {
            this.exit = exit;
        }
    }
}
