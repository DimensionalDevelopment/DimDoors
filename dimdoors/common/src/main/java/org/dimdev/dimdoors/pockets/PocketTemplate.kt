package org.dimdev.dimdoors.pockets

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.util.schematic.Schematic
import org.dimdev.dimdoors.util.schematic.SchematicPlacer
import org.dimdev.dimdoors.world.pocket.type.Pocket

sealed interface PocketTemplate {
    val size: Vec3i

    fun place(pocket: Pocket<*, *>)

    data class SchematicTemplate(val schematic: Schematic) : PocketTemplate {
        override val size: Vec3i get() = Vec3i(schematic.width.toInt(), schematic.height.toInt(), schematic.length.toInt())

        override fun place(pocket: Pocket<*, *>) {
            pocket.setSize(schematic.width.toInt(), schematic.height.toInt(), schematic.length.toInt())
            SchematicPlacer.place(schematic, DimensionalDoors.getWorld(pocket.world)!!, pocket.origin)
        }

        companion object {
            @JvmStatic
            fun create(tag: CompoundTag) = SchematicTemplate(Schematic.fromNbt(tag))
        }
    }

    data class NbtTemplate(val template: StructureTemplate) : PocketTemplate {
        override val size: Vec3i get() = template.size

        override fun place(pocket: Pocket<*, *>) {
            pocket.size = template.size
            val world = DimensionalDoors.getWorld(pocket.world)!!
            template.placeInWorld(world, BlockPos.ZERO, pocket.origin, SETTINGS, world.random, 0)
        }

        companion object {
            private val SETTINGS = StructurePlaceSettings()

            @JvmStatic
            fun create(tag: CompoundTag) = NbtTemplate(StructureTemplate().apply { load(BuiltInRegistries.BLOCK.asLookup(), tag) })
        }
    }
}
