package org.dimdev.dimdoors.world.pocket.type;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.dimdoors.api.util.BlockBoxUtil;
import org.dimdev.dimdoors.pockets.generator.LazyPocketGenerator;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.world.level.component.ChunkLazilyGeneratedComponent;

import java.util.Map;

public class LazyGenerationPocket extends Pocket {
	public static String KEY = "lazy_gen_pocket";

	private LazyPocketGenerator generator;
	private int toBeGennedChunkCount = 0;

	public void chunkLoaded(Chunk chunk) {
		if (isDoneGenerating()) return;

		ChunkLazilyGeneratedComponent lazyGenned = ChunkLazilyGeneratedComponent.get(chunk);
		if (lazyGenned.hasBeenLazyGenned()) return;

		BlockBox chunkBox = BlockBoxUtil.getBox(chunk);
		if (!chunkBox.intersects(getBox())) return;

		generator.generateChunk(this, chunk);
		lazyGenned.setGenned();
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
		BlockBox box = getBox();

		toBeGennedChunkCount = (Math.floorDiv(box.getMaxX(), 16) - Math.floorDiv(box.getMinX(), 16) + 1) * (Math.floorDiv(box.getMaxZ(), 16) - Math.floorDiv(box.getMinZ(), 16) + 1);
	}

	@Override
	public NbtCompound toTag(NbtCompound tag) {
		super.toTag(tag);

		if (generator != null) tag.put("generator", generator.toTag(new NbtCompound()));
		if (toBeGennedChunkCount > 0) tag.putInt("to_be_genned_chunks", toBeGennedChunkCount);

		return tag;
	}

	@Override
	public AbstractPocketType<?> getType() {
		return AbstractPocketType.LAZY_GENERATION_POCKET;
	}

	public static String getKEY() {
		return KEY;
	}

	@Override
	public Pocket fromTag(NbtCompound tag) {
		super.fromTag(tag);

		if (tag.contains("generator", NbtType.COMPOUND)) generator = (LazyPocketGenerator) PocketGenerator.deserialize(tag.getCompound("generator"));
		if (tag.contains("to_be_genned_chunks", NbtType.INT)) toBeGennedChunkCount = tag.getInt("to_be_genned_chunks");

		return this;
	}

	@Override
	public Map<BlockPos, BlockEntity> getBlockEntities() {

		return super.getBlockEntities();
	}

	public static LazyGenerationPocketBuilder<?, LazyGenerationPocket> builderLazyGenerationPocket() {
		return new LazyGenerationPocketBuilder<>(AbstractPocketType.LAZY_GENERATION_POCKET);
	}

	public static class LazyGenerationPocketBuilder<P extends LazyGenerationPocketBuilder<P, T>, T extends LazyGenerationPocket> extends PocketBuilder<P, T> {
		protected LazyGenerationPocketBuilder(AbstractPocketType<T> type) {
			super(type);
		}
	}
}
