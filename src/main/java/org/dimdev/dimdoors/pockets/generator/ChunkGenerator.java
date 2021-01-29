package org.dimdev.dimdoors.pockets.generator;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.gen.GenerationStep;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.pockets.TemplateUtils;
import org.dimdev.dimdoors.pockets.VirtualPocket;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.world.level.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.Pocket;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ChunkGenerator extends VirtualPocket {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "chunk";

	private Identifier dimensionID;
	private Vec3i size;
	private Vec3i offset;
	private int virtualYOffset;
	private int weight;

	public ChunkGenerator() {

	}

	@Override
	public VirtualPocket fromTag(CompoundTag tag) {
		this.dimensionID = new Identifier(tag.getString("dimension_id"));

		int[] temp = tag.getIntArray("size");
		this.size = new Vec3i(temp[0], temp[1], temp[2]);

		temp = tag.contains("offset") ? tag.getIntArray("offset") : new int[]{0, 0, 0};
		this.offset = new Vec3i(temp[0], temp[1], temp[2]);

		this.virtualYOffset = tag.contains("virtual_y_offset") ? tag.getInt("virtual_y_offset") : 0;
		this.weight = tag.contains("weight") ? tag.getInt("weight") : 5;
		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		tag.putString("dimension_id", dimensionID.toString());
		tag.putIntArray("size", new int[]{this.size.getX(), this.size.getY(), this.size.getZ()});
		tag.putIntArray("offset", new int[]{this.offset.getX(), this.offset.getY(), this.offset.getZ()});
		tag.putInt("virtual_y_offset", this.virtualYOffset);
		tag.putInt("weight", this.weight);
		return tag;
	}

	@Override
	public void init(String group) {

	}

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationParameters parameters) {
		ServerWorld world = parameters.getWorld();
		VirtualLocation sourceVirtualLocation = parameters.getSourceVirtualLocation();
		VirtualTarget linkTo = parameters.getLinkTo();
		LinkProperties linkProperties = parameters.getLinkProperties();

		int ChunkSizeX = ((this.size.getX() >> 4) + (this.size.getX() % 16 == 0 ? 0 : 1));
		int ChunkSizeZ = ((this.size.getZ() >> 4) + (this.size.getZ() % 16 == 0 ? 0 : 1));

		Pocket pocket = DimensionalRegistry.getPocketDirectory(world.getRegistryKey()).newPocket();
		pocket.setSize(size.getX(), size.getY(), size.getZ());
		pocket.offsetOrigin(offset);

		LOGGER.info("Generating chunk pocket at location " + pocket.getOrigin());

		ServerWorld genWorld = DimensionalDoorsInitializer.getWorld(RegistryKey.of(Registry.DIMENSION, dimensionID));
		net.minecraft.world.gen.chunk.ChunkGenerator genWorldChunkGenerator = genWorld.getChunkManager().getChunkGenerator();

		ArrayList<Chunk> protoChunks = new ArrayList<>();
		for (int z = 0; z < ChunkSizeZ; z++) {
			for (int x = 0; x < ChunkSizeX; x++) {
				protoChunks.add(new ProtoChunk(new ChunkPos(pocket.getOrigin().add(x * 16, 0, z * 16)), UpgradeData.NO_UPGRADE_DATA));
			}
		}
		ChunkRegion protoRegion = new ChunkRegionHack(genWorld, protoChunks);

		for (Chunk protoChunk : protoChunks) {
			genWorldChunkGenerator.populateNoise(genWorld, genWorld.getStructureAccessor(), protoChunk);
		}
		for (Chunk protoChunk : protoChunks) {
			genWorldChunkGenerator.buildSurface(protoRegion, protoChunk);
		}
		for (Chunk protoChunk : protoChunks) {
			genWorldChunkGenerator.populateBiomes(BuiltinRegistries.BIOME, protoChunk);
		}
		for (GenerationStep.Carver carver : GenerationStep.Carver.values()) {
			for (Chunk protoChunk : protoChunks) {
				genWorldChunkGenerator.carve(genWorld.getSeed(), genWorld.getBiomeAccess(), protoChunk, carver);
			}
		}
		genWorldChunkGenerator.generateFeatures(protoRegion, genWorld.getStructureAccessor());

		BlockPos firstCorner = pocket.getOrigin();
		BlockPos secondCorner = new BlockPos(firstCorner.getX() + size.getX() - 1, Math.min(firstCorner.getY() + size.getY() - 1, world.getHeight() - virtualYOffset - 1), firstCorner.getZ() + size.getZ() - 1); // subtracting 1 here since it should be 0 inclusive and size exclusive

		BlockPos pocketOriginChunkOffset = new ChunkPos(pocket.getOrigin()).getStartPos().subtract(firstCorner);
		for (BlockPos blockPos : BlockPos.iterate(firstCorner, secondCorner)) {
			world.setBlockState(blockPos, protoRegion.getBlockState(blockPos.add(pocketOriginChunkOffset).add(0, virtualYOffset, 0)));
		} // TODO: BlockEntities/ Entities/ Biomes/ Structure Data

		world.setBlockState(world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pocket.getOrigin()), ModBlocks.DETACHED_RIFT.getDefaultState());

		DetachedRiftBlockEntity rift = ModBlockEntityTypes.DETACHED_RIFT.instantiate();
		world.setBlockEntity(world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pocket.getOrigin()), rift);


		rift.setDestination(new PocketEntranceMarker());
		rift.getDestination().setLocation(new Location((ServerWorld) Objects.requireNonNull(rift.getWorld()), rift.getPos()));
		TemplateUtils.registerRifts(Collections.singletonList(rift), linkTo, linkProperties, pocket);

		pocket.virtualLocation = sourceVirtualLocation;

		return pocket;
	}

	@Override
	public String toString() {
		return null;
	}

	@Override
	public VirtualPocketType<? extends VirtualPocket> getType() {
		return VirtualPocketType.CHUNK;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public int getWeight(PocketGenerationParameters parameters) {
		return this.weight;
	}

	private static class ChunkRegionHack extends ChunkRegion { // Please someone tell me if there is a better way
		ChunkRegionHack(ServerWorld world, List<Chunk> chunks) {
			super(world, chunks);
		}

		public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
			Chunk chunk = super.getChunk(chunkX, chunkZ, leastStatus, false);
			return chunk == null ? new ProtoChunk(new ChunkPos(chunkX, chunkZ), UpgradeData.NO_UPGRADE_DATA) : chunk;
		}
	}
}
