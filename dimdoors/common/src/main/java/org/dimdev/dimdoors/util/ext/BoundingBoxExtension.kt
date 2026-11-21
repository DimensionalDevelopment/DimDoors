package org.dimdev.dimdoors.util.ext

import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.levelgen.structure.BoundingBox

val BoundingBox.positions get() = ((minX() shr 4)..(maxX() shr 4)).flatMap { x -> ((minZ() shr 4)..(maxZ() shr 4)).map { z -> ChunkPos(x, z) } }
val BoundingBox.chunkPositionsAsLong get() = ((minX() shr 4)..(maxX() shr 4)).flatMap { x -> ((minZ() shr 4)..(maxZ() shr 4)).map { z -> ChunkPos.asLong(x, z) } }