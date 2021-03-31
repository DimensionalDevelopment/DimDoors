package org.dimdev.dimdoors.api;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Bootstrap;
import net.minecraft.util.registry.Registry;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.door.data.condition.Condition;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;
import org.dimdev.test.ServerTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(ServerTestRunner.class)
public class DimensionalDoorsApiTest implements DimensionalDoorsApi {
	private static boolean hasCalledRegisterVirtualTargetTypes = false;
	private static boolean hasCalledRegisterVirtualSingularPocketTypes = false;
	private static boolean hasCalledRegisterModifierTypes = false;
	private static boolean hasCalledRegisterPocketGeneratorTypes = false;
	private static boolean hasCalledRegisterAbstractPocketTypes = false;
	private static boolean hasCalledRegisterPocketAddonTypes = false;
	private static boolean hasCalledRegisterConditionTypes = false;
	private static boolean hasCalledPostInitialize = false;

	@Test
	public void apiTest() {
		Bootstrap.initialize();
		FabricLoader.getInstance().getEntrypoints("main", ModInitializer.class).stream()
				.filter(DimensionalDoorsInitializer.class::isInstance)
				.map(DimensionalDoorsInitializer.class::cast)
				.forEach(DimensionalDoorsInitializer::onInitialize);

		assertTrue(hasCalledRegisterVirtualTargetTypes);
		assertTrue(hasCalledRegisterVirtualSingularPocketTypes);
		assertTrue(hasCalledRegisterModifierTypes);
		assertTrue(hasCalledRegisterPocketGeneratorTypes);
		assertTrue(hasCalledRegisterAbstractPocketTypes);
		assertTrue(hasCalledRegisterPocketAddonTypes);
		assertTrue(hasCalledRegisterConditionTypes);
		assertTrue(hasCalledPostInitialize);
	}

	@Override
	public void registerVirtualTargetTypes(Registry<VirtualTarget.VirtualTargetType<?>> registry) {
		System.out.println("test1234");
		hasCalledRegisterVirtualTargetTypes = true;
	}

	@Override
	public void registerVirtualSingularPocketTypes(Registry<ImplementedVirtualPocket.VirtualPocketType<?>> registry) {
		hasCalledRegisterVirtualSingularPocketTypes = true;
	}

	@Override
	public void registerModifierTypes(Registry<Modifier.ModifierType<?>> registry) {
		hasCalledRegisterModifierTypes = true;
	}

	@Override
	public void registerPocketGeneratorTypes(Registry<PocketGenerator.PocketGeneratorType<?>> registry) {
		hasCalledRegisterPocketGeneratorTypes = true;
	}

	@Override
	public void registerAbstractPocketTypes(Registry<AbstractPocket.AbstractPocketType<?>> registry) {
		hasCalledRegisterAbstractPocketTypes = true;
	}

	@Override
	public void registerPocketAddonTypes(Registry<PocketAddon.PocketAddonType<?>> registry) {
		hasCalledRegisterPocketAddonTypes = true;
	}

	@Override
	public void registerConditionTypes(Registry<Condition.ConditionType<?>> registry) {
		hasCalledRegisterConditionTypes= true;
	}

	@Override
	public void postInitialize() {
		hasCalledPostInitialize = true;
	}
}
