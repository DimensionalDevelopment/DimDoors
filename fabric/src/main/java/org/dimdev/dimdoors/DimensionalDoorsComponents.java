package org.dimdev.dimdoors;

import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import dev.onyxstudios.cca.api.v3.level.LevelComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.level.LevelComponentInitializer;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.item.component.CounterComponent;
import org.dimdev.dimdoors.item.component.fabric.CounterComponentImpl;
import org.dimdev.dimdoors.world.level.component.ChunkLazilyGeneratedComponent;
import org.dimdev.dimdoors.world.level.component.fabric.ChunkLazilyGeneratedComponentImpl;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;

@SuppressWarnings("UnstableApiUsage")
public class DimensionalDoorsComponents implements LevelComponentInitializer, ItemComponentInitializer, ChunkComponentInitializer {
	public static final ComponentKey<DimensionalRegistry> DIMENSIONAL_REGISTRY_COMPONENT_KEY = ComponentRegistryV3.INSTANCE.getOrCreate(DimensionalDoors.id("dimensional_registry"), DimensionalRegistry.class);
	public static final ComponentKey<CounterComponentImpl> COUNTER_COMPONENT_KEY = ComponentRegistryV3.INSTANCE.getOrCreate(DimensionalDoors.id("counter"), CounterComponentImpl.class);
	public static final ComponentKey<ChunkLazilyGeneratedComponentImpl> CHUNK_LAZILY_GENERATED_COMPONENT_KEY = ComponentRegistryV3.INSTANCE.getOrCreate(DimensionalDoors.id("chunk_lazily_generated"), ChunkLazilyGeneratedComponentImpl.class);

	@Override
	public void registerLevelComponentFactories(LevelComponentFactoryRegistry registry) {
		registry.register(DIMENSIONAL_REGISTRY_COMPONENT_KEY, level -> new DimensionalRegistry());
	}

	@Override
	public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
		registry.register(ModItems.RIFT_CONFIGURATION_TOOL.get(), COUNTER_COMPONENT_KEY, CounterComponentImpl::new);
	}

	@Override
	public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
		registry.register(CHUNK_LAZILY_GENERATED_COMPONENT_KEY, chunk -> new ChunkLazilyGeneratedComponentImpl());
	}
}
