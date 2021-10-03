package org.dimdev.dimdoors.world.decay;

import java.util.function.Supplier;

import com.google.gson.JsonObject;
import com.mojang.serialization.Lifecycle;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.modifier.AbsoluteRiftBlockEntityModifier;
import org.dimdev.dimdoors.pockets.modifier.DimensionalDoorModifier;
import org.dimdev.dimdoors.pockets.modifier.OffsetModifier;
import org.dimdev.dimdoors.pockets.modifier.PocketEntranceModifier;
import org.dimdev.dimdoors.pockets.modifier.RelativeReferenceModifier;
import org.dimdev.dimdoors.pockets.modifier.RiftDataModifier;
import org.dimdev.dimdoors.pockets.modifier.RiftManager;
import org.dimdev.dimdoors.pockets.modifier.ShellModifier;
import org.dimdev.dimdoors.world.decay.processors.SimpleDecayProcesor;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

public interface DecayProcessor {
    Registry<DecayProcessor.DecayProcessorType<? extends DecayProcessor>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<DecayProcessor.DecayProcessorType<? extends DecayProcessor>>(RegistryKey.ofRegistry(new Identifier("dimdoors", "decay_processor_type")), Lifecycle.stable())).buildAndRegister();

    DecayProcessor DUMMY = new DecayProcessor() {
        @Override
        public DecayProcessor fromJson(JsonObject json) {
            return this;
        }

        @Override
        public DecayProcessorType<? extends DecayProcessor> getType() {
            return DecayProcessor.DecayProcessorType.NONE_PROCESSOR_TYPE;
        }

        @Override
        public String getKey() {
            return ID;
        }

        @Override
        public int process(World world, BlockPos pos) {
            return 0;
        }

        private static final String ID = "none";
    };

    static DecayProcessor deserialize(JsonObject nbt) {
        Identifier id = Identifier.tryParse(nbt.get("type").getAsString());
        return REGISTRY.getOrEmpty(id).orElse(DecayProcessorType.NONE_PROCESSOR_TYPE).fromJson(nbt);
    }

    static JsonObject serialize(DecayProcessor modifier) {
        return modifier.toJson(new JsonObject());
    }


    DecayProcessor fromJson(JsonObject json);

    default JsonObject toJson(JsonObject json) {
        return this.getType().toJson(json);
    }

    DecayProcessor.DecayProcessorType<? extends DecayProcessor> getType();

    String getKey();

    int process(World world, BlockPos pos);

    interface DecayProcessorType<T extends DecayProcessor> {
        DecayProcessorType<SimpleDecayProcesor> SIMPLE_PROCESSOR_TYPE = register(new Identifier("dimdoors", SimpleDecayProcesor.KEY), SimpleDecayProcesor::new);
        DecayProcessorType<DecayProcessor> NONE_PROCESSOR_TYPE = register(new Identifier("dimdoors", "none"), () -> DUMMY);

        DecayProcessor fromJson(JsonObject nbt);

        JsonObject toJson(JsonObject nbt);

        static void register() {
            DimensionalDoorsInitializer.apiSubscribers.forEach(d -> d.registerDecayProcessors(REGISTRY));
        }

        static <U extends DecayProcessor> DecayProcessorType<U> register(Identifier id, Supplier<U> factory) {
            return Registry.register(REGISTRY, id, new DecayProcessorType<U>() {
                @Override
                public DecayProcessor fromJson(JsonObject json) {
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