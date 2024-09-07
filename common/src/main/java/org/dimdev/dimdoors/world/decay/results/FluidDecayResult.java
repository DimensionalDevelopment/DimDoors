package org.dimdev.dimdoors.world.decay.results;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.DecayResult;
import org.dimdev.dimdoors.world.decay.DecayResultType;
import org.dimdev.dimdoors.world.decay.DecaySource;

public class FluidDecayResult implements DecayResult {
	public static final Codec<FluidDecayResult> CODEC = RecordCodecBuilder.create(instance -> DecayResult.entropyCodec(instance).and(Registry.FLUID.byNameCodec().fieldOf("fluid").forGetter(blockDecayResult -> blockDecayResult.fluid)).apply(instance, FluidDecayResult::new));

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
		return DecayResultType.FLUID_RESULT_TYPE.get();
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
	public int process(Level world, BlockPos pos, BlockState origin, BlockState target, FluidState targetFluid, DecaySource source) {
		BlockState newState = fluid.defaultFluidState().createLegacyBlock();
		world.setBlockAndUpdate(pos, newState);
		return entropy;
	}

	@Override
	public Object produces(Object prior) {
		return FluidStack.create(fluid, 1000);
	}

	private static <T extends Comparable<T>> FluidState transferProperty(FluidState from, FluidState to, Property<T> property) {
		return to.setValue(property, from.getValue(property));
	}
}