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

public interface DecayResult {
    Registrar<DecayResultType<? extends DecayResult>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<DecayResultType<? extends DecayResult>>builder(DimensionalDoors.id("decay_processor_type")).build();

    static DecayResult deserialize(CompoundTag nbt) {
        ResourceLocation id = ResourceLocation.tryParse(nbt.getString("type"));
        return REGISTRY.delegate(id).orElseGet(DecayResultType.NONE_PROCESSOR_TYPE).fromNbt(nbt);
    }

    static CompoundTag serialize(DecayResult modifier) {
        return modifier.toNbt(new CompoundTag());
    }


    DecayResult fromNbt(CompoundTag nbt);

    default CompoundTag toNbt(CompoundTag nbt) {
        return this.getType().toNbt(nbt);
    }

    DecayResultType<? extends DecayResult> getType();

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
