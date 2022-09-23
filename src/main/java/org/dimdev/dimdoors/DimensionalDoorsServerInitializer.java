package org.dimdev.dimdoors;

import net.fabricmc.api.DedicatedServerModInitializer;

public class DimensionalDoorsServerInitializer implements DedicatedServerModInitializer {

	@Override
	public void onInitializeServer() {
		DimensionalDoorsInitializer.getDimensionalDoorBlockRegistrar().run();
		DimensionalDoorsInitializer.getDimensionalDoorItemRegistrar().run();
	}

}
