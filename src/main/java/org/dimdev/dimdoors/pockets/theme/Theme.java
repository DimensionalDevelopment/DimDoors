package org.dimdev.dimdoors.pockets.theme;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.tag.TagKey;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public record Theme(Map<Entry, Block> entries) implements Function<BlockState, BlockState> {
	public static Codec<Theme> CODEC = Codec.unboundedMap(Entry.CODEC, Registry.BLOCK.getCodec()).xmap(Theme::new, Theme::entries);

	public static final Theme NONE = new Theme(Map.of());

	@Override
	public BlockState apply(BlockState blockState) {
		if(entries.isEmpty()) return blockState;

		for (Map.Entry<Entry, Block> converter : entries.entrySet()) {
			if (converter.getKey().test(blockState)) {
				BlockState apply = converter.getValue().getDefaultState();
				if (apply != null) {
					for (Map.Entry<Property<?>, Comparable<?>> a : blockState.getEntries().entrySet()) {
						if (apply.contains(a.getKey())) {
							apply = apply.with((Property) a.getKey(), (Comparable) a.getValue());
						}
					}

					return apply;
				}
				return blockState;
			}
		}

		return blockState;
	}

	public interface Entry extends Predicate<BlockState> {
		Codec<Entry> CODEC = Codecs.xor(TagKey.stringCodec(Registry.BLOCK_KEY), Registry.BLOCK.getCodec())
				.xmap(a -> a.mapBoth(TagEntry::new, BlockEntry::new), b -> b.mapBoth(TagEntry::tag, BlockEntry::block))
				.xmap(either -> either.map(Function.identity(), Function.identity()), entry -> entry instanceof TagEntry ? Either.left((TagEntry) entry) : Either.right((BlockEntry) entry));

		public static Entry of(Object obj) {
			if(obj instanceof TagKey tag) return new TagEntry(tag);
			else if(obj instanceof Block block) return new BlockEntry(block);
			else return null;
		}

		record TagEntry(TagKey<Block> tag) implements Entry {
			@Override
			public boolean test(BlockState blockState) {
				return blockState.isIn(tag);
			}
		}

		record BlockEntry(Block block) implements Entry {
			@Override
			public boolean test(BlockState blockState) {
				return blockState.isOf(block);
			}
		}
	}
}
