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

import java.util.function.Supplier;

public class ModFluids {
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.FLUID);

	public static final ArchitecturyFluidAttributes ETERNAL_FLUID_ATTRIBUTES = SimpleArchitecturyFluidAttributes
			.ofSupplier(() -> ModFluids.FLOWING_ETERNAL_FLUID, () -> ModFluids.ETERNAL_FLUID).block(ModBlocks.ETERNAL_FLUID)
			.bucketItem(ModItems.ETERNAL_FLUID_BUCKET).explosionResistance(100000)
			.sourceTexture(DimensionalDoors.id("block/eternal_fluid_still"))
			.flowingTexture(DimensionalDoors.id("block/eternal_fluid_flow"));
	public static final RegistrySupplier<? extends  Fluid> FLOWING_ETERNAL_FLUID = register("flowing_eternal_fluid", () -> new ArchitecturyFlowingFluid.Flowing(ModFluids.ETERNAL_FLUID_ATTRIBUTES));
	public static final RegistrySupplier<? extends FlowingFluid> ETERNAL_FLUID = register("eternal_fluid", () -> new ArchitecturyFlowingFluid.Source(ModFluids.ETERNAL_FLUID_ATTRIBUTES));

	public static final ArchitecturyFluidAttributes LEAK_ATTRIBUTES = SimpleArchitecturyFluidAttributes.ofSupplier(() -> ModFluids.FLOWING_LEAK, () -> ModFluids.LEAK).block(ModBlocks.LEAK).bucketItem(ModItems.ETERNAL_FLUID_BUCKET).convertToSource(true)
			.sourceTexture(DimensionalDoors.id("block/leak_still"))
			.flowingTexture(DimensionalDoors.id("block/leak_flow"));

	public static final RegistrySupplier<? extends  Fluid> FLOWING_LEAK = register("flowing_leak", () -> new ArchitecturyFlowingFluid.Flowing(ModFluids.LEAK_ATTRIBUTES));
	public static final RegistrySupplier<? extends FlowingFluid> LEAK = register("leak", () -> new ArchitecturyFlowingFluid.Source(ModFluids.LEAK_ATTRIBUTES));


	private static <T extends Fluid> RegistrySupplier<? extends T> register(String string, Supplier<T> fluid) {
		return FLUIDS.register(DimensionalDoors.id(string), fluid);
	}

	public static void init() {
		FLUIDS.register();
	}

}
