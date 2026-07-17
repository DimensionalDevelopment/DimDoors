package org.dimdev.dimdoors.pockets.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.HashMap;
import java.util.Map;

public record OffsetModifier(Equation offsetX, Equation offsetY, Equation offsetZ) implements Modifier {
    public static final MapCodec<OffsetModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Equation.CODEC.optionalFieldOf("offsetX", Equation.ZERO).forGetter(OffsetModifier::offsetX),
            Equation.CODEC.optionalFieldOf("offsetY", Equation.ZERO).forGetter(OffsetModifier::offsetY),
            Equation.CODEC.optionalFieldOf("offsetZ", Equation.ZERO).forGetter(OffsetModifier::offsetZ)
    ).apply(instance, OffsetModifier::new));

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String KEY = "offset";

    @Override
    public ModifierType<? extends Modifier> getType() {
        return ModifierType.OFFSET_MODIFIER_TYPE;
    }

    @Override
    public void apply(PocketGenerationContext parameters, RiftManager manager) {

    }

    @Override
    public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
        Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());
        builder.offsetOrigin(new Vec3i((int) offsetX.apply(variableMap), (int) offsetY.apply(variableMap), (int) offsetZ.apply(variableMap)));
    }
}