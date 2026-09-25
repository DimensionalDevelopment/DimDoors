package org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud

import net.minecraft.core.Holder
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.Type

interface OverworldCloudData : CloudData {
    val cloudHeight: Float

    val cloudColor: Vec3

    override val type: Holder<out Type<CloudData>>
        get() = CloudDatum.OVERWORLD
    }
}
