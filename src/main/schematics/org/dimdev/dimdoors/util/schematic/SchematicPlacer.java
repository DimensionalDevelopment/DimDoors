package org.dimdev.dimdoors.util.schematic;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import net.fabricmc.loader.api.FabricLoader;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.api.util.BlockPlacementType;

public final class SchematicPlacer {
	public static final Logger LOGGER = LogManager.getLogger();

	private SchematicPlacer() {
	}

	public static void place(Schematic schematic, StructureWorldAccess world, BlockPos origin, BlockPlacementType placementType) {
		LOGGER.debug("Placing schematic: {}", schematic.getMetadata().getName());
		for (String id : schematic.getMetadata().getRequiredMods()) {
			if (!FabricLoader.getInstance().isModLoaded(id)) {
				LOGGER.warn("Schematic \"" + schematic.getMetadata().getName() + "\" depends on mod \"" + id + "\", which is missing!");
			}
		}
		RelativeBlockSample blockSample = Schematic.getBlockSample(schematic);
		blockSample.place(origin, world, placementType, false);
	}

	public static Map<BlockPos, RiftBlockEntity> getAbsoluteRifts(Schematic schematic, BlockPos origin) {
		RelativeBlockSample blockSample = Schematic.getBlockSample(schematic);
		return blockSample.getAbsoluteRifts(origin);
	}

	public static void place(Schematic schematic, ServerWorld world, Chunk chunk, BlockPos origin, BlockPlacementType placementType) {
		LOGGER.debug("Placing schematic: {}", schematic.getMetadata().getName());
		for (String id : schematic.getMetadata().getRequiredMods()) {
			if (!FabricLoader.getInstance().isModLoaded(id)) {
				LOGGER.warn("Schematic \"" + schematic.getMetadata().getName() + "\" depends on mod \"" + id + "\", which is missing!");
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
		int width = schematic.getWidth();
		int length = schematic.getLength();
		byte[] biomeDataArray = schematic.getBiomeData().array();
		if (biomeDataArray.length == 0) return new int[0][0];
		int[][] biomeData = new int[width][length];
		for (int x = 0; x < width; x++) {
			for (int z = 0; z < length; z++) {
				biomeData[x][z] = biomeDataArray[x + z * width];
			}
		}
		return biomeData;
	}

	private static void placeEntities(int originX, int originY, int originZ, Schematic schematic, StructureWorldAccess world) {
		List<NbtCompound> entityNbts = schematic.getEntities();
		for (NbtCompound nbt : entityNbts) {
			NbtList nbtList = Objects.requireNonNull(nbt.getList("Pos", 6), "Entity in schematic  \"" + schematic.getMetadata().getName() + "\" did not have a Pos nbt list!");
			SchematicPlacer.processPos(nbtList, originX, originY, originZ, nbt);

			EntityType<?> entityType = EntityType.fromNbt(nbt).orElseThrow(AssertionError::new);
			Entity e = entityType.create(world.toServerWorld());
			// TODO: fail with an exception
			if (e != null) {
				e.readNbt(nbt);
				world.spawnEntityAndPassengers(e);
			}
		}
	}

	public static NbtCompound fixEntityId(NbtCompound nbt) {
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

	private static void processPos(NbtList nbtList, int originX, int originY, int originZ, NbtCompound nbt) {
		double x = nbtList.getDouble(0);
		double y = nbtList.getDouble(1);
		double z = nbtList.getDouble(2);
		nbt.remove("Pos");
		nbt.put("Pos", NbtOps.INSTANCE.createList(Stream.of(NbtDouble.of(x + originX),
				NbtDouble.of(y + originY),
				NbtDouble.of(z + originZ))));
	}
}
