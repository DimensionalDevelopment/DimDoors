package org.dimdev.dimdoors.world.pocket;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import com.flowpowered.math.vector.Vector3i;
import com.mojang.serialization.Codec;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.AncientFabricBlock;
import org.dimdev.dimdoors.block.FabricBlock;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.world.level.DimensionalRegistry;
import org.dimdev.dimdoors.util.EntityUtils;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public final class Pocket {
	private static final int BLOCKS_PAINTED_PER_DYE = 1000000;

	// TODO: please someone make all these private and add a getter & setter where needed
	public final int id;
	public BlockBox box;
	public VirtualLocation virtualLocation;
	public PocketColor dyeColor = PocketColor.WHITE;
	public PocketColor nextDyeColor = PocketColor.NONE;
	public int count = 0;

	public RegistryKey<World> world;

	private Pocket(int id, BlockBox box, VirtualLocation virtualLocation, PocketColor dyeColor, PocketColor nextDyeColor, int count, RegistryKey<World> world) {
		this.id = id;
		this.box = box;
		this.virtualLocation = virtualLocation;
		this.dyeColor = dyeColor;
		this.nextDyeColor = nextDyeColor;
		this.count = count;
		this.world = world;
	}

	public Pocket(int id, RegistryKey<World> world, int x, int z) {
		int gridSize = DimensionalRegistry.getPocketDirectory(world).getGridSize() * 16;
		this.id = id;
		this.world = world;
		this.box = BlockBox.create(x * gridSize, 0, z * gridSize, (x + 1) * gridSize, 0, (z + 1) * gridSize);
		this.virtualLocation = new VirtualLocation(world, x, z, 0);
	}

	boolean isInBounds(BlockPos pos) {
		return this.box.contains(pos);
	}

	public BlockPos getOrigin() {
		return new BlockPos(this.box.minX, this.box.minY, this.box.minZ);
	}

	public void offsetOrigin(Vec3i vec) {
		offsetOrigin(vec.getX(), vec.getY(), vec.getZ());
	}

	public void offsetOrigin(int x, int y, int z) {
		this.box = box.offset(x, y, z);
	}

	public boolean addDye(Entity entity, DyeColor dyeColor) {
		PocketColor color = PocketColor.from(dyeColor);

		int maxDye = amountOfDyeRequiredToColor(this);

		if (this.dyeColor == color) {
			EntityUtils.chat(entity, new TranslatableText("dimdoors.pockets.dyeAlreadyAbsorbed"));
			return false;
		}

		if (this.nextDyeColor != PocketColor.NONE && this.nextDyeColor == color) {
			if (this.count + 1 > maxDye) {
				repaint(dyeColor);
				this.dyeColor = color;
				this.nextDyeColor = PocketColor.NONE;
				this.count = 0;
				EntityUtils.chat(entity, new TranslatableText("dimdoors.pocket.pocketHasBeenDyed", dyeColor));
			} else {
				this.count++;
				EntityUtils.chat(entity, new TranslatableText("dimdoors.pocket.remainingNeededDyes", this.count, maxDye, color));
			}
		} else {
			this.nextDyeColor = color;
			this.count = 1;
			EntityUtils.chat(entity, new TranslatableText("dimdoors.pocket.remainingNeededDyes", this.count, maxDye, color));
		}
		return true;
	}

    private void repaint(DyeColor dyeColor) {
        ServerWorld serverWorld = DimensionalDoorsInitializer.getWorld(world);
        BlockState innerWall = ModBlocks.fabricFromDye(dyeColor).getDefaultState();
        BlockState outerWall = ModBlocks.ancientFabricFromDye(dyeColor).getDefaultState();

		BlockPos.stream(box).forEach(pos -> {
			if (serverWorld.getBlockState(pos).getBlock() instanceof AncientFabricBlock) {
				serverWorld.setBlockState(pos, outerWall);
			} else if (serverWorld.getBlockState(pos).getBlock() instanceof FabricBlock) {
				serverWorld.setBlockState(pos, innerWall);
			}
		});
    }

	private static int amountOfDyeRequiredToColor(Pocket pocket) {
		int outerVolume = pocket.box.getBlockCountX() * pocket.box.getBlockCountY() * pocket.box.getBlockCountZ();
		int innerVolume = (pocket.box.getBlockCountX() - 5) * (pocket.box.getBlockCountY() - 5) * (pocket.box.getBlockCountZ() - 5);

		return Math.max((outerVolume - innerVolume) / BLOCKS_PAINTED_PER_DYE, 1);
	}

	public void setSize(Vec3i size) {
		setSize(size.getX(), size.getY(), size.getZ());
	}

	public void setSize(int x, int y, int z) {
		this.box = BlockBox.create(this.box.minX, this.box.minY, this.box.minZ, this.box.minX + x - 1, this.box.minY + y - 1, this.box.minZ + z - 1);
	}

	public Vector3i getSize() {
		Vec3i dimensions = this.box.getDimensions();
		return new Vector3i(dimensions.getX(), dimensions.getY(), dimensions.getZ());
	}

	public CompoundTag toTag() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("id", this.id);
		tag.putIntArray("box", IntStream.of(this.box.minX, this.box.minY, this.box.minZ, this.box.maxX, this.box.maxY, this.box.maxZ).toArray());
		tag.put("virtualLocation", VirtualLocation.toTag(this.virtualLocation));
		tag.putInt("dyeColor", this.dyeColor.getId());
		tag.putInt("nextDyeColor", this.nextDyeColor.getId());
		tag.putInt("count", this.count);
		tag.putString("world", world.getValue().toString());
		return tag;
	}

	public static Pocket fromTag(CompoundTag tag) {
		int[] box = tag.getIntArray("box");
		return new Pocket(
				tag.getInt("id"),
				new BlockBox(box[0], box[1], box[2], box[3], box[4], box[5]),
				VirtualLocation.fromTag(tag.getCompound("virtualLocation")),
				PocketColor.from(tag.getInt("dyeColor")),
				PocketColor.from(tag.getInt("nextDyeColor")),
				tag.getInt("count"),
				RegistryKey.of(Registry.DIMENSION, new Identifier(tag.getString("world")))
		);
	}

	public enum PocketColor {
		WHITE(0, DyeColor.WHITE),
		ORANGE(1, DyeColor.ORANGE),
		MAGENTA(2, DyeColor.MAGENTA),
		LIGHT_BLUE(3, DyeColor.LIGHT_BLUE),
		YELLOW(4, DyeColor.YELLOW),
		LIME(5, DyeColor.LIME),
		PINK(6, DyeColor.PINK),
		GRAY(7, DyeColor.GRAY),
		LIGHT_GRAY(8, DyeColor.LIGHT_GRAY),
		CYAN(9, DyeColor.CYAN),
		PURPLE(10, DyeColor.PURPLE),
		BLUE(11, DyeColor.BLUE),
		BROWN(12, DyeColor.BROWN),
		GREEN(13, DyeColor.GREEN),
		RED(14, DyeColor.RED),
		BLACK(15, DyeColor.BLACK),
		NONE(16, null);

		private final int id;
		private final DyeColor color;

		public static Codec<PocketColor> CODEC = Codec.INT.xmap(PocketColor::from, PocketColor::getId);

		PocketColor(int id, DyeColor color) {
			this.id = id;
			this.color = color;
		}

		public DyeColor getColor() {
			return this.color;
		}

		public Integer getId() {
			return this.id;
		}

		public static PocketColor from(DyeColor color) {
			for (PocketColor a : PocketColor.values()) {
				if (color == a.color) {
					return a;
				}
			}

			return NONE;
		}

		public static PocketColor from(int id) {
			for (PocketColor a : PocketColor.values()) {
				if (id == a.id) {
					return a;
				}
			}

			return NONE;
		}
	}

	public Map<BlockPos, BlockEntity> getBlockEntities() {
		ServerWorld serverWorld = DimensionalDoorsInitializer.getWorld(this.world);
		Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();
		ChunkPos.stream(new ChunkPos(new BlockPos(box.minX, box.minY, box.minZ)), new ChunkPos(new BlockPos(box.maxX, box.maxY, box.maxZ))).forEach(chunkPos -> serverWorld.getChunk(chunkPos.x, chunkPos.z).getBlockEntities().forEach((blockPos, blockEntity) -> {
			if (this.box.contains(blockPos)) blockEntities.put(blockPos, blockEntity);
		}));
		return blockEntities;
	}

	public BlockBox getBox() {
		return box;
	}

	public Map<String, Double> toVariableMap(Map<String, Double> stringDoubleMap) {
		stringDoubleMap.put("originX", (double) this.box.minX);
		stringDoubleMap.put("originY", (double) this.box.minY);
		stringDoubleMap.put("originZ", (double) this.box.minZ);
		stringDoubleMap.put("width", (double) this.box.getDimensions().getX());
		stringDoubleMap.put("height", (double) this.box.getDimensions().getY());
		stringDoubleMap.put("length", (double) this.box.getDimensions().getZ());
		stringDoubleMap.put("depth", (double) this.virtualLocation.getDepth());
		stringDoubleMap.put("id", (double) this.id); // don't really know why you would need this but it's there if needed
		return stringDoubleMap;
	}

	public void expand(int amount) {
		if (amount == 0) return;
		this.box = BlockBox.create(box.minX - amount, box.minY - amount, box.minZ - amount, box.maxX + amount, box.maxY + amount, box.maxZ + amount);
	}
}
