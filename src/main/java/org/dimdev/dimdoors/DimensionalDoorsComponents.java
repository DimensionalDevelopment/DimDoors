package org.dimdev.dimdoors;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.level.LevelComponentFactory;
import dev.onyxstudios.cca.api.v3.level.LevelComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.level.LevelComponentInitializer;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactory;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactory;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import nerdhub.cardinal.components.api.component.Component;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.world.pocket.PocketRegistry;

public class DimensionalDoorsComponents implements ScoreboardComponentInitializer, WorldComponentInitializer {
	public static final ComponentKey<RiftRegistry> RIFT_REGISTRY_COMPONENT_KEY = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("dimdoors:rift_registry"), RiftRegistry.class);
	public static final ComponentKey<PocketRegistry> POCKET_REGISTRY_COMPONENT_KEY = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("dimdoors:pocket_registry"), PocketRegistry.class);
	@Override
	public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
		registry.register(RIFT_REGISTRY_COMPONENT_KEY, (ScoreboardComponentFactory<RiftRegistry>) scoreboard -> new RiftRegistry());
	}

	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
		registry.register(POCKET_REGISTRY_COMPONENT_KEY, world -> new PocketRegistry());
	}
}
