package org.dimdev.dimdoors.fabric.forge.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleType;
import org.dimdev.dimdoors.forge.client.DimensionalDoorModelVariantProvider;
import org.dimdev.dimdoors.forge.client.DimensionalDoorsClient;
import org.dimdev.dimdoors.forge.client.ModEntityModelLayers;

public class DimensionalDoorsClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DimensionalDoorsClient.init();
        ModelLoadingRegistry.INSTANCE.registerVariantProvider((manager) -> new DimensionalDoorModelVariantProvider());
        DimensionRenderering.initClient();
        DimensionalDoorsClient.initParticles(
                (particleType, particleProvider) -> ParticleFactoryRegistry.getInstance().register((ParticleType) particleType, (ParticleProvider) particleProvider),
                (particleType, spriteSetFunction) -> ParticleFactoryRegistry.getInstance().register(particleType, (ParticleFactoryRegistry.PendingParticleFactory) spriteSetFunction::apply));
        DimensionalDoorsClient.initEntitiesClient(EntityRendererRegistry::register);
        ModEntityModelLayers.initClient((modelLayerLocation, layerDefinitionSupplier) -> EntityModelLayerRegistry.registerModelLayer(modelLayerLocation, layerDefinitionSupplier::get));
    }
}
