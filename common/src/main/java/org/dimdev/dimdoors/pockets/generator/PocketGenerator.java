package org.dimdev.dimdoors.pockets.generator;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.Weighted;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.TemplateUtils;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.modifier.RiftManager;
import org.dimdev.dimdoors.util.CodecUtils;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.PocketImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public abstract class PocketGenerator implements Weighted<PocketGenerationContext> {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Registrar<PocketGeneratorType<?>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<PocketGeneratorType<? extends PocketGenerator>>builder(DimensionalDoors.id("pocket_generator_type")).build();

	public static final Codec<Supplier<PocketGenerator>> CODEC_LOADER = CodecUtils.codecWithReference(ResourceLocation.CODEC.<PocketGeneratorType<?>>xmap(REGISTRY::get, REGISTRY::getId).dispatch(PocketGenerator::getType, PocketGeneratorType::mapCodec), id -> PocketLoader.getInstance().getGenerator(id));
	public static final Codec<PocketGenerator> CODEC = CODEC_LOADER.xmap(Supplier::get, a -> () -> a);

	private static final String defaultWeightEquation = "5"; // TODO: make config
	private static final int fallbackWeight = 5; // TODO: make config

	protected CompoundTag builder;
	protected Equation weight;
	protected Boolean setupLoot;
	protected final List<Modifier> modifierList;
	protected final List<String> tags;

//	public PocketGenerator() { }

//	public PocketGenerator(String weight) {
//		this.weight = weight;
//		parseWeight();
//	}

	public static  <T extends PocketGenerator> Products.P5<RecordCodecBuilder.Mu<T>, CompoundTag, Equation, Boolean, List<Modifier>, List<String>> commonFields(RecordCodecBuilder.Instance<T> instance) {
		return instance.group(
				CompoundTag.CODEC.optionalFieldOf("builder", new CompoundTag()).forGetter(a -> a.builder),
				Equation.CODEC.fieldOf("weight").orElseGet(() -> Equation.parseOrCrash(defaultWeightEquation)).forGetter(a -> a.weight),
				Codec.BOOL.optionalFieldOf("setup_loot", false).forGetter(a -> a.setupLoot),
				Modifier.CODEC.listOf().optionalFieldOf("modifiers", new ArrayList<>()).forGetter(a -> a.modifierList),
				Codec.STRING.listOf().optionalFieldOf("tags", new ArrayList<>()).forGetter(a -> a.tags)
		);

	}

	public PocketGenerator(CompoundTag builder, Equation weight, boolean setupLoot, List<Modifier> modifierList, List<String> tags) {
        this.builder = builder;
        this.weight = weight;
        this.setupLoot = setupLoot;
        this.modifierList = modifierList;
        this.tags = tags;
    }

	//	private void parseWeight() { TODO: Extract logic for later use.
//		try {
//			this.weightEquation = Equation.parse(weight);
//		} catch (EquationParseException e) {
//			LOGGER.error("Could not parse weight equation \"" + weight + "\", defaulting to default weight equation \"" + defaultWeightEquation + "\"", e);
//			try {
//				// FIXME: do we actually want to have it serialize to the broken String equation we input?
//				this.weightEquation = Equation.newEquation(Equation.parse(defaultWeightEquation)::apply, stringBuilder -> stringBuilder.append(weight));
//			} catch (EquationParseException equationParseException) {
//				LOGGER.error("Could not parse default weight equation \"" + defaultWeightEquation + "\", defaulting to fallback weight \"" + fallbackWeight + "\"", equationParseException);
//				// FIXME: do we actually want to have it serialize to the broken String equation we input?
//				this.weightEquation = Equation.newEquation(stringDoubleMap -> (double) fallbackWeight, stringBuilder -> stringBuilder.append(weight));
//			}
//		}
//	}

	public abstract Pocket prepareAndPlacePocket(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder);

	public abstract PocketGeneratorType<? extends PocketGenerator> getType();

	public abstract String getKey();

	@Override
	public double getWeight(PocketGenerationContext parameters) {
		return this.weight.apply(parameters.toVariableMap(new HashMap<>()));
	}

	public boolean isSetupLoot() {
		return setupLoot != null && setupLoot;
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

	public void setup(Pocket pocket, RiftManager manager, PocketGenerationContext parameters, boolean setupLootTables) {
		ServerLevel world = parameters.world();

		if (!(pocket instanceof LazyGenerationPocket)) { // should not iterate over that which does not exist & area may be massive, getBlockEntities() might force generation
			if (setupLootTables) // temp
				pocket.getBlockEntities().forEach((blockPos, blockEntity) -> {
					if (/*setupLootTables &&*/ blockEntity instanceof Container inventory) { // comment in if needed
						if (inventory.isEmpty()) {
							if (blockEntity instanceof ChestBlockEntity || blockEntity instanceof DispenserBlockEntity) {
								TemplateUtils.setupLootTable(world, blockEntity, inventory, LOGGER);
								if (inventory.isEmpty()) {
									LOGGER.error(", however Inventory is: empty!");
								}
							}
						}
					}
				});
		}
		manager.getRifts().forEach(rift -> rift.getDestination().setLocation(new Location(world, rift.getBlockPos())));
		TemplateUtils.registerRifts(manager.getRifts(), parameters.linkTo(), parameters.linkProperties(), pocket);
	}

	public RiftManager getRiftManager(Pocket pocket) {
		return new RiftManager(pocket);
	}

	// why would you want to check for exact tags, but still need a blackList? Good question, but there is probably some use case for it.
	public boolean checkTags(List<String> required, List<String> blackList, boolean exact) {
		if (exact && required.size() != tags.size()) return false;
		if (required != null) {
			for (String req : required) {
				if (!tags.contains(req)) return false;
			}
		}
		if (blackList != null) {
			for (String black : blackList) {
				if (tags.contains(black)) return false;
			}
		}
		return true;
	}

	public Pocket.PocketBuilder<?, ?> pocketBuilder(PocketGenerationContext parameters) { // TODO: PocketBuilder from json
		if (builder == null){
			return PocketImpl.builder()
					.expand(getSize(parameters));
		}
		AbstractPocket.AbstractPocketBuilder<?, ?> abstractBuilder = AbstractPocket.deserializeBuilder(builder);
		if (! (abstractBuilder instanceof Pocket.PocketBuilder<?, ?> builder)) {
			return PocketImpl.builder()
					.expand(getSize(parameters));
		}
        return builder.expand(getSize(parameters));
	}

	public abstract Vec3i getSize(PocketGenerationContext parameters);

	public record PocketGeneratorType<T extends PocketGenerator>(MapCodec<T> mapCodec) {
		public static final RegistrySupplier<PocketGeneratorType<SchematicGenerator>> SCHEMATIC = register(SchematicGenerator.KEY, SchematicGenerator.CODEC);
//		RegistrySupplier<PocketGeneratorType<ChunkGenerator>> CHUNK = register(DimensionalDoors.id(ChunkGenerator.KEY), ChunkGenerator::new);
		public static final RegistrySupplier<PocketGeneratorType<VoidGenerator>> VOID = register(VoidGenerator.KEY, VoidGenerator.CODEC);

		public static void register() {}

		static <U extends PocketGenerator> RegistrySupplier<PocketGeneratorType<U>> register(String id, MapCodec<U> mapCodec) {
			return REGISTRY.register(DimensionalDoors.id(id), () -> new PocketGeneratorType<U>(mapCodec));
		}
	}
}
