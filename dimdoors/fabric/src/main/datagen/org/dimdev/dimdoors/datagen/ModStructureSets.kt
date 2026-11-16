package org.dimdev.dimdoors.datagen

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.structure.StructureSet
import org.dimdev.dimdoors.util.ext.key

object ModStructureSets {
    @JvmField val GATEWAYS: ResourceKey<StructureSet> = Registries.STRUCTURE_SET.key("gateways")
}
