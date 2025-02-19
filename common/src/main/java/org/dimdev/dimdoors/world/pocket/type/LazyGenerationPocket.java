package org.dimdev.dimdoors.world.pocket.type;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
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

public class LazyGenerationPocket extends Pocket {
	public static String KEY = "lazy_gen_pocket";


	public static <T extends LazyGenerationPocket> Products.P8<RecordCodecBuilder.Mu<T>, Integer, ResourceKey<Level>, Integer, BoundingBox, VirtualLocation, Map<ResourceLocation, PocketAddon>, LazyPocketGenerator, Integer> lazyFields(RecordCodecBuilder.Instance<T> instance) {
		return commonPocketFields(instance).and(
				PocketGenerator.CODEC.flatXmap(pocketGenerator -> pocketGenerator instanceof LazyPocketGenerator lazy ? DataResult.success(lazy) : DataResult.error(() -> "Pocket Generator doesn't extend LazyPocketGenerator"), DataResult::success).optionalFieldOf("generator", null).forGetter(a -> a.generator)).and(
				Codec.INT.optionalFieldOf("toBeGennedChunkCount", 0).forGetter(a -> a.toBeGennedChunkCount)
		);
	}

	public static final MapCodec<LazyGenerationPocket> CODEC = RecordCodecBuilder.mapCodec(instance -> lazyFields(instance).apply(instance, LazyGenerationPocket::new));

	protected LazyPocketGenerator generator;
	protected int toBeGennedChunkCount = 0;

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
	public AbstractPocketType<?, ?> getType() {
		return AbstractPocketType.LAZY_GENERATION_POCKET.get();
	}

	public static String getKEY() {
		return KEY;
	}

	@Override
	public Map<BlockPos, BlockEntity> getBlockEntities() {

		return super.getBlockEntities();
	}

	public static LazyGenerationPocketBuilder<LazyGenerationPocketBuilderImpl, LazyGenerationPocket> builderLazyGenerationPocket() {
		return new LazyGenerationPocketBuilderImpl();
	}

	public static abstract class LazyGenerationPocketBuilder<P extends LazyGenerationPocketBuilder<P, T>, T extends LazyGenerationPocket> extends PocketBuilder<P, T> {
		protected LazyGenerationPocketBuilder(Vec3i origin, Vec3i size, VirtualLocation virtualLocation, int range) {
			super(origin, size, virtualLocation, range);
		}

		protected LazyGenerationPocketBuilder() {
			super();
		}
	}

	public static class LazyGenerationPocketBuilderImpl extends LazyGenerationPocketBuilder<LazyGenerationPocketBuilderImpl, LazyGenerationPocket> {
		protected LazyGenerationPocketBuilderImpl(Vec3i origin, Vec3i size, VirtualLocation virtualLocation, int range) {
			super(origin, size, virtualLocation, range);
		}

		protected LazyGenerationPocketBuilderImpl() {
			super();
		}

		@Override
		public AbstractPocketType<?, ?> getType() {
			return AbstractPocketType.LAZY_GENERATION_POCKET.get();
		}
	}
}
