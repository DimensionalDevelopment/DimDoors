package org.dimdev.dimdoors.fluid;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModFluids {
    public record FluidDetails(Identifier still, Identifier flowing, Identifier overlay) {
        public static FluidDetails of(Identifier id) {
            return new FluidDetails(
                    Identifier.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + "_still"),
                    Identifier.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + "_flow"),
                    Identifier.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + "_flow")
            );
        }
    }

    public static FluidDetails ETERNAL_FLUID_DETAILS = FluidDetails.of(DimensionalDoors.id("eternal_fluid"));

    public static final Fluid FLOWING_ETERNAL_FLUID = register("flowing_eternal_fluid", DimensionalDoors.getSided().createFlowingEternalFluid());
    public static final FlowingFluid ETERNAL_FLUID = register("eternal_fluid", DimensionalDoors.getSided().createEternalFluid());

    public static FluidDetails LEAK_DETAILS = FluidDetails.of(DimensionalDoors.id("leak"));
    public static final Fluid FLOWING_LEAK = register("flowing_leak_fluid", DimensionalDoors.getSided().createFlowingLeakFluid()); //TODO: Figure how in the future migrate without deleting existing flowing leak.
    public static final FlowingFluid LEAK = register("leak", DimensionalDoors.getSided().createLeakFluid());

    private static <T extends Fluid> T register(String string, T fluid) {
        return DimensionalDoors.getSided().registerFluid(string, fluid);
    }

    public static void init() {
    }
}
