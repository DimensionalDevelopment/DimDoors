package org.dimdev.dimdoors.pockets.virtual.reference;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.pockets.generator.LazyPocketGenerator;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.LazyCompatibleModifier;
import org.dimdev.dimdoors.pockets.modifier.LazyModifier;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.modifier.RiftManager;
import org.dimdev.dimdoors.pockets.virtual.AbstractVirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.api.util.math.Equation.EquationParseException;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public abstract class PocketGeneratorReference extends AbstractVirtualPocket {
	private static final Logger LOGGER = LogManager.getLogger();

	protected String weight;
	protected Equation weightEquation;
	protected Boolean setupLoot;
	protected final List<Modifier> modifierList = Lists.newArrayList();
	protected final List<NbtCompound> addons = new ArrayList<>();

	private void parseWeight() {
		try {
			this.weightEquation = Equation.parse(weight);
		} catch (EquationParseException e) {
			LOGGER.debug("Defaulting to default weight equation for {}", this);
			LOGGER.debug("Exception Stacktrace", e);
			try {
				// FIXME: do we actually want to have it serialize to the broken String equation we input?
				this.weightEquation = Equation.newEquation(Equation.parse(DimensionalDoorsInitializer.getConfig().getPocketsConfig().defaultWeightEquation)::apply, stringBuilder -> stringBuilder.append(weight));
			} catch (EquationParseException equationParseException) {
				LOGGER.debug("Defaulting to default weight equation for {}", this);
				LOGGER.debug("Exception Stacktrace", e);
				// FIXME: do we actually want to have it serialize to the broken String equation we input?
				this.weightEquation = Equation.newEquation(stringDoubleMap -> (double) DimensionalDoorsInitializer.getConfig().getPocketsConfig().fallbackWeight, stringBuilder -> stringBuilder.append(weight));
			}
		}
	}

	@Override
	public ImplementedVirtualPocket fromNbt(NbtCompound nbt, ResourceManager manager) {
		if (nbt.contains("weight")) { // override referenced pockets weight
			this.weight = nbt.getString("weight");
			parseWeight();
		}

		if (nbt.contains("setup_loot")) setupLoot = nbt.getBoolean("setup_loot");

		if (nbt.contains("modifiers")) {
			NbtList modifiersNbt = nbt.getList("modifiers", 10);
			for (int i = 0; i < modifiersNbt.size(); i++) {
				modifierList.add(Modifier.deserialize(modifiersNbt.getCompound(i), manager));
			}
		}
		if (nbt.contains("modifier_references")) {
			NbtList modifiersNbt = nbt.getList("modifier_references", NbtType.STRING);
			for (NbtElement nbtElement : modifiersNbt) {
				modifierList.add(Modifier.deserialize(nbtElement, manager));
			}
		}

		if (nbt.contains("addons", NbtType.LIST)) {
			NbtList addonsNbt = nbt.getList("addons", 10);
			for (int i = 0; i < addonsNbt.size(); i++) {
				// TODO: something with the ResourceManager??? Probably need AddonBuilder now.
				addons.add(addonsNbt.getCompound(i));
			}
		}

		return this;
	}

	@Override
	protected NbtCompound toNbtInternal(NbtCompound nbt, boolean allowReference) {

		if (weight != null) nbt.putString("weight", weight);

		if (setupLoot != null) nbt.putBoolean("setup_loot", setupLoot);

		NbtList modifiersNbt = new NbtList();
		// TODO: deserialize with ResourceManager
		for (Modifier modifier : modifierList) {
			modifiersNbt.add(modifier.toNbt(new NbtCompound()));
		}
		if (modifiersNbt.size() > 0) nbt.put("modifiers", modifiersNbt);

		NbtList addonsNbt = new NbtList();
		// TODO: something with the ResourceManager??? Probably need AddonBuilder now.
		addonsNbt.addAll(addons);
		if (addonsNbt.size() > 0) nbt.put("addons", addonsNbt);

		return nbt;
	}

	@Override
	public double getWeight(PocketGenerationContext parameters) {
		try {
			return weightEquation != null ? this.weightEquation.apply(parameters.toVariableMap(Maps.newHashMap())) : peekReferencedPocketGenerator(parameters).getWeight(parameters);
		} catch (RuntimeException e) {
			LOGGER.error(this.toString());
			throw new AssertionError(e);
		}
	}

	public void applyModifiers(PocketGenerationContext parameters, RiftManager manager) {
		for (Modifier modifier : modifierList) {
			modifier.apply(parameters, manager);
		}
	}

	public void applyModifiers(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
		for (Modifier modifier : modifierList) {
			modifier.apply(parameters, builder);
		}
	}

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationContext parameters) {
		PocketGenerator generator = getReferencedPocketGenerator(parameters);


		Pocket.PocketBuilder<?, ?> builder = generator.pocketBuilder(parameters)
				.virtualLocation(parameters.sourceVirtualLocation()); // TODO: virtualLocation thing still makes little sense
		generator.applyModifiers(parameters, builder);
		this.applyModifiers(parameters, builder);

		LazyPocketGenerator.currentlyGenerating = true;
		// ensure we aren't missing any chunks that were already loaded previously
		// for lazy gen
		Set<Chunk> alreadyLoadedChunks = StreamSupport.stream(parameters.world().getChunkManager().threadedAnvilChunkStorage.entryIterator().spliterator(), false).map(ChunkHolder::getWorldChunk).filter(Objects::nonNull).collect(Collectors.toSet());

		Pocket pocket = generator.prepareAndPlacePocket(parameters, builder);
		BlockPos originalOrigin = pocket.getOrigin();

		RiftManager manager = generator.getRiftManager(pocket);

		generator.applyModifiers(parameters, manager);

		this.applyModifiers(parameters, manager);

		if (pocket instanceof LazyGenerationPocket) {
			if (!(generator instanceof LazyPocketGenerator)) throw new RuntimeException("pocket was instance of LazyGenerationPocket but generator was not instance of LazyPocketGenerator");
			LazyGenerationPocket lazyPocket = (LazyGenerationPocket) pocket;
			LazyPocketGenerator clonedGenerator = ((LazyPocketGenerator) generator).cloneWithLazyModifiers(originalOrigin);
			if (setupLoot != null) clonedGenerator.setSetupLoot(setupLoot);

			attachLazyModifiers(clonedGenerator);
			clonedGenerator.attachToPocket(lazyPocket);
			lazyPocket.init();

			alreadyLoadedChunks.forEach(lazyPocket::chunkLoaded);

			LazyPocketGenerator.currentlyGenerating = false;

			while (!LazyPocketGenerator.generationQueue.isEmpty()) {
				Chunk chunk = LazyPocketGenerator.generationQueue.remove();

				LazyCompatibleModifier.runQueuedModifications(chunk);
				MinecraftServer server = DimensionalDoorsInitializer.getServer();
				DimensionalDoorsInitializer.getServer().send(new ServerTask(server.getTicks(), () -> (lazyPocket).chunkLoaded(chunk)));
			}
			LazyCompatibleModifier.runLeftoverModifications(DimensionalDoorsInitializer.getWorld(lazyPocket.getWorld()));
		} else {
			LazyPocketGenerator.currentlyGenerating = false;
			LazyPocketGenerator.generationQueue.clear();
		}

		generator.setup(pocket, manager, parameters, setupLoot != null ? setupLoot : generator.isSetupLoot());

		return pocket;
	}

	@Override
	public PocketGeneratorReference peekNextPocketGeneratorReference(PocketGenerationContext parameters) {
		return this;
	}

	@Override
	public PocketGeneratorReference getNextPocketGeneratorReference(PocketGenerationContext parameters) {
		return this;
	}

	public abstract PocketGenerator peekReferencedPocketGenerator(PocketGenerationContext parameters);

	public abstract PocketGenerator getReferencedPocketGenerator(PocketGenerationContext parameters);

	@Override
	public abstract String toString();

	public void attachLazyModifiers(LazyPocketGenerator generator) {
		generator.attachLazyModifiers(modifierList.stream().filter(LazyModifier.class::isInstance).map(LazyModifier.class::cast).collect(Collectors.toList()));
	}
}
