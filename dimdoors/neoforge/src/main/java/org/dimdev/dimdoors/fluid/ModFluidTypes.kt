package org.dimdev.dimdoors.fluid

import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.registries.NeoForgeRegistries
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors

object ModFluidTypes : PlatformRegistry<FluidType>(NeoForgeRegistries.Keys.FLUID_TYPES, NeoForgeRegistries.FLUID_TYPES, DimensionalDoors.getSided()) {
    val ETERNAL = create("eternal_fluid") { FluidType(FluidType.Properties.create().lightLevel(15).temperature(1300).viscosity(6000)) }
    val LEAK = create("leak") { FluidType(FluidType.Properties.create()) }
}
