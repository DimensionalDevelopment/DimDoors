package org.dimdev.dimdoors.particle;

import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.particle.client.LimboAshParticle;
import org.dimdev.dimdoors.particle.client.MonolithParticle;
import org.dimdev.dimdoors.particle.client.RiftParticle;

public class ModParticleTypes {
	public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.PARTICLE_TYPE);
	public static final RegistrySupplier<SimpleParticleType> MONOLITH = REGISTRY.register("monolith", () -> new SimpleParticleType(true) {});
	public static final RegistrySupplier<RiftParticleType> RIFT = REGISTRY.register("rift", () -> new RiftParticleType());
	public static final RegistrySupplier<SimpleParticleType> LIMBO_ASH = REGISTRY.register("limbo_ash", () -> new SimpleParticleType(false) {});

	public static void init() {
		REGISTRY.register();
	}

	@Environment(EnvType.CLIENT)
	public static void initClient() {
		ParticleProviderRegistry.register(MONOLITH, (particleOptions, clientLevel, x, y, z, g, h, i) -> new MonolithParticle(clientLevel, x, y, z));
		ParticleProviderRegistry.register(RIFT, RiftParticle.Factory::new);
		ParticleProviderRegistry.register(LIMBO_ASH, LimboAshParticle.Factory::new);
	}
}
