package org.dimdev.dimdoors.api.block.entity;

import com.mojang.datafixers.types.Type;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.fabric.mixin.lookup.BlockEntityTypeAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MutableBlockEntityType<T extends BlockEntity> extends BlockEntityType<T> {

	public MutableBlockEntityType(BlockEntityFactory<? extends T> factory, Set<Block> blocks, Type<?> type) {
		// ensure that the Set is mutable
		super(factory, new HashSet<>(blocks), type);
	}

	public boolean addBlock(Block block) {
		return getBlocks(this).add(block);
	}

	public boolean removeBlock(Block block) {
		return getBlocks(this).remove(block);
	}

	@ExpectPlatform
	public static Set<Block> getBlocks(BlockEntityType<?> type) {
		throw new RuntimeException();
	}

	public static final class Builder<T extends BlockEntity> {
		private final BlockEntityFactory<? extends T> factory;
		private final Set<Block> blocks;

		private Builder(BlockEntityFactory<? extends T> factory, Set<Block> blocks) {
			this.factory = factory;
			this.blocks = blocks;
		}

		public static <T extends BlockEntity> Builder<T> create(BlockEntityFactory<? extends T> factory, Block... blocks) {
			// ensure mutability
			return new Builder<>(factory, new HashSet<>(Arrays.asList(blocks)));
		}

		public MutableBlockEntityType<T> build() {
			return build(null);
		}

		public MutableBlockEntityType<T> build(Type<?> type) {
			return new MutableBlockEntityType<>(this.factory, this.blocks, type);
		}
	}


	// exists for convenience so that no access widener for BlockEntityType.BlockEntityFactory is necessary
	@FunctionalInterface
	public interface BlockEntityFactory<T extends BlockEntity> extends BlockEntityType.BlockEntitySupplier<T> {
	}
}
