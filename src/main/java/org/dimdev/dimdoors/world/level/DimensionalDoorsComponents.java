package org.dimdev.dimdoors.world.level;

import net.minecraft.util.Identifier;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.level.LevelComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.level.LevelComponentInitializer;

public class DimensionalDoorsComponents implements LevelComponentInitializer {
	public static final ComponentKey<DimensionalRegistry> DIMENSIONAL_REGISTRY_COMPONENT_KEY = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("dimdoors:dimensional_registry"), DimensionalRegistry.class);

	@Override
	public void registerLevelComponentFactories(LevelComponentFactoryRegistry registry) {
		registry.register(DIMENSIONAL_REGISTRY_COMPONENT_KEY, level -> new DimensionalRegistry());
	}
}
