package org.dimdev.dimdoors.world.pocket.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.addon.AddonProvider;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

public class Pocket extends AbstractPocket<Pocket> implements AddonProvider {
	public static String KEY = "pocket";

	private final Map<Identifier, PocketAddon> addons = new HashMap<>();
	private int range = -1;
	private BlockBox box; // TODO: make protected
	public VirtualLocation virtualLocation;

	public Pocket(int id, RegistryKey<World> world, int x, int z) {
		super(id, world);
		int gridSize = DimensionalRegistry.getPocketDirectory(world).getGridSize() * 16;
		this.box = BlockBox.create(new Vec3i(x * gridSize, 0, z * gridSize), new Vec3i((x + 1) * gridSize, 0, (z + 1) * gridSize));
		this.virtualLocation = new VirtualLocation(world, x, z, 0);
	}

	protected Pocket() {
	}

	public boolean hasAddon(Identifier id) {
		return addons.containsKey(id);
	}

	public <C extends PocketAddon> boolean addAddon(C addon) {
		if (addon.applicable(this)) {
			addon.addAddon(addons);
			return true;
		}
		return false;
	}

	public <C extends PocketAddon> C getAddon(Identifier id) {
		return (C) addons.get(id);
	}

	public <T> List<T> getAddonsInstanceOf(Class<T> clazz) {
		return addons.values().stream()
				.filter(clazz::isInstance)
				.map(clazz::cast)
				.collect(Collectors.toList());
	}

	public boolean isInBounds(BlockPos pos) {
		return this.box.contains(pos);
	}

	public BlockPos getOrigin() {
		return new BlockPos(this.box.getMinX(), this.box.getMinY(), this.box.getMinZ());
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
		this.box = BlockBox.create(new Vec3i(this.box.getMinX(), this.box.getMinY(), this.box.getMinZ()), new Vec3i(this.box.getMinX() + x - 1, this.box.getMinY() + y - 1, this.box.getMinZ() + z - 1));
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
		return this.box.getDimensions();
	}

	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);

		nbt.putInt("range", range);
		nbt.putIntArray("box", IntStream.of(this.box.getMinX(), this.box.getMinY(), this.box.getMinZ(), this.box.getMaxX(), this.box.getMaxY(), this.box.getMaxZ()).toArray());
		nbt.put("virtualLocation", VirtualLocation.toNbt(this.virtualLocation));

		NbtList addonsTag = new NbtList();
		addonsTag.addAll(addons.values().stream().map(addon -> addon.toNbt(new NbtCompound())).collect(Collectors.toList()));
		if (addonsTag.size() > 0) nbt.put("addons", addonsTag);

		return nbt;
	}

	@Override
	public AbstractPocketType<?> getType() {
		return AbstractPocketType.POCKET;
	}

	public Pocket fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);

		this.range = nbt.getInt("range");
		int[] box = nbt.getIntArray("box");
		this.box = BlockBox.create(new Vec3i(box[0], box[1], box[2]), new Vec3i(box[3], box[4], box[5]));
		this.virtualLocation = VirtualLocation.fromNbt(nbt.getCompound("virtualLocation"));

		if (nbt.contains("addons", NbtType.LIST)) {
			for (NbtElement addonTag : nbt.getList("addons", NbtType.COMPOUND)) {
				PocketAddon addon = PocketAddon.deserialize((NbtCompound) addonTag);
				addons.put(addon.getId(), addon);
			}
		}

		return this;
	}

	public Map<BlockPos, BlockEntity> getBlockEntities() {
		ServerWorld serverWorld = DimensionalDoorsInitializer.getWorld(this.getWorld());
		Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();
		ChunkPos.stream(new ChunkPos(new BlockPos(box.getMinX(), box.getMinY(), box.getMinZ())), new ChunkPos(new BlockPos(box.getMaxX(), box.getMaxY(), box.getMaxZ()))).forEach(chunkPos -> serverWorld.getChunk(chunkPos.x, chunkPos.z).getBlockEntities().forEach((blockPos, blockEntity) -> {
			if (this.box.contains(blockPos)) blockEntities.put(blockPos, blockEntity);
		}));
		return blockEntities;
	}

	public BlockBox getBox() {
		return box;
	}

	public Map<String, Double> toVariableMap(Map<String, Double> variableMap) {
		variableMap = super.toVariableMap(variableMap);
		variableMap.put("originX", (double) this.box.getMinX());
		variableMap.put("originY", (double) this.box.getMinY());
		variableMap.put("originZ", (double) this.box.getMinZ());
		variableMap.put("width", (double) this.box.getDimensions().getX());
		variableMap.put("height", (double) this.box.getDimensions().getY());
		variableMap.put("length", (double) this.box.getDimensions().getZ());
		variableMap.put("depth", (double) this.virtualLocation.getDepth());
		return variableMap;
	}

	@Override
	public Pocket getReferencedPocket() {
		return this;
	}

	public void expand(int amount) {
		this.box.expand(amount);
	}

	public static PocketBuilder<?, Pocket> builder() {
		return new PocketBuilder(AbstractPocketType.POCKET);
	}

	protected void setBox(BlockBox box) {
		this.box = box;
	}

	// TODO: flesh this out a bit more, stuff like box() makes little sense in how it is implemented atm
	public static class PocketBuilder<P extends PocketBuilder<P, T>, T extends Pocket> extends AbstractPocketBuilder<P, T> {
		private final Map<Identifier, PocketAddon.PocketBuilderAddon<?>> addons = new HashMap<>();

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
		public P fromNbt(NbtCompound nbt) {
			if (nbt.contains("addons", NbtType.LIST)) {
				for (NbtElement addonTag : nbt.getList("addons", NbtType.COMPOUND)) {
					PocketAddon.PocketBuilderAddon<?> addon = PocketAddon.deserializeBuilder((NbtCompound) addonTag);
					addons.put(addon.getId(), addon);
				}
			}

			return getSelf();
		}

		public NbtCompound toNbt(NbtCompound nbt) {
			NbtList addonsTag = new NbtList();
			addonsTag.addAll(addons.values().stream().map(addon -> addon.toNbt(new NbtCompound())).collect(Collectors.toList()));
			if (addonsTag.size() > 0) nbt.put("addons", addonsTag);

			return nbt;
		}

		public boolean hasAddon(Identifier id) {
			return addons.containsKey(id);
		}

		protected <C extends PocketAddon.PocketBuilderAddon<?>> boolean addAddon(C addon) {
			if (addon.applicable(this)) {
				addon.addAddon(addons);
				return true;
			}
			return false;
		}

		public <C extends PocketAddon.PocketBuilderAddon<?>> C getAddon(Identifier id) {
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
			instance.setBox(BlockBox.create(new Vec3i(origin.getX(), origin.getY(), origin.getZ()), new Vec3i(origin.getX() + size.getX(), origin.getY() + size.getY(), origin.getZ() + size.getZ())));
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
