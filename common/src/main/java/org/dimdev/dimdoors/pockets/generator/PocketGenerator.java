package org.dimdev.dimdoors.pockets.generator;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.*;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.limlib.api.util.Weighted;
import org.dimdev.limlib.api.util.math.Equation;
import org.dimdev.dimdoors.pockets.PocketCreator;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.TemplateUtils;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.modifier.RiftManager;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.PocketImpl;

import java.util.*;

public abstract class PocketGenerator<T extends PocketGenerator<T>> implements Weighted<PocketGenerationContext>, PocketCreator {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<PocketGenerator<?>> CODEC = PocketGeneratorType.CODEC.dispatch(PocketGenerator::type, PocketGeneratorType::codec);

    public static <T extends PocketGenerator<T>> Products.P5<RecordCodecBuilder.Mu<T>, Optional<AbstractPocket.AbstractPocketBuilder<?, ?>>, Equation, Optional<Boolean>, List<Holder<Modifier>>, List<String>> commonFields(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                Pocket.PocketBuilder.CODEC.optionalFieldOf("builder").forGetter(a -> Optional.ofNullable(a.builder)),
                Equation.CODEC.optionalFieldOf("weight", Equation.FIVE).forGetter(a -> a.weight),
                Codec.BOOL.optionalFieldOf("setup_loot").forGetter(a -> Optional.ofNullable(a.setupLoot)),
                Modifier.HOLDER_CODEC.listOf().fieldOf("modifiers").forGetter(a -> a.modifiers),
                Codec.STRING.listOf().optionalFieldOf("tags", List.of()).forGetter(a -> a.tags)
        );
    }


    protected final AbstractPocket.AbstractPocketBuilder<?, ?> builder;
    protected final Equation weight;
    protected final List<Holder<Modifier>> modifiers;
    protected final Boolean setupLoot;
    protected final List<String> tags;


    protected PocketGenerator(Optional<AbstractPocket.AbstractPocketBuilder<?, ?>> builder, Equation weight, Optional<Boolean> setupLoot, List<Holder<Modifier>> modifiers, List<String> tags) {
        this.builder = builder.orElse(null);
        this.weight = weight;
        this.setupLoot = setupLoot.orElse(null);
        this.modifiers = modifiers;
        this.tags = tags;
    }

    public abstract Pocket<?, ?> prepareAndPlacePocket(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder);

    public Pocket<?, ?> prepareAndPlacePocket(PocketGenerationContext parameters) {
        return prepareAndPlacePocket(parameters, setupLoot);
    }

    public Pocket<?, ?> prepareAndPlacePocket(PocketGenerationContext parameters, Boolean setupLoot) {

        Pocket.PocketBuilder<?, ?> builder = pocketBuilder(parameters)
                .virtualLocation(parameters.sourceVirtualLocation()); // TODO: virtualLocation thing still makes little sense

        this.applyModifiers(parameters, builder);

        Pocket<?, ?> pocket = prepareAndPlacePocket(parameters, builder);

        RiftManager manager = getRiftManager(pocket);

        this.applyModifiers(parameters, manager);

        setup(pocket, manager, parameters, setupLoot != null ? setupLoot : false);

        return pocket;
    }

    public abstract PocketGeneratorType<T> type();

    @Override
    public double getWeight(PocketGenerationContext parameters) {
        return this.weight.apply(parameters.toVariableMap(new HashMap<>()));
    }

    public boolean isSetupLoot() {
        return setupLoot != null && setupLoot;
    }


    public void applyModifiers(PocketGenerationContext parameters, RiftManager manager) {
        for (Holder<Modifier> modifier : modifiers) {
            modifier.value().apply(parameters, manager);
        }
    }

    public void applyModifiers(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
        for (Holder<Modifier> modifier : modifiers) {
            modifier.value().apply(parameters, builder);
        }
    }

    public void setup(Pocket<?, ?> pocket, RiftManager manager, PocketGenerationContext parameters, boolean setupLootTables) {
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

        manager.getRifts().forEach(rift -> rift.getDestination().setLocation(Location.ofWorld(world, rift.getBlockPos())));
        TemplateUtils.registerRifts(manager.getRifts(), parameters.linkTo(), parameters.linkProperties(), pocket);
    }

    public RiftManager getRiftManager(Pocket<?, ?> pocket) {
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
        if (builder == null) {
            return PocketImpl.builder()
                    .expand(getSize(parameters));
        }
        AbstractPocket.AbstractPocketBuilder<?, ?> abstractBuilder = builder.copy();

        if (abstractBuilder instanceof Pocket.PocketBuilder<?, ?> builder) {
            return builder.expand(getSize(parameters));
        }
        return PocketImpl.builder().expand(getSize(parameters));
    }

    public abstract Vec3i getSize(PocketGenerationContext parameters);

    public record PocketGeneratorType<T extends PocketGenerator<T>>(MapCodec<T> codec) {
        public static final Codec<PocketGeneratorType<?>> CODEC = ModRegistries.POCKET_GENERATOR_TYPE.byNameCodec();

        public static final PocketGeneratorType<SchematicGenerator> SCHEMATIC = register(SchematicGenerator.KEY, SchematicGenerator.CODEC);
        public static final PocketGeneratorType<VoidGenerator> VOID = register(VoidGenerator.KEY, VoidGenerator.CODEC);

        public static void register() {
        }

        static <U extends PocketGenerator<U>> PocketGeneratorType<U> register(String id, MapCodec<U> codec) {
            return DimensionalDoors.getSided().register(ModRegistryKeys.POCKET_GENERATOR_TYPE, id, new PocketGeneratorType<>(codec));
        }
    }
}
