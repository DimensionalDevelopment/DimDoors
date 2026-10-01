package org.dimdev.dimdoors.client

import net.fabricmc.fabric.api.renderer.v1.render.RenderContext
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.ItemOverrides
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState
import java.util.function.Supplier

class GeneratedDoorBakedModel
/**
 * @param portalId the portal model to draw underneath `sourceId`, or `null` for the
 * block models, which get their portal from the block entity renderer instead
 */(
    private val sourceId: ModelResourceLocation,
    private val portalId: ResourceLocation?
) : BakedModel {
    private val source get() = Minecraft.getInstance().modelManager.getModel(sourceId)
    private val portal = Minecraft.getInstance().modelManager.getModel(portalId)

    override fun getQuads(state: BlockState?, direction: Direction?, random: RandomSource): List<BakedQuad>? {



        val sourceQuads = source.getQuads(state, direction, random)
        val portalQuads = portal.getQuads(state, direction, random)

        if (portalQuads.isEmpty()) {
            return sourceQuads
        }

        val quads = mutableListOf<BakedQuad>()

        quads.addAll(portalQuads)
        quads.addAll(sourceQuads)

        return quads
    }

    override fun useAmbientOcclusion(): Boolean = source.useAmbientOcclusion()

    override fun isGui3d(): Boolean = source.isGui3d

    override fun usesBlockLight(): Boolean = source.usesBlockLight()

    override fun isCustomRenderer(): Boolean = source.isCustomRenderer

    override fun getParticleIcon(): TextureAtlasSprite = source.particleIcon

    override fun getTransforms(): ItemTransforms = source.transforms

    override fun getOverrides(): ItemOverrides = source.overrides

    override fun isVanillaAdapter(): Boolean = source.isVanillaAdapter

    override fun emitBlockQuads(
        blockView: BlockAndTintGetter?,
        state: BlockState?,
        pos: BlockPos?,
        randomSupplier: Supplier<RandomSource>,
        context: RenderContext?
    ) {
        source.emitBlockQuads(
            blockView,
            state,
            pos,
            randomSupplier,
            context
        )
    }

    override fun emitItemQuads(
        stack: ItemStack?,
        randomSupplier: Supplier<RandomSource>,
        context: RenderContext?
    ) {
        // Portal first, so the door overwrites it wherever the door is opaque. The two are
        // coplanar, so whichever is emitted last wins the depth test - emitting the portal
        // second hides the door behind it completely. Must stay in sync with getQuads.
        if (portalId != null) {
            portal.emitItemQuads(
                stack,
                randomSupplier,
                context
            )
        }

        source.emitItemQuads(
            stack,
            randomSupplier,
            context
        )
    }
}