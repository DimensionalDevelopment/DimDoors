package org.dimdev.dimdoors.pockets.generator;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.math.Equation;
import org.dimdev.dimdoors.util.math.Equation.EquationParseException;
import org.dimdev.dimdoors.world.level.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.HashMap;
import java.util.Map;

public class VoidGenerator extends LazyPocketGenerator {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "void";
	private String width;
	private Equation heightEquation;
	private String height;
	private Equation widthEquation;
	private String length;
	private Equation lengthEquation;

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationParameters parameters, Pocket.PocketBuilder<?, ?> builder) {
		Pocket pocket = DimensionalRegistry.getPocketDirectory(parameters.getWorld().getRegistryKey()).newPocket(builder);
		Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());
		pocket.setSize((int) widthEquation.apply(variableMap), (int) heightEquation.apply(variableMap), (int) lengthEquation.apply(variableMap));

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
	public Vec3i getSize(PocketGenerationParameters parameters) {
		Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());
		return new Vec3i((int) widthEquation.apply(variableMap), (int) heightEquation.apply(variableMap), (int) lengthEquation.apply(variableMap));
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

		return tag;
	}

	@Override
	public LazyPocketGenerator cloneWithEmptyModifiers() {
		VoidGenerator generator = (VoidGenerator) super.cloneWithEmptyModifiers();
		generator.width = width;
		generator.height = height;
		generator.length = length;

		return generator;
	}

	@Override
	public LazyPocketGenerator getNewInstance() {
		return new VoidGenerator();
	}
}
