package org.dimdev.dimdoors.world.pocket.type;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
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
import java.util.stream.IntStream;

public class Pocket extends AbstractPocket<Pocket> implements AddonProvider {
	public static String KEY = "pocket";

	private final Map<PocketAddon.PocketAddonType<?, ?>, PocketAddon> addons = new HashMap<>();
	private int range = -1;
	protected BoundingBox box;
	public VirtualLocation virtualLocation;
    private Map<BlockPos, BlockEntity> cachedBlockEntities = null;

	public Pocket(int id, ResourceKey<Level> world, int x, int z) {
		super(id, world);
		int gridSize = DimensionalRegistry.getPocketDirectory(world).getGridSize() * 16;
		this.box = BoundingBox.fromCorners(new Vec3i(x * gridSize, 0, z * gridSize), new Vec3i((x + 1) * gridSize, 0, (z + 1) * gridSize));
		this.virtualLocation = new VirtualLocation(world, x, z, 0);
	}

	protected Pocket() {
	}

	public boolean hasAddon(ResourceLocation id) {
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

    public <T extends PocketAddon> Optional<T> getAddon(PocketAddon.PocketAddonType<T, ?>  type) {
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
		if (this.range > 0) throw new UnsupportedOperationException("Cannot set range of Pocket that has already been initialized.");
		this.range = range;
	}

	public int getRange() {
		if (range < 1) throw new UnsupportedOperationException("Range of pocket has not been initialized yet.");
		return range;
	}

	public Vec3i getSize() {
		return this.box.getLength();
	}

	public CompoundTag toNbt(CompoundTag nbt, HolderLookup.Provider provider) {
		super.toNbt(nbt, provider);

		nbt.putInt("range", range);
		nbt.putIntArray("box", IntStream.of(this.box.minX(), this.box.minY(), this.box.minZ(), this.box.maxX(), this.box.maxY(), this.box.maxZ()).toArray());
		nbt.put("virtualLocation", VirtualLocation.toNbt(this.virtualLocation));

        PocketAddon.LIST_CODEC.encodeStart(NbtOps.INSTANCE, new ArrayList<>(addons.values())).result().ifPresent(tag -> nbt.put("addons", tag));

		return nbt;
	}

	@Override
	public AbstractPocketType<?> getType() {
		return AbstractPocketType.POCKET.get();
	}

	public Pocket fromNbt(CompoundTag nbt, HolderLookup.Provider provider) {
		super.fromNbt(nbt, provider);

		this.range = nbt.getInt("range");
		int[] box = nbt.getIntArray("box");
		this.box = BoundingBox.fromCorners(new Vec3i(box[0], box[1], box[2]), new Vec3i(box[3], box[4], box[5]));
		this.virtualLocation = VirtualLocation.fromNbt(nbt.getCompound("virtualLocation"));

        if (nbt.contains("addons", Tag.TAG_LIST)) {
            PocketAddon.LIST_CODEC.parse(NbtOps.INSTANCE, nbt.get("addons")).result().ifPresent(addons -> {
                addons.forEach(a -> a.addAddon(this.addons));
            });
		}

		return this;
	}

    public void cacheBlockEntities(List<BlockEntity> entities) {
        this.cachedBlockEntities = entities.stream()
                .collect(Collectors.toMap(BlockEntity::getBlockPos, be -> be));
    }

    public Map<BlockPos, BlockEntity> getBlockEntities() {
//        if (cachedBlockEntities != null) {
//            return cachedBlockEntities;
//        }

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
	public Pocket getReferencedPocket() {
		return this;
	}

	public void expand(int amount) {
		this.box = this.box.inflatedBy(amount);
	}

	public static PocketBuilder<?, Pocket> builder() {
		return new PocketBuilder(AbstractPocketType.POCKET.get());
	}

	protected void setBox(BoundingBox box) {
		this.box = box;
	}

	// TODO: flesh this out a bit more, stuff like box() makes little sense in how it is implemented atm
	public static class PocketBuilder<P extends PocketBuilder<P, T>, T extends Pocket> extends AbstractPocketBuilder<P, T> {
		private Map<PocketAddon.PocketAddonType<?, ?>, PocketAddon.PocketBuilderAddon<?, ?>> addons = new HashMap<>();

		private Vec3i origin = new Vec3i(0, 0, 0);
		private Vec3i size = new Vec3i(0, 0, 0);
		private Vec3i expected = new Vec3i(0, 0, 0);
		private VirtualLocation virtualLocation;
		private int range = -1;

		protected PocketBuilder(AbstractPocketType<T> type) {
			super(type);
			initAddons();
		}

		public void initAddons() {

		}

		// TODO: actually utilize fromTag/ toTag methods + implement them
		public P fromNbt(CompoundTag nbt, HolderLookup.Provider provider) {
            if (nbt.contains("addons", Tag.TAG_LIST)) {
                PocketAddon.LIST_BUILDER_CODEC.decode(NbtOps.INSTANCE, nbt.get("addons")).map(a -> a.getFirst()).result().ifPresent(tag -> {
                    tag.forEach(addon -> this.addons.put(addon.getType(), addon));
                });
			}

			return getSelf();
		}

		public CompoundTag toNbt(CompoundTag nbt, HolderLookup.Provider provider) {
            PocketAddon.LIST_BUILDER_CODEC.encodeStart(NbtOps.INSTANCE, this.addons.values().stream().toList()).result().ifPresent(tag -> {
                nbt.put("addons", tag);
            });

			return nbt;
		}

        public AbstractPocketType<?> getType() {
            return AbstractPocketType.POCKET.get();
        }

        public boolean hasAddon(ResourceLocation id) {
			return addons.containsKey(id);
		}

		protected <C extends PocketAddon.PocketBuilderAddon<?, ?>> boolean addAddon(C addon) {
			if (addon.applicable(this)) {
				addon.addAddon(addons);
				return true;
			}
			return false;
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
