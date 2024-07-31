package org.dimdev.dimdoors;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import org.dimdev.dimdoors.api.util.StreamUtils;

public class DimensionalDoorsFabric implements ModInitializer {

	@Override
    public void onInitialize() {
		StreamUtils.setup(this);
        DimensionalDoors.init();

		PlayerBlockBreakEvents.AFTER.register(DimensionalDoors::afterBlockBreak);
    }
}
