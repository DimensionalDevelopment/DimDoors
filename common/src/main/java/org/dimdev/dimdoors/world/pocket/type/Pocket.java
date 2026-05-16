package org.dimdev.dimdoors.world.pocket.type;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.addon.AddonProvider;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Pocket<T extends Pocket<T, V>, V extends Pocket.PocketBuilder<T, V>> extends AbstractPocket<T, V> implements AddonProvider {
    public static String KEY = "pocket";

    public static <T extends Pocket<?, ?>> Products.P6<RecordCodecBuilder.Mu<T>, Integer, ResourceKey<Level>, Integer, BoundingBox, VirtualLocation, List<PocketAddon>> commonPocketFields(RecordCodecBuilder.Instance<T> instance) {
        return commonFields(instance).and(Codec.INT.fieldOf("range").forGetter(a -> a.getRange()))
                .and(BoundingBox.CODEC.fieldOf("box").forGetter(a -> a.getBox()))
                .and(VirtualLocation.CODEC.fieldOf("virtualLocation").forGetter(a -> a.virtualLocation))
                .and(PocketAddon.LIST_CODEC.fieldOf("addons").forGetter(a -> a.addons.values().stream().toList()));
    }


    protected final Map<PocketAddon.PocketAddonType<?, ?>, PocketAddon> addons = new HashMap<>();
    private int range = -1;
    protected BoundingBox box;
    public VirtualLocation virtualLocation;

    public Pocket(int id, ResourceKey<Level> world, int range, BoundingBox box, VirtualLocation virtualLocation, List<PocketAddon> addons) {
        super(id, world);
        this.range = range;
        this.box = box;
        this.virtualLocation = virtualLocation;

        addons.forEach(addon -> this.addons.put(addon.getType(), addon));
    }

    public Pocket(int id, ResourceKey<Level> world, int x, int z) {
        super(id, world);
        int gridSize = DimensionalRegistry.getPocketDirectory(world).getGridSize() * 16;
        this.box = BoundingBox.fromCorners(new Vec3i(x * gridSize, 0, z * gridSize), new Vec3i((x + 1) * gridSize, 0, (z + 1) * gridSize));
        this.virtualLocation = new VirtualLocation(world, x, z, 0);
    }

    protected Pocket() {
    }

    public boolean hasAddon(PocketAddon.PocketAddonType<?, ?> id) {
        return addons.containsKey(id);
    }

    public <C extends PocketAddon> boolean addAddon(C addon) {
        if (addon.applicable(this)) {
            addon.addAddon(addons);
            return true;
        }
        return false;
    }

    public <C extends PocketAddon> C getAddon(ResourceLocation id) {
        return (C) addons.get(id);
    }

    public List<PocketAddon> getAddons(Predicate<PocketAddon> predicate) {
        return addons.values().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public <T extends PocketAddon> Optional<T> getAddon(PocketAddon.PocketAddonType<T, ?> type) {
        return Optional.ofNullable((T) addons.get(type));
    }

    public boolean isInBounds(BlockPos pos) {
        return this.box.isInside(pos);
    }

    public BlockPos getOrigin() {
        return new BlockPos(this.box.minX(), this.box.minY(), this.box.minZ());
    }

    public void offsetOrigin(Vec3i vec) {
        this.box.move(vec);
    }

    public void offsetOrigin(int x, int y, int z) {
        this.box.move(x, y, z);
    }

    public void setSize(Vec3i size) {
        setSize(size.getX(), size.getY(), size.getZ());
    }

    public void setSize(int x, int y, int z) {
        this.box = BoundingBox.fromCorners(new Vec3i(this.box.minX(), this.box.minY(), this.box.minZ()), new Vec3i(this.box.minX() + x - 1, this.box.minY() + y - 1, this.box.minZ() + z - 1));
    }

    public void setRange(int range) {
        if (this.range > 0)
            throw new UnsupportedOperationException("Cannot set range of Pocket that has already been initialized.");
        this.range = range;
    }

    public int getRange() {
        if (range < 1) throw new UnsupportedOperationException("Range of pocket has not been initialized yet.");
        return range;
    }

    public Vec3i getSize() {
        return this.box.getLength();
    }

    public Map<BlockPos, BlockEntity> getBlockEntities() {

        Level serverWorld = DimensionalDoors.getWorld(this.getWorld());
        Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();

        // VERIFY THIS LINE - should be minZ() not minY()
        ChunkPos minChunk = new ChunkPos(box.minX() >> 4, box.minZ() >> 4);
        ChunkPos maxChunk = new ChunkPos(box.maxX() >> 4, box.maxZ() >> 4);

        ChunkPos.rangeClosed(minChunk, maxChunk).forEach(chunkPos -> {
            serverWorld.getChunk(chunkPos.x, chunkPos.z)
                    .getBlockEntities()
                    .forEach((blockPos, blockEntity) -> {
                        if (this.box.isInside(blockPos)) {
                            blockEntities.put(blockPos, blockEntity);
                        }
                    });
        });

        return blockEntities;
    }

    public BoundingBox getBox() {
        return box;
    }

    public Map<String, Double> toVariableMap(Map<String, Double> variableMap) {
        variableMap = super.toVariableMap(variableMap);
        variableMap.put("originX", (double) this.box.minX());
        variableMap.put("originY", (double) this.box.minY());
        variableMap.put("originZ", (double) this.box.minZ());
        variableMap.put("width", (double) this.box.getLength().getX());
        variableMap.put("height", (double) this.box.getLength().getY());
        variableMap.put("length", (double) this.box.getLength().getZ());
        variableMap.put("depth", (double) this.virtualLocation.getDepth());
        return variableMap;
    }

    @Override
    public Pocket<?, ?> getReferencedPocket() {
        return this;
    }

    public void expand(int amount) {
        this.box = this.box.inflatedBy(amount);
    }

    protected void setBox(BoundingBox box) {
        this.box = box;
    }

    // TODO: flesh this out a bit more, stuff like box() makes little sense in how it is implemented atm
    public static abstract class PocketBuilder<T extends Pocket<T, P>, P extends PocketBuilder<T, P>> extends AbstractPocketBuilder<T, P> {

        protected Map<PocketAddon.PocketAddonType<?, ?>, PocketAddon.PocketBuilderAddon<?, ?>> addons = new HashMap<>();

        protected Vec3i origin = new Vec3i(0, 0, 0);
        protected Vec3i size = new Vec3i(0, 0, 0);
        protected Vec3i expected = new Vec3i(0, 0, 0);
        protected VirtualLocation virtualLocation;
        protected int range = -1;

        public PocketBuilder(List<PocketAddon.PocketBuilderAddon<?, ?>> addons) {
            this.addons = Util.make(new HashMap<>(), map -> {
                addons.forEach(pocketBuilderAddon -> map.put(pocketBuilderAddon.getType(), pocketBuilderAddon));
            });
        }

        protected PocketBuilder() {
            super();
            initAddons();
        }



        protected static <T extends PocketBuilder<?, ?>> Products.P1<RecordCodecBuilder.Mu<T>, List<PocketAddon.PocketBuilderAddon<?,?>>> commonFields(RecordCodecBuilder.Instance<T> instance) {
            return instance.group(
                    PocketAddon.LIST_BUILDER_CODEC.optionalFieldOf("addons", List.of()).<T>forGetter(t -> t.addons.values().stream().toList()));
        }

        public void initAddons() {

        }

        public boolean hasAddon(PocketAddon.PocketAddonType<?, ?> id) {
            return addons.containsKey(id);
        }

        protected <C extends PocketAddon.PocketBuilderAddon<?, ?>> void addAddon(C addon) {
            if (addon.applicable(this)) {
                addon.addAddon(addons);
            }
        }

        public <C extends PocketAddon.PocketBuilderAddon<?, ?>> C getAddon(PocketAddon.PocketAddonType<?, ?> type) {
            return (C) addons.get(type);
        }

        @Override
        public Vec3i getExpectedSize() {
            return expected;
        }

        public T build() {
            if (range < 1) throw new RuntimeException("Cannot create pocket with range < 1");

            T instance = super.build();

            instance.setRange(range);
            instance.setBox(BoundingBox.fromCorners(new Vec3i(origin.getX(), origin.getY(), origin.getZ()), new Vec3i(origin.getX() + size.getX() - 1, origin.getY() + size.getY() - 1, origin.getZ() + size.getZ() - 1)));
            instance.virtualLocation = virtualLocation;

            addons.values().forEach(addon -> addon.apply(instance));

            return instance;
        }

        @Override
        public P copy() {
            var copy = super.copy();
            copy.range = range;
            copy.origin = origin;
            copy.size = size;
            copy.expected = expected;
            copy.virtualLocation = virtualLocation;
            copy.addons = Maps.newHashMap(addons);
            return copy;
        }

        public P offsetOrigin(Vec3i offset) {
            this.origin = new Vec3i(origin.getX() + offset.getX(), origin.getY() + offset.getY(), origin.getZ() + offset.getZ());
            return getSelf();
        }

        public P expand(Vec3i expander) {
            this.size = new Vec3i(size.getX() + expander.getX(), size.getY() + expander.getY(), size.getZ() + expander.getZ());
            this.expected = new Vec3i(expected.getX() + expander.getX(), expected.getY() + expander.getY(), expected.getZ() + expander.getZ());
            return getSelf();
        }

        public P expandExpected(Vec3i expander) {
            this.expected = new Vec3i(expected.getX() + expander.getX(), expected.getY() + expander.getY(), expected.getZ() + expander.getZ());
            return getSelf();
        }

        public P virtualLocation(VirtualLocation virtualLocation) {
            this.virtualLocation = virtualLocation;
            return getSelf();
        }

        public P range(int range) {
            this.range = range;
            return getSelf();
        }
    }
}
