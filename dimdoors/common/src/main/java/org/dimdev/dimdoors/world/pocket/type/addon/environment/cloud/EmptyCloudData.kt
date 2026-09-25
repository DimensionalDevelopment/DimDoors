package org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud

import net.minecraft.core.Holder
import org.dimdev.dimcore.api.Type
import org.dimdev.dimdoors.SingletonInstance

object EmptyCloudData : CloudData, SingletonInstance<EmptyCloudData>() {
    override val type: Holder<out Type<CloudData>> get() = CloudDatum.EMPTY
}
