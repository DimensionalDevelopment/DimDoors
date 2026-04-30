package org.dimdev.dimdoors.particle;

import net.minecraft.core.particles.SimpleParticleType;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModParticleTypes {
    public static final SimpleParticleType MONOLITH = DimensionalDoors.getSided().registerParticleType("monolith", new SimpleParticleType(true) {});
    public static final RiftParticleType RIFT = DimensionalDoors.getSided().registerParticleType("rift", new RiftParticleType());
    public static final SimpleParticleType LIMBO_ASH = DimensionalDoors.getSided().registerParticleType("limbo_ash", new SimpleParticleType(false) {});

    public static void init() {}
}
