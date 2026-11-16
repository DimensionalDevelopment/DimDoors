package org.dimdev.dimdoors.client.render.entity

import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import org.dimdev.dimdoors.entity.monolith.MonolithEntity
import org.dimdev.dimdoors.util.ext.id

class MonolithRenderer(ctx: EntityRendererProvider.Context) :
    MobRenderer<MonolithEntity, MonolithModel>(ctx, MonolithModel(ctx).also { instance = it }, 0f) {
    override fun shouldShowName(mobEntity: MonolithEntity): Boolean {
        return false
    }

    override fun getTextureLocation(entity: MonolithEntity) = (if (entity.solid) SOLID else TRANSPARENT)[entity.textureState]

    companion object {
        private fun textureList(type: String) = (0..18).map { "textures/mob/monolith/$type/monolith_$it.png" }.map { it.id() }

        val TRANSPARENT = textureList("transparent")
        val SOLID = textureList("solid")
        lateinit var instance: MonolithModel
    }
}
