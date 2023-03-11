package org.dimdev.dimdoors.world.decay;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.decay.predicates.SimpleDecayPredicate;

import java.util.Set;
import java.util.function.Supplier;

public interface DecayPredicate {
    Registry<DecayPredicateType<? extends DecayPredicate>> REGISTRY = FabricRegistryBuilder.from(new MappedRegistry<DecayPredicateType<? extends DecayPredicate>>(ResourceKey.createRegistryKey(DimensionalDoors.id("decay_predicate_type")), Lifecycle.stable(), false)).buildAndRegister();

    DecayPredicate NONE = new DecayPredicate() {
        private static final String ID = "none";

        @Override
        public DecayPredicate fromNbt(CompoundTag nbt) {
            return this;
        }

        @Override
        public DecayPredicateType<? extends DecayPredicate> getType() {
            return DecayPredicateType.NONE_PREDICATE_TYPE;
        }

        @Override
        public String getKey() {
            return ID;
        }

        @Override
        public boolean test(Level world, BlockPos pos, BlockState origin, BlockState target) {
            return false;
        }

		@Override
		public Set<Block> constructApplicableBlocks() {
			return Set.of();
		}
	};

    static DecayPredicate deserialize(CompoundTag nbt) {
        ResourceLocation id = ResourceLocation.tryParse(nbt.getString("type"));
        return REGISTRY.getOptional(id).orElse(DecayPredicateType.NONE_PREDICATE_TYPE).fromNbt(nbt);
    }

    static CompoundTag serialize(DecayPredicate modifier) {
        return modifier.toNbt(new CompoundTag());
    }


    DecayPredicate fromNbt(CompoundTag nbt);

    default CompoundTag toNbt(CompoundTag nbt) {
        return this.getType().toNbt(nbt);
    }

    DecayPredicate.DecayPredicateType<? extends DecayPredicate> getType();

    String getKey();

    boolean test(Level world, BlockPos pos, BlockState origin, BlockState target);

    Set<Block> constructApplicableBlocks();

    interface DecayPredicateType<T extends DecayPredicate> {
        DecayPredicateType<DecayPredicate> NONE_PREDICATE_TYPE = register(DimensionalDoors.id("none"), () -> NONE);
        DecayPredicateType<SimpleDecayPredicate> SIMPLE_PREDICATE_TYPE = register(DimensionalDoors.id(SimpleDecayPredicate.KEY), SimpleDecayPredicate::new);

        DecayPredicate fromNbt(CompoundTag nbt);

		CompoundTag toNbt(CompoundTag nbt);

        static void register() {
            DimensionalDoors.apiSubscribers.forEach(d -> d.registerDecayPredicates(REGISTRY));
        }

        static <U extends DecayPredicate> DecayPredicateType<U> register(ResourceLocation id, Supplier<U> factory) {
            return Registry.register(REGISTRY, id, new DecayPredicateType<U>() {
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
