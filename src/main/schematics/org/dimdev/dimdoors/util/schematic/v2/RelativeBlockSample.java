package org.dimdev.dimdoors.util.schematic.v2;

import java.util.*;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.util.BlockBoxUtil;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.api.util.NbtType;

public class RelativeBlockSample implements BlockView, ModifiableWorld {
	public final Schematic schematic;
	private final int[][][] blockData;
	private final int[][] biomeData;
	private final BiMap<BlockState, Integer> blockPalette;
	private final BiMap<Biome, Integer> biomePalette;
	private final Map<BlockPos, BlockState> blockContainer;
	private final Map<BlockPos, Biome> biomeContainer;
	private final Map<BlockPos, CompoundTag> blockEntityContainer;
	private final BiMap<CompoundTag, Vec3d> entityContainer;

	public RelativeBlockSample(Schematic schematic) {
		this.schematic = schematic;
		this.blockData = SchematicPlacer.getBlockData(schematic);
		this.biomeData = SchematicPlacer.getBiomeData(schematic);
		this.blockPalette = ImmutableBiMap.copyOf(schematic.getBlockPalette());
		this.biomePalette = ImmutableBiMap.copyOf(schematic.getBiomePalette());
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
		for (CompoundTag blockEntityTag : schematic.getBlockEntities()) {
			int[] arr = blockEntityTag.getIntArray("Pos");
			BlockPos position = new BlockPos(arr[0], arr[1], arr[2]);
			this.blockEntityContainer.put(position, blockEntityTag);
		}

		this.entityContainer = HashBiMap.create();
		for (CompoundTag entityTag : schematic.getEntities()) {
			ListTag doubles = entityTag.getList("Pos", NbtType.DOUBLE);
			this.entityContainer.put(entityTag, new Vec3d(doubles.getDouble(0), doubles.getDouble(1), doubles.getDouble(2)));
		}
	}

	@Override
	public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
		return Optional.of(this.getBlockState(pos))
				.map(BlockState::getBlock)
				.filter(BlockEntityProvider.class::isInstance)
				.map(BlockEntityProvider.class::cast)
				.map(bep -> bep.createBlockEntity(this))
				.orElse(null);
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return this.blockContainer.get(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return this.blockContainer.get(pos).getFluidState();
	}

	public void place(BlockPos origin, ServerWorld world, boolean blockUpdate, boolean biomes) {
		this.blockContainer.forEach((pos, state) -> {
			BlockPos actualPos = origin.add(pos);
			world.setBlockState(actualPos, state, 0, 0);
			if (blockUpdate) world.getChunkManager().markForUpdate(actualPos);
		});
		for (Map.Entry<BlockPos, CompoundTag> entry : this.blockEntityContainer.entrySet()) {
			BlockPos pos = entry.getKey();
			BlockPos actualPos = origin.add(entry.getKey());

			CompoundTag tag = entry.getValue();
			if(tag.contains("Id")) {
				tag.put("id", tag.get("Id")); // boogers
				tag.remove("Id");
			}

			BlockEntity blockEntity = BlockEntity.createFromTag(this.getBlockState(pos), tag);
			if (blockEntity != null) {
				world.toServerWorld().setBlockEntity(actualPos, blockEntity);
			}
		}
		for (Map.Entry<CompoundTag, Vec3d> entry : this.entityContainer.entrySet()) {
			CompoundTag tag = entry.getKey();
			ListTag doubles = tag.getList("Pos", NbtType.DOUBLE);
			Vec3d vec = entry.getValue().add(origin.getX(), origin.getY(), origin.getZ());
			doubles.set(0, NbtOps.INSTANCE.createDouble(vec.x));
			doubles.set(1, NbtOps.INSTANCE.createDouble(vec.y));
			doubles.set(2, NbtOps.INSTANCE.createDouble(vec.z));
			tag.put("Pos", doubles);
			Entity entity = EntityType.getEntityFromTag(tag, world.toServerWorld()).orElseThrow(NoSuchElementException::new);
			world.spawnEntity(entity);
		}
	}

