package org.dimdev.dimdoors.world.pocket;

import java.util.stream.IntStream;

import com.flowpowered.math.vector.Vector3i;
import com.mojang.serialization.Codec;

import org.dimdev.dimdoors.world.level.DimensionalRegistry;
import org.dimdev.dimdoors.util.EntityUtils;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public final class Pocket {
	private static final int BLOCKS_PAINTED_PER_DYE = 1106;

	public final int id;
	public BlockBox box;
	public VirtualLocation virtualLocation;
	public PocketColor dyeColor = PocketColor.WHITE;
	public PocketColor nextDyeColor = PocketColor.NONE;
	public int count = 0;

	public RegistryKey<World> world;

	private Pocket(int id, BlockBox box, VirtualLocation virtualLocation, PocketColor dyeColor, PocketColor nextDyeColor, int count) {
		this.id = id;
		this.box = box;
		this.virtualLocation = virtualLocation;
		this.dyeColor = dyeColor;
		this.nextDyeColor = nextDyeColor;
		this.count = count;
	}

	public Pocket(int id, RegistryKey<World> world, int x, int z) {
		int gridSize = DimensionalRegistry.getPocketDirectory(world).getGridSize() * 16;
		this.id = id;
		this.world = world;
		this.box = new BlockBox(x * gridSize, 0, z * gridSize, (x + 1) * gridSize, 0, (z + 1) * gridSize);
		this.virtualLocation = new VirtualLocation(world, x, z, 0);
	}

	boolean isInBounds(BlockPos pos) {
		return this.box.contains(pos);
	}

	public BlockPos getOrigin() {
		return new BlockPos(this.box.minX, this.box.minY, this.box.minZ);
	}

	public void offsetOrigin(Vec3i vec) {
		this.box = box.offset(vec.getX(), vec.getY(), vec.getZ());
	}

	public boolean addDye(Entity entity, DyeColor dyeColor) {
		PocketColor color = PocketColor.from(dyeColor);

		int maxDye = amountOfDyeRequiredToColor(this);

		if (this.dyeColor == color) {
			EntityUtils.chat(entity, new TranslatableText("dimdoors.pockets.dyeAlreadyAbsorbed"));
			return false;
		}

		if (this.nextDyeColor != null && this.nextDyeColor == color) {
			if (this.count + 1 > amountOfDyeRequiredToColor(this)) {
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

//    private void repaint(DyeColor dyeColor) {
//        BlockPos origin = getOrigin();
//        World world = WorldUtils.getWorld(dim);
//        BlockState innerWall = ModBlocks.getDefaultState()..withProperty(..., dyeColor); // <-- forgot the exact name of the color property
//        BlockState outerWall = ModBlocks.ETERNAL_FABRIC.getDefaultState().withProperty(..., dyeColor);
//
//        for (int x = origin.getX(); x < origin.getX() + size; x++) {
//            for (int y = origin.getY(); y < origin.getY() + size; y++) {
//                for (int z = origin.getZ(); z < origin.getZ() + size; z++) {
//                    int layer = Collections.min(Arrays.asList(x, y, z, size - 1 - x, size - 1 - y, size - 1 - z));
//                    if (layer == 0) {
//                        if (world.getBlockState(x, y, z).getBlock() == ModBlocks.ETERNAL_FABRIC) {
//                            world.setBlockState(x, y, z, outerWall);
//                        }
//                    } else if (layer < 5) {
//                        if (world.getBlockState(x, y, z).getBlock() == ModBlocks.FABRIC) {
//                            world.setBlockState(x, y, z, innerWall);
//                        }
//                    }
//                }
//            }
//        }
//
//        return schematic;
//    }

	private static int amountOfDyeRequiredToColor(Pocket pocket) {
		int outerVolume = pocket.box.getBlockCountX() * pocket.box.getBlockCountY() * pocket.box.getBlockCountZ();
		int innerVolume = (pocket.box.getBlockCountX() - 5) * (pocket.box.getBlockCountY() - 5) * (pocket.box.getBlockCountZ() - 5);

		return (outerVolume - innerVolume) / BLOCKS_PAINTED_PER_DYE;
	}

	public void setSize(int x, int y, int z) {
		this.box = new BlockBox(this.box.minX, this.box.minY, this.box.minZ, this.box.minX + x, this.box.minY + y, this.box.minZ + z);
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
				tag.getInt("count")
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
}
