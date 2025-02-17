package org.dimdev.dimdoors.pockets.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.HashMap;
import java.util.Map;

public class OffsetModifier extends AbstractModifier {
	public static final MapCodec<OffsetModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Equation.CODEC.optionalFieldOf("offset_x", Equation.ZERO).forGetter(a -> a.offsetX),
			Equation.CODEC.optionalFieldOf("offset_y", Equation.ZERO).forGetter(a -> a.offsetY),
			Equation.CODEC.optionalFieldOf("offset_z", Equation.ZERO).forGetter(a -> a.offsetZ)
	).apply(instance, OffsetModifier::new));
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "offset";

	private Equation offsetX;
	private Equation offsetY;
	private Equation offsetZ;

	public OffsetModifier(Equation offsetX, Equation offsetY, Equation offsetZ) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

	@Override
	public CompoundTag toNbtInternal(CompoundTag nbt, boolean allowReference) {
		super.toNbtInternal(nbt, allowReference);

		if (!offsetX.equals("0")) nbt.putString("offset_x", offsetX);
		if (!offsetY.equals("0")) nbt.putString("offset_y", offsetY);
		if (!offsetZ.equals("0")) nbt.putString("offset_z", offsetZ);

		return nbt;
	}

	@Override
	public ModifierType<? extends Modifier> getType() {
		return ModifierType.OFFSET_MODIFIER_TYPE.get();
	}

    @Override
	public void apply(PocketGenerationContext parameters, RiftManager manager) {

	}

	@Override
	public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
		Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());
		builder.offsetOrigin(new Vec3i((int) offsetXEquation.apply(variableMap), (int) offsetYEquation.apply(variableMap), (int) offsetZEquation.apply(variableMap)));
	}
}
