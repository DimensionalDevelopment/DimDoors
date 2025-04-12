package org.dimdev.dimdoors.world.pocket.type;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.pockets.modifier.NoneModifer;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.addon.AddonProvider;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Pocket extends AbstractPocket implements AddonProvider {

    public static <T extends Pocket> Products.P6<RecordCodecBuilder.Mu<T>, Integer, ResourceKey<Level>, Integer, BoundingBox, VirtualLocation, Map<ResourceLocation, PocketAddon>> commonPocketFields(RecordCodecBuilder.Instance<T> instance) {
		return commonCodecFields(instance)
				.and(Codec.INT.fieldOf("range").forGetter(a -> a.range))
				.and(BoundingBox.CODEC.fieldOf("box").forGetter(a -> a.box))
				.and(VirtualLocation.CODEC.fieldOf("virtualLocation").forGetter(a -> a.virtualLocation))
				.and(Codec.unboundedMap(ResourceLocation.CODEC, PocketAddon.CODEC).xmap(m -> (Map<ResourceLocation, PocketAddon>) new HashMap<>(m), Function.identity()).optionalFieldOf("addons", new HashMap<>()).forGetter(a -> a.addons));
	}

	protected Map<ResourceLocation, PocketAddon> addons = new HashMap<>();
	protected int range = -1;
	protected BoundingBox box;
	public VirtualLocation virtualLocation;

	public Pocket(int id, ResourceKey<Level> world, int x, int z) {
		super(id, world);
		int gridSize = DimensionalRegistry.getPocketDirectory(world).getGridSize() * 16;
		this.box = BoundingBox.fromCorners(new Vec3i(x * gridSize, 0, z * gridSize), new Vec3i((x + 1) * gridSize, 0, (z + 1) * gridSize));
		this.virtualLocation = new VirtualLocation(world, x, z, 0);
		this.addons = new HashMap<>();
	}

	protected Pocket() {
	}

	public Pocket(int id, ResourceKey<Level> world, int range, BoundingBox box, VirtualLocation virtualLocation, Map<ResourceLocation, PocketAddon> addons) {
		super(id, world);
        this.range = range;
        this.box = box;
        this.virtualLocation = virtualLocation;
        this.addons = addons;
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

	public <T> List<T> getAddonsInstanceOf(Class<T> clazz) {
		return addons.values().stream()
				.filter(clazz::isInstance)
				.map(clazz::cast)
				.collect(Collectors.toList());
	}

	public List<PocketAddon> getSyncedAddon() {
		return addons.values().stream()
				.filter(a -> a.getType().isSyncable())
				.collect(Collectors.toList());
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

	public Map<BlockPos, BlockEntity> getBlockEntities() {
		Level serverWorld = DimensionalDoors.getWorld(this.getWorld());
		Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();
		ChunkPos.rangeClosed(new ChunkPos(new BlockPos(box.minX(), box.minY(), box.minY())), new ChunkPos(new BlockPos(box.maxX(), box.maxY(), box.maxZ()))).forEach(chunkPos -> serverWorld.getChunk(chunkPos.x, chunkPos.z).getBlockEntities().forEach((blockPos, blockEntity) -> {
			if (this.box.isInside(blockPos)) blockEntities.put(blockPos, blockEntity);
		}));
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
		this.box.inflatedBy(amount);
	}

	protected void setBox(BoundingBox box) {
		this.box = box;
	}

	// TODO: flesh this out a bit more, stuff like box() makes little sense in how it is implemented atm
	public static abstract class PocketBuilder<P extends PocketBuilder<P, T>, T extends Pocket> extends AbstractPocketBuilder<P, T> {
		protected final Map<ResourceLocation, PocketAddon.PocketBuilderAddon<?, ?>> addons = new HashMap<>();

		protected Vec3i origin = new Vec3i(0, 0, 0);
		protected Vec3i size = new Vec3i(0, 0, 0);
		protected Vec3i expected = new Vec3i(0, 0, 0);
		protected VirtualLocation virtualLocation;
		protected int range = -1;

		protected PocketBuilder() {
			this(new Vec3i(0,0,0), new Vec3i(0,0,0), null, -1);
		}

		protected PocketBuilder(Vec3i origin, Vec3i size, VirtualLocation virtualLocation, int range) {
            this.origin = origin;
            this.size = size;
            this.virtualLocation = virtualLocation;
            this.range = range;

			initAddons();
        }


		public static <P extends PocketBuilder<?, ?>> Products.P5<RecordCodecBuilder.Mu<P>, Vec3i, Vec3i, Optional<VirtualLocation>, Integer, Map<ResourceLocation, PocketAddon.PocketBuilderAddon<?, ?>>> commonPocketBuilderFields(RecordCodecBuilder.Instance<P> instance) {
			return instance.group(
					Vec3i.CODEC.optionalFieldOf("origin", new Vec3i(0, 0, 0)).<P>forGetter(a -> a.origin),
					Vec3i.CODEC.optionalFieldOf("size", new Vec3i(0, 0, 0)).<P>forGetter(a -> a.origin),
					VirtualLocation.CODEC.optionalFieldOf("virtualLocation").<P>forGetter(a -> Optional.ofNullable(a.virtualLocation)),
					Codec.INT.optionalFieldOf("range", -1).<P>forGetter(a -> a.range),
					Codec.unboundedMap(ResourceLocation.CODEC, PocketAddon.BUILDER_CODEC).optionalFieldOf("addons", new HashMap<>()).forGetter(a -> a.addons));
		}

		public void initAddons() {

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

		public P addons(Map<ResourceLocation, PocketAddon.PocketBuilderAddon<?,?>> addons) {
			this.addons.putAll(addons);
			return (P) this;
		}

		public <C extends PocketAddon.PocketBuilderAddon<?, ?>> C getAddon(ResourceLocation id) {
			return (C) addons.get(id);
		}

		@Override
		public Vec3i getExpectedSize() {
			return expected;
		}

		public T build() {
			if (range < 1) throw new RuntimeException("Cannot create pocket with range < 1");

			T instance = super.build();

			instance.setRange(range);
			instance.setBox(BoundingBox.fromCorners(new Vec3i(origin.getX(), origin.getY(), origin.getZ()), new Vec3i(origin.getX() + size.getX(), origin.getY() + size.getY(), origin.getZ() + size.getZ())));
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
