package org.dimdev.dimdoors.util.schematic;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

import net.fabricmc.fabric.api.util.NbtType;

import org.dimdev.dimdoors.api.util.BlockBoxUtil;
import org.dimdev.dimdoors.api.util.BlockPlacementType;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;

public class RelativeBlockSample implements BlockView, ModifiableWorld {
	public final Schematic schematic;
	private final int[][][] blockData;
	private final int[][] biomeData;
	private final BiMap<BlockState, Integer> blockPalette;
	private final BiMap<Biome, Integer> biomePalette;
	private final Map<BlockPos, BlockState> blockContainer;
	private final Map<BlockPos, Biome> biomeContainer;
	private final Map<BlockPos, NbtCompound> blockEntityContainer;
	private final BiMap<NbtCompound, Vec3d> entityContainer;

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
					this.setBlockState(new BlockPos(x, y, z), this.blockPalette.inverse().get(this.blockData[x][y][z]), 2);
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
		for (NbtCompound blockEntityNbt : schematic.getBlockEntities()) {
			int[] arr = blockEntityNbt.getIntArray("Pos");
			BlockPos position = new BlockPos(arr[0], arr[1], arr[2]);
			this.blockEntityContainer.put(position, blockEntityNbt);
		}

		this.entityContainer = HashBiMap.create();
		for (NbtCompound entityNbt : schematic.getEntities()) {
			NbtList doubles = entityNbt.getList("Pos", NbtType.DOUBLE);
			this.entityContainer.put(entityNbt, new Vec3d(doubles.getDouble(0), doubles.getDouble(1), doubles.getDouble(2)));
		}
	}

	@Override
	public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
		BlockState blockState = this.getBlockState(pos);

		if (blockState.getBlock() instanceof BlockEntityProvider) {
			return ((BlockEntityProvider) blockState.getBlock()).createBlockEntity(pos, blockState);
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

	public void place(BlockPos origin, StructureWorldAccess world, BlockPlacementType placementType, boolean biomes) {
		// TODO: properly implement placement types
		this.blockContainer.forEach((pos, state) -> {
			BlockPos actualPos = origin.add(pos);
			world.setBlockState(actualPos, state, 0, 0);
			if (placementType.shouldMarkForUpdate()) ((ServerWorld) world).getChunkManager().markForUpdate(actualPos);
		});
		for (Map.Entry<BlockPos, NbtCompound> entry : this.blockEntityContainer.entrySet()) {
			BlockPos pos = entry.getKey();
			BlockPos actualPos = origin.add(entry.getKey());

			NbtCompound nbt = entry.getValue();
			if(nbt.contains("Id")) {
				nbt.put("id", nbt.get("Id")); // boogers
				nbt.remove("Id");
			}

			BlockEntity blockEntity = BlockEntity.createFromNbt(actualPos, this.getBlockState(pos), nbt);
			if (blockEntity != null) {
				placementType.getBlockEntityPlacer().accept(world.toServerWorld(), blockEntity);
			}
		}
		for (Map.Entry<NbtCompound, Vec3d> entry : this.entityContainer.entrySet()) {
			NbtCompound nbt = entry.getKey();
			NbtList doubles = nbt.getList("Pos", NbtType.DOUBLE);
			Vec3d vec = entry.getValue().add(origin.getX(), origin.getY(), origin.getZ());
			doubles.set(0, NbtOps.INSTANCE.createDouble(vec.x));
			doubles.set(1, NbtOps.INSTANCE.createDouble(vec.y));
			doubles.set(2, NbtOps.INSTANCE.createDouble(vec.z));
			nbt.put("Pos", doubles);
			Entity entity = EntityType.getEntityFromNbt(nbt, world.toServerWorld()).orElseThrow(NoSuchElementException::new);
			world.spawnEntity(entity);
		}
	}

	public void place(BlockPos origin, ServerWorld world, Chunk chunk, BlockPlacementType placementType, boolean biomes) {
		ChunkPos pos = chunk.getPos();
		BlockBox chunkBox = BlockBoxUtil.getBox(chunk);
		Vec3i schemDimensions = new Vec3i(schematic.getWidth(), schematic.getHeight(), schematic.getLength());
		BlockBox schemBox = BlockBox.create(origin, origin.add(schemDimensions).add(-1, -1, -1));
		if (!schemBox.intersects(chunkBox)) return;
		BlockBox intersection = BlockBoxUtil.intersect(schemBox, chunkBox);

		ServerChunkManager serverChunkManager = world.getChunkManager();

		ChunkSection[] sections = chunk.getSectionArray();

		if (placementType.useSection()) {
			BlockPos.stream(intersection).forEach(blockPos -> {
				int x = Math.floorMod(blockPos.getX(), 16);
				int y = Math.floorMod(blockPos.getY(), 16);
				int z = Math.floorMod(blockPos.getZ(), 16);
				int sectionY = chunk.getSectionIndex(blockPos.getY());
				ChunkSection section = sections[sectionY];
				if (section == null) {
					section = new ChunkSection(sectionY, world.getRegistryManager().get(RegistryKeys.BIOME));
					sections[sectionY] = section;
				}
				if(section.getBlockState(x, y, z).isAir()) {
					BlockState newState = this.blockContainer.get(blockPos.subtract(origin));
					// FIXME: newState can be null in some circumstances
					// TODO: is null checking the right fix or just a band-aid?
					if (newState != null && !newState.isAir()) {
						section.setBlockState(x, y, z, newState, false);
						if (placementType.shouldMarkForUpdate()) serverChunkManager.markForUpdate(blockPos);
					}
				}
			});
		} else {
			BlockPos.stream(intersection).forEach(blockPos -> { // FIXME: currently extremely unstable since it can try to get neighbouring chunks which can cause a deadlock
				if(chunk.getBlockState(blockPos).isAir()) {
					BlockState newState = this.blockContainer.get(blockPos.subtract(origin));
					if (!newState.isAir()) {
						chunk.setBlockState(blockPos, newState, false);
					}
				}
			});
		}

		// do the lighting thing
		serverChunkManager.getLightingProvider().light(chunk, false);

		// TODO: depending on size of blockEntityContainer it might be faster to iterate over BlockPos.stream(intersection) instead
		this.blockEntityContainer.forEach((blockPos, nbt) -> {
			BlockPos actualPos = blockPos.add(origin);
			if (intersection.contains(actualPos)) {
				if(nbt.contains("Id")) {
					nbt.put("id", nbt.get("Id")); // boogers
					nbt.remove("Id");
				}

				BlockEntity blockEntity = BlockEntity.createFromNbt(actualPos, this.getBlockState(blockPos), nbt);
				if (blockEntity != null && !(blockEntity instanceof RiftBlockEntity)) {
					chunk.setBlockEntity(blockEntity);
				}
			}
		});

		// TODO: is it ok if this is not executed with MinecraftServer#send?
		this.entityContainer.forEach(((nbt, vec3d) -> {
			NbtList doubles = nbt.getList("Pos", NbtType.DOUBLE);
			Vec3d vec = vec3d.add(origin.getX(), origin.getY(), origin.getZ());
			if (intersection.contains(new Vec3i((int) vec.x, (int) vec.y, (int) vec.z))) {
				doubles.set(0, NbtOps.INSTANCE.createDouble(vec.x));
				doubles.set(1, NbtOps.INSTANCE.createDouble(vec.y));
				doubles.set(2, NbtOps.INSTANCE.createDouble(vec.z));
				nbt.put("Pos", doubles);

				Entity entity = EntityType.getEntityFromNbt(nbt, world.toServerWorld()).orElseThrow(NoSuchElementException::new);
				world.getServer().execute(() -> {
					world.spawnEntity(entity);
				});
			}
		}));
	}

	public Map<BlockPos, RiftBlockEntity> getAbsoluteRifts(BlockPos origin) {
		Map<BlockPos, RiftBlockEntity> rifts = new HashMap<>();
		this.blockEntityContainer.forEach( (blockPos, nbt) ->  {
			BlockPos actualPos = origin.add(blockPos);

			if(nbt.contains("Id")) {
				nbt.put("id", nbt.get("Id")); // boogers
				nbt.remove("Id");
			}
			BlockState state = getBlockState(blockPos);
			BlockEntity blockEntity = BlockEntity.createFromNbt(actualPos, state, nbt);
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

	public Map<BlockPos, NbtCompound> getBlockEntityContainer() {
		return this.blockEntityContainer;
	}

	@Override
	public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
		this.blockContainer.put(pos, state);
		return true;
	}

	@Override
	public boolean removeBlock(BlockPos pos, boolean move) {
		return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
	}

	@Override
	public boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth) {
		return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
	}

	public boolean hasBiomes() {
		return this.biomeData.length != 0;
	}

	@Override
	public int getHeight() {
		return this.schematic.getHeight();
	}

	@Override
	public int getBottomY() {
		return 0;
	}
}
