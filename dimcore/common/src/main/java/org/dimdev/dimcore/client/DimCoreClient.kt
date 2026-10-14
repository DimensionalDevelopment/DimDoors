package org.dimdev.dimcore.client

import org.dimdev.dimcore.api.client.ClientReloadListenerProvider
import org.dimdev.dimcore.api.client.ClientReloadListenerRegister
import org.dimdev.dimcore.api.client.IClientSided
import org.dimdev.dimcore.api.client.ModClient
import org.dimdev.dimcore.client.node.RenderNodes

object DimCoreClient : ModClient<IClientSided<*>>, ClientReloadListenerProvider {

    override fun registerClientReloadListeners(register: ClientReloadListenerRegister) {
        register.register("render_nodes", RenderNodes::reloadAll)
    }

    override fun init(sided: IClientSided<*>) {}

    override val modId: String = "dimcore"
}