package org.dimdev.dimdoors.world.decay;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.decay.results.*;

public record DecayResultType<T extends DecayResult>(MapCodec<T> codec) {
    public static final Registrar<DecayResultType<? extends DecayResult>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<DecayResultType<? extends DecayResult>>builder(DimensionalDoors.id("decay_result_type")).build();


    public static final Codec<DecayResultType<? extends DecayResult>> CODEC = ResourceLocation.CODEC.xmap(REGISTRY::get, REGISTRY::getId);

    public static final RegistrySupplier<DecayResultType<BlockDecayImplResult>> BLOCK_RESULT_TYPE = register(DimensionalDoors.id(BlockDecayImplResult.KEY), BlockDecayImplResult.CODEC);
    public static final RegistrySupplier<DecayResultType<NoneDecayResult>> NONE_PROCESSOR_TYPE = register(DimensionalDoors.id(NoneDecayResult.KEY), MapCodec.unit(NoneDecayResult::instance));
    public static final RegistrySupplier<DecayResultType<SelfDecayResult>> SELF_RESULT_TYPE = register(DimensionalDoors.id(SelfDecayResult.KEY), MapCodec.unit(SelfDecayResult::instance));
    public static final RegistrySupplier<DecayResultType<DoubleBlockDecayResult>> DOUBLE_BLOCK_RESULT_TYPE = register(DimensionalDoors.id(DoubleBlockDecayResult.KEY), DoubleBlockDecayResult.CODEC);
    public static final RegistrySupplier<DecayResultType<FluidDecayResult>> FLUID_RESULT_TYPE = register(DimensionalDoors.id(FluidDecayResult.KEY), FluidDecayResult.CODEC);

    public static void register() {
    }

    static <T, V, U extends DecayResult> RegistrySupplier<DecayResultType<U>> register(ResourceLocation id, MapCodec<U> codec) {
        return REGISTRY.register(id, () -> new DecayResultType<>(codec));
    }
}
