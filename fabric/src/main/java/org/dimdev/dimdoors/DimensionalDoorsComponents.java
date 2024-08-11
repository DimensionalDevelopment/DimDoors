package org.dimdev.dimdoors;

import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.world.level.component.fabric.ChunkLazilyGeneratedComponentImpl;
import org.dimdev.dimdoors.world.level.registry.fabric.DimensionalRegistryImpl;

@SuppressWarnings("UnstableApiUsage")
public class DimensionalDoorsComponents implements WorldComponentInitializer, ItemComponentInitializer, ChunkComponentInitializer {
	public static final ComponentKey<DimensionalRegistryImpl> DIMENSIONAL_REGISTRY_COMPONENT_KEY = ComponentRegistryV3.INSTANCE.getOrCreate(DimensionalDoors.id("dimensional_registry"), DimensionalRegistryImpl.class);
	public static final ComponentKey<IdCounterImpl> COUNTER_COMPONENT_KEY = ComponentRegistryV3.INSTANCE.getOrCreate(DimensionalDoors.id("counter"), IdCounterImpl.class);
	public static final ComponentKey<ChunkLazilyGeneratedComponentImpl> CHUNK_LAZILY_GENERATED_COMPONENT_KEY = ComponentRegistryV3.INSTANCE.getOrCreate(DimensionalDoors.id("chunk_lazily_generated"), ChunkLazilyGeneratedComponentImpl.class);

	@Override
	public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
		registry.register(ModItems.RIFT_CONFIGURATION_TOOL.get(), COUNTER_COMPONENT_KEY, IdCounterImpl::new);
	}

	@Override
	public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
		registry.register(CHUNK_LAZILY_GENERATED_COMPONENT_KEY, chunk -> new ChunkLazilyGeneratedComponentImpl());
	}

	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry worldComponentFactoryRegistry) {
		worldComponentFactoryRegistry.register(DIMENSIONAL_REGISTRY_COMPONENT_KEY, DimensionalRegistryImpl::createImpl);
	}
}
