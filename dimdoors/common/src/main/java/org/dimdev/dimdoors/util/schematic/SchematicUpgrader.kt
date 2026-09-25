package org.dimdev.dimdoors.util.schematic

import com.mojang.serialization.Dynamic
import net.minecraft.SharedConstants
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.util.datafix.DataFixers
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import kotlin.jvm.optionals.getOrNull

object SchematicUpgrader {
    const val V1_DATA_VERSION = 1631

    @JvmStatic
    fun update(tag: CompoundTag): CompoundTag {
        var upgraded: Dynamic<Tag> = toV3(Dynamic(NbtOps.INSTANCE, tag))
        val current = SharedConstants.getCurrentVersion().dataVersion.version
        val dataVersion = upgraded.get("Schematic").get("DataVersion").asInt(current)

        if (dataVersion < current) {
            upgraded = DataFixers.getDataFixer().update(SchematicDataFixer.SCHEMATIC, upgraded, dataVersion, current)
            upgraded = upgraded.update("Schematic") { it.set("DataVersion", it.createInt(current)) }
        }

        return upgraded.value as CompoundTag
    }

    @JvmStatic
    fun <T> toV3(root: Dynamic<T>): Dynamic<T> {
        var schematic = root.get("Schematic").result().getOrNull() ?: root
        val version = schematic.get("Version").asInt(1)

        if (version < 2) schematic = v1ToV2(schematic)
        if (version < 3) schematic = v2ToV3(schematic)

        return root.emptyMap().set("Schematic", schematic)
    }

    private fun <T> v1ToV2(v1: Dynamic<T>): Dynamic<T> {
        var v2 = v1.set("Version", v1.createInt(2)).set("DataVersion", v1.createInt(V1_DATA_VERSION)).remove("TileEntities")

        v1.get("TileEntities").result().getOrNull()?.let { tileEntities ->
            v2 = v2.set("BlockEntities", v1.createList(tileEntities.asStream().map { it.remove("ContentVersion") }))
        }

        return v2
    }

    private fun <T> v2ToV3(v2: Dynamic<T>): Dynamic<T> {
        val width = v2.get("Width").asInt(0) and 0xFFFF
        val length = v2.get("Length").asInt(0) and 0xFFFF
        val height = v2.get("Height").asInt(0) and 0xFFFF
        val dataVersion = (v2.get("DataVersion").asNumber().result().getOrNull()
            ?: v2.get("Data Version").asNumber().result().getOrNull())?.toInt()
            ?: V1_DATA_VERSION

        var blocks = copy(v2, "Palette", v2.emptyMap(), "Palette")
        blocks = copy(v2, "BlockData", blocks, "Data")
        v2.get("BlockEntities").result().getOrNull()?.let { blockEntities ->
            blocks = blocks.set("BlockEntities", v2.createList(blockEntities.asStream().map { nestExtraData(it) }))
        }

        var v3 = v2.emptyMap()
            .set("Version", v2.createInt(3))
            .set("DataVersion", v2.createInt(dataVersion))
        v3 = copy(v2, "Metadata", v3, "Metadata")
        v3 = copy(v2, "Width", v3, "Width")
        v3 = copy(v2, "Height", v3, "Height")
        v3 = copy(v2, "Length", v3, "Length")
        v3 = copy(v2, "Offset", v3, "Offset")
        v3 = v3.set("Blocks", blocks)

        val biomePalette = v2.get("BiomePalette").result().getOrNull()
        val biomeData = v2.get("BiomeData").asByteBufferOpt().result().getOrNull()
        if (biomePalette != null && biomeData != null) {
            v3 = v3.set("Biomes", v2.emptyMap()
                .set("Palette", biomePalette)
                .set("Data", v2.createByteList(expandBiomes(biomeData, width, height, length))))
        }

        v2.get("Entities").result().getOrNull()?.let { entities ->
            v3 = v3.set("Entities", v2.createList(entities.asStream().map { nestExtraData(it) }))
        }

        return v3
    }

    private fun <T> nestExtraData(entry: Dynamic<T>): Dynamic<T> {
        var nested = copy(entry, "Pos", entry.emptyMap(), "Pos")
        (entry.get("Id").result().getOrNull() ?: entry.get("id").result().getOrNull())?.let { nested = nested.set("Id", it) }
        return nested.set("Data", entry.remove("Pos").remove("Id").remove("id"))
    }

    private fun <T> copy(from: Dynamic<T>, key: String, to: Dynamic<T>, newKey: String): Dynamic<T> {
        return from.get(key).result().getOrNull()?.let { to.set(newKey, it) } ?: to
    }

    private fun expandBiomes(columns: ByteBuffer, width: Int, height: Int, length: Int): ByteBuffer {
        val input = columns.asReadOnlyBuffer()
        input.rewind()

        val column = IntArray(width * length) { readVarInt(input) }

        val output = ByteArrayOutputStream()
        repeat(height) {
            column.forEach { writeVarInt(output, it) }
        }

        return ByteBuffer.wrap(output.toByteArray())
    }

    private fun readVarInt(buffer: ByteBuffer): Int {
        var value = 0
        var shift = 0
        var next: Int
        do {
            next = buffer.get().toInt() and 0xFF
            value = value or ((next and 0x7F) shl shift)
            shift += 7
        } while (next and 0x80 != 0)
        return value
    }

    private fun writeVarInt(output: ByteArrayOutputStream, value: Int) {
        var remaining = value
        while (remaining and 0x7F.inv() != 0) {
            output.write((remaining and 0x7F) or 0x80)
            remaining = remaining ushr 7
        }
        output.write(remaining)
    }
}
