package org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud

import org.dimdev.dimdoors.SingletonInstance

object EmptyCloudData : CloudData, SingletonInstance<EmptyCloudData>() {
    override val type get() = CloudDatum.EMPTY
}
