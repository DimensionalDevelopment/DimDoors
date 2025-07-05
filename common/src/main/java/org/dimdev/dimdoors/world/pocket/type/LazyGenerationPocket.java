package org.dimdev.dimdoors.world.pocket.type;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.dimdev.dimdoors.api.util.BlockBoxUtil;
import org.dimdev.dimdoors.pockets.generator.LazyPocketGenerator;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.world.level.component.ChunkLazilyGeneratedComponent;

import java.util.Map;

public class LazyGenerationPocket extends Pocket {
	public static String KEY = "lazy_gen_pocket";

	private LazyPocketGenerator generator;
	private int toBeGennedChunkCount = 0;

	public void chunkLoaded(LevelChunk chunk) {
		if (isDoneGenerating()) return;

		if (ChunkLazilyGeneratedComponent.isGenerated(chunk)) return;

		BoundingBox chunkBox = BlockBoxUtil.getBox(chunk);
		if (!chunkBox.intersects(getBox())) return;

		generator.generateChunk(this, chunk);
		ChunkLazilyGeneratedComponent.setGenerated(chunk, true);
		toBeGennedChunkCount--;

		if (isDoneGenerating()) {
			this.generator = null; // saving up on some ram
		}
	}

	public boolean isDoneGenerating() {
		return toBeGennedChunkCount == 0;
	}

	public void attachGenerator(LazyPocketGenerator generator) {
		this.generator = generator;
	}

	public void init() {
		BoundingBox box = getBox();

		toBeGennedChunkCount = (Math.floorDiv(box.maxX(), 16) - Math.floorDiv(box.minX(), 16) + 1) * (Math.floorDiv(box.maxZ(), 16) - Math.floorDiv(box.minZ(), 16) + 1);
	}

	@Override
	public CompoundTag toNbt(CompoundTag nbt, HolderLookup.Provider provider) {
		super.toNbt(nbt, provider);

		if (generator != null) nbt.put("generator", generator.toNbt(new CompoundTag(), provider));
		if (toBeGennedChunkCount > 0) nbt.putInt("to_be_genned_chunks", toBeGennedChunkCount);

		return nbt;
	}

	@Override
	public AbstractPocketType<?> getType() {
		return AbstractPocketType.LAZY_GENERATION_POCKET.get();
	}

	public static String getKEY() {
		return KEY;
	}

	@Override
	public Pocket fromNbt(CompoundTag nbt, HolderLookup.Provider provider) {
		super.fromNbt(nbt, provider);

		if (nbt.contains("generator", Tag.TAG_COMPOUND)) generator = (LazyPocketGenerator) PocketGenerator.deserialize(nbt.getCompound("generator"), provider);
		if (nbt.contains("to_be_genned_chunks", Tag.TAG_INT)) toBeGennedChunkCount = nbt.getInt("to_be_genned_chunks");

		return this;
	}

	@Override
	public Map<BlockPos, BlockEntity> getBlockEntities() {

		return super.getBlockEntities();
	}

	public static LazyGenerationPocketBuilder<?, LazyGenerationPocket> builderLazyGenerationPocket() {
		return new LazyGenerationPocketBuilder<>(AbstractPocketType.LAZY_GENERATION_POCKET.get());
	}

	public static class LazyGenerationPocketBuilder<P extends LazyGenerationPocketBuilder<P, T>, T extends LazyGenerationPocket> extends PocketBuilder<P, T> {
		protected LazyGenerationPocketBuilder(AbstractPocketType<T> type) {
			super(type);
		}
	}
}