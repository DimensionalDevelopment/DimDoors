package org.dimdev.dimdoors.pockets.generator;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.pockets.TemplateUtils;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.modifier.RiftManager;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.api.util.Weighted;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.api.util.math.Equation.EquationParseException;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.*;
import java.util.function.Supplier;

public abstract class PocketGenerator implements Weighted<PocketGenerationContext> {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Registry<PocketGeneratorType<? extends PocketGenerator>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<PocketGeneratorType<? extends PocketGenerator>>(RegistryKey.ofRegistry(new Identifier("dimdoors", "pocket_generator_type")), Lifecycle.stable())).buildAndRegister();

	private static final String defaultWeightEquation = "5"; // TODO: make config
	private static final int fallbackWeight = 5; // TODO: make config
	protected final List<Modifier> modifierList = new ArrayList<>();

	private NbtCompound builderNbt;
	protected String weight = defaultWeightEquation;
	protected Equation weightEquation;
	protected Boolean setupLoot;

	private final List<String> tags = new ArrayList<>();

	public PocketGenerator() { }

	public PocketGenerator(String weight) {
		this.weight = weight;
		parseWeight();
	}

	public static PocketGenerator deserialize(NbtCompound nbt) {
		Identifier id = Identifier.tryParse(nbt.getString("type")); // TODO: return some NONE PocketGenerator if type cannot be found or deserialization fails.
		PocketGeneratorType<? extends PocketGenerator> type = REGISTRY.get(id);
		if (type == null) {
			LOGGER.error("Could not deserialize PocketGenerator: " + nbt.toString());
			return null;
		}
		return type.fromNbt(nbt);
	}

	public static NbtCompound serialize(PocketGenerator pocketGenerator) {
		return pocketGenerator.toNbt(new NbtCompound());
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

	public PocketGenerator fromNbt(NbtCompound nbt) {
		if (nbt.contains("builder", NbtType.COMPOUND)) builderNbt = nbt.getCompound("builder");

		this.weight = nbt.contains("weight") ? nbt.getString("weight") : defaultWeightEquation;
		parseWeight();

		if (nbt.contains("setup_loot")) setupLoot = nbt.getBoolean("setup_loot");

		if (nbt.contains("modifiers")) {
			NbtList modifiersNbt = nbt.getList("modifiers", 10);
			for (int i = 0; i < modifiersNbt.size(); i++) {
				modifierList.add(Modifier.deserialize(modifiersNbt.getCompound(i)));
			}
		}

		if (nbt.contains("tags")) {
			NbtList nbtList = nbt.getList("tags", NbtType.STRING);
			for (int i = 0; i < nbtList.size(); i++) {
				tags.add(nbtList.getString(i));
			}
		}
		return this;
	}

	public NbtCompound toNbt(NbtCompound nbt) {
		this.getType().toNbt(nbt);

		if (builderNbt != null) nbt.put("builder", builderNbt);

		if (!weight.equals(defaultWeightEquation)) nbt.putString("weight", weight);

		if (setupLoot != null) nbt.putBoolean("setup_loot", setupLoot);

		NbtList modifiersNbt = new NbtList();
		for (Modifier modifier : modifierList) {
			modifiersNbt.add(modifier.toNbt(new NbtCompound()));
		}
		if (modifiersNbt.size() > 0) nbt.put("modifiers", modifiersNbt);

		if (tags.size() > 0) {
			NbtList nbtList = new NbtList();
			for (String nbtStr : tags) {
				nbtList.add(NbtString.of(nbtStr));
			}
			nbt.put("tags", nbtList);
		}


		return nbt;
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
		ServerWorld world = parameters.world();

		if (!(pocket instanceof LazyGenerationPocket)) { // should not iterate over that which does not exist & area may be massive, getBlockEntities() might force generation
			if (setupLootTables) // temp
				pocket.getBlockEntities().forEach((blockPos, blockEntity) -> {
					if (/*setupLootTables &&*/ blockEntity instanceof Inventory) { // comment in if needed
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
		}
		manager.getRifts().forEach(rift -> rift.getDestination().setLocation(new Location(world, rift.getPos())));
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
		PocketGeneratorType<SchematicGenerator> SCHEMATIC = register(new Identifier("dimdoors", SchematicGenerator.KEY), SchematicGenerator::new);
		PocketGeneratorType<ChunkGenerator> CHUNK = register(new Identifier("dimdoors", ChunkGenerator.KEY), ChunkGenerator::new);
		PocketGeneratorType<VoidGenerator> VOID = register(new Identifier("dimdoors", VoidGenerator.KEY), VoidGenerator::new);

		PocketGenerator fromNbt(NbtCompound nbt);

		NbtCompound toNbt(NbtCompound nbt);

		static void register() {
			DimensionalDoorsInitializer.apiSubscribers.forEach(d -> d.registerPocketGeneratorTypes(REGISTRY));
		}

		static <U extends PocketGenerator> PocketGeneratorType<U> register(Identifier id, Supplier<U> constructor) {
			return Registry.register(REGISTRY, id, new PocketGeneratorType<U>() {
				@Override
				public PocketGenerator fromNbt(NbtCompound nbt) {
					return constructor.get().fromNbt(nbt);
				}

				@Override
				public NbtCompound toNbt(NbtCompound nbt) {
					nbt.putString("type", id.toString());
					return nbt;
				}
			});

		}
	}
}
