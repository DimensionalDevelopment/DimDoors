package org.dimdev.dimdoors.datagen;

import java.nio.file.Path;
import java.util.function.Consumer;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;

import net.minecraft.advancement.Advancement;

public class AdvancementProvider extends FabricAdvancementProvider {
	public AdvancementProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	private static Path getOutput(Path root, Advancement advancement) {
		return root.resolve("data/" + advancement.getId().getNamespace() + "/advancements/" + advancement.getId().getPath() + ".json");
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
