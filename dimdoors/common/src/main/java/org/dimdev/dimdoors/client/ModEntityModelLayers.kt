package org.dimdev.dimdoors.client

import net.minecraft.client.model.geom.ModelLayerLocation
import org.dimdev.dimdoors.DimensionalDoors

object ModEntityModelLayers {
    @JvmField
    var MONOLITH: ModelLayerLocation = ModelLayerLocation(DimensionalDoors.id("monolith"), "body")
}
