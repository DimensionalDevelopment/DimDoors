package org.dimdev.dimdoors.util.schematic;

import dev.architectury.platform.Platform;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.BlockPlacementType;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public final class SchematicPlacer {
	public static final Logger LOGGER = LogManager.getLogger();

	private SchematicPlacer() {
	}

	public static void place(Schematic schematic, WorldGenLevel world, BlockPos origin, BlockPlacementType placementType) {
		LOGGER.debug("Placing schematic: {}", schematic.getMetadata().name());
		for (String id : schematic.getMetadata().requiredMods()) {
			if (!Platform.isModLoaded(id)) {
				LOGGER.warn("Schematic \"" + schematic.getMetadata().name() + "\" depends on mod \"" + id + "\", which is missing!");
			}
		}
		RelativeBlockSample blockSample = Schematic.getBlockSample(schematic);
		blockSample.place(origin, world, placementType, false);
	}

	public static Map<BlockPos, RiftBlockEntity> getAbsoluteRifts(Schematic schematic, BlockPos origin) {
		RelativeBlockSample blockSample = Schematic.getBlockSample(schematic);
		return blockSample.getAbsoluteRifts(origin);
	}

	public static void place(Schematic schematic, ServerLevel world, ChunkAccess chunk, BlockPos origin, BlockPlacementType placementType) {
		LOGGER.debug("Placing schematic: {}", schematic.getMetadata().name());
		for (String id : schematic.getMetadata().requiredMods()) {
			if (!Platform.isModLoaded(id)) {
				LOGGER.warn("Schematic \"" + schematic.getMetadata().name() + "\" depends on mod \"" + id + "\", which is missing!");
			}
		}
		RelativeBlockSample blockSample = Schematic.getBlockSample(schematic);
		blockSample.place(origin, world, chunk, placementType, false);
	}



	public static int[][][] getBlockData(Schematic schematic) {
		int width = schematic.getWidth();
		int height = schematic.getHeight();
		int length = schematic.getLength();
		byte[] blockDataIntArray = schematic.getBlockData().array();
		int[][][] blockData = new int[width][height][length];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < length; z++) {
					blockData[x][y][z] = blockDataIntArray[x + z * width + y * width * length];
				}
			}
		}
		return blockData;
	}

	public static int[][] getBiomeData(Schematic schematic) {
//		int width = schematic.getWidth();
//		int length = schematic.getLength();
//		byte[] biomeDataArray = schematic.getBiomeData().array();
//		if (biomeDataArray.length == 0) return new int[0][0];
//		int[][] biomeData = new int[width][length];
//		for (int x = 0; x < width; x++) {
//			for (int z = 0; z < length; z++) {
//				biomeData[x][z] = biomeDataArray[x + z * width];
//			}
//		}
//		return biomeData;
		return new int[0][0];
	}

	private static void placeEntities(BlockPos origin, Schematic schematic, WorldGenLevel world) {
		List<CompoundTag> entityNbts = schematic.getEntities();
		for (CompoundTag nbt : entityNbts) {
			ListTag nbtList = Objects.requireNonNull(nbt.getList("Pos", 6), "Entity in schematic  \"" + schematic.getMetadata().name() + "\" did not have a Pos nbt list!");
			SchematicPlacer.processPos(nbtList, origin, schematic.getOffset(), nbt);

			EntityType<?> entityType = EntityType.by(fixEntityId(nbt)).orElseThrow(AssertionError::new);
			Entity e = entityType.create(world.getLevel());
			// TODO: fail with an exception
			if (e != null) {
				e.load(nbt);

				e.getSelfAndPassengers().forEach(e1 -> System.out.println("Blep: " + e.getDisplayName().getString() + " " + world.addFreshEntity(e1)));

				world.addFreshEntityWithPassengers(e);
			}
		}
	}

	public static CompoundTag fixEntityId(CompoundTag nbt) {
		if (!nbt.contains("Id") && nbt.contains("id")) {
			nbt.putString("Id", nbt.getString("id"));
		} else if (nbt.contains("Id") && !nbt.contains("id")) {
			nbt.putString("id", nbt.getString("Id"));
		}
		if (!nbt.contains("Id") || !nbt.contains("id")) {
			System.err.println("An unexpected error occurred parsing this entity");
			System.err.println(nbt.toString());
			throw new IllegalStateException("Entity did not have an 'Id' nbt string, nor an 'id' nbt string!");
		}
		return nbt;
	}

	private static void processPos(ListTag nbtList, BlockPos origin, Vec3i offset, CompoundTag nbt) {
		double x = nbtList.getDouble(0);
		double y = nbtList.getDouble(1);
		double z = nbtList.getDouble(2);
		nbt.remove("Pos");
		nbt.put("Pos", NbtOps.INSTANCE.createList(Stream.of(DoubleTag.valueOf(x + origin.getX() - offset.getX()),
				DoubleTag.valueOf(y + origin.getY() - offset.getY()),
				DoubleTag.valueOf(z + origin.getZ() - offset.getZ()))));
	}
}
