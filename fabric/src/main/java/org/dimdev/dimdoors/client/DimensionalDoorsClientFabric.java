package org.dimdev.dimdoors.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;

public class DimensionalDoorsClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DimensionalDoorsClient.init();
        DimensionRenderering.initClient();
        ModelLoadingRegistry.INSTANCE.registerVariantProvider((manager) -> new DimensionalDoorModelVariantProvider());
    }
}
