package org.dimdev.dimdoors.particle;

import org.dimdev.dimdoors.particle.client.LimboAshParticle;
import org.dimdev.dimdoors.particle.client.MonolithParticle;
import org.dimdev.dimdoors.particle.client.RiftParticle;
import org.dimdev.dimdoors.particle.client.RiftParticleEffect;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import org.dimdev.dimdoors.Util;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;

public class ModParticleTypes {
	public static final DefaultParticleType MONOLITH = FabricParticleTypes.simple(true);
	public static final ParticleType<RiftParticleEffect> RIFT = new RiftParticleType();
	public static final DefaultParticleType LIMBO_ASH = FabricParticleTypes.simple(false);

	public static void init() {
		Registry.register(Registry.PARTICLE_TYPE, Util.id("monolith"), MONOLITH);
		Registry.register(Registry.PARTICLE_TYPE, Util.id("rift"), RIFT);
		Registry.register(Registry.PARTICLE_TYPE, Util.id("limbo_ash"), LIMBO_ASH);
	}

	@Environment(EnvType.CLIENT)
	public static void initClient() {
		ParticleFactoryRegistry.getInstance().register(MONOLITH, new MonolithParticle.Factory());
		ParticleFactoryRegistry.getInstance().register(RIFT, RiftParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(LIMBO_ASH, LimboAshParticle.Factory::new);
	}
}
