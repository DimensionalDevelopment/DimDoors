package org.dimdev.dimcore.api.transfer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public record FluidUnit(Fluid fluid, DataComponentPatch components, long amount) implements Unit<FluidUnit> {
    public static final Codec<FluidUnit> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidUnit::fluid),
            DataComponentPatch.CODEC.fieldOf("components").forGetter(FluidUnit::components),
            Codec.LONG.fieldOf("amount").forGetter(FluidUnit::amount)
    ).apply(instance, FluidUnit::new));

    private static final FluidUnit EMPTY = new FluidUnit(Fluids.EMPTY, 0);

    public FluidUnit(Fluid fluid, long amount) {
        this(fluid, DataComponentPatch.EMPTY, amount);
    }

    @Override
    public FluidUnit withAmount(long amount) {
        return new FluidUnit(fluid, components, amount);
    }

    @Override
    public boolean sameResource(FluidUnit other) {
        return fluid == other.fluid && components.equals(other.components);
    }

    public static FluidUnit empty() {
        return EMPTY;
    }
}