	public void place(BlockPos origin, ServerWorld world, Chunk chunk, boolean blockUpdate, boolean biomes) {
		ChunkPos pos = chunk.getPos();
		BlockBox chunkBox = BlockBox.create(pos.getStartX(), 0, pos.getStartZ(), pos.getEndX(), chunk.getHeight() - 1, pos.getEndZ());
		BlockBox schemBox = BlockBox.create(origin.getX(), origin.getY(), origin.getZ(), origin.getX() + schematic.getWidth() - 1, origin.getY() + schematic.getHeight() - 1, origin.getZ() + schematic.getLength() - 1);
		BlockBox intersection = BlockBoxUtil.intersection(chunkBox, schemBox);
		if (!BlockBoxUtil.isRealBox(intersection)) return;

		BlockPos.stream(intersection).forEach(blockPos -> {
			if(chunk.getBlockState(blockPos).isAir()) {
				BlockState newState = this.blockContainer.get(blockPos.subtract(origin));
				if (!newState.isAir()) {
					chunk.setBlockState(blockPos, newState, false);
					if (blockUpdate) world.getChunkManager().markForUpdate(blockPos);
				}
			}
		});

		// TODO: depending on size of blockEntityContainer it might be faster to iterate over BlockPos.stream(intersection) instead
		this.blockEntityContainer.forEach((blockPos, tag) -> {
			BlockPos actualPos = blockPos.add(origin);
			if (intersection.contains(actualPos)) {
				if(tag.contains("Id")) {
					tag.put("id", tag.get("Id")); // boogers
					tag.remove("Id");
				}

				BlockEntity blockEntity = BlockEntity.createFromTag(this.getBlockState(blockPos), tag);
				if (blockEntity != null && !(blockEntity instanceof RiftBlockEntity)) {
					chunk.setBlockEntity(actualPos, blockEntity);
				}
			}
		});

		// TODO: is it ok if this is not executed with MinecraftServer#send?
		this.entityContainer.forEach(((tag, vec3d) -> {
			ListTag doubles = tag.getList("Pos", NbtType.DOUBLE);
			Vec3d vec = vec3d.add(origin.getX(), origin.getY(), origin.getZ());
			if (intersection.contains(new Vec3i(vec.x, vec.y, vec.z))) {
				doubles.set(0, NbtOps.INSTANCE.createDouble(vec.x));
				doubles.set(1, NbtOps.INSTANCE.createDouble(vec.y));
				doubles.set(2, NbtOps.INSTANCE.createDouble(vec.z));
				tag.put("Pos", doubles);

				Entity entity = EntityType.getEntityFromTag(tag, world.toServerWorld()).orElseThrow(NoSuchElementException::new);
				world.spawnEntity(entity);
			}
		}));
	}

	public List<RiftBlockEntity> placeRiftsOnly(BlockPos origin, ServerWorld world) {
		List<RiftBlockEntity> rifts = new ArrayList<>();
		this.blockEntityContainer.forEach( (blockPos, tag) ->  {
			BlockPos actualPos = origin.add(blockPos);

			if(tag.contains("Id")) {
				tag.put("id", tag.get("Id")); // boogers
				tag.remove("Id");
			}
			BlockState state = getBlockState(blockPos);
			BlockEntity blockEntity = BlockEntity.createFromTag(state, tag);
			if (blockEntity instanceof RiftBlockEntity) {
				world.setBlockState(actualPos, state, 0);
				world.getChunkManager().markForUpdate(blockPos);
				if (state.getBlock() instanceof DoorBlock) {
					world.setBlockState(actualPos.up(), getBlockState(blockPos.up()), 0);
				}
				world.toServerWorld().addBlockEntity(blockEntity);
				rifts.add((RiftBlockEntity) blockEntity);
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
}
