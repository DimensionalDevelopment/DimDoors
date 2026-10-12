package org.dimdev.dimcore.client

import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimcore.api.client.ClientReloadListenerProvider
import org.dimdev.dimcore.api.client.ClientReloadListenerRegister
import org.dimdev.dimcore.api.client.IClientSided
import org.dimdev.dimcore.api.client.ModClient
import org.dimdev.dimcore.client.node.RenderNodes
import org.dimdev.dimcore.compat.iris.node.IrisNodeEvents

object DimCoreClient : ModClient<IClientSided<*>>, ClientReloadListenerProvider {

    override fun registerClientReloadListeners(register: ClientReloadListenerRegister) {
        register.register("render_nodes", RenderNodes::reloadAll)
        if (platform.isModLoaded("iris")) IrisNodeEvents.register()
    }

    override fun init(sided: IClientSided<*>) {}

    override val modId: String = "dimcore"
}