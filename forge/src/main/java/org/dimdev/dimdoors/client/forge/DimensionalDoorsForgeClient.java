package org.dimdev.dimdoors.client.forge;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.client.DimensionalDoorsClient;
import org.dimdev.dimdoors.client.config.ModMenu;

@Mod.EventBusSubscriber(modid = DimensionalDoors.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DimensionalDoorsForgeClient {
    public static void setupClient(FMLClientSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> ModMenu.getConfigScreen(screen)));
        DimensionalDoorsClient.init();
    }

    public static void registerParticles(RegisterParticleProvidersEvent event) {
        DimensionalDoorsClient.initParticles(
                (particleType, particleProvider) -> event.registerSpecial((ParticleType) particleType, (ParticleProvider) particleProvider),
                (particleType, spriteSetFunction) -> event.registerSpriteSet(particleType, (ParticleEngine.SpriteParticleRegistration) spriteSetFunction::apply));

    }
}
