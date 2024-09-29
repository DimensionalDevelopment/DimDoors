package org.dimdev.dimdoors.datagen;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementProvider extends net.minecraft.data.advancements.AdvancementProvider {

	public AdvancementProvider(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture) {
		super(arg, completableFuture, List.of(AdvancementProvider::generateAdvancement));

	}

	public static void generateAdvancement(HolderLookup.Provider arg, Consumer<AdvancementHolder> consumer) {
		new AdvancementTab().accept(consumer);
	}
}
