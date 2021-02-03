package org.dimdev.dimdoors.pockets.generator;

import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.math.StringEquationParser;
import org.dimdev.dimdoors.world.level.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.Pocket;

import java.util.HashMap;
import java.util.Map;

public class VoidGenerator extends PocketGenerator {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "void";
	private String width;
	private StringEquationParser.Equation heightEquation;
	private String height;
	private StringEquationParser.Equation widthEquation;
	private String length;
	private StringEquationParser.Equation lengthEquation;
	private String offsetX;
	private StringEquationParser.Equation offsetXEquation;
	private String offsetY;
	private StringEquationParser.Equation offsetYEquation;
	private String offsetZ;
	private StringEquationParser.Equation offsetZEquation;

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
			widthEquation = StringEquationParser.parse(width);
			height = tag.getString("height");
			heightEquation = StringEquationParser.parse(height);
			length = tag.getString("length");
			lengthEquation = StringEquationParser.parse(length);

			offsetX = tag.contains("offset_x") ? tag.getString("offset_x") : "0";
			offsetXEquation = StringEquationParser.parse(offsetX);
			offsetY = tag.contains("offset_y") ? tag.getString("offset_y") : "0";
			offsetYEquation = StringEquationParser.parse(offsetY);
			offsetZ = tag.contains("offset_z") ? tag.getString("offset_z") : "0";
			offsetZEquation = StringEquationParser.parse(offsetZ);
		} catch (StringEquationParser.EquationParseException e) {
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
