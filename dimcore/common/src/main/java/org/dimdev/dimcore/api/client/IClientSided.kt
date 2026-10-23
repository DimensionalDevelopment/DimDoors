package org.dimdev.dimcore.api.client

import net.minecraft.client.KeyMapping
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import org.dimdev.dimcore.api.ext.cast

interface IClientSided<T : IClientSided<T>> {
    fun self(): T = this.cast()
    fun registerKeyBinding(mapping: KeyMapping)
}

interface ClientReloadListenerProvider {
    fun registerClientReloadListeners(register: ClientReloadListenerRegister)
}

fun interface ClientReloadListenerRegister {
    fun register(name: String, consumer: (ResourceManager) -> Unit)
}

interface EntityRendererProvider {
    fun registerEntityRenderers(register: EntityRendererRegister)
}

interface EntityRendererRegister {
    fun <T : Entity> registerEntity(type: EntityType<T>, provider: EntityRendererProvider<T>)
    fun <T : BlockEntity> registerBlockEntity(type: BlockEntityType<T>, provider: BlockEntityRendererProvider<T>)
}