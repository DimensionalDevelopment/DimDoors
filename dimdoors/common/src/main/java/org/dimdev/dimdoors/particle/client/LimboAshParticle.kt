package org.dimdev.dimdoors.particle.client

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.BaseAshSmokeParticle
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.SimpleParticleType
import java.util.*

class LimboAshParticle(world: ClientLevel, x: Double, y: Double, z: Double, velocityX: Double, velocityY: Double, velocityZ: Double, scaleMultiplier: Float, spriteProvider: SpriteSet) : BaseAshSmokeParticle(
    world, x, y, z,
    0.1f, 0.1f, 0.01f,
    velocityX, velocityY, velocityZ,
    scaleMultiplier, spriteProvider,
    0.0f, 20, 0.0125f, false
) {

    init {
        this.rCol = 0.0431f
        this.gCol = 0.0353f
        this.bCol = 0.0352f
        this.gravity *= -1f
    }

    class Factory(private val spriteProvider: SpriteSet) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            particleOptions: SimpleParticleType,
            clientLevel: ClientLevel,
            d: Double,
            e: Double,
            f: Double,
            g: Double,
            h: Double,
            i: Double
        ): Particle {
            val random = clientLevel.random
            val j = random.nextFloat().toDouble() * 0.4 * random.nextFloat().toDouble() * 0.1
            val k = random.nextFloat().toDouble() * 0.8 * random.nextFloat().toDouble() * 0.1 // * 5.0D;
            val l = random.nextFloat().toDouble() * 0.4 * random.nextFloat().toDouble() * 0.1
            return LimboAshParticle(clientLevel, d, e, f, j, k, l, 1.0f, this.spriteProvider)
        }

        override fun equals(obj: Any?): Boolean {
            if (obj === this) return true
            if (obj == null || obj.javaClass != this.javaClass) return false
            val that = obj as Factory
            return this.spriteProvider == that.spriteProvider
        }

        override fun hashCode(): Int {
            return Objects.hash(spriteProvider)
        }

        override fun toString(): String {
            return "Factory[" +
                    "spriteProvider=" + spriteProvider + ']'
        }
    }
}
