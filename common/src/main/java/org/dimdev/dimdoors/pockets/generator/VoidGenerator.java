package org.dimdev.dimdoors.pockets.generator;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VoidGenerator extends PocketGenerator<VoidGenerator> {
    public static final MapCodec<VoidGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
            .and(Equation.CODEC.fieldOf("height").<VoidGenerator>forGetter(a -> a.height))
            .and(Equation.CODEC.fieldOf("width").<VoidGenerator>forGetter(a -> a.width))
            .and(Equation.CODEC.fieldOf("length").<VoidGenerator>forGetter(a -> a.length))
            .apply(instance, VoidGenerator::new));

    public static final String KEY = "void";
    private final Equation height;
    private final Equation width;
    private final Equation length;

    public VoidGenerator(Optional<AbstractPocket.AbstractPocketBuilder<?, ?>> builder, Equation weight, Optional<Boolean> setupLoot, List<Holder<Modifier>> modifiers, List<String> tags, Equation height, Equation width, Equation length) {
        super(builder, weight, setupLoot, modifiers, tags);
        this.height = height;
        this.width = width;
        this.length = length;
    }

    @Override
    public Pocket<?, ?> prepareAndPlacePocket(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
        Pocket<?, ?> pocket = DimensionalRegistry.createPocket(parameters.world().dimension(), builder);
        Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());
        pocket.setSize((int) width.apply(variableMap), (int) height.apply(variableMap), (int) length.apply(variableMap));

        return pocket;
    }

    @Override
    public PocketGeneratorType<VoidGenerator> type() {
        return PocketGeneratorType.VOID;
    }

    @Override
    public Vec3i getSize(PocketGenerationContext parameters) {
        Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());
        return new Vec3i((int) width.apply(variableMap), (int) height.apply(variableMap), (int) length.apply(variableMap));
    }
}