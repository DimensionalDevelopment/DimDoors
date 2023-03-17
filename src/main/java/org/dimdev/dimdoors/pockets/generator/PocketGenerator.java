package org.dimdev.dimdoors.pockets.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.collect.Multimap;
import com.mojang.serialization.Lifecycle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.ReferenceSerializable;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.api.util.Weighted;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.api.util.math.Equation.EquationParseException;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.TemplateUtils;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.modifier.RiftManager;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public abstract class PocketGenerator implements Weighted<PocketGenerationContext>, ReferenceSerializable {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Registry<PocketGeneratorType<? extends PocketGenerator>> REGISTRY = FabricRegistryBuilder.from(new MappedRegistry<PocketGeneratorType<? extends PocketGenerator>>(ResourceKey.createRegistryKey(DimensionalDoors.resource("pocket_generator_type")), Lifecycle.stable(), false)).buildAndRegister();
	public static final String RESOURCE_STARTING_PATH = "pockets/generator"; //TODO: might want to restructure data packs

	private static final String defaultWeightEquation = "5"; // TODO: make config
	private static final int fallbackWeight = 5; // TODO: make config
	protected final List<Modifier> modifierList = new ArrayList<>();

	private String resourceKey = null;

	private CompoundTag builderNbt;
	protected String weight = defaultWeightEquation;
	protected Equation weightEquation;
	protected Boolean setupLoot;

	private final List<String> tags = new ArrayList<>();

	public PocketGenerator() { }

	public PocketGenerator(String weight) {
		this.weight = weight;
		parseWeight();
	}

	public static PocketGenerator deserialize(Tag nbt, ResourceManager manager) {
		switch (nbt.getId()) {
			case Tag.TAG_COMPOUND: // It's a serialized Modifier
				return PocketGenerator.deserialize((CompoundTag) nbt, manager);
			case Tag.TAG_STRING: // It's a reference to a resource location
				// TODO: throw if manager is null
				return ResourceUtil.loadReferencedResource(manager, RESOURCE_STARTING_PATH, nbt.getAsString(), ResourceUtil.NBT_READER.andThenComposable(nbtElement -> deserialize(nbtElement, manager)));
			default:
				throw new RuntimeException(String.format("Unexpected NbtType %d!", nbt.getId()));
		}
	}

	public static PocketGenerator deserialize(Tag nbt) {
		return deserialize(nbt, null);
	}

	public static PocketGenerator deserialize(CompoundTag nbt, ResourceManager manager) {
		ResourceLocation id = ResourceLocation.tryParse(nbt.getString("type")); // TODO: return some NONE PocketGenerator if type cannot be found or deserialization fails.
		PocketGeneratorType<? extends PocketGenerator> type = REGISTRY.get(id);
		if (type == null) {
			LOGGER.error("Could not deserialize PocketGenerator: " + nbt.toString());
			return null;
		}
		return type.fromNbt(nbt, manager);
	}

	public static PocketGenerator deserialize(CompoundTag nbt) {
		return deserialize(nbt, null);
	}

	public static Tag serialize(PocketGenerator pocketGenerator, boolean allowReference) {
		return pocketGenerator.toNbt(new CompoundTag(), allowReference);
	}

	public static Tag serialize(PocketGenerator pocketGenerator) {
		return serialize(pocketGenerator, false);
	}

	private void parseWeight() {
		try {
			this.weightEquation = Equation.parse(weight);
		} catch (EquationParseException e) {
			LOGGER.error("Could not parse weight equation \"" + weight + "\", defaulting to default weight equation \"" + defaultWeightEquation + "\"", e);
			try {
				// FIXME: do we actually want to have it serialize to the broken String equation we input?
				this.weightEquation = Equation.newEquation(Equation.parse(defaultWeightEquation)::apply, stringBuilder -> stringBuilder.append(weight));
			} catch (EquationParseException equationParseException) {
				LOGGER.error("Could not parse default weight equation \"" + defaultWeightEquation + "\", defaulting to fallback weight \"" + fallbackWeight + "\"", equationParseException);
				// FIXME: do we actually want to have it serialize to the broken String equation we input?
				this.weightEquation = Equation.newEquation(stringDoubleMap -> (double) fallbackWeight, stringBuilder -> stringBuilder.append(weight));
			}
		}
	}

	public PocketGenerator fromNbt(CompoundTag nbt, ResourceManager manager) {
		if (nbt.contains("builder", Tag.TAG_COMPOUND)) builderNbt = nbt.getCompound("builder");

		this.weight = nbt.contains("weight") ? nbt.getString("weight") : defaultWeightEquation;
		parseWeight();

		if (nbt.contains("setup_loot")) setupLoot = nbt.getBoolean("setup_loot");

		if (nbt.contains("modifiers")) {
			ListTag modifiersNbt = nbt.getList("modifiers", 10);
			for (int i = 0; i < modifiersNbt.size(); i++) {
				modifierList.add(Modifier.deserialize(modifiersNbt.getCompound(i), manager));
			}
		}

		if (nbt.contains("modifier_references")) {
			ListTag modifiersNbt = nbt.getList("modifier_references", Tag.TAG_STRING);
			for (Tag nbtElement : modifiersNbt) {
				modifierList.add(Modifier.deserialize(nbtElement, manager));
			}
		}

		if (nbt.contains("tags")) {
			ListTag nbtList = nbt.getList("tags", Tag.TAG_STRING);
			for (int i = 0; i < nbtList.size(); i++) {
				tags.add(nbtList.getString(i));
			}
		}
		return this;
	}

	public PocketGenerator fromNbt(CompoundTag nbt) {
		return fromNbt(nbt, null);
	}

	public Tag toNbt(CompoundTag nbt, boolean allowReference) {
		if (allowReference && this.resourceKey != null) {
			return StringTag.valueOf(this.resourceKey);
		}
		return toNbtInternal(nbt, allowReference);
	}

	protected CompoundTag toNbtInternal(CompoundTag nbt, boolean allowReference) {
		this.getType().toNbt(nbt);

		if (builderNbt != null) nbt.put("builder", builderNbt);

		if (!weight.equals(defaultWeightEquation)) nbt.putString("weight", weight);

		if (setupLoot != null) nbt.putBoolean("setup_loot", setupLoot);

		ListTag modifiersNbt = new ListTag();
		ListTag modifierReferences = new ListTag();
		for (Modifier modifier : modifierList) {
			Tag modNbt = modifier.toNbt(new CompoundTag(), allowReference);
			switch (modNbt.getId()) {
				case Tag.TAG_COMPOUND:
					modifiersNbt.add(modNbt);
					break;
				case Tag.TAG_STRING:
					modifierReferences.add(modNbt);
					break;
				default:
					throw new RuntimeException(String.format("Unexpected NbtType %d!", modNbt.getId()));
			}
		}
		if (modifiersNbt.size() > 0) nbt.put("modifiers", modifiersNbt);
		if (modifierReferences.size() > 0) nbt.put("modifier_references", modifierReferences);

		if (tags.size() > 0) {
			ListTag nbtList = new ListTag();
			for (String nbtStr : tags) {
				nbtList.add(StringTag.valueOf(nbtStr));
			}
			nbt.put("tags", nbtList);
		}

		return nbt;
	}

	public Tag toNbt(CompoundTag nbt) {
		return toNbt(nbt, false);
	}

	public void processFlags(Multimap<String, String> flags) {
		// TODO: discuss some flag standardization
		Collection<String> reference = flags.get("reference");
		if (reference.stream().findFirst().map(string -> string.equals("local") || string.equals("global")).orElse(false)) {
			resourceKey = flags.get("resource_key").stream().findFirst().orElse(null);
		}
	}

	public abstract Pocket prepareAndPlacePocket(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder);

	public abstract PocketGeneratorType<? extends PocketGenerator> getType();

	public abstract String getKey();

	@Override
	public double getWeight(PocketGenerationContext parameters) {
		return this.weightEquation.apply(parameters.toVariableMap(new HashMap<>()));
	}

	public boolean isSetupLoot() {
		return setupLoot != null && setupLoot;
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

	public void setup(Pocket pocket, RiftManager manager, PocketGenerationContext parameters, boolean setupLootTables) {
		ServerLevel world = parameters.world();

		if (!(pocket instanceof LazyGenerationPocket)) { // should not iterate over that which does not exist & area may be massive, getBlockEntities() might force generation
			if (setupLootTables) // temp
				pocket.getBlockEntities().forEach((blockPos, blockEntity) -> {
					if (/*setupLootTables &&*/ blockEntity instanceof Container) { // comment in if needed
						Container inventory = (Container) blockEntity;
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
		if (builderNbt == null){
			return Pocket.builder()
					.expand(getSize(parameters));
		}
		AbstractPocket.AbstractPocketBuilder<?, ?> abstractBuilder = AbstractPocket.deserializeBuilder(builderNbt);
		if (! (abstractBuilder instanceof Pocket.PocketBuilder)) {
			return Pocket.builder()
					.expand(getSize(parameters));
		}
		Pocket.PocketBuilder<?, ?> builder = (Pocket.PocketBuilder<?, ?>) abstractBuilder;
		return builder.expand(getSize(parameters));
	}

	public abstract Vec3i getSize(PocketGenerationContext parameters);

	public interface PocketGeneratorType<T extends PocketGenerator> {
		PocketGeneratorType<SchematicGenerator> SCHEMATIC = register(DimensionalDoors.resource(SchematicGenerator.KEY), SchematicGenerator::new);
		PocketGeneratorType<ChunkGenerator> CHUNK = register(DimensionalDoors.resource(ChunkGenerator.KEY), ChunkGenerator::new);
		PocketGeneratorType<VoidGenerator> VOID = register(DimensionalDoors.resource(VoidGenerator.KEY), VoidGenerator::new);

		PocketGenerator fromNbt(CompoundTag nbt, ResourceManager manager);

		CompoundTag toNbt(CompoundTag nbt);

		static void register() {
			DimensionalDoors.apiSubscribers.forEach(d -> d.registerPocketGeneratorTypes(REGISTRY));
		}

		static <U extends PocketGenerator> PocketGeneratorType<U> register(ResourceLocation id, Supplier<U> constructor) {
			return Registry.register(REGISTRY, id, new PocketGeneratorType<U>() {
				@Override
				public PocketGenerator fromNbt(CompoundTag nbt, ResourceManager manager) {
					return constructor.get().fromNbt(nbt, manager);
				}

				@Override
				public CompoundTag toNbt(CompoundTag nbt) {
					nbt.putString("type", id.toString());
					return nbt;
				}
			});

		}
	}
}
