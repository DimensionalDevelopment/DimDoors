package org.dimdev.dimdoors.fluid;

import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.core.fluid.SimpleArchitecturyFluidAttributes;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

import java.util.Optional;
import java.util.function.Supplier;

//TODO: Figure how the <meep> I'm gonna decouple this from architectury
public class ModFluids {

    public static final ArchitecturyFluidAttributes ETERNAL_FLUID_ATTRIBUTES = SimpleArchitecturyFluidAttributes.of(() -> ModFluids.FLOWING_ETERNAL_FLUID, () -> ModFluids.ETERNAL_FLUID).block(() -> Optional.of(ModBlocks.ETERNAL_FLUID)).bucketItem(() -> Optional.of(ModItems.ETERNAL_FLUID_BUCKET)).explosionResistance(100000)
        .sourceTexture(DimensionalDoors.id("block/eternal_fluid_still"))
        .flowingTexture(DimensionalDoors.id("block/eternal_fluid_flow"))
        .overlayTexture(DimensionalDoors.id("block/eternal_fluid_flow"));
    public static final Fluid FLOWING_ETERNAL_FLUID = register("flowing_eternal_fluid", new ArchitecturyFlowingFluid.Flowing(ModFluids.ETERNAL_FLUID_ATTRIBUTES));
    public static final FlowingFluid ETERNAL_FLUID = register("eternal_fluid", new ArchitecturyFlowingFluid.Source(ModFluids.ETERNAL_FLUID_ATTRIBUTES));

    public static final ArchitecturyFluidAttributes LEAK_ATTRIBUTES = SimpleArchitecturyFluidAttributes.of(() -> ModFluids.FLOWING_LEAK, () -> ModFluids.LEAK).block(() -> Optional.of(ModBlocks.LEAK)).bucketItem(() -> Optional.of(ModItems.LEAK_BUCKET))
        .sourceTexture(DimensionalDoors.id("block/leak_still"))
        .flowingTexture(DimensionalDoors.id("block/leak_flow"))
        .overlayTexture(DimensionalDoors.id("block/leak_flow"));
    public static final Fluid FLOWING_LEAK = register("flowing_leak_fluid", new ArchitecturyFlowingFluid.Flowing(ModFluids.LEAK_ATTRIBUTES));
    public static final FlowingFluid LEAK = register("leak", new ArchitecturyFlowingFluid.Source(ModFluids.LEAK_ATTRIBUTES));

    private static <T extends Fluid> T register(String string, T fluid) {
    return DimensionalDoors.getSided().registerFluid(string, fluid);
    }

    public static void init() {
    }

}
