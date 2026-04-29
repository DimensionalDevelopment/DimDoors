package org.dimdev.dimdoors.particle;

import dev.architectury.platform.Platform;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.architectury.utils.Env;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.particle.client.LimboAshParticle;
import org.dimdev.dimdoors.particle.client.MonolithParticle;
import org.dimdev.dimdoors.particle.client.RiftParticle;

public class ModParticleTypes {
    public static final SimpleParticleType MONOLITH = DimensionalDoors.getSided().registerParticleType("monolith", new SimpleParticleType(true) {});
    public static final RiftParticleType RIFT = DimensionalDoors.getSided().registerParticleType("rift", new RiftParticleType());
    public static final SimpleParticleType LIMBO_ASH = DimensionalDoors.getSided().registerParticleType("limbo_ash", new SimpleParticleType(false) {});

    public static void init() {}
}
