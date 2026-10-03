package org.dimdev.dimcore.api.client

import net.minecraft.client.KeyMapping
import net.minecraft.server.packs.resources.ResourceManager
import org.dimdev.dimcore.api.ext.cast
import java.util.function.Consumer

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
