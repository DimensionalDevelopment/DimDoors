package org.dimdev.dimdoors.particle.client

import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*

class RiftParticle(
    world: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    velocityX: Double,
    velocityY: Double,
    velocityZ: Double,
    color: Float,
    ageSpread: Int,
    spriteProvider: SpriteSet
) : TextureSheetParticle(world, x, y, z, 160.0, 8.0, 0.0) {
    private val sprites: SpriteSet

    init {
        this.gravity = 0f
        this.friction = 0.91f
        this.sprites = spriteProvider
        this.xd = velocityX
        this.yd = velocityY
        this.zd = velocityZ

        this.quadSize *= 0.55f
        this.lifetime = ageSpread - ageSpread / 2 + this.random.nextInt(ageSpread)

        this.setColor(color, color, color)
        this.setSpriteFromAge(spriteProvider)
    }

    override fun render(buffer: VertexConsumer, renderInfo: Camera, partialTicks: Float) {
        this.setSpriteFromAge(this.sprites)
        setAlpha(1 - age.toFloat() / lifetime)
        super.render(buffer, renderInfo, partialTicks)
    }

    override fun getRenderType(): ParticleRenderType {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
    }

    public override fun getLightColor(partialTick: Float): Int {
        return 15728880
    }

    class Factory(private val spriteProvider: SpriteSet) : ParticleProvider<RiftParticleOptions> {
        override fun createParticle(
            riftParticleOptions: RiftParticleOptions, clientWorld: ClientLevel,
            x: Double, y: Double, z: Double,
            velocityX: Double, velocityY: Double, velocityZ: Double
        ): Particle {
            return RiftParticle(
                clientWorld, x, y, z,
                velocityX, velocityY, velocityZ,
                riftParticleOptions.color,
                riftParticleOptions.averageAge,
                this.spriteProvider
            )
        }
    }
}
