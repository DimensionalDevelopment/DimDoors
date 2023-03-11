package org.dimdev.dimdoors.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.particle.client.LimboAshParticle;
import org.dimdev.dimdoors.particle.client.MonolithParticle;
import org.dimdev.dimdoors.particle.client.RiftParticle;
import org.dimdev.dimdoors.particle.client.RiftParticleEffect;

public class ModParticleTypes {
	public static final SimpleParticleType MONOLITH = FabricParticleTypes.simple(true);
	public static final ParticleType<RiftParticleEffect> RIFT = new RiftParticleType();
	public static final SimpleParticleType LIMBO_ASH = FabricParticleTypes.simple(false);

	public static void init() {
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, DimensionalDoors.id("monolith"), MONOLITH);
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, DimensionalDoors.id("rift"), RIFT);
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, DimensionalDoors.id("limbo_ash"), LIMBO_ASH);
	}

	@Environment(EnvType.CLIENT)
	public static void initClient() {
		ParticleFactoryRegistry.getInstance().register(MONOLITH, new MonolithParticle.Factory());
		ParticleFactoryRegistry.getInstance().register(RIFT, RiftParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(LIMBO_ASH, LimboAshParticle.Factory::new);
	}
}
