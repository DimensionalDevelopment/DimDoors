package org.dimdev.dimdoors.api;

import net.minecraft.registry.Registry;

import org.dimdev.dimdoors.item.door.data.condition.Condition;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.decay.DecayPredicate;
import org.dimdev.dimdoors.world.decay.DecayProcessor;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

// TODO: javadocs for everything, refactoring
public interface DimensionalDoorsApi {

	default void registerAbstractPocketTypes(Registry<AbstractPocket.AbstractPocketType<?>> registry) {
	}

	default void registerPocketAddonTypes(Registry<PocketAddon.PocketAddonType<?>> registry) {
	}

	default void registerConditionTypes(Registry<Condition.ConditionType<?>> registry) {
	}

	default void postInitialize() {}

}
