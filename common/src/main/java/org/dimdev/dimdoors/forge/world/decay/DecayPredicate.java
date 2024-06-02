package org.dimdev.dimdoors.forge.world.decay;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.Registries;
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
import org.dimdev.dimdoors.forge.world.decay.predicates.FluidDecayPredicate;
import org.dimdev.dimdoors.forge.world.decay.predicates.SimpleDecayPredicate;

import java.util.Set;
import java.util.function.Supplier;

public interface DecayPredicate {
    Registrar<DecayPredicateType<? extends DecayPredicate>> REGISTRY = Registries.get(DimensionalDoors.MOD_ID).<DecayPredicateType<? extends DecayPredicate>>builder(DimensionalDoors.id("decay_predicate_type")).build();

    DecayPredicate NONE = new DecayPredicate() {
        private static final String ID = "none";

        @Override
        public DecayPredicate fromNbt(CompoundTag nbt) {
            return this;
        }

        @Override
        public DecayPredicateType<? extends DecayPredicate> getType() {
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

    static DecayPredicate deserialize(CompoundTag nbt) {
        ResourceLocation id = ResourceLocation.tryParse(nbt.getString("type"));
        return REGISTRY.delegate(id).orElseGet(DecayPredicateType.NONE_PREDICATE_TYPE).fromNbt(nbt);
    }

    static CompoundTag serialize(DecayPredicate modifier) {
        return modifier.toNbt(new CompoundTag());
    }


    DecayPredicate fromNbt(CompoundTag nbt);

    default CompoundTag toNbt(CompoundTag nbt) {
        return this.getType().toNbt(nbt);
    }

    DecayPredicateType<? extends DecayPredicate> getType();

    String getKey();

    boolean test(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid);

	default Set<Fluid> constructApplicableFluids() {
		return Set.of();
	}

	default Set<Block> constructApplicableBlocks() {
		return Set.of();
	}

    interface DecayPredicateType<T extends DecayPredicate> {
        RegistrySupplier<DecayPredicateType<DecayPredicate>> NONE_PREDICATE_TYPE = register(DimensionalDoors.id("none"), () -> NONE);
        RegistrySupplier<DecayPredicateType<DecayPredicate>> SIMPLE_PREDICATE_TYPE = register(DimensionalDoors.id(SimpleDecayPredicate.KEY), SimpleDecayPredicate::new);
		RegistrySupplier<DecayPredicateType<DecayPredicate>> FLUID_PREDICATE_TYPE = register(DimensionalDoors.id(FluidDecayPredicate.KEY), FluidDecayPredicate::new);

		DecayPredicate fromNbt(CompoundTag nbt);

        CompoundTag toNbt(CompoundTag nbt);

        static void register() {
        }

        static <U extends DecayPredicate> RegistrySupplier<DecayPredicateType<U>> register(ResourceLocation id, Supplier<U> factory) {
            return REGISTRY.register(id, () -> new DecayPredicateType<U>() {
                @Override
                public DecayPredicate fromNbt(CompoundTag nbt) {
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
