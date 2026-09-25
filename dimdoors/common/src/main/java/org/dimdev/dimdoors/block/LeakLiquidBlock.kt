package org.dimdev.dimdoors.block

import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.material.FlowingFluid
import org.dimdev.dimdoors.fluid.ModFluids

class LeakLiquidBlock(properties: Properties) : LiquidBlock(ModFluids.LEAK.value(), properties)
