package org.dimdev.dimdoors.api.util

import net.minecraft.core.Direction
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property

fun <T : Comparable<T>> BlockState.valueOrNull(property: Property<T>): T? = if(this.hasProperty(property)) this.getValue(property) else null
val BlockState.horizontalFacing: Direction? get() = valueOrNull(HorizontalDirectionalBlock.FACING)
