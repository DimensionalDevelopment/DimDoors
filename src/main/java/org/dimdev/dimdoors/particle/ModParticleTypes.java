package org.dimdev.dimdoors.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import org.dimdev.dimdoors.particle.client.RiftParticle;
import org.dimdev.dimdoors.mixin.accessor.DefaultParticleTypeAccessor;
import org.dimdev.dimdoors.particle.client.MonolithParticle;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import org.dimdev.dimdoors.particle.client.RiftParticleEffect;

public class ModParticleTypes {
	public static final DefaultParticleType MONOLITH = FabricParticleTypes.simple(true);
	public static final ParticleType<RiftParticleEffect> RIFT = new RiftParticleType();

	public static void init() {
		Registry.register(Registry.PARTICLE_TYPE, new Identifier("dimdoors", "monolith"), MONOLITH);
		Registry.register(Registry.PARTICLE_TYPE, new Identifier("dimdoors", "rift"), RIFT);
	}

	@Environment(EnvType.CLIENT)
	public static void initClient() {
		ParticleFactoryRegistry.getInstance().register(MONOLITH, new MonolithParticle.Factory());
		ParticleFactoryRegistry.getInstance().register(RIFT, RiftParticle.Factory::new);
	}
}
