package org.dimdev.dimdoors.client;

import net.fabricmc.api.ClientModInitializer;

public class DimensionalDoorsClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DimensionalDoorsClient.init();
        DimensionRenderering.initClient();
    }
}
