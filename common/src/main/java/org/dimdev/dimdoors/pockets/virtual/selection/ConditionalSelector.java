package org.dimdev.dimdoors.pockets.virtual.selection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.limlib.api.util.math.Equation;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.HashMap;
import java.util.List;

public class ConditionalSelector implements ImplementedVirtualPocket<ConditionalSelector> {
    public static final MapCodec<ConditionalSelector> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ConditionalPocket.CODEC.listOf().fieldOf("pockets").forGetter(ConditionalSelector::getPocketMap)
    ).apply(instance, ConditionalSelector::new));

    public static final String KEY = "conditional";

    private final List<ConditionalPocket> pockets;

    public ConditionalSelector(List<ConditionalPocket> pockets) {
        this.pockets = pockets;
    }

    public List<ConditionalPocket> getPocketMap() {
        return pockets;
    }

    @Override
    public Pocket<?, ?> prepareAndPlacePocket(PocketGenerationContext parameters, Boolean setupLoot) {
        return getNextPocket(parameters).prepareAndPlacePocket(parameters, setupLoot);
    }

    @Override
    public Pocket<?, ?> prepareAndPlacePocket(PocketGenerationContext parameters) {
        return getNextPocket(parameters).prepareAndPlacePocket(parameters);
    }

    @Override
    public PocketGeneratorReference<?> getNextPocketGeneratorReference(PocketGenerationContext parameters) {
        return getNextPocket(parameters).getNextPocketGeneratorReference(parameters);
    }

    @Override
    public PocketGeneratorReference<?> peekNextPocketGeneratorReference(PocketGenerationContext parameters) {
        return getNextPocket(parameters).peekNextPocketGeneratorReference(parameters);
    }

    @Override
    public VirtualPocketType<ConditionalSelector> getType() {
        return VirtualPocketType.CONDITIONAL_SELECTOR;
    }

    @Override
    public double getWeight(PocketGenerationContext parameters) {
        return getNextPocket(parameters).getWeight(parameters);
    }

    private VirtualPocket getNextPocket(PocketGenerationContext parameters) {
        var map = parameters.toVariableMap(new HashMap<>());
        return pockets.stream().filter(entry -> entry.condition().asBoolean(map)).findFirst().map(ConditionalPocket::pocket).orElse(NoneVirtualPocket.NONE);

    }

    public record ConditionalPocket(Equation condition, VirtualPocket pocket) {
        public static final Codec<ConditionalPocket> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(
                        Equation.CODEC.fieldOf("condition").forGetter(ConditionalPocket::condition),
                        VirtualPocket.CODEC.fieldOf("pocket").forGetter(ConditionalPocket::pocket))
                .apply(instance, ConditionalPocket::new)
        );
    }
}