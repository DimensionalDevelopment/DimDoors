package org.dimdev.dimdoors.particle;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.particle.client.LimboAshParticle;
import org.dimdev.dimdoors.particle.client.MonolithParticle;
import org.dimdev.dimdoors.particle.client.RiftParticle;
import org.dimdev.dimdoors.particle.client.RiftParticleEffect;

public class ModParticleTypes {
	public static final DefaultParticleType MONOLITH = FabricParticleTypes.simple(true);
	public static final ParticleType<RiftParticleEffect> RIFT = new RiftParticleType();
	public static final DefaultParticleType LIMBO_ASH = FabricParticleTypes.simple(false);

	public static void init() {
		Registry.register(Registries.PARTICLE_TYPE, DimensionalDoors.id("monolith"), MONOLITH);
		Registry.register(Registries.PARTICLE_TYPE, DimensionalDoors.id("rift"), RIFT);
		Registry.register(Registries.PARTICLE_TYPE, DimensionalDoors.id("limbo_ash"), LIMBO_ASH);
	}

	@Environment(EnvType.CLIENT)
	public static void initClient() {
		ParticleFactoryRegistry.getInstance().register(MONOLITH, new MonolithParticle.Factory());
		ParticleFactoryRegistry.getInstance().register(RIFT, RiftParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(LIMBO_ASH, LimboAshParticle.Factory::new);
	}
}
