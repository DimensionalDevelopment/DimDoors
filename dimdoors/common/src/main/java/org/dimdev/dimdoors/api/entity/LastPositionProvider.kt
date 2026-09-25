package org.dimdev.dimdoors.api.entity

import net.minecraft.world.phys.Vec3

interface LastPositionProvider {
    val lastPos: Vec3
}
