package org.dimdev.dimdoors.pockets.addon.environment.cloud

import org.dimdev.dimdoors.util.codec.SingletonInstance

object EmptyCloudData : CloudData, SingletonInstance<EmptyCloudData>() {
    override val type get() = CloudDatum.EMPTY
}
