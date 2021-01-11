package org.dimdev.dimdoors.particle;

import org.dimdev.dimdoors.mixin.DefaultParticleTypeAccessor;
import org.dimdev.dimdoors.particle.client.MonolithParticle;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class ModParticleTypes {
	public static final DefaultParticleType MONOLITH = DefaultParticleTypeAccessor.createDefaultParticleType(false);

	public static void init() {
		Registry.register(Registry.PARTICLE_TYPE, new Identifier("dimdoors", "monolith"), MONOLITH);
	}

	@Environment(EnvType.CLIENT)
	public static void initClient() {
		ParticleFactoryRegistry.getInstance().register(MONOLITH, new MonolithParticle.Factory());
	}
}
