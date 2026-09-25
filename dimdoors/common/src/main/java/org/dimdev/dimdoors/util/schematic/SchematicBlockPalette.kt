package org.dimdev.dimdoors.util.schematic

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.UnboundedMapCodec
import net.minecraft.commands.arguments.blocks.BlockStateParser
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.util.StringRepresentable
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property
import java.util.Objects

object SchematicBlockPalette {
    @JvmField
    val CODEC: UnboundedMapCodec<BlockState, Int> = Codec.unboundedMap(Entry.CODEC, Codec.INT)

    private fun <T : Comparable<T>> process(property: Property<T>, value: String, state: BlockState): BlockState {
        return state.setValue(property, property.getValue(value).orElseThrow { NullPointerException() })
    }

    interface Entry {
        companion object {
            @JvmField
            val CODEC: Codec<BlockState> = Codec.STRING.comapFlatMap({ to(it) }, { from(it) })

            @JvmStatic
            fun to(string: String): DataResult<BlockState> {
                // wow so hacky whooooo lets go
//            string = string.replace("dimdoors:iron_dimensional_door", "dimdoors:block_ag_dim_minecraft_iron_door");
//            string = string.replace("dimdoors:quartz_dimensional_door", "dimdoors:block_ag_dim_dimdoors_quartz_door");
//            string = string.replace("dimdoors:oak_dimensional_door", "dimdoors:block_ag_dim_minecraft_oak_door");
//            string = string.replace("dimdoors:gold_dimensional_door", "dimdoors:block_ag_dim_dimdoors_gold_door");
                val reader = StringReader(string)
                val parser: BlockStateParser.BlockResult

                try {
                    parser = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), reader, true)
                } catch (e: CommandSyntaxException) {
                    return DataResult.error { e.message }
                }
                return DataResult.success(parser.blockState())
//            if (!string.contains("[") && !string.contains("]")) {
//                BlockState state = Registry.BLOCK.get(new Identifier(string)).getDefaultState();
//                return DataResult.success(state);
//            } else {
//                Block block = Objects.requireNonNull(Registry.BLOCK.get(new Identifier(string.substring(0, string.indexOf("[")))));
//                BlockState state = block.getDefaultState();
//
//                String[] stateArray = string.substring(string.indexOf("[") + 1, string.length() - 1).split(",");
//                for (String stateString : stateArray) {
//                    Property<?> property;
//                    try {
//                        property = Objects.requireNonNull(block.getStateManager().getProperty(stateString.split("=")[0]));
//                    } catch (RuntimeException e) {
//                        return DataResult.error("Unknown block state property \"" + stateString + "\"", state);
//                    }
//                    state = process(property, stateString.split("=")[1], state);
//                }
//
//                return DataResult.success(state);
//            }
            }

            @JvmStatic
            fun from(state: BlockState): String {
                val builder = StringBuilder()
                builder.append(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(state.block)))
                // Ensures that [ and ] are only added when properties are present
                var flag = true
                val iterator = state.properties.iterator()
                while (iterator.hasNext()) {
                    if (flag) {
                        builder.append("[")
                        flag = false
                    }

                    val property = iterator.next()
                    builder.append(property.name)
                    builder.append("=")

                    val value = state.values[property]
                    if (value is Enum<*>) {
                        // Enum might have override toString
                        builder.append((value as StringRepresentable).serializedName)
                    } else {
                        builder.append(value.toString())
                    }

                    if (iterator.hasNext()) {
                        builder.append(",")
                    }
                }
                if (!flag) {
                    builder.append("]")
                }
                return builder.toString()
            }
        }
    }
}
