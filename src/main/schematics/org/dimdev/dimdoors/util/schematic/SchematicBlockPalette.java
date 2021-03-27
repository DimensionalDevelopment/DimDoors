package org.dimdev.dimdoors.util.schematic;

import java.util.Iterator;
import java.util.Objects;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.UnboundedMapCodec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.Registry;

public class SchematicBlockPalette {
	public static final UnboundedMapCodec<BlockState, Integer> CODEC = Codec.unboundedMap(Entry.CODEC, Codec.INT);

	private static <T extends Comparable<T>> BlockState process(Property<T> property, String value, BlockState state) {
		return state.with(property, property.parse(value).orElseThrow(NullPointerException::new));
	}

	public interface Entry {
		Codec<BlockState> CODEC = Codec.STRING.comapFlatMap(Entry::to, Entry::from);

		static DataResult<BlockState> to(String string) {
			StringReader reader = new StringReader(string);
			BlockArgumentParser parser = new BlockArgumentParser(reader, true);
			try {
				parser.parse(true);
			} catch (CommandSyntaxException e) {
				return DataResult.error(e.getMessage());
			}
			return DataResult.success(parser.getBlockState());
//			if (!string.contains("[") && !string.contains("]")) {
//				BlockState state = Registry.BLOCK.get(new Identifier(string)).getDefaultState();
//				return DataResult.success(state);
//			} else {
//				Block block = Objects.requireNonNull(Registry.BLOCK.get(new Identifier(string.substring(0, string.indexOf("[")))));
//				BlockState state = block.getDefaultState();
//
//				String[] stateArray = string.substring(string.indexOf("[") + 1, string.length() - 1).split(",");
//				for (String stateString : stateArray) {
//					Property<?> property;
//					try {
//						property = Objects.requireNonNull(block.getStateManager().getProperty(stateString.split("=")[0]));
//					} catch (RuntimeException e) {
//						return DataResult.error("Unknown block state property \"" + stateString + "\"", state);
//					}
//					state = process(property, stateString.split("=")[1], state);
//				}
//
//				return DataResult.success(state);
//			}
		}

		static String from(BlockState state) {
			StringBuilder builder = new StringBuilder();
			builder.append(Objects.requireNonNull(Registry.BLOCK.getId(state.getBlock())));
			// Ensures that [ and ] are only added when properties are present
			boolean flag = true;
			Iterator<Property<?>> iterator = state.getProperties().iterator();
			while (iterator.hasNext()) {
				if (flag) {
					builder.append("[");
					flag = false;
				}

				Property<?> property = iterator.next();
				builder.append(property.getName());
				builder.append("=");

				if (state.get(property) instanceof Enum<?>) {
					// Enum might have override toString
					builder.append(((StringIdentifiable) state.get(property)).asString());
				} else {
					builder.append(state.get(property).toString());
				}

				if (iterator.hasNext()) {
					builder.append(",");
				}
			}
			if (!flag) {
				builder.append("]");
			}
			return builder.toString();
		}
	}
}
