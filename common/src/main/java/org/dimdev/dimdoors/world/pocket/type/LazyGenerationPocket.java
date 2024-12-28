package org.dimdev.dimdoors.world.pocket.type;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.dimdev.dimdoors.api.util.BlockBoxUtil;
import org.dimdev.dimdoors.pockets.generator.LazyPocketGenerator;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.world.level.component.ChunkLazilyGeneratedComponent;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.Map;
import java.util.function.Function;

public class LazyGenerationPocket extends Pocket {
	public static String KEY = "lazy_gen_pocket";

	public static final MapCodec<LazyGenerationPocket> CODEC = RecordCodecBuilder.<LazyGenerationPocket>mapCodec(new Function<RecordCodecBuilder.Instance<LazyGenerationPocket>, App<RecordCodecBuilder.Mu<LazyGenerationPocket>, LazyGenerationPocket>>() {
		@Override
		public App<RecordCodecBuilder.Mu<LazyGenerationPocket>, LazyGenerationPocket> apply(RecordCodecBuilder.Instance<LazyGenerationPocket> lazyGenerationPocketInstance) {
			return lazyGenerationPocketInstance.group(

			);
		}
	})

	private LazyPocketGenerator generator;
	private int toBeGennedChunkCount = 0;

	public LazyGenerationPocket() {
		super();
	}

	public LazyGenerationPocket(int id, ResourceKey<Level> world, int range, BoundingBox box, VirtualLocation virtualLocation, Map<ResourceLocation, PocketAddon> addons, LazyPocketGenerator generator, int toBeGennedChunkCount) {
		super(id, world, range, box, virtualLocation, addons);
        this.generator = generator;
        this.toBeGennedChunkCount = toBeGennedChunkCount;
    }


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
	public CompoundTag toNbt(CompoundTag nbt) {
		super.toNbt(nbt);

		if (generator != null) nbt.put("generator", generator.toNbt(new CompoundTag()));
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
	public V fromNbt(CompoundTag nbt) {
		super.fromNbt(nbt);

		if (nbt.contains("generator", Tag.TAG_COMPOUND)) generator = (LazyPocketGenerator) PocketGenerator.deserialize(nbt.getCompound("generator"));
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
