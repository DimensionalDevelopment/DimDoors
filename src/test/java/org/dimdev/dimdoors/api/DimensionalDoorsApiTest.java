package org.dimdev.dimdoors.api;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
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
public class DimensionalDoorsApiTest {

	@Test
	public void apiTest() {
		SharedConstants.createGameVersion();
		Bootstrap.initialize();
		FabricLoader.getInstance().getEntrypoints("main", ModInitializer.class).stream()
				.filter(DimensionalDoorsInitializer.class::isInstance)
				.map(DimensionalDoorsInitializer.class::cast)
				.forEach(DimensionalDoorsInitializer::onInitialize);

		DimDoorsTestApi apiTest = FabricLoader.getInstance().getEntrypoints("dimdoors:api", DimensionalDoorsApi.class).stream()
				.filter(DimDoorsTestApi.class::isInstance)
				.map(DimDoorsTestApi.class::cast)
				.findFirst()
				.orElseThrow(RuntimeException::new);

		assertTrue(apiTest.hasCalledRegisterVirtualTargetTypes);
		assertTrue(apiTest.hasCalledRegisterVirtualSingularPocketTypes);
		assertTrue(apiTest.hasCalledRegisterModifierTypes);
		assertTrue(apiTest.hasCalledRegisterPocketGeneratorTypes);
		assertTrue(apiTest.hasCalledRegisterAbstractPocketTypes);
		assertTrue(apiTest.hasCalledRegisterPocketAddonTypes);
		assertTrue(apiTest.hasCalledRegisterConditionTypes);
		assertTrue(apiTest.hasCalledPostInitialize);
	}

	public static class DimDoorsTestApi implements DimensionalDoorsApi {
		private boolean hasCalledRegisterVirtualTargetTypes = false;
		private boolean hasCalledRegisterVirtualSingularPocketTypes = false;
		private boolean hasCalledRegisterModifierTypes = false;
		private boolean hasCalledRegisterPocketGeneratorTypes = false;
		private boolean hasCalledRegisterAbstractPocketTypes = false;
		private boolean hasCalledRegisterPocketAddonTypes = false;
		private boolean hasCalledRegisterConditionTypes = false;
		private boolean hasCalledPostInitialize = false;

		@Override
		public void registerVirtualTargetTypes(Registry<VirtualTarget.VirtualTargetType<?>> registry) {
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
}
