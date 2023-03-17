package org.dimdev.dimdoors.particle;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.fabricmc.api.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;

import org.dimdev.dimdoors.Constants;
import org.dimdev.dimdoors.particle.client.LimboAshParticle;
import org.dimdev.dimdoors.particle.client.MonolithParticle;
import org.dimdev.dimdoors.particle.client.RiftParticle;
import org.dimdev.dimdoors.particle.client.RiftParticleEffect;

public class ModParticleTypes {
	private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Constants.MODID);

	public static final RegistryObject<SimpleParticleType> MONOLITH = PARTICLE_TYPES.register("monolith", () -> new SimpleParticleType(true));
	public static final RegistryObject<ParticleType<RiftParticleEffect>> RIFT = PARTICLE_TYPES.register("rift", RiftParticleType::new);
	public static final RegistryObject<SimpleParticleType> LIMBO_ASH = PARTICLE_TYPES.register("limbo_ash", () -> new SimpleParticleType(false));

	public static void init(IEventBus bus) {
		PARTICLE_TYPES.register(bus);
	}

	@OnlyIn(Dist.CLIENT)
	public static void initClient() {
		ParticleFactoryRegistry.getInstance().register(MONOLITH, new MonolithParticle.Factory());
		ParticleFactoryRegistry.getInstance().register(RIFT, RiftParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(LIMBO_ASH, LimboAshParticle.Factory::new);
	}
}
