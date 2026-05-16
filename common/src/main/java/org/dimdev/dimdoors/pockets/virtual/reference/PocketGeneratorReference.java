package org.dimdev.dimdoors.pockets.virtual.reference;

import com.google.common.collect.Maps;
import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.RiftManager;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.*;

public abstract class PocketGeneratorReference<T extends PocketGeneratorReference<T>> implements ImplementedVirtualPocket<T> {
    public static <T extends PocketGeneratorReference<T>> Products.P1<RecordCodecBuilder.Mu<T>, Equation> commonFields(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                Equation.CODEC.optionalFieldOf("weight", Equation.FIVE).<T>forGetter(a -> a.weight)
        );
    }

    private static final Logger LOGGER = LogManager.getLogger();

    protected Equation weight;

    public PocketGeneratorReference(Equation weight) {
        this.weight = weight;
    }

    @Override
    public double getWeight(PocketGenerationContext parameters) {
        try {
            return weight != null ? weight.apply(parameters.toVariableMap(Maps.newHashMap())) : peekReferencedPocketGenerator(parameters).value().getWeight(parameters);
        } catch (RuntimeException e) {
            LOGGER.error(this.toString());
            throw new AssertionError(e);
        }
    }

    public Pocket<?, ?> prepareAndPlacePocket(PocketGenerationContext parameters) {
        return prepareAndPlacePocket(parameters, false);
    }

    @Override
    public Pocket<?, ?> prepareAndPlacePocket(PocketGenerationContext parameters, Boolean setupLoot) {
        PocketGenerator<?> generator = getReferencedPocketGenerator(parameters).value();

        Pocket.PocketBuilder<?, ?> builder = generator.pocketBuilder(parameters)
                .virtualLocation(parameters.sourceVirtualLocation()); // TODO: virtualLocation thing still makes little sense
        generator.applyModifiers(parameters, builder);

        Pocket<?, ?> pocket = generator.prepareAndPlacePocket(parameters, builder);
        if (pocket == null) {
            return null;
        }

        RiftManager manager = generator.getRiftManager(pocket);

        generator.applyModifiers(parameters, manager);


        generator.setup(pocket, manager, parameters, setupLoot != null ? setupLoot : generator.isSetupLoot());

        return pocket;
    }

    @Override
    public PocketGeneratorReference<?> peekNextPocketGeneratorReference(PocketGenerationContext parameters) {
        return this;
    }

    @Override
    public PocketGeneratorReference<?> getNextPocketGeneratorReference(PocketGenerationContext parameters) {
        return this;
    }

    public abstract Holder<PocketGenerator<?>> peekReferencedPocketGenerator(PocketGenerationContext parameters);

    public abstract Holder<PocketGenerator<?>> getReferencedPocketGenerator(PocketGenerationContext parameters);

    @Override
    public abstract String toString();

}