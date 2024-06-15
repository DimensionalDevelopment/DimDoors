package org.dimdev.dimdoors.world.decay;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.decay.conditions.FluidDecayCondition;
import org.dimdev.dimdoors.world.decay.conditions.SimpleDecayCondition;

import java.util.Set;
import java.util.function.Supplier;

public interface DecayCondition {
    Registrar<DecayPredicateType<? extends DecayCondition>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<DecayPredicateType<? extends DecayCondition>>builder(DimensionalDoors.id("decay_predicate_type")).build();

    DecayCondition NONE = new DecayCondition() {
        private static final String ID = "none";

        @Override
        public DecayCondition fromNbt(CompoundTag nbt) {
            return this;
        }

        @Override
        public DecayPredicateType<? extends DecayCondition> getType() {
            return DecayPredicateType.NONE_PREDICATE_TYPE.get();
        }

        @Override
        public String getKey() {
            return ID;
        }

        @Override
        public boolean test(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid) {
            return false;
        }
	};

    static DecayCondition deserialize(CompoundTag nbt) {
        ResourceLocation id = ResourceLocation.tryParse(nbt.getString("type"));
        return REGISTRY.delegate(id).orElseGet(DecayPredicateType.NONE_PREDICATE_TYPE).fromNbt(nbt);
    }

    static CompoundTag serialize(DecayCondition modifier) {
        return modifier.toNbt(new CompoundTag());
    }


    DecayCondition fromNbt(CompoundTag nbt);

    default CompoundTag toNbt(CompoundTag nbt) {
        return this.getType().toNbt(nbt);
    }

    DecayPredicateType<? extends DecayCondition> getType();

    String getKey();

    boolean test(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid);

	default Set<Fluid> constructApplicableFluids() {
		return Set.of();
	}

	default Set<Block> constructApplicableBlocks() {
		return Set.of();
	}

    interface DecayPredicateType<T extends DecayCondition> {
        RegistrySupplier<DecayPredicateType<DecayCondition>> NONE_PREDICATE_TYPE = register(DimensionalDoors.id("none"), () -> NONE);
        RegistrySupplier<DecayPredicateType<DecayCondition>> SIMPLE_PREDICATE_TYPE = register(DimensionalDoors.id(SimpleDecayCondition.KEY), SimpleDecayCondition::new);
		RegistrySupplier<DecayPredicateType<DecayCondition>> FLUID_PREDICATE_TYPE = register(DimensionalDoors.id(FluidDecayCondition.KEY), FluidDecayCondition::new);

		DecayCondition fromNbt(CompoundTag nbt);

        CompoundTag toNbt(CompoundTag nbt);

        static void register() {
        }

        static <U extends DecayCondition> RegistrySupplier<DecayPredicateType<U>> register(ResourceLocation id, Supplier<U> factory) {
            return REGISTRY.register(id, () -> new DecayPredicateType<U>() {
                @Override
                public DecayCondition fromNbt(CompoundTag nbt) {
                    return factory.get().fromNbt(nbt);
                }

                @Override
                public CompoundTag toNbt(CompoundTag nbt) {
					nbt.putString("type", id.toString());
                    return nbt;
                }
            });
        }
    }
}
