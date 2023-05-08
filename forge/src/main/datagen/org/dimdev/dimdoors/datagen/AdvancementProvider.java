package datagen.org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementProvider extends net.minecraft.data.advancements.AdvancementProvider {

	public AdvancementProvider(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture) {
		super(arg, completableFuture, List.of((arg1, consumer) -> generateAdvancement(arg1, consumer)));

	}

	public static void generateAdvancement(HolderLookup.Provider arg, Consumer<Advancement> consumer) {
		new datagen.org.dimdev.dimdoors.datagen.AdvancementTab().accept(consumer);
	}

	@Override
	public String getName() {
		return "Dimdoors Advancements";
	}
}
