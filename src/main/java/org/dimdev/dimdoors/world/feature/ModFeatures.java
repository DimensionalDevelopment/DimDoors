package org.dimdev.dimdoors.world.feature;

import org.dimdev.dimdoors.world.ModBiomes;
import org.dimdev.dimdoors.world.feature.gateway.LimboGatewayFeature;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SandstonePillarsV2Gateway;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicV2Gateway;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicV2GatewayFeature;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicV2GatewayFeatureConfig;
import org.dimdev.dimdoors.world.feature.gateway.schematic.TwoPillarsV2Gateway;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;

@SuppressWarnings("deprecation")
public final class ModFeatures {
    public static final Feature<SchematicV2GatewayFeatureConfig> SCHEMATIC_GATEWAY_FEATURE = Registry.register(Registry.FEATURE, new Identifier("dimdoors", "schematic_gateway"), new SchematicV2GatewayFeature(SchematicV2GatewayFeatureConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> LIMBO_GATEWAY_FEATURE = Registry.register(Registry.FEATURE, new Identifier("dimdoors", "limbo_gateway"), new LimboGatewayFeature());
    public static final SchematicV2Gateway SANDSTONE_PILLARS_GATEWAY = new SandstonePillarsV2Gateway();
    public static final SchematicV2Gateway TWO_PILLARS_GATEWAY = new TwoPillarsV2Gateway();
    public static final RegistryKey<ConfiguredFeature<?, ?>> SANDSTONE_PILLARS = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, new Identifier("dimdoors", "sandstone_pillars"));
    public static final RegistryKey<ConfiguredFeature<?, ?>> TWO_PILLARS = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, new Identifier("dimdoors", "two_pillars"));
    public static final RegistryKey<ConfiguredFeature<?, ?>> LIMBO_GATEWAY = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, new Identifier("dimdoors", "limbo_gateway"));

    public static void init() {
        SANDSTONE_PILLARS_GATEWAY.init();
        TWO_PILLARS_GATEWAY.init();
        BiomeModifications.addFeature(ctx -> {
            Biome biome = ctx.getBiome();
            return biome.getCategory() != Biome.Category.NONE &&
                    biome.getCategory() != Biome.Category.THEEND &&
                    biome.getCategory() != Biome.Category.NETHER &&
                    biome.getCategory() != Biome.Category.OCEAN &&
                    biome.getCategory() != Biome.Category.MUSHROOM;
        }, GenerationStep.Feature.SURFACE_STRUCTURES, TWO_PILLARS);
        BiomeModifications.addFeature(ctx -> ctx.getBiome().getCategory() == Biome.Category.DESERT, GenerationStep.Feature.SURFACE_STRUCTURES, SANDSTONE_PILLARS);
        BiomeModifications.addFeature(ctx -> ctx.getBiomeKey().getValue().equals(ModBiomes.LIMBO_KEY.getValue()), GenerationStep.Feature.SURFACE_STRUCTURES, LIMBO_GATEWAY);
    }
}
