package org.dimdev.dimdoors.pockets.generator;

import com.bedrockk.molang.Expression;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.ModRegistries;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.Weighted;
import org.dimdev.dimdoors.pockets.PocketCreator;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.TemplateUtils;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.modifier.RiftManager;
import org.dimdev.dimdoors.util.CodecUtils;
import org.dimdev.dimdoors.util.MolangUtils;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocketBuilder;
import org.dimdev.dimdoors.world.pocket.type.PocketBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class PocketGenerator implements Weighted<PocketGenerationContext>, PocketCreator {

    public static <T extends PocketGenerator> Products.P5<RecordCodecBuilder.Mu<T>, PocketBuilder, Expression, HolderSet<Modifier>, Boolean, List<String>>commonFields(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                PocketBuilder.CODEC.optionalFieldOf("builder", new PocketBuilder(List.of())).forGetter(a -> a.builder),
                MolangUtils.CODEC.optionalFieldOf("weight", MolangUtils.FIVE).forGetter(a -> a.weight),
                Modifier.LIST_CODEC.optionalFieldOf("modifiers", HolderSet.empty()).forGetter(a -> a.modifiers),
                Codec.BOOL.optionalFieldOf("setup_loot", false).forGetter(a -> a.setupLoot),
                Codec.STRING.listOf().optionalFieldOf("tags", List.of()).forGetter(a -> a.tags)
        );
    }

    public static final Codec<PocketGenerator> CODEC = PocketGeneratorType.CODEC.dispatch(PocketGenerator::getType, PocketGeneratorType::codec);
    public static final Codec<Holder<PocketGenerator>> HOLDER_CODEC = RegistryFileCodec.create(ModRegistries.POCKET_GENERATOR, CODEC);

	private static final Logger LOGGER = LogManager.getLogger();

    protected PocketBuilder builder;
    protected Expression weight;
    protected HolderSet<Modifier> modifiers;
    protected Boolean setupLoot;
    protected List<String> tags;

	public PocketGenerator(PocketBuilder builder, Expression weight, List<Modifier> modifiers, Boolean setupLoot, List<String> tags) {
        this.builder = builder;
        this.weight = weight;
        this.modifiers = modifiers;
        this.setupLoot = setupLoot;
        this.tags = tags;
    }

    public abstract Pocket prepareAndPlacePocket(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder);

    public UUID prepareAndPlacePocket(PocketGenerationContext parameters) {
        return prepareAndPlacePocket(parameters, setupLoot);
    }

    public UUID prepareAndPlacePocket(PocketGenerationContext parameters, Boolean setupLoot) {

//        Pocket.PocketBuilder<?, ?> builder = pocketBuilder(parameters)
//                .virtualLocation(parameters.sourceVirtualLocation()); // TODO: virtualLocation thing still makes little sense
//
//        this.applyModifiers(parameters, builder);
//
//        Pocket pocket = prepareAndPlacePocket(parameters, builder);
//
//        RiftManager manager = getRiftManager(pocket);
//
//        this.applyModifiers(parameters, manager);
//
//        setup(pocket, manager, parameters, setupLoot != null ? setupLoot : false);

        return null;
    }

	public abstract PocketGeneratorType<? extends PocketGenerator> getType();

	public abstract String getKey();

	@Override
	public double getWeight(PocketGenerationContext parameters) {
		return MolangUtils.evaulateDouble(this.weight, parameters.toVariableMap(new HashMap<>()));
	}

	public boolean isSetupLoot() {
		return setupLoot != null && setupLoot;
	}



	public void applyModifiers(PocketGenerationContext parameters, RiftManager manager) {
		for (var modifier : modifiers) {
			modifier.value().apply(parameters, manager);
		}
	}

	public void applyModifiers(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
		for (Modifier modifier : modifierList) {
			modifier.apply(parameters, builder);
		}
	}

	public void setup(Pocket pocket, RiftManager manager, PocketGenerationContext parameters, boolean setupLootTables) {
        ServerLevel world = parameters.world();

        if (setupLootTables) // temp
            pocket.getBlockEntities().forEach((blockPos, blockEntity) -> {
                if (blockEntity instanceof Container inventory) { // comment in if needed
                    if (inventory.isEmpty()) {
                        if (blockEntity instanceof RandomizableContainerBlockEntity randomizableContainerBlock) {
                            TemplateUtils.setupLootTable(world, randomizableContainerBlock, LOGGER);
                        }
                    }
                }
            });

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

	public abstract Vec3i getSize(PocketGenerationContext parameters);

	public record PocketGeneratorType<T extends PocketGenerator>(MapCodec<T> codec) {
        public static final Registrar<PocketGeneratorType<? extends PocketGenerator>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<PocketGeneratorType<? extends PocketGenerator>>builder(DimensionalDoors.id("pocket_generator_type")).build();
        public static final Codec<PocketGeneratorType<?>> CODEC = CodecUtils.registarCodec(REGISTRY);

		public static final RegistrySupplier<PocketGeneratorType<SchematicGenerator>> SCHEMATIC = register(DimensionalDoors.id(SchematicGenerator.KEY), SchematicGenerator.CODEC);
        public static final RegistrySupplier<PocketGeneratorType<VoidGenerator>> VOID = register(DimensionalDoors.id(VoidGenerator.KEY), VoidGenerator.CODEC);

        public static void register() {}

		static <U extends PocketGenerator> RegistrySupplier<PocketGeneratorType<U>> register(ResourceLocation id, MapCodec<U> codec) {
			return REGISTRY.register(id, () -> new PocketGeneratorType<>(codec));

		}
	}
}