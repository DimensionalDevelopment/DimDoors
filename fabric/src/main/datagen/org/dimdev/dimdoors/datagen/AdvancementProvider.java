package org.dimdev.dimdoors.datagen;

import java.util.function.Consumer;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;

public class AdvancementProvider extends FabricAdvancementProvider {
	public AdvancementProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	public void generateAdvancement(Consumer<Advancement> consumer) {
		new AdvancementTab().accept(consumer);
	}

	@Override
	public String getName() {
		return "Dimdoors Advancements";
	}
}