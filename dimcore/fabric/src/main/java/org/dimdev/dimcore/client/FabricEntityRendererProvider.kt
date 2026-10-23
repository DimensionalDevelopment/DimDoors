package org.dimdev.dimcore.client

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import org.dimdev.dimcore.api.client.EntityRendererRegister

object FabricEntityRendererProvider : EntityRendererRegister {
    override fun <T : Entity> registerEntity(type: EntityType<T>, provider: EntityRendererProvider<T>) {
        EntityRendererRegistry.register(type, provider)
    }

    override fun <T : BlockEntity> registerBlockEntity(type: BlockEntityType<T>, provider: BlockEntityRendererProvider<T>) {
        BlockEntityRenderers.register(type, provider)
    }
}
