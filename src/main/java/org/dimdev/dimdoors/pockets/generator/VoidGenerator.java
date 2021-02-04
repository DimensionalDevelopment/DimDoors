package org.dimdev.dimdoors.pockets.generator;

import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.math.Equation;
import org.dimdev.dimdoors.util.math.Equation.EquationParseException;
import org.dimdev.dimdoors.world.level.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.Pocket;

import java.util.HashMap;
import java.util.Map;

public class VoidGenerator extends PocketGenerator {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "void";
	private String width;
	private Equation heightEquation;
	private String height;
	private Equation widthEquation;
	private String length;
	private Equation lengthEquation;
	private String offsetX;
	private Equation offsetXEquation;
	private String offsetY;
	private Equation offsetYEquation;
	private String offsetZ;
	private Equation offsetZEquation;

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationParameters parameters) {
		Pocket pocket = DimensionalRegistry.getPocketDirectory(parameters.getWorld().getRegistryKey()).newPocket();
		Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());
		pocket.setSize((int) widthEquation.apply(variableMap), (int) heightEquation.apply(variableMap), (int) lengthEquation.apply(variableMap));
		pocket.offsetOrigin((int) offsetXEquation.apply(variableMap), (int) offsetYEquation.apply(variableMap), (int) offsetZEquation.apply(variableMap));

		return pocket;
	}

	@Override
	public PocketGeneratorType<? extends PocketGenerator> getType() {
		return PocketGeneratorType.VOID;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public PocketGenerator fromTag(CompoundTag tag) {
		super.fromTag(tag);

		try {
			width = tag.getString("width");
			widthEquation = Equation.parse(width);
			height = tag.getString("height");
			heightEquation = Equation.parse(height);
			length = tag.getString("length");
			lengthEquation = Equation.parse(length);

			offsetX = tag.contains("offset_x") ? tag.getString("offset_x") : "0";
			offsetXEquation = Equation.parse(offsetX);
			offsetY = tag.contains("offset_y") ? tag.getString("offset_y") : "0";
			offsetYEquation = Equation.parse(offsetY);
			offsetZ = tag.contains("offset_z") ? tag.getString("offset_z") : "0";
			offsetZEquation = Equation.parse(offsetZ);
		} catch (EquationParseException e) {
			LOGGER.error(e);
		}

		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		tag.putString("width", width);
		tag.putString("height", height);
		tag.putString("length", length);

		if (!offsetX.equals("0")) tag.putString("offset_x", offsetX);
		if (!offsetY.equals("0")) tag.putString("offset_y", offsetY);
		if (!offsetZ.equals("0")) tag.putString("offset_z", offsetZ);

		return tag;
	}
}
