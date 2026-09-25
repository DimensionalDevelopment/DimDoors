package org.dimdev.dimdoors.util.schematic

import com.mojang.datafixers.DSL
import com.mojang.datafixers.schemas.Schema
import com.mojang.datafixers.types.templates.Hook.HookFunction
import com.mojang.datafixers.types.templates.TypeTemplate
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Dynamic
import com.mojang.serialization.DynamicOps
import net.minecraft.util.datafix.fixes.References
import java.util.TreeMap
import kotlin.jvm.optionals.getOrNull

object SpongeSchematicV2FixTypes {
    private const val PALETTE_STATE = "State"
    private const val PALETTE_ID = "Id"
    private const val PALETTE_ORIGINAL = "Original"
    private val SPONGE_TO_VANILLA: HookFunction = object : HookFunction {
        override fun <T> apply(ops: DynamicOps<T>, value: T): T = spongeToVanilla(Dynamic(ops, value)).value
    }
    private val VANILLA_TO_SPONGE: HookFunction = object : HookFunction {
        override fun <T> apply(ops: DynamicOps<T>, value: T): T = vanillaToSponge(Dynamic(ops, value)).value
    }
    private val FLAT_PALETTE_TO_BLOCK_STATE_LIST: HookFunction = object : HookFunction {
        override fun <T> apply(ops: DynamicOps<T>, value: T): T = flatPaletteToBlockStateList(ops, value)
    }
    private val BLOCK_STATE_LIST_TO_FLAT_PALETTE: HookFunction = object : HookFunction {
        override fun <T> apply(ops: DynamicOps<T>, value: T): T = blockStateListToFlatPalette(ops, value)
    }

    @JvmStatic
    fun schematic(schema: Schema): TypeTemplate {
        val spongePalette = DSL.hook(
            DSL.list(DSL.optionalFields(
                Pair.of(PALETTE_STATE, References.BLOCK_STATE.`in`(schema)),
                Pair.of(PALETTE_ID, DSL.constType(DSL.intType()))
            )),
            FLAT_PALETTE_TO_BLOCK_STATE_LIST,
            BLOCK_STATE_LIST_TO_FLAT_PALETTE
        )
        val spongeBlockEntity = DSL.or(
            DSL.hook(References.BLOCK_ENTITY.`in`(schema), SPONGE_TO_VANILLA, VANILLA_TO_SPONGE),
            DSL.remainder()
        )
        val spongeEntity = DSL.or(
            DSL.hook(References.ENTITY_TREE.`in`(schema), SPONGE_TO_VANILLA, VANILLA_TO_SPONGE),
            DSL.remainder()
        )

        return DSL.optionalFields("Schematic", DSL.optionalFields(
            Pair.of("Blocks", DSL.optionalFields(
                Pair.of("Palette", spongePalette),
                Pair.of("BlockEntities", DSL.list(spongeBlockEntity))
            )),
            Pair.of("Entities", DSL.list(spongeEntity))
        ))
    }

    private fun <T> spongeToVanilla(entry: Dynamic<T>): Dynamic<T> {
        var vanilla = entry.get("Data").orElseEmptyMap()
        entry.get("Id").result().getOrNull()?.let { vanilla = vanilla.set("id", it) }
        entry.get("Pos").result().getOrNull()?.let { vanilla = vanilla.set("Pos", it) }
        return vanilla
    }

    private fun <T> vanillaToSponge(entry: Dynamic<T>): Dynamic<T> {
        var sponge = entry.emptyMap()
        entry.get("Pos").result().getOrNull()?.let { sponge = sponge.set("Pos", it) }
        entry.get("id").result().getOrNull()?.let { sponge = sponge.set("Id", it) }
        return sponge.set("Data", entry.remove("id").remove("Pos"))
    }

    private fun <T> flatPaletteToBlockStateList(ops: DynamicOps<T>, value: T): T {
        val entries = Dynamic(ops, value).asMapOpt().result().getOrNull() ?: return value

        val converted = mutableListOf<T>()
        entries.forEach { entry ->
            entry.first.asString().result().ifPresent { blockState ->
                val fields = mutableListOf<Pair<T, T>>()
                readBlockState(ops, blockState)?.let { state -> fields.add(Pair.of(ops.createString(PALETTE_STATE), state)) }
                fields.add(Pair.of(ops.createString(PALETTE_ID), entry.second.value))
                fields.add(Pair.of(ops.createString(PALETTE_ORIGINAL), ops.createString(blockState)))
                converted.add(ops.createMap(fields.stream()))
            }
        }
        return ops.createList(converted.stream())
    }

    @JvmStatic
    fun <T> blockStateListToFlatPalette(ops: DynamicOps<T>, value: T): T {
        val entries = Dynamic(ops, value).asStreamOpt().result().getOrNull() ?: return value

        val converted = mutableListOf<Pair<T, T>>()
        entries.forEach { entry ->
            entry.get(PALETTE_ID).result().ifPresent { id ->
                val blockState = entry.get(PALETTE_STATE).result().getOrNull()?.let { writeBlockState(it) }
                    ?: entry.get(PALETTE_ORIGINAL).asString("")
                if (blockState.isNotEmpty()) {
                    converted.add(Pair.of(ops.createString(blockState), id.value))
                }
            }
        }
        return ops.createMap(converted.stream())
    }

    @JvmStatic
    fun <T> readBlockState(ops: DynamicOps<T>, value: String): T? {
        if (value.isEmpty()) {
            return null
        }

        val propertyStart = value.indexOf('[')
        if (propertyStart == 0 || propertyStart != -1 && !value.endsWith("]")) {
            return null
        }

        val name = if (propertyStart == -1) value else value.substring(0, propertyStart)
        val state = mutableListOf<Pair<T, T>>()
        state.add(Pair.of(ops.createString("Name"), ops.createString(qualifyMinecraftId(name))))

        if (propertyStart != -1) {
            val body = value.substring(propertyStart + 1, value.length - 1)
            if (body.isNotEmpty()) {
                val properties = mutableListOf<Pair<T, T>>()
                for (property in body.split(",")) {
                    val split = property.indexOf('=')
                    if (split <= 0 || split >= property.length - 1) {
                        return null
                    }

                    properties.add(Pair.of(
                        ops.createString(property.substring(0, split)),
                        ops.createString(property.substring(split + 1))
                    ))
                }
                state.add(Pair.of(ops.createString("Properties"), ops.createMap(properties.stream())))
            }
        }

        return ops.createMap(state.stream())
    }

    private fun <T> writeBlockState(state: Dynamic<T>): String? {
        val name = state.get("Name").asString().result().getOrNull() ?: return null

        val builder = StringBuilder(name)
        state.get("Properties").result().ifPresent { properties ->
            val sorted = TreeMap<String, String>()
            properties.asMapOpt().result().ifPresent { entries ->
                entries.forEach { entry ->
                    val key = entry.first.asString().result().getOrNull()
                    val value = entry.second.asString().result().getOrNull()
                    if (key != null && value != null) {
                        sorted[key] = value
                    }
                }
            }

            if (sorted.isNotEmpty()) {
                builder.append(sorted.entries.joinToString(",", "[", "]") { "${it.key}=${it.value}" })
            }
        }

        return builder.toString()
    }

    private fun qualifyMinecraftId(id: String): String {
        return if (id.indexOf(':') == -1) "minecraft:$id" else id
    }
}
