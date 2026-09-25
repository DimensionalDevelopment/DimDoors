package org.dimdev.dimdoors.fluid

import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimcore.api.fluid.FluidDetails
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided

object ModFluids : PlatformRegistry.FluidPlatformRegistry(getSided()) {
    var ETERNAL_FLUID_DETAILS = FluidDetails.of(DimensionalDoors.id("eternal_fluid"))
    val FLOWING_ETERNAL_FLUID = create("flowing_eternal_fluid") { getSided().createFlowingEternalFluid() }
    val ETERNAL_FLUID = create("eternal_fluid") { getSided().createEternalFluid() }

    var LEAK_DETAILS = FluidDetails.of(DimensionalDoors.id("leak"))
    val FLOWING_LEAK = create("flowing_leak_fluid") { getSided().createFlowingLeakFluid() }
    val LEAK = create("leak") { getSided().createLeakFluid() }
}
