package org.dimdev.dimcore.api.client

import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.client.resources.model.Material
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import org.dimdev.dimcore.DimCore.clientPlatform

object SpriteOverrides {
    private class Entry {
        val alternates = mutableListOf<Pair<ResourceLocation, () -> Boolean>>()
        var shown: ResourceLocation? = null
        var original: Array<out NativeImage>? = null
    }

    private val entries = mutableMapOf<Material, Entry>()

    fun register(sprite: Material, alternate: ResourceLocation, predicate: () -> Boolean) {
        if (entries.isEmpty()) clientPlatform.onClientTick(::tick)
        entries.getOrPut(sprite, ::Entry).alternates += alternate to predicate
    }

    fun reload(manager: ResourceManager) {
        entries.values.forEach {
            it.original?.forEach(NativeImage::close)
            it.original = null
            it.shown = null
        }
    }

    private fun tick() {
        val minecraft = Minecraft.getInstance()
        if (minecraft.overlay != null) return

        entries.forEach { (material, entry) ->
            val next = entry.alternates.firstOrNull { (_, predicate) -> predicate() }?.first
            if (next == entry.shown) return@forEach
            entry.shown = next

            val atlas = minecraft.textureManager.getTexture(material.atlasLocation()) as TextureAtlas
            val sprite = atlas.getSprite(material.texture())
            val contents = sprite.contents()
            val original = entry.original ?: contents.byMipLevel
            val alternate = next?.let { atlas.getSprite(it).contents().byMipLevel }?.takeIf { it[0].width == original[0].width && it[0].height == original[0].height }

            contents.byMipLevel = alternate ?: original
            entry.original = original.takeIf { alternate != null }

            atlas.bind()
            sprite.uploadFirstFrame()
        }
    }
}
