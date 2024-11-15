package org.dimdev.dimdoors.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModBiomeModifiers {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIERS =  DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, DimensionalDoors.MOD_ID);

    public static final DeferredHolder<Codec<? extends BiomeModifier>, Codec<AddFeaturesBiomeModifier>> ADD_FEATURES_BIOME_MODIFIER_TYPE = BIOME_MODIFIERS.register("add_features", () ->
            RecordCodecBuilder.create(builder -> builder.group(
                    Biome.LIST_CODEC.fieldOf("whiteList").forGetter(AddFeaturesBiomeModifier::whiteList),
                    Biome.LIST_CODEC.fieldOf("blackList").forGetter(AddFeaturesBiomeModifier::blackList),
                    PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(AddFeaturesBiomeModifier::features),
                    GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(AddFeaturesBiomeModifier::step)
            ).apply(builder, AddFeaturesBiomeModifier::new))
    );
    public static void init() {
        BIOME_MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
