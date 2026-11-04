package org.dimdev.dimdoors.util.schematic

import com.mojang.serialization.DataResult
import com.mojang.serialization.Dynamic
import com.mojang.serialization.OptionalDynamic
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
        val dataVersion = upgraded["Schematic"]["DataVersion"].asInt(current)

        if (dataVersion < current) {
            upgraded = DataFixers.getDataFixer().update(SchematicDataFixer.SCHEMATIC, upgraded, dataVersion, current)
            upgraded = upgraded.update("Schematic") { it.set("DataVersion", current) }
        }

        return upgraded.value as CompoundTag
    }

    @JvmStatic
    fun <T> toV3(root: Dynamic<T>): Dynamic<T> {
        var schematic = root["Schematic"].getOrNull() ?: root
        val version = schematic["Version"].asInt(1)

        if (version < 2) schematic = v1ToV2(schematic)
        if (version < 3) schematic = v2ToV3(schematic)

        return root.emptyMap().set("Schematic", schematic)
    }

    private fun <T> v1ToV2(v1: Dynamic<T>): Dynamic<T> {
        val v2 = v1.set("Version", 2).set("DataVersion", V1_DATA_VERSION) - "TileEntities"
        return copyList(v1, "TileEntities", v2, "BlockEntities") { it - "ContentVersion" }
    }

    private fun <T> v2ToV3(v2: Dynamic<T>): Dynamic<T> {
        val width = v2["Width"].asInt(0) and 0xFFFF
        val length = v2["Length"].asInt(0) and 0xFFFF
        val height = v2["Height"].asInt(0) and 0xFFFF
        val dataVersion = v2["DataVersion"].asInt(v2["Data Version"].asInt(V1_DATA_VERSION))

        var blocks = copy(v2, "BlockData", v2.copy(v2.emptyMap(), "Palette"), "Data")
        blocks = v2.copyList(blocks, "BlockEntities") { nestExtraData(it) }

        var v3 = v2.copy(v2.emptyMap().set("Version", 3).set("DataVersion", dataVersion), "Metadata", "Width", "Height", "Length", "Offset").set("Blocks", blocks)
        v3 = copyList(v2, "Entities", v3, "Entities") { nestExtraData(it) }

        val biomePalette = v2["BiomePalette"].getOrNull()
        val biomeData = v2["BiomeData"].asByteBufferOpt().getOrNull()
        if (biomePalette != null && biomeData != null) {
            v3 = v3.set("Biomes", v2.emptyMap()
                .set("Palette", biomePalette)
                .set("Data", v2.createByteList(expandBiomes(biomeData, width, height, length))))
        }

        return v3
    }

    private fun <T> nestExtraData(entry: Dynamic<T>): Dynamic<T> =
        entry.copy(copy(entry, "id", entry.emptyMap(), "Id"), "Pos", "Id").set("Data", entry - "Pos" - "Id" - "id")

    fun <T> OptionalDynamic<T>.getOrNull(): Dynamic<T>? {
        return this.result().getOrNull()
    }

    fun <T> Dynamic<T>.set(key: String, value: Int): Dynamic<T> {
        return this.set(key, this.createInt(value))
    }

    operator fun <T> Dynamic<T>.minus(key: String): Dynamic<T> = this.remove(key)

    private fun <T> Dynamic<T>.copy(to: Dynamic<T>, vararg keys: String): Dynamic<T> = keys.fold(to) { acc, key -> copy(this, key, acc, key) }

    fun <T> DataResult<T>.getOrNull(): T? = this.result().orElse(null)

    private fun <T> copy(from: Dynamic<T>, fromKey: String, to: Dynamic<T>, toKey: String): Dynamic<T> {
        return from[fromKey].getOrNull()?.let { to.set(toKey, it) } ?: to
    }

    private fun <T> Dynamic<T>.copyList(to: Dynamic<T>, key: String, transform: (Dynamic<T>) -> Dynamic<T>): Dynamic<T> {
        return copyList(this, key, to, key, transform)
    }

    private fun <T> copyList(from: Dynamic<T>, fromKey: String, to: Dynamic<T>, toKey: String, transform: (Dynamic<T>) -> Dynamic<T>): Dynamic<T> {
        return from[fromKey].getOrNull()?.let { to.set(toKey, to.createList(it.asStream().map { entry -> transform(entry) })) } ?: to
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

    internal fun writeVarInt(output: ByteArrayOutputStream, value: Int) {
        var remaining = value
        while (remaining and 0x7F.inv() != 0) {
            output.write((remaining and 0x7F) or 0x80)
            remaining = remaining ushr 7
        }
        output.write(remaining)
    }
}
