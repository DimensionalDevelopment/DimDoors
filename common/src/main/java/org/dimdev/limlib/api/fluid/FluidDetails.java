package org.dimdev.limlib.api.fluid;

import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.fluid.ModFluids;

public record FluidDetails(ResourceLocation still, ResourceLocation flowing, ResourceLocation overlay) {
    public static FluidDetails of(ResourceLocation id) {
        return new FluidDetails(
                ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + "_still"),
                ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + "_flow"),
                ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + "_flow")
        );
    }
}
