package org.dimdev.dimdoors;

import net.fabricmc.api.ModInitializer;

public class DimensionalDoorsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        DimensionalDoors.init();
    }
}
