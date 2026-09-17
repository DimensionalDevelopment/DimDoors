package org.dimdev.dimcore;

import net.fabricmc.api.ModInitializer;
import org.dimdev.dimcore.transfer.FabricTransfer;

public class DimCoreFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FabricTransfer.registerExposed();
    }
}
