package org.dimdev.dimdoors.api;

import org.dimdev.dimdoors.block.door.data.condition.Condition;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.decay.DecayPredicate;
import org.dimdev.dimdoors.world.decay.DecayProcessor;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import net.minecraft.util.registry.Registry;

// TODO: javadocs for everything, refactoring
public interface DimensionalDoorsApi {
	default void registerVirtualTargetTypes(Registry<VirtualTarget.VirtualTargetType<?>> registry) {
	}

	default void registerVirtualSingularPocketTypes(Registry<ImplementedVirtualPocket.VirtualPocketType<?>> registry) {
	}

	default void registerModifierTypes(Registry<Modifier.ModifierType<?>> registry) {
	}

	default void registerPocketGeneratorTypes(Registry<PocketGenerator.PocketGeneratorType<?>> registry) {
	}

	default void registerAbstractPocketTypes(Registry<AbstractPocket.AbstractPocketType<?>> registry) {
	}

	default void registerPocketAddonTypes(Registry<PocketAddon.PocketAddonType<?>> registry) {
	}

	default void registerConditionTypes(Registry<Condition.ConditionType<?>> registry) {
	}

	default void postInitialize() {}

	default void registerDecayProcessors(Registry<DecayProcessor.DecayProcessorType<? extends DecayProcessor>> registry) {
	}

	default void registerDecayPredicates(Registry<DecayPredicate.DecayPredicateType<? extends DecayPredicate>> registry) {
	}
}
