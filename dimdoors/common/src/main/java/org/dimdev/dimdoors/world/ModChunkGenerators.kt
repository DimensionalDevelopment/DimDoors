package org.dimdev.dimdoors.world

import com.mojang.serialization.MapCodec
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.chunk.ChunkGenerator
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.world.pocket.BlankChunkGenerator

object ModChunkGenerators : PlatformRegistry<MapCodec<out ChunkGenerator>>(Registries.CHUNK_GENERATOR, BuiltInRegistries.CHUNK_GENERATOR, DimensionalDoors.getSided()) {
    @JvmField val BLANK = create("blank") { BlankChunkGenerator.CODEC }
}
