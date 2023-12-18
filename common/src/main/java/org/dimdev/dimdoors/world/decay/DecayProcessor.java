package org.dimdev.dimdoors.world.decay;

import dev.architectury.fluid.FluidStack;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.DimensionalDoors;

public interface DecayProcessor<S, T> {
    Registrar<DecayProcessorType<? extends DecayProcessor<?, ?>>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<DecayProcessorType<? extends DecayProcessor<?, ?>>>builder(DimensionalDoors.id("decay_processor_type")).build();

    static DecayProcessor<?, ?> deserialize(CompoundTag nbt) {
        ResourceLocation id = ResourceLocation.tryParse(nbt.getString("type"));
        return REGISTRY.delegate(id).orElseGet(DecayProcessorType.NONE_PROCESSOR_TYPE).fromNbt(nbt);
    }

    static CompoundTag serialize(DecayProcessor<?, ?> modifier) {
        return modifier.toNbt(new CompoundTag());
    }


    DecayProcessor<S, T> fromNbt(CompoundTag nbt);

    default CompoundTag toNbt(CompoundTag nbt) {
        return this.getType().toNbt(nbt);
    }

    DecayProcessorType<? extends DecayProcessor<S, T>> getType();

    String getKey();

    int process(Level world, BlockPos pos, BlockState origin, BlockState targetState, FluidState targetFluid);

    default Object produces(Object prior) {
        return defaultProduces(prior);
    }

    static Object defaultProduces(Object object) {
        if(object instanceof Fluid fluid) return FluidStack.create(fluid, FluidStack.bucketAmount());
        else if(object instanceof Block block) return new ItemStack(block);
        else return null;
    }
}
