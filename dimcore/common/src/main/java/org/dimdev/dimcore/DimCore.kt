package org.dimdev.dimcore

import com.mojang.logging.LogUtils
import org.dimdev.dimcore.api.Platform
import org.dimdev.dimcore.api.client.ClientPlatform
import org.dimdev.dimcore.api.transfer.TransferBridge
import org.slf4j.Logger
import java.util.*
import java.util.function.Supplier

object DimCore {
    const val MOD_ID: String = "dimcore"
    val LOGGER: Logger = LogUtils.getLogger()

    @JvmStatic val platform = load(Platform::class.java)
    @JvmStatic val clientPlatform = load(ClientPlatform::class.java)
    @JvmStatic var transfer = load(TransferBridge::class.java)

    private fun <T> load(type: Class<T>): T = ServiceLoader.load<T>(type).findFirst().orElseThrow(Supplier { IllegalStateException("No " + type.getSimpleName() + " implementation found") })
}
