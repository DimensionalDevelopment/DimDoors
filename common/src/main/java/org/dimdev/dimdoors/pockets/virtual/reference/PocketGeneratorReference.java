package org.dimdev.dimdoors.pockets.virtual.reference;

import com.google.common.collect.Maps;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.api.util.math.Equation.EquationParseException;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.generator.LazyPocketGenerator;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.LazyCompatibleModifier;
import org.dimdev.dimdoors.pockets.modifier.LazyModifier;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.modifier.RiftManager;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class PocketGeneratorReference implements VirtualPocket {
	private static final Logger LOGGER = LogManager.getLogger();

	protected Equation weight;
	protected Boolean setupLoot;
	protected List<Modifier> modifierList;
	protected List<PocketAddon.PocketBuilderAddon<?, ?>> addons;

	public PocketGeneratorReference(Equation weight, Boolean setupLoot, List<Modifier> modifierList, List<PocketAddon.PocketBuilderAddon<?, ?>> addons) {
        this.weight = weight;
        this.setupLoot = setupLoot;
        this.modifierList = modifierList;
        this.addons = addons;
    }

//	private void parseWeight() {
//		try {
//			this.weightEquation = Equation.parse(weight);
//		} catch (EquationParseException e) {
//			LOGGER.debug("Defaulting to default weight equation for {}", this);
//			LOGGER.debug("Exception Stacktrace", e);
//			try {
//				// FIXME: do we actually want to have it serialize to the broken String equation we input?
//				this.weightEquation = Equation.newEquation(Equation.parse(DimensionalDoors.getConfig().getPocketsConfig().defaultWeightEquation)::apply, stringBuilder -> stringBuilder.append(weight));
//			} catch (EquationParseException equationParseException) {
//				LOGGER.debug("Defaulting to default weight equation for {}", this);
//				LOGGER.debug("Exception Stacktrace", e);
//				// FIXME: do we actually want to have it serialize to the broken String equation we input?
//				this.weightEquation = Equation.newEquation(stringDoubleMap -> (double) DimensionalDoors.getConfig().getPocketsConfig().fallbackWeight, stringBuilder -> stringBuilder.append(weight));
//			}
//		}
//	}

	public static  <T extends PocketGeneratorReference> Products.P4<RecordCodecBuilder.Mu<T>, Equation, Boolean, List<Modifier>, List<PocketAddon.PocketBuilderAddon<?, ?>>> commonFields(RecordCodecBuilder.Instance<T> instance) {
		return instance.group(
				Equation.CODEC.optionalFieldOf("weight", Equation.parseOrCrash(DimensionalDoors.getConfig().getPocketsConfig().defaultWeightEquation)).forGetter(a -> a.weight),
				Codec.BOOL.optionalFieldOf("setup_loot", false).forGetter(a -> a.setupLoot),
				Modifier.CODEC.listOf().optionalFieldOf("modifiers", new ArrayList<>()).forGetter(a -> a.modifierList),
				PocketAddon.BUILDER_CODEC.listOf().optionalFieldOf("addons", new ArrayList<>()).forGetter(a -> a.addons)
		);

	}

	@Override
	public double getWeight(PocketGenerationContext parameters) {
		try {
			return weight != null ? this.weight.apply(parameters.toVariableMap(Maps.newHashMap())) : peekReferencedPocketGenerator(parameters).getWeight(parameters);
		} catch (RuntimeException e) {
			LOGGER.error(this.toString());
			throw new AssertionError(e);
		}
	}

	public void applyModifiers(PocketGenerationContext parameters, RiftManager manager) {
		for (var modifier : modifierList) {
			modifier.apply(parameters, manager);
		}
	}

	public void applyModifiers(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
		for (var modifier : modifierList) {
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
		Set<LevelChunk> alreadyLoadedChunks = StreamSupport.stream(parameters.world().getChunkSource().chunkMap.getChunks().spliterator(), false).map(ChunkHolder::getTickingChunk).filter(Objects::nonNull).collect(Collectors.toSet());

		Pocket pocket = generator.prepareAndPlacePocket(parameters, builder);
		BlockPos originalOrigin = pocket.getOrigin();

		RiftManager manager = generator.getRiftManager(pocket);

		generator.applyModifiers(parameters, manager);

		this.applyModifiers(parameters, manager);

		if (pocket instanceof LazyGenerationPocket lazyPocket) {
            if (!(generator instanceof LazyPocketGenerator lazyPocketGenerator)) {
                throw new RuntimeException("pocket was instance of LazyGenerationPocket but generator was not instance of LazyPocketGenerator");
            } else {
				LazyPocketGenerator clonedGenerator = lazyPocketGenerator.cloneWithLazyModifiers(originalOrigin);
                if (setupLoot != null) clonedGenerator.setSetupLoot(setupLoot);

                attachLazyModifiers(clonedGenerator);
                clonedGenerator.attachToPocket(lazyPocket);
                lazyPocket.init();

                alreadyLoadedChunks.forEach(lazyPocket::chunkLoaded);

                LazyPocketGenerator.currentlyGenerating = false;

                while (!LazyPocketGenerator.generationQueue.isEmpty()) {
                    LevelChunk chunk = LazyPocketGenerator.generationQueue.remove();

                    LazyCompatibleModifier.runQueuedModifications(chunk);
                    MinecraftServer server = DimensionalDoors.getServer();
                    DimensionalDoors.getServer().tell(new TickTask(server.getTickCount(), () -> (lazyPocket).chunkLoaded(chunk)));
                }
                LazyCompatibleModifier.runLeftoverModifications(DimensionalDoors.getWorld(lazyPocket.getWorld()));
            }
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
