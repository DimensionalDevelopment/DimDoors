package org.dimdev.dimdoors.pockets.modifier;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.HashMap;
import java.util.Map;

public class OffsetModifier implements Modifier {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "offset";

	private String offsetX;
	private Equation offsetXEquation;
	private String offsetY;
	private Equation offsetYEquation;
	private String offsetZ;
	private Equation offsetZEquation;

	@Override
	public Modifier fromTag(NbtCompound tag) {

		try {
			offsetX = tag.contains("offset_x") ? tag.getString("offset_x") : "0";
			offsetXEquation = Equation.parse(offsetX);
			offsetY = tag.contains("offset_y") ? tag.getString("offset_y") : "0";
			offsetYEquation = Equation.parse(offsetY);
			offsetZ = tag.contains("offset_z") ? tag.getString("offset_z") : "0";
			offsetZEquation = Equation.parse(offsetZ);
		} catch (Equation.EquationParseException e) {
			LOGGER.error(e);
		}

		return this;
	}

	@Override
	public NbtCompound toTag(NbtCompound tag) {
		Modifier.super.toTag(tag);

		if (!offsetX.equals("0")) tag.putString("offset_x", offsetX);
		if (!offsetY.equals("0")) tag.putString("offset_y", offsetY);
		if (!offsetZ.equals("0")) tag.putString("offset_z", offsetZ);

		return tag;
	}

	@Override
	public ModifierType<? extends Modifier> getType() {
		return ModifierType.OFFSET_MODIFIER_TYPE;
	}

	@Override
	public String getKey() {
		return KEY;
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
