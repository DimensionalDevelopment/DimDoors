package org.dimdev.dimdoors.particle

import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided

object ModParticleTypes : PlatformRegistry<ParticleType<*>>(Registries.PARTICLE_TYPE, BuiltInRegistries.PARTICLE_TYPE, getSided()) {
    val MONOLITH = create("monolith") { object : SimpleParticleType(true) {} }
    val RIFT = create("rift") { RiftParticleType }
    val LIMBO_ASH = create("limbo_ash") { object : SimpleParticleType(false) {} }
}
