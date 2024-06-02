package org.dimdev.dimdoors.forge.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.AddFeaturesBiomeModifier;

public class ModBiomeModifiers {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIERS =  DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, DimensionalDoors.MOD_ID);

    public static final RegistryObject<Codec<AddFeaturesBiomeModifier>> ADD_FEATURES_BIOME_MODIFIER_TYPE = BIOME_MODIFIERS.register("add_features", () ->
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
