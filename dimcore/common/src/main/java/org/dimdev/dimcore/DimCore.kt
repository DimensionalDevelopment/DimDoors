package org.dimdev.dimcore

import org.dimdev.dimcore.api.Platform
import org.dimdev.dimcore.api.client.ClientPlatform
import org.dimdev.dimcore.api.transfer.TransferBridge
import java.util.*
import java.util.function.Supplier

object DimCore {
    @JvmStatic val platform = load(Platform::class.java)
    @JvmStatic val clientPlatform = load(ClientPlatform::class.java)
    @JvmStatic var transfer = load(TransferBridge::class.java)

    private fun <T> load(type: Class<T>): T = ServiceLoader.load<T>(type).findFirst().orElseThrow(Supplier { IllegalStateException("No " + type.getSimpleName() + " implementation found") })
}
