package org.dimdev.dimcore.api.fluid

import net.minecraft.resources.ResourceLocation

data class FluidDetails(val still: ResourceLocation, val flowing: ResourceLocation, val overlay: ResourceLocation) {
    companion object {
        fun of(id: ResourceLocation): FluidDetails {
            return FluidDetails(
                ResourceLocation.fromNamespaceAndPath(id.namespace, "block/${id.path}_still"),
                ResourceLocation.fromNamespaceAndPath(id.namespace, "block/${id.path}_flow"),
                ResourceLocation.fromNamespaceAndPath(id.namespace, "block/${id.path}_flow")
            )
        }
    }
}
