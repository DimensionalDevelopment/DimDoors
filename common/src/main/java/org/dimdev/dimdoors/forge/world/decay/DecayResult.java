package org.dimdev.dimdoors.forge.world.decay;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public interface DecayResult {
    public static <T extends DecayResult> Products.P2<RecordCodecBuilder.Mu<T>, Integer, Float> entropyCodec(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                Codec.INT.optionalFieldOf("entropy", 0).forGetter(DecayResult::entropy),
                Codec.FLOAT.optionalFieldOf("world_thread_chance", 0.1f).forGetter(DecayResult::worldThreadChance));
    }

    Codec<DecayResult> CODEC = org.dimdev.dimdoors.world.decay.DecayResultType.CODEC.dispatch("type", DecayResult::getType, DecayResultType::codec);


    default int entropy() {
        return 0;
    }

    default float worldThreadChance() {
        return 0f;
    }

    DecayResultType<? extends DecayResult> getType();

    int process(Level world, BlockPos pos, BlockState origin, BlockState targetState, FluidState targetFluid, DecaySource source);

    default Object produces(Object prior) {
        return defaultProduces(prior);
    }

    static Object defaultProduces(Object object) {
        if(object instanceof Fluid fluid) return FluidStack.create(fluid, FluidStack.bucketAmount());
        else if(object instanceof Block block) return new ItemStack(block);
        else return null;
    }
}
