package org.dimdev.dimdoors.util.schematic;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.util.BlockBoxUtil;
import org.dimdev.dimdoors.api.util.BlockPlacementType;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class RelativeBlockSample implements BlockGetter, LevelWriter {
	public final Schematic schematic;
	private final int[][][] blockData;
	private final int[][] biomeData;
	private final BiMap<BlockState, Integer> blockPalette;
	private final BiMap<Biome, Integer> biomePalette;
	private final Map<BlockPos, BlockState> blockContainer;
	private final Map<BlockPos, Biome> biomeContainer;
	private final Map<BlockPos, CompoundTag> blockEntityContainer;
	private final BiMap<CompoundTag, Vec3> entityContainer;

	public RelativeBlockSample(Schematic schematic) {
		this.schematic = schematic;
		this.blockData = SchematicPlacer.getBlockData(schematic);
		this.biomeData = SchematicPlacer.getBiomeData(schematic);
		this.blockPalette = ImmutableBiMap.copyOf(schematic.getBlockPalette());
		this.biomePalette = /*ImmutableBiMap.copyOf(schematic.getBiomePalette());*/ HashBiMap.create(0);
		this.blockContainer = Maps.newHashMap();
		this.biomeContainer = Maps.newHashMap();
		this.blockEntityContainer = Maps.newHashMap();
		int width = schematic.getWidth();
		int height = schematic.getHeight();
		int length = schematic.getLength();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < length; z++) {
					this.setBlock(new BlockPos(x, y, z), this.blockPalette.inverse().get(this.blockData[x][y][z]), 2);
				}
			}
		}
		if (hasBiomes()) {
			for (int x = 0; x < width; x++) {
				for (int z = 0; z < length; z++) {
					this.biomeContainer.put(new BlockPos(x, 0, z), this.biomePalette.inverse().get(this.biomeData[x][z]));
				}
			}
		}
		for (CompoundTag blockEntityNbt : schematic.getBlockEntities()) {
			int[] arr = blockEntityNbt.getIntArray("Pos");
			BlockPos position = new BlockPos(arr[0], arr[1], arr[2]);
			this.blockEntityContainer.put(position, blockEntityNbt);
		}

		this.entityContainer = HashBiMap.create();
		for (CompoundTag entityNbt : schematic.getEntities()) {
			ListTag doubles = entityNbt.getList("Pos", Tag.TAG_DOUBLE);
			this.entityContainer.put(entityNbt, new Vec3(doubles.getDouble(0), doubles.getDouble(1), doubles.getDouble(2)));
		}
	}

	@Override
	public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
		BlockState blockState = this.getBlockState(pos);

		if (blockState.getBlock() instanceof EntityBlock) {
			return ((EntityBlock) blockState.getBlock()).newBlockEntity(pos, blockState);
		}

		return null;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return this.blockContainer.get(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return this.blockContainer.get(pos).getFluidState();
	}

	public void place(BlockPos origin, WorldGenLevel world, BlockPlacementType placementType, boolean biomes) {
		// TODO: properly implement placement types
		this.blockContainer.forEach((pos, state) -> {
			BlockPos actualPos = origin.offset(pos);
			world.setBlock(actualPos, state, 0, 0);
			if (placementType.shouldMarkForUpdate()) ((ServerLevel) world).getChunkSource().blockChanged(actualPos);
		});
		for (Map.Entry<BlockPos, CompoundTag> entry : this.blockEntityContainer.entrySet()) {
			BlockPos pos = entry.getKey();
			BlockPos actualPos = origin.offset(entry.getKey());

			CompoundTag nbt = entry.getValue();
			if(nbt.contains("Id")) {
				nbt.put("id", nbt.get("Id")); // boogers
				nbt.remove("Id");
			}

			BlockEntity blockEntity = BlockEntity.loadStatic(actualPos, this.getBlockState(pos), nbt);
			if (blockEntity != null) {
				placementType.getBlockEntityPlacer().accept(world.getLevel(), blockEntity);
			}
		}
		for (Map.Entry<CompoundTag, Vec3> entry : this.entityContainer.entrySet()) {
			CompoundTag nbt = entry.getKey();
			ListTag doubles = nbt.getList("Pos", Tag.TAG_DOUBLE);
			Vec3 vec = entry.getValue().add(origin.getX(), origin.getY(), origin.getZ());
			doubles.set(0, NbtOps.INSTANCE.createDouble(vec.x));
			doubles.set(1, NbtOps.INSTANCE.createDouble(vec.y));
			doubles.set(2, NbtOps.INSTANCE.createDouble(vec.z));
			nbt.put("Pos", doubles);
			Entity entity = EntityType.create(nbt, world.getLevel()).orElseThrow(NoSuchElementException::new);
			world.addFreshEntity(entity);
		}
	}

	public void place(BlockPos origin, ServerLevel world, ChunkAccess chunk, BlockPlacementType placementType, boolean biomes) {
		ChunkPos pos = chunk.getPos();
		BoundingBox chunkBox = BlockBoxUtil.getBox(chunk);
		Vec3i schemDimensions = new Vec3i(schematic.getWidth(), schematic.getHeight(), schematic.getLength());
		BoundingBox schemBox = BoundingBox.fromCorners(origin, origin.offset(schemDimensions).offset(-1, -1, -1));
		if (!schemBox.intersects(chunkBox)) return;
		BoundingBox intersection = BlockBoxUtil.intersect(schemBox, chunkBox);

		ServerChunkCache serverChunkManager = world.getChunkSource();

		LevelChunkSection[] sections = chunk.getSections();

		if (placementType.useSection()) {
			BlockPos.betweenClosedStream(intersection).forEach(blockPos -> {
				int x = Math.floorMod(blockPos.getX(), 16);
				int y = Math.floorMod(blockPos.getY(), 16);
				int z = Math.floorMod(blockPos.getZ(), 16);
				int sectionY = chunk.getSectionIndex(blockPos.getY());
				LevelChunkSection section = sections[sectionY];
				if (section == null) {
					section = new LevelChunkSection(sectionY, world.registryAccess().registryOrThrow(Registries.BIOME));
					sections[sectionY] = section;
				}
				if(section.getBlockState(x, y, z).isAir()) {
					BlockState newState = this.blockContainer.get(blockPos.subtract(origin));
					// FIXME: newState can be null in some circumstances
					// TODO: is null checking the right fix or just a band-aid?
					if (newState != null && !newState.isAir()) {
						section.setBlockState(x, y, z, newState, false);
						if (placementType.shouldMarkForUpdate()) serverChunkManager.blockChanged(blockPos);
					}
				}
			});
		} else {
			BlockPos.betweenClosedStream(intersection).forEach(blockPos -> { // FIXME: currently extremely unstable since it can try to get neighbouring chunks which can cause a deadlock
				if(chunk.getBlockState(blockPos).isAir()) {
					BlockState newState = this.blockContainer.get(blockPos.subtract(origin));
					if (!newState.isAir()) {
						chunk.setBlockState(blockPos, newState, false);
					}
				}
			});
		}

		// do the lighting thing
		serverChunkManager.getLightEngine().lightChunk(chunk, false);

		// TODO: depending on size of blockEntityContainer it might be faster to iterate over BlockPos.stream(intersection) instead
		this.blockEntityContainer.forEach((blockPos, nbt) -> {
			BlockPos actualPos = blockPos.offset(origin);
			if (intersection.isInside(actualPos)) {
				if(nbt.contains("Id")) {
					nbt.put("id", nbt.get("Id")); // boogers
					nbt.remove("Id");
				}

				BlockEntity blockEntity = BlockEntity.loadStatic(actualPos, this.getBlockState(blockPos), nbt);
				if (blockEntity != null && !(blockEntity instanceof RiftBlockEntity)) {
					chunk.setBlockEntity(blockEntity);
				}
			}
		});

		// TODO: is it ok if this is not executed with MinecraftServer#send?
		this.entityContainer.forEach(((nbt, vec3d) -> {
			ListTag doubles = nbt.getList("Pos", Tag.TAG_DOUBLE);
			Vec3 vec = vec3d.add(origin.getX(), origin.getY(), origin.getZ());
			if (intersection.isInside(new Vec3i((int) vec.x, (int) vec.y, (int) vec.z))) {
				doubles.set(0, NbtOps.INSTANCE.createDouble(vec.x));
				doubles.set(1, NbtOps.INSTANCE.createDouble(vec.y));
				doubles.set(2, NbtOps.INSTANCE.createDouble(vec.z));
				nbt.put("Pos", doubles);

				Entity entity = EntityType.create(nbt, world.getLevel()).orElseThrow(NoSuchElementException::new);
				world.getServer().execute(() -> {
					world.addFreshEntity(entity);
				});
			}
		}));
	}

	public Map<BlockPos, RiftBlockEntity> getAbsoluteRifts(BlockPos origin) {
		Map<BlockPos, RiftBlockEntity> rifts = new HashMap<>();
		this.blockEntityContainer.forEach( (blockPos, nbt) ->  {
			BlockPos actualPos = origin.offset(blockPos);

			if(nbt.contains("Id")) {
				nbt.put("id", nbt.get("Id")); // boogers
				nbt.remove("Id");
			}
			BlockState state = getBlockState(blockPos);
			BlockEntity blockEntity = BlockEntity.loadStatic(actualPos, state, nbt);
			if (blockEntity instanceof RiftBlockEntity) {
				rifts.put(actualPos, (RiftBlockEntity) blockEntity);
			}
		});
		return rifts;
	}

	public int[][][] getBlockData() {
		return this.blockData;
	}

	public BiMap<BlockState, Integer> getBlockPalette() {
		return this.blockPalette;
	}

	public Map<BlockPos, BlockState> getBlockContainer() {
		return this.blockContainer;
	}

	public Map<BlockPos, CompoundTag> getBlockEntityContainer() {
		return this.blockEntityContainer;
	}

	@Override
	public boolean setBlock(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
		this.blockContainer.put(pos, state);
		return true;
	}

	@Override
	public boolean removeBlock(BlockPos pos, boolean move) {
		return this.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
	}

	@Override
	public boolean destroyBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth) {
		return this.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
	}

	public boolean hasBiomes() {
		return this.biomeData.length != 0;
	}

	@Override
	public int getHeight() {
		return this.schematic.getHeight();
	}

	@Override
	public int getMinBuildHeight() {
		return 0;
	}
}
