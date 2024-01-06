package org.dimdev.dimdoors.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.EntityType;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DimensionalDoorsClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DimensionalDoorsClient.init();
        ModelLoadingPlugin.register(new DimensionalDoorsModelLoadingPlugin());

        DimensionRenderering.initClient();
        DimensionalDoorsClient.initParticles(
                (particleType, particleProvider) -> ParticleFactoryRegistry.getInstance().register((ParticleType) particleType, (ParticleProvider) particleProvider),
                (particleType, spriteSetFunction) -> ParticleFactoryRegistry.getInstance().register(particleType, (ParticleFactoryRegistry.PendingParticleFactory) spriteSetFunction::apply));
        DimensionalDoorsClient.initEntitiesClient(EntityRendererRegistry::register);
        ModEntityModelLayers.initClient((modelLayerLocation, layerDefinitionSupplier) -> EntityModelLayerRegistry.registerModelLayer(modelLayerLocation, layerDefinitionSupplier::get));
    }
}
