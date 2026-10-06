package org.dimdev.dimdoors.util

import net.minecraft.core.UUIDUtil
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.rift.registry.RegistryVertices
import org.dimdev.dimdoors.rift.registry.RiftRegistry
import org.dimdev.dimdoors.rift.registry.Vertex
import java.util.*

object UUIDExtensions {
    val MAP_CODEC = CodecUtils.mutableMap(UUIDUtil.CODEC)

    val UUID.riftLocation: Location? get() = RiftRegistry.instance.locationOf(this)

    fun UUID.rift(): Vertex = Vertex(this, RegistryVertices.RIFT)


    /* Simple method used to derive uuids based on a has derived from an existing UUID and two seperate Strings.*/
    fun UUID.derive(first: String, second: String): UUID {
        val salt = (first.hashCode().toLong() shl 32) or (second.hashCode().toLong() and 0xFFFFFFFFL)
        return UUID(scramble(mostSignificantBits xor salt), scramble(leastSignificantBits xor salt.inv()))
    }

    private fun scramble(value: Long): Long = (value xor (value ushr 32)) * -0x61c8864680b583ebL
}