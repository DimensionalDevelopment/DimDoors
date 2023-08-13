package org.dimdev.dimdoors.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DimensionalDoorsClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DimensionalDoorsClient.init();
        DimensionRenderering.initClient();
        DimensionalDoorsClient.initParticles(
                (particleType, particleProvider) -> ParticleFactoryRegistry.getInstance().register((ParticleType) particleType, (ParticleProvider) particleProvider),
                (particleType, spriteSetFunction) -> ParticleFactoryRegistry.getInstance().register(particleType, (ParticleFactoryRegistry.PendingParticleFactory) spriteSetFunction::apply));
    }
}
