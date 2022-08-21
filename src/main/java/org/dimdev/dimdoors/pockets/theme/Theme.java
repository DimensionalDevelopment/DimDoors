package org.dimdev.dimdoors.pockets.theme;

import net.minecraft.block.BlockState;

import java.util.List;
import java.util.function.Function;

public record Theme(List<Converter> converters) implements Function<BlockState, BlockState> {
	public static final Theme NONE = new Theme(List.of());

	@Override
	public BlockState apply(BlockState blockState) {
		for (Converter converter : converters) {
			if (converter.test(blockState)) {
				BlockState apply = converter.apply(blockState);
				if (apply != null) return apply;
				return blockState;
			}
		}

		return blockState;
	}
}
