package org.dimdev.dimdoors.world.decay;

import java.util.function.Supplier;

import com.google.gson.JsonObject;
import com.mojang.serialization.Lifecycle;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.world.decay.predicates.SimpleDecayPredicate;
import org.dimdev.dimdoors.world.decay.processors.SimpleDecayProcesor;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

public interface DecayPredicate {
    Registry<DecayPredicateType<? extends DecayPredicate>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<DecayPredicateType<? extends DecayPredicate>>(RegistryKey.ofRegistry(new Identifier("dimdoors", "decay_predicate_type")), Lifecycle.stable())).buildAndRegister();

    DecayPredicate DUMMY = new DecayPredicate() {
        private static final String ID = "none";

        @Override
        public DecayPredicate fromJson(JsonObject json) {
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
        public boolean test(World world, BlockPos pos) {
            return false;
        }
    };

    static DecayPredicate deserialize(JsonObject nbt) {
        Identifier id = Identifier.tryParse(nbt.get("type").getAsString());
        return REGISTRY.getOrEmpty(id).orElse(DecayPredicateType.NONE_PREDICATE_TYPE).fromJson(nbt);
    }

    static JsonObject serialize(DecayPredicate modifier) {
        return modifier.toJson(new JsonObject());
    }


    DecayPredicate fromJson(JsonObject json);

    default JsonObject toJson(JsonObject json) {
        return this.getType().toJson(json);
    }

    DecayPredicate.DecayPredicateType<? extends DecayPredicate> getType();

    String getKey();

    boolean test(World world, BlockPos pos);

    interface DecayPredicateType<T extends DecayPredicate> {
        DecayPredicateType<DecayPredicate> NONE_PREDICATE_TYPE = register(new Identifier("dimdoors", "none"), () -> DUMMY);
        DecayPredicateType<SimpleDecayPredicate> SIMPLE_PREDICATE_TYPE = register(new Identifier("dimdoors", SimpleDecayProcesor.KEY), SimpleDecayPredicate::new);

        DecayPredicate fromJson(JsonObject nbt);

        JsonObject toJson(JsonObject nbt);

        static void register() {
            DimensionalDoorsInitializer.apiSubscribers.forEach(d -> d.registerDecayPredicates(REGISTRY));
        }

        static <U extends DecayPredicate> DecayPredicateType<U> register(Identifier id, Supplier<U> factory) {
            return Registry.register(REGISTRY, id, new DecayPredicateType<U>() {
                @Override
                public DecayPredicate fromJson(JsonObject json) {
                    return factory.get().fromJson(json);
                }

                @Override
                public JsonObject toJson(JsonObject json) {
                    json.addProperty("type", id.toString());
                    return json;
                }
            });
        }
    }
}