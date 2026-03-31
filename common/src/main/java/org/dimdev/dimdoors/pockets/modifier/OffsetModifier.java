package org.dimdev.dimdoors.pockets.modifier;

import com.bedrockk.molang.Expression;
import com.bedrockk.molang.runtime.value.MoValue;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.util.MolangUtils;

import java.util.HashMap;
import java.util.Map;

public record OffsetModifier(Expression offsetX, Expression offsetY, Expression offsetZ) implements Modifier {
    public static final MapCodec<OffsetModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MolangUtils.CODEC.optionalFieldOf("offsetX", MolangUtils.ZERO).forGetter(OffsetModifier::offsetX),
            MolangUtils.CODEC.optionalFieldOf("offsetY", MolangUtils.ZERO).forGetter(OffsetModifier::offsetY),
            MolangUtils.CODEC.optionalFieldOf("offsetZ", MolangUtils.ZERO).forGetter(OffsetModifier::offsetZ)
    ).apply(instance, OffsetModifier::new));

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String KEY = "offset";

    @Override
    public ModifierType<? extends Modifier> getType() {
        return ModifierType.OFFSET_MODIFIER_TYPE.get();
    }

    @Override
    public void apply(PocketGenerationContext parameters, RiftManager manager) {

    }

    @Override
    public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
        Map<String, MoValue> variableMap = parameters.toVariableMap(new HashMap<>());
        builder.offsetOrigin(new Vec3i((int) MolangUtils.evaulateDouble(offsetX, variableMap), (int) MolangUtils.evaulateDouble(offsetY, variableMap), (int) MolangUtils.evaulateDouble(offsetZ, variableMap)));
    }
}