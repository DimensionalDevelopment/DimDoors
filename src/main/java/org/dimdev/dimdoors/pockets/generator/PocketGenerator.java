package org.dimdev.dimdoors.pockets.generator;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.pockets.TemplateUtils;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.modifier.RiftManager;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.Weighted;
import org.dimdev.dimdoors.util.math.Equation;
import org.dimdev.dimdoors.util.math.Equation.EquationParseException;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.*;
import java.util.function.Supplier;

public abstract class PocketGenerator implements Weighted<PocketGenerationParameters> {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Registry<PocketGeneratorType<? extends PocketGenerator>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<PocketGeneratorType<? extends PocketGenerator>>(RegistryKey.ofRegistry(new Identifier("dimdoors", "pocket_generator_type")), Lifecycle.stable())).buildAndRegister();

	private static final String defaultWeightEquation = "5"; // TODO: make config
	private static final int fallbackWeight = 5; // TODO: make config
	private final List<Modifier> modifierList = new ArrayList<>();

	private CompoundTag builderTag;
	protected String weight;
	protected Equation weightEquation;
	protected Boolean setupLoot;

	private final List<String> tags = new ArrayList<>();

	public PocketGenerator() { }

	public PocketGenerator(String weight) {
		this.weight = weight;
		parseWeight();
	}

	public static PocketGenerator deserialize(CompoundTag tag) {
		Identifier id = Identifier.tryParse(tag.getString("type")); // TODO: return some NONE PocketGenerator if type cannot be found or deserialization fails.
		PocketGeneratorType<? extends PocketGenerator> type = REGISTRY.get(id);
		if (type == null) {
			LOGGER.error("Could not deserialize PocketGenerator: " + tag.toString());
			return null;
		}
		return type.fromTag(tag);
	}

	public static CompoundTag serialize(PocketGenerator pocketGenerator) {
		return pocketGenerator.toTag(new CompoundTag());
	}

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

	public PocketGenerator fromTag(CompoundTag tag) {
		if (tag.contains("builder", NbtType.COMPOUND)) builderTag = tag.getCompound("builder");

		this.weight = tag.contains("weight") ? tag.getString("weight") : defaultWeightEquation;
		parseWeight();

		if (tag.contains("setup_loot")) setupLoot = tag.getBoolean("setup_loot");

		if (tag.contains("modifiers")) {
			ListTag modifiersTag = tag.getList("modifiers", 10);
			for (int i = 0; i < modifiersTag.size(); i++) {
				modifierList.add(Modifier.deserialize(modifiersTag.getCompound(i)));
			}
		}

		if (tag.contains("tags")) {
			ListTag listTag = tag.getList("tags", NbtType.STRING);
			for (int i = 0; i < listTag.size(); i++) {
				tags.add(listTag.getString(i));
			}
		}
		return this;
	}

	public CompoundTag toTag(CompoundTag tag) {
		this.getType().toTag(tag);

		if (builderTag != null) tag.put("builder", builderTag);

		if (!weight.equals("5")) tag.putString("weight", weight);

		if (setupLoot != null) tag.putBoolean("setup_loot", setupLoot);

		ListTag modifiersTag = new ListTag();
		for (Modifier modifier : modifierList) {
			modifiersTag.add(modifier.toTag(new CompoundTag()));
		}
		if (modifiersTag.size() > 0) tag.put("modifiers", modifiersTag);

		if (tags.size() > 0) {
			ListTag listTag = new ListTag();
			for (String tagString : tags) {
				listTag.add(StringTag.of(tagString));
			}
			tag.put("tags", listTag);
		}


		return tag;
	}

	public abstract Pocket prepareAndPlacePocket(PocketGenerationParameters parameters, Pocket.PocketBuilder<?, ?> builder);

	public abstract PocketGeneratorType<? extends PocketGenerator> getType();

	public abstract String getKey();

	@Override
	public double getWeight(PocketGenerationParameters parameters) {
		return this.weightEquation.apply(parameters.toVariableMap(new HashMap<>()));
	}

	public Boolean getSetupLoot() {
		return setupLoot;
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

	public void setup(Pocket pocket, PocketGenerationParameters parameters, boolean setupLootTables) {
		ServerWorld world = DimensionalDoorsInitializer.getWorld(pocket.getWorld());
		List<RiftBlockEntity> rifts = new ArrayList<>();

		pocket.getBlockEntities().forEach((blockPos, blockEntity) -> {
			if (blockEntity instanceof RiftBlockEntity) {
				LOGGER.debug("Rift found in pocket at " + blockPos);
				RiftBlockEntity rift = (RiftBlockEntity) blockEntity;
				rift.getDestination().setLocation(new Location((ServerWorld) Objects.requireNonNull(rift.getWorld()), rift.getPos()));
				rifts.add(rift);
			} else if (setupLootTables && blockEntity instanceof Inventory) {
				Inventory inventory = (Inventory) blockEntity;
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
		TemplateUtils.registerRifts(rifts, parameters.getLinkTo(), parameters.getLinkProperties(), pocket);
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

	public Pocket.PocketBuilder<?, ?> pocketBuilder(PocketGenerationParameters parameters) { // TODO: PocketBuilder from json
		if (builderTag == null){
			return Pocket.builder()
					.expand(getSize(parameters));
		}
		AbstractPocket.AbstractPocketBuilder<?, ?> abstractBuilder = AbstractPocket.deserializeBuilder(builderTag);
		if (! (abstractBuilder instanceof Pocket.PocketBuilder)) {
			return Pocket.builder()
					.expand(getSize(parameters));
		}
		Pocket.PocketBuilder<?, ?> builder = (Pocket.PocketBuilder<?, ?>) abstractBuilder;
		return builder.expand(getSize(parameters));
	}

	public abstract Vec3i getSize(PocketGenerationParameters parameters);

	public interface PocketGeneratorType<T extends PocketGenerator> {
		PocketGeneratorType<SchematicGenerator> SCHEMATIC = register(new Identifier("dimdoors", SchematicGenerator.KEY), SchematicGenerator::new);
		PocketGeneratorType<ChunkGenerator> CHUNK = register(new Identifier("dimdoors", ChunkGenerator.KEY), ChunkGenerator::new);
		PocketGeneratorType<VoidGenerator> VOID = register(new Identifier("dimdoors", VoidGenerator.KEY), VoidGenerator::new);

		PocketGenerator fromTag(CompoundTag tag);

		CompoundTag toTag(CompoundTag tag);

		static void register() {
		}

		static <U extends PocketGenerator> PocketGeneratorType<U> register(Identifier id, Supplier<U> constructor) {
			return Registry.register(REGISTRY, id, new PocketGeneratorType<U>() {
				@Override
				public PocketGenerator fromTag(CompoundTag tag) {
					return constructor.get().fromTag(tag);
				}

				@Override
				public CompoundTag toTag(CompoundTag tag) {
					tag.putString("type", id.toString());
					return tag;
				}
			});

		}
	}
}
