package org.dimdev.dimdoors.util.schematic.v2;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import io.github.boogiemonster1o1.libcbe.api.ConditionalBlockEntityProvider;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.StructureWorldAccess;

import net.fabricmc.fabric.api.util.NbtType;

public class RelativeBlockSample implements BlockView, ModifiableWorld {
	public final Schematic schematic;
	private final int[][][] blockData;
	private final BiMap<BlockState, Integer> blockPalette;
	private final Map<BlockPos, BlockState> blockContainer;
	private final Map<BlockPos, CompoundTag> blockEntityContainer;
	private final BiMap<CompoundTag, Vec3d> entityContainer;
	private StructureWorldAccess world;

	public RelativeBlockSample(Schematic schematic) {
		this.schematic = schematic;
		this.blockData = SchematicPlacer.getBlockData(schematic);
		this.blockPalette = ImmutableBiMap.copyOf(schematic.getBlockPalette());
		this.blockContainer = Maps.newHashMap();
		this.blockEntityContainer = Maps.newHashMap();
		int width = schematic.getWidth();
		int height = schematic.getHeight();
		int length = schematic.getLength();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < length; z++) {
					this.setBlockState(new BlockPos(x, y, z), this.blockPalette.inverse().get(this.blockData[x][y][z]), 2);
					this.blockContainer.put(new BlockPos(x, y, z), this.blockPalette.inverse().get(this.blockData[x][y][z]));
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
		Block block = this.getBlockState(pos).getBlock();
		if (block.hasBlockEntity()) {
			if (block instanceof ConditionalBlockEntityProvider && ((ConditionalBlockEntityProvider) block).hasBlockEntity(this.getBlockState(pos)) && ((ConditionalBlockEntityProvider) block).hasBlockEntity(pos, this)) {
				return ((ConditionalBlockEntityProvider) block).createBlockEntity(this.world);
			} else {
				return ((BlockEntityProvider) block).createBlockEntity(this.world);
			}
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

	public void place(BlockPos origin) {
		if (this.world == null) {
			throw new UnsupportedOperationException("Can not place in a null world!");
		}
		this.blockContainer.forEach((pos, state) -> this.world.setBlockState(origin.add(pos), state, 0b0000011));
		for (Map.Entry<BlockPos, CompoundTag> entry : this.blockEntityContainer.entrySet()) {
			BlockPos pos = entry.getKey();
			BlockPos actualPos = origin.add(entry.getKey());

			BlockEntity blockEntity = BlockEntity.createFromTag(this.getBlockState(pos), entry.getValue());
			if (blockEntity != null) {
				this.world.toServerWorld().setBlockEntity(actualPos, blockEntity);
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
			Entity entity = EntityType.getEntityFromTag(tag, this.world.toServerWorld()).orElseThrow(NoSuchElementException::new);
			this.world.spawnEntity(entity);
		}
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

	public StructureWorldAccess getWorld() {
		return this.world;
	}

	public RelativeBlockSample setWorld(StructureWorldAccess world) {
		this.world = world;
		return this;
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
}
