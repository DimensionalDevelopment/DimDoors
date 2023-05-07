package org.dimdev.dimdoors.fabric;

import net.fabricmc.api.ModInitializer;
import org.dimdev.dimdoors.DimensionalDoors;

public class DimensionalDoorsForge implements ModInitializer {
    @Override
    public void onInitialize() {
        DimensionalDoors.init();
    }
}
