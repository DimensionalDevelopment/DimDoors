package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;

import java.util.function.Consumer;

public class AdvancementProvider extends FabricAdvancementProvider {
	public AdvancementProvider(FabricDataOutput dataGenerator) {
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
