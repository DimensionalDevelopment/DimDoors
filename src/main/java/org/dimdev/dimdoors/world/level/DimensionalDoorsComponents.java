package org.dimdev.dimdoors.world.level;

import net.minecraft.util.Identifier;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import dev.onyxstudios.cca.api.v3.level.LevelComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.level.LevelComponentInitializer;
import org.dimdev.dimdoors.item.ModItems;

public class DimensionalDoorsComponents implements LevelComponentInitializer, ItemComponentInitializer {
	public static final ComponentKey<DimensionalRegistry> DIMENSIONAL_REGISTRY_COMPONENT_KEY = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("dimdoors:dimensional_registry"), DimensionalRegistry.class);
	public static final ComponentKey<Counter> COUNTER_COMPONENT_KEY = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("dimdoors:counter"), Counter.class);


	@Override
	public void registerLevelComponentFactories(LevelComponentFactoryRegistry registry) {
		registry.register(DIMENSIONAL_REGISTRY_COMPONENT_KEY, level -> new DimensionalRegistry());
	}

	@Override
	public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
		registry.registerFor(ModItems.RIFT_CONFIGURATION_TOOL, COUNTER_COMPONENT_KEY, Counter::new);
	}
}
