package org.dimdev.dimdoors.world.decay.results;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.Decay;
import org.dimdev.dimdoors.world.decay.DecayInventoryHelper;

import java.util.List;

public class FluidDecayResult implements DecayResult {
    public static final MapCodec<FluidDecayResult> CODEC = RecordCodecBuilder.mapCodec(instance -> DecayResult.entropyCodec(instance).and(BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(blockDecayResult -> blockDecayResult.fluid)).apply(instance, FluidDecayResult::new));

    public static final String KEY = "fluid";
    private final float worldThreadChance;

    protected Fluid fluid;

    protected int entropy;

    public FluidDecayResult(int entropy, float worldThreadChance, Fluid fluid) {
    this.entropy = entropy;
    this.worldThreadChance = worldThreadChance;
    this.fluid = fluid;
    }

    @Override
    public DecayResultType<FluidDecayResult> getType() {
    return DecayResultType.FLUID_RESULT_TYPE;
    }

    @Override
    public int entropy() {
    return entropy;
    }

    @Override
    public float worldThreadChance() {
    return worldThreadChance;
    }

    @Override
    public int process(Decay.DecayContext context) {
        List<ItemStack> contents = DecayInventoryHelper.takeContents(context.world(), context.targetBlockPos());
    BlockState newState = fluid.defaultFluidState().createLegacyBlock();
    context.world().setBlockAndUpdate(context.targetBlockPos(), newState);
        DecayInventoryHelper.transferOrDrop(context.world(), context.targetBlockPos(), contents);
    return entropy;
    }

    @Override
    public List<Result> produces() {
        return List.of(new Result(fluid, 1));
    }

    private static <T extends Comparable<T>> FluidState transferProperty(FluidState from, FluidState to, Property<T> property) {
    return to.setValue(property, from.getValue(property));
    }
}
