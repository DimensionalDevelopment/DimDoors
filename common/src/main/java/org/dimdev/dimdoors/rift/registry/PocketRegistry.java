package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.util.CodecUtils;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.PocketInfo;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static org.dimdev.dimdoors.api.rift.target.TargetResolver.castOrNull;

public class PocketRegistry extends SubSystem<PocketRegistry> implements VertexProvider {
    public static final MapCodec<PocketRegistry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CodecUtils.unboundedMap(Level.RESOURCE_KEY_CODEC, PocketDirectory.CODEC).fieldOf("directories").forGetter(PocketRegistry::getDirectories),
            CodecUtils.unboundedMap(PocketInfo.STRING_CODEC, PocketEntrancePointer.CODEC).fieldOf("entrance_pointers").forGetter(PocketRegistry::getPocketEntrancePointers)
    ).apply(instance, PocketRegistry::new));

    private final Map<ResourceKey<Level>, PocketDirectory> directories;
    private final Map<PocketInfo, PocketEntrancePointer> pocketEntrancePointers;

    public PocketRegistry() {
        this(new HashMap<>(), new HashMap<>());
    }

    public PocketRegistry(Map<ResourceKey<Level>, PocketDirectory> directories) {
        this(directories, new HashMap<>());
    }

    public PocketRegistry(Map<ResourceKey<Level>, PocketDirectory> directories, Map<PocketInfo, PocketEntrancePointer> pocketEntrancePointers) {
        this.directories = directories;
        this.pocketEntrancePointers = pocketEntrancePointers;
    }

    public Map<ResourceKey<Level>, PocketDirectory> getDirectories() {
        return directories;
    }

    public Map<PocketInfo, PocketEntrancePointer> getPocketEntrancePointers() {
        return pocketEntrancePointers;
    }

    public void forEachPocketDirectory(BiConsumer<ResourceKey<Level>, PocketDirectory> consumer) {
        directories.forEach(consumer);
    }

    public PocketDirectory peekPocketDirectory(ResourceKey<Level> key) {
        if (!ModDimensions.isPocketDimension(key)) {
            return null;
        }

        return directories.get(key);
    }

    public PocketDirectory getOrCreate(ResourceKey<Level> key) {
        return directories.computeIfAbsent(key, this::createDirectory);
    }

    private PocketDirectory createDirectory(ResourceKey<Level> key) {
        var directory = new PocketDirectory();
        this.setDirty();
        return directory;
    }

    public PocketDirectory getPocketDirectory(ResourceKey<Level> key) {
        if (!ModDimensions.isPocketDimension(key)) {
            throw new UnsupportedOperationException("PocketRegistry is only available for pocket dimensions!");
        }

        return getInstance().getOrCreate(key);
    }

    public Pocket<?, ?> createPocket(ResourceKey<Level> key, Pocket.PocketBuilder<?, ?> builder) {
        return getPocketDirectory(key).newPocket(key, builder);
    }

    @Override
    public List<? extends RegistryVertex> collectVertices() {
        return new ArrayList<>(this.pocketEntrancePointers.values());
    }

    @Override
    public Type<PocketRegistry> type() {
        return SubsystemTypes.POCKET;
    }

    public static PocketRegistry getInstance() {
        return getInstance(SubsystemTypes.POCKET);
    }

    public <T extends Pocket<?, ?>> T getPocket(PocketInfo info, Class<T> clazz) {
        var directory = getPocketDirectory(info.world());
        return directory == null ? null : directory.getPocket(info.id(), clazz);
    }

    public Set<Location> getPocketEntrances(Pocket<?, ?> pocket) {
        Objects.requireNonNull(pocket, "pocket");
        return this.getPocketEntrances(new PocketInfo(pocket.getWorld(), pocket.getId()));
    }

    public Set<Location> getPocketEntrances(PocketInfo info) {
        Objects.requireNonNull(info, "info");

        PocketEntrancePointer pointer = this.pocketEntrancePointers.get(info);
        if (pointer == null) {
            return Collections.emptySet();
        }

        return RiftGraph.getInstance().targets(pointer).stream()
                .map(RiftRegistry.getInstance()::findRift)
                .flatMap(Optional::stream)
                .map(Rift::getLocation)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Location getPocketEntrance(Pocket<?, ?> pocket) {
        Objects.requireNonNull(pocket, "pocket");
        return this.getPocketEntrance(new PocketInfo(pocket.getWorld(), pocket.getId()));
    }

    public Location getPocketEntrance(PocketInfo info) {
        return this.getPocketEntrances(info).stream()
                .findFirst()
                .orElse(null);
    }

    public void addPocketEntrance(Pocket<?, ?> pocket, Location location) {
        Objects.requireNonNull(pocket, "pocket");
        Objects.requireNonNull(location, "location");

        PocketInfo info = new PocketInfo(pocket.getWorld(), pocket.getId());
        PocketEntrancePointer pointer = this.pocketEntrancePointers.computeIfAbsent(info, key -> {
            PocketEntrancePointer created = new PocketEntrancePointer(key.world(), key.id());
            RiftGraph.getInstance().addVertex(created);
            return created;
        });

        Rift rift = RiftRegistry.getInstance().getRift(location);
        if (RiftGraph.getInstance().addEdge(pointer, rift)) {
            this.setDirty();
        }
    }

    public boolean removePocketReferences(Pocket<?, ?> pocket) {
        Objects.requireNonNull(pocket, "pocket");
        return this.removePocketReferences(pocket.getWorld(), pocket.getId());
    }

    public boolean removePocketReferences(ResourceKey<Level> world, int pocketId) {
        Objects.requireNonNull(world, "world");

        PocketEntrancePointer pointer = this.pocketEntrancePointers.remove(new PocketInfo(world, pocketId));
        if (pointer == null) {
            return false;
        }

        Set<Rift> affectedRifts = new LinkedHashSet<>();
        if (RiftGraph.getInstance().containsVertex(pointer)) {
            RiftGraph.getInstance().sources(pointer).stream()
                    .map(RiftRegistry.getInstance()::findRift)
                    .flatMap(Optional::stream)
                    .forEach(affectedRifts::add);
            RiftGraph.getInstance().targets(pointer).stream()
                    .map(RiftRegistry.getInstance()::findRift)
                    .flatMap(Optional::stream)
                    .forEach(affectedRifts::add);

            RiftGraph.getInstance().removeVertex(pointer);
        }

        affectedRifts.forEach(Rift::markDirty);
        this.setDirty();
        return true;
    }

    public Pocket<?, ?> getPocketAt(Location location) {
        var directory = directories.get(location.world);
        if(directory == null) return null;
        return directory.getPocketAt(location.getBlockPos());
    }

    public <P extends Pocket<?, ?>> P getPocketAt(Location location, Class<P> pocketClass) {
        var pocket = getPocketAt(location);

        return castOrNull(pocket, pocketClass);
    }
}
