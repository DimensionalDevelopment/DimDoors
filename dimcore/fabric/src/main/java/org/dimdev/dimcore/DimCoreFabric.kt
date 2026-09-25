package org.dimdev.dimcore

import net.fabricmc.api.ModInitializer
import org.dimdev.dimcore.transfer.FabricTransfer

class DimCoreFabric : ModInitializer {
    override fun onInitialize() = FabricTransfer
}
