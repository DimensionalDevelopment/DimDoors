package org.dimdev.dimdoors.world

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import org.dimdev.dimcore.api.PlacedContentTracker
import java.util.Arrays

object ChunkPlacedContentTracker : PlacedContentTracker {
    private val SECTION_CODEC: Codec<Pair<Int, LongArray>> = RecordCodecBuilder.create { instance ->
        instance.group(
            Codec.INT.fieldOf("y").forGetter { it.first },
            Codec.LONG_STREAM.xmap({ it.toArray() }, { Arrays.stream(it) }).fieldOf("bits").forGetter { it.second }
        ).apply(instance) { y, bits -> y to bits }
    }

    @JvmField val CODEC: Codec<Int2ObjectOpenHashMap<LongArray>> = SECTION_CODEC.listOf().xmap(
        { sections -> Int2ObjectOpenHashMap<LongArray>().apply { sections.forEach { (y, bits) -> put(y, bits) } } },
        { sections -> sections.int2ObjectEntrySet().map { it.intKey to it.value } }
    )

    override fun isPlaced(level: Level, pos: BlockPos): Boolean {
        val bits = DataValues.PLACED_CONTENT.get(level.getChunk(pos))?.get(SectionPos.blockToSectionCoord(pos.y)) ?: return false
        val i = index(pos)
        return (bits[i ushr 6] and (1L shl (i and 63))) != 0L
    }

    override fun onPlaced(level: ServerLevel, pos: BlockPos) {
        val chunk = level.getChunk(pos)
        val bits = DataValues.PLACED_CONTENT.getOrCreate(chunk).computeIfAbsent(SectionPos.blockToSectionCoord(pos.y)) { LongArray(64) }
        val i = index(pos)
        bits[i ushr 6] = bits[i ushr 6] or (1L shl (i and 63))
        chunk.isUnsaved = true
    }

    override fun onRemoved(level: ServerLevel, pos: BlockPos) {
        val chunk = level.getChunk(pos)
        val bits = DataValues.PLACED_CONTENT.get(chunk)?.get(SectionPos.blockToSectionCoord(pos.y)) ?: return
        val i = index(pos)
        bits[i ushr 6] = bits[i ushr 6] and (1L shl (i and 63)).inv()
        chunk.isUnsaved = true
    }

    private fun index(pos: BlockPos) = ((pos.y and 15) shl 8) or ((pos.z and 15) shl 4) or (pos.x and 15)
}
