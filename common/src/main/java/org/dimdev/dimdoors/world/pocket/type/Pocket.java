package org.dimdev.dimdoors.world.pocket.type;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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

	public static <T extends Pocket> Products.P6<RecordCodecBuilder.Mu<T>, Integer, ResourceKey<Level>, Integer, BoundingBox, VirtualLocation, List<PocketAddon>> pocketFields(RecordCodecBuilder.Instance<T> instance) {
		return AbstractPocket.commonFields(instance)
				.and(Codec.INT.fieldOf("range").forGetter(Pocket::getRange))
				.and(BoundingBox.CODEC.fieldOf("box").forGetter(Pocket::getBox))
				.and(VirtualLocation.CODEC.fieldOf("virtualLocation").forGetter(Pocket::getVirtualLocation))
				.and(PocketAddon.CODEC.listOf().fieldOf("addons").<T>forGetter(Pocket::getAddons));
	}

	public static Codec<Pocket> CODEC = RecordCodecBuilder.create(instance -> Pocket.pocketFields(instance).apply(instance, Pocket::new));

	public static String KEY = "pocket";

	private final List<PocketAddon> addons;
	private int range = -1;
	private BoundingBox box; // TODO: make protected
	private VirtualLocation virtualLocation;

	public Pocket(int id, ResourceKey<Level> world, int range, BoundingBox box, VirtualLocation virtualLocation, List<PocketAddon> addons) {
		this.id = id;
		this.world = world;
		this.range = range;
		this.box = box;
		this.virtualLocation = virtualLocation;
		this.addons = addons;
	}

	public Pocket(int id, ResourceKey<Level> world, int x, int z) {
		super(id, world);
		int gridSize = DimensionalRegistry.getPocketDirectory(world).getGridSize() * 16;
		this.box = BoundingBox.fromCorners(new Vec3i(x * gridSize, 0, z * gridSize), new Vec3i((x + 1) * gridSize, 0, (z + 1) * gridSize));
		this.virtualLocation = new VirtualLocation(world, x, z, 0);
		this.addons = new ArrayList<>();
	}

	public <C extends PocketAddon> boolean addAddon(C addon) {
		if (addon.applicable(this)) {
			addons.add(addon);
			return true;
		}
		return false;
	}

	public <C extends PocketAddon> Optional<C> getAddon(ResourceLocation id) {
		return (Optional<C>) addons.stream().filter(object -> id.equals(object.getId())).findAny();
	}

	public <T> List<T> getAddons(Class<T> clazz) {
		return addons.stream()
				.filter(clazz::isInstance)
				.map(clazz::cast)
				.collect(Collectors.toList());
	}

	public List<PocketAddon> getAddons(Predicate<PocketAddon> clazz) {
		return addons.stream()
				.filter(clazz)
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

	public List<PocketAddon> getAddons() {
		return addons;
	}

	public CompoundTag toNbt(CompoundTag nbt) {
		super.toNbt(nbt);

		nbt.putInt("range", range);
		nbt.putIntArray("box", IntStream.of(this.box.minX(), this.box.minY(), this.box.minZ(), this.box.maxX(), this.box.maxY(), this.box.maxZ()).toArray());
		nbt.put("virtualLocation", VirtualLocation.toNbt(this.getVirtualLocation()));

		ListTag addonsTag = new ListTag();
		addonsTag.addAll(addons.stream().map(addon -> addon.toNbt(new CompoundTag())).collect(Collectors.toList()));
		if (addonsTag.size() > 0) nbt.put("addons", addonsTag);

		return nbt;
	}

	@Override
	public AbstractPocketType<?> getType() {
		return AbstractPocketType.POCKET.get();
	}

	public Pocket fromNbt(CompoundTag nbt) {
		super.fromNbt(nbt);

		this.range = nbt.getInt("range");
		int[] box = nbt.getIntArray("box");
		this.box = BoundingBox.fromCorners(new Vec3i(box[0], box[1], box[2]), new Vec3i(box[3], box[4], box[5]));
		this.virtualLocation = VirtualLocation.fromNbt(nbt.getCompound("virtualLocation"));

		if (nbt.contains("addons", Tag.TAG_LIST)) {
			for (Tag addonTag : nbt.getList("addons", Tag.TAG_COMPOUND)) {
				PocketAddon addon = PocketAddon.deserialize((CompoundTag) addonTag);
				addons.add(addon);
			}
		}

		return this;
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
		variableMap.put("depth", (double) this.getVirtualLocation().getDepth());
		return variableMap;
	}

	@Override
	public Pocket getReferencedPocket() {
		return this;
	}

	public void expand(int amount) {
		this.box.inflatedBy(amount);
	}

	public static PocketBuilder<?, Pocket> builder() {
		return new PocketBuilder(AbstractPocketType.POCKET.get());
	}

	protected void setBox(BoundingBox box) {
		this.box = box;
	}

	public VirtualLocation getVirtualLocation() {
		return virtualLocation;
	}

	// TODO: flesh this out a bit more, stuff like box() makes little sense in how it is implemented atm
	public static class PocketBuilder<P extends PocketBuilder<P, T>, T extends Pocket> extends AbstractPocketBuilder<P, T> {
		private final Map<ResourceLocation, PocketAddon.PocketBuilderAddon<?>> addons = new HashMap<>();

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
		public P fromNbt(CompoundTag nbt) {
			if (nbt.contains("addons", Tag.TAG_LIST)) {
				for (Tag addonTag : nbt.getList("addons", Tag.TAG_COMPOUND)) {
					PocketAddon.PocketBuilderAddon<?> addon = PocketAddon.deserializeBuilder((CompoundTag) addonTag);
					addons.put(addon.getId(), addon);
				}
			}

			return getSelf();
		}

		public CompoundTag toNbt(CompoundTag nbt) {
			ListTag addonsTag = new ListTag();
			addonsTag.addAll(addons.values().stream().map(addon -> addon.toNbt(new CompoundTag())).collect(Collectors.toList()));
			if (addonsTag.size() > 0) nbt.put("addons", addonsTag);

			return nbt;
		}

		public boolean hasAddon(ResourceLocation id) {
			return addons.containsKey(id);
		}

		protected <C extends PocketAddon.PocketBuilderAddon<?>> boolean addAddon(C addon) {
			if (addon.applicable(this)) {
				addon.addAddon(addons);
				return true;
			}
			return false;
		}

		public <C extends PocketAddon.PocketBuilderAddon<?>> C getAddon(ResourceLocation id) {
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
			instance.setVirtualLocation(virtualLocation);

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

	public void setVirtualLocation(VirtualLocation virtualLocation) {
		this.virtualLocation = virtualLocation;
	}
}
