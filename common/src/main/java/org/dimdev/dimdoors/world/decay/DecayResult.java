package org.dimdev.dimdoors.world.decay;

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
import org.dimdev.dimdoors.api.util.LocationValue;

public interface DecayResult {
    public static <T extends DecayResult> Products.P2<RecordCodecBuilder.Mu<T>, Integer, LocationValue> entropyCodec(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(Codec.INT.optionalFieldOf("entropy", 0).forGetter(DecayResult::entropy), LocationValue.CODEC.optionalFieldOf("world_thread_chance", LocationValue.Constant.ZERO).forGetter(DecayResult::worldThreadChance));
    }

    Codec<DecayResult> CODEC = DecayResultType.CODEC.dispatch("type", DecayResult::getType, DecayResultType::codec);


    default int entropy() {
        return 0;
    }

    default LocationValue worldThreadChance() {
        return LocationValue.Constant.ZERO;
    }

    DecayResultType<? extends DecayResult> getType();

    String getKey();

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
