package org.dimdev.dimdoors.pockets.virtual.reference;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.pockets.generator.LazyPocketGenerator;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.LazyModifier;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.modifier.RiftManager;
import org.dimdev.dimdoors.pockets.virtual.VirtualSingularPocket;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.math.Equation;
import org.dimdev.dimdoors.util.math.Equation.EquationParseException;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public abstract class PocketGeneratorReference extends VirtualSingularPocket {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String defaultWeightEquation = "5"; // TODO: make config
	private static final int fallbackWeight = 5; // TODO: make config

	protected String weight;
	protected Equation weightEquation;
	protected Boolean setupLoot;
	protected final List<Modifier> modifierList = Lists.newArrayList();
	protected final List<CompoundTag> addons = new ArrayList<>();


	private void parseWeight() {
		try {
			this.weightEquation = Equation.parse(weight);
		} catch (EquationParseException e) {
			LOGGER.error("Could not parse weight equation \"" + weight + "\", defaulting to default weight equation \"" + defaultWeightEquation + "\"", e);
			try {
				this.weightEquation = Equation.parse(defaultWeightEquation);
			} catch (EquationParseException equationParseException) {
				LOGGER.error("Could not parse default weight equation \"" + defaultWeightEquation + "\", defaulting to fallback weight \"" + fallbackWeight + "\"", equationParseException);
				this.weightEquation = stringDoubleMap -> fallbackWeight;
			}
		}
	}

	@Override
	public VirtualSingularPocket fromTag(CompoundTag tag) {
		if (tag.contains("weight")) { // override referenced pockets weight
			this.weight = tag.getString("weight");
			parseWeight();
		}

		if (tag.contains("setup_loot")) setupLoot = tag.getBoolean("setup_loot");

		if (tag.contains("modifiers")) {
			ListTag modifiersTag = tag.getList("modifiers", 10);
			for (int i = 0; i < modifiersTag.size(); i++) {
				modifierList.add(Modifier.deserialize(modifiersTag.getCompound(i)));
			}
		}

		if (tag.contains("addons", NbtType.LIST)) {
			ListTag modifiersTag = tag.getList("addons", 10);
			for (int i = 0; i < modifiersTag.size(); i++) {
				addons.add(modifiersTag.getCompound(i));
			}
		}

		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		if (weight != null) tag.putString("weight", weight);

		if (setupLoot != null) tag.putBoolean("setup_loot", setupLoot);

		ListTag modifiersTag = new ListTag();
		for (Modifier modifier : modifierList) {
			modifiersTag.add(modifier.toTag(new CompoundTag()));
		}
		if (modifiersTag.size() > 0) tag.put("modifiers", modifiersTag);

		ListTag addonsTag = new ListTag();
		addonsTag.addAll(addons);
		if (addonsTag.size() > 0) tag.put("addons", addonsTag);

		return tag;
	}

	@Override
	public double getWeight(PocketGenerationParameters parameters) {
		try {
			return weightEquation != null ? this.weightEquation.apply(parameters.toVariableMap(Maps.newHashMap())) : peekReferencedPocketGenerator(parameters).getWeight(parameters);
		} catch (RuntimeException e) {
			LOGGER.error(this.toString());
			throw new AssertionError(e);
		}
	}

	public void applyModifiers(PocketGenerationParameters parameters, RiftManager manager) {
		for (Modifier modifier : modifierList) {
			modifier.apply(parameters, manager);
		}
	}

	public void applyModifiers(PocketGenerationParameters parameters, Pocket.PocketBuilder<?, ?> builder) {
		for (Modifier modifier : modifierList) {
			modifier.apply(parameters, builder);
		}
	}

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationParameters parameters) {
		PocketGenerator generator = getReferencedPocketGenerator(parameters);


		Pocket.PocketBuilder<?, ?> builder = generator.pocketBuilder(parameters)
				.virtualLocation(parameters.getSourceVirtualLocation()); // TODO: virtualLocation thing still makes little sense
		generator.applyModifiers(parameters, builder);
		this.applyModifiers(parameters, builder);

		LazyPocketGenerator.currentlyGenerating = true;
		Pocket pocket = generator.prepareAndPlacePocket(parameters, builder);
		BlockPos originalOrigin = pocket.getOrigin();

		RiftManager manager = generator.getRiftManager(pocket);

		generator.applyModifiers(parameters, manager);

		this.applyModifiers(parameters, manager);
		generator.setup(pocket, manager, parameters, setupLoot != null ? setupLoot : generator.isSetupLoot());
		if (pocket instanceof LazyGenerationPocket) {
			if (!(generator instanceof LazyPocketGenerator)) throw new RuntimeException("pocket was instance of LazyGenerationPocket but generator was not instance of LazyPocketGenerator");
			LazyGenerationPocket lazyPocket = (LazyGenerationPocket) pocket;
			LazyPocketGenerator clonedGenerator = ((LazyPocketGenerator) generator).cloneWithLazyModifiers(originalOrigin);
			if (setupLoot != null) clonedGenerator.setSetupLoot(setupLoot);

			attachLazyModifiers(clonedGenerator);
			clonedGenerator.attachToPocket(lazyPocket);
			lazyPocket.init();

			LazyPocketGenerator.currentlyGenerating = false;
			while (!LazyPocketGenerator.generationQueue.isEmpty()) {
				Chunk chunk = LazyPocketGenerator.generationQueue.remove();
				MinecraftServer server = DimensionalDoorsInitializer.getServer();
				DimensionalDoorsInitializer.getServer().send(new ServerTask(server.getTicks(), () -> (lazyPocket).chunkLoaded(chunk)));
			}
		} else {
			LazyPocketGenerator.generationQueue.clear();
			LazyPocketGenerator.currentlyGenerating = false;
		}

		return pocket;
	}

	@Override
	public PocketGeneratorReference peekNextPocketGeneratorReference(PocketGenerationParameters parameters) {
		return this;
	}

	@Override
	public PocketGeneratorReference getNextPocketGeneratorReference(PocketGenerationParameters parameters) {
		return this;
	}

	public abstract PocketGenerator peekReferencedPocketGenerator(PocketGenerationParameters parameters);

	public abstract PocketGenerator getReferencedPocketGenerator(PocketGenerationParameters parameters);

	@Override
	public abstract String toString();

	public void attachLazyModifiers(LazyPocketGenerator generator) {
		generator.attachLazyModifiers(modifierList.stream().filter(LazyModifier.class::isInstance).map(LazyModifier.class::cast).collect(Collectors.toList()));
	}
}
