package org.dimdev.dimdoors.world.decay.results;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;

public record DecayResultType<T extends DecayResult>(MapCodec<T> codec) {
    public static final ResourceKey<Registry<DecayResultType<? extends DecayResult>>> KEY = ResourceKey.createRegistryKey(DimensionalDoors.id("decay_result_type"));
    public static final Registry<DecayResultType<? extends DecayResult>> REGISTRY = DimensionalDoors.getSided().createRegistry(KEY);


    public static final Codec<DecayResultType<? extends DecayResult>> CODEC = REGISTRY.byNameCodec();

    public static final DecayResultType<SingleBlockDecayResult> BLOCK_RESULT_TYPE = register(DimensionalDoors.id(SingleBlockDecayResult.KEY), SingleBlockDecayResult.CODEC);
    public static final DecayResultType<NoneDecayResult> NONE_PROCESSOR_TYPE = register(DimensionalDoors.id(NoneDecayResult.KEY), MapCodec.unit(NoneDecayResult::instance));
    public static final DecayResultType<SelfDecayResult> SELF_RESULT_TYPE = register(DimensionalDoors.id(SelfDecayResult.KEY), MapCodec.unit(SelfDecayResult::instance));
    public static final DecayResultType<DoubleBlockDecayResult> DOUBLE_BLOCK_RESULT_TYPE = register(DimensionalDoors.id(DoubleBlockDecayResult.KEY), DoubleBlockDecayResult.CODEC);
    public static final DecayResultType<FluidDecayResult> FLUID_RESULT_TYPE = register(DimensionalDoors.id(FluidDecayResult.KEY), FluidDecayResult.CODEC);

    public static void register() {
    }

    static <T, U extends DecayResult> DecayResultType<U> register(ResourceLocation id, MapCodec<U> codec) {
        return DimensionalDoors.getSided().register(KEY, id, new DecayResultType<>(codec));
    }
}
