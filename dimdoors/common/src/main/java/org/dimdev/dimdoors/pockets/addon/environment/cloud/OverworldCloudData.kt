package org.dimdev.dimdoors.pockets.addon.environment.cloud

import net.minecraft.world.phys.Vec3

interface OverworldCloudData : CloudData {
    val cloudHeight: Float

    val cloudColor: Vec3

    override val type get() = CloudDatum.OVERWORLD
}
