package org.dimdev.dimdoors.util.schematic;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Iterator;
import java.util.Objects;

public class SchematicBlockPalette {
	public static final UnboundedMapCodec<BlockState, Integer> CODEC = Codec.unboundedMap(Entry.CODEC, Codec.INT);

	private static <T extends Comparable<T>> BlockState process(Property<T> property, String value, BlockState state) {
		return state.setValue(property, property.getValue(value).orElseThrow(NullPointerException::new));
	}

	public interface Entry {
		Codec<BlockState> CODEC = Codec.STRING.comapFlatMap(Entry::to, Entry::from);

		static DataResult<BlockState> to(String string) {
			// wow so hacky whooooo lets go
//			string = string.replace("dimdoors:iron_dimensional_door", "dimdoors:block_ag_dim_minecraft_iron_door");
//			string = string.replace("dimdoors:quartz_dimensional_door", "dimdoors:block_ag_dim_dimdoors_quartz_door");
//			string = string.replace("dimdoors:oak_dimensional_door", "dimdoors:block_ag_dim_minecraft_oak_door");
//			string = string.replace("dimdoors:gold_dimensional_door", "dimdoors:block_ag_dim_dimdoors_gold_door");
			StringReader reader = new StringReader(string);
			BlockStateParser.BlockResult parser;

			try {
				parser = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), reader, true);
			} catch (CommandSyntaxException e) {
				return DataResult.error(e::getMessage);
			}
			return DataResult.success(parser.blockState());
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
			builder.append(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(state.getBlock())));
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

				if (state.getValue(property) instanceof Enum<?>) {
					// Enum might have override toString
					builder.append(((StringRepresentable) state.getValue(property)).getSerializedName());
				} else {
					builder.append(state.getValue(property).toString());
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
