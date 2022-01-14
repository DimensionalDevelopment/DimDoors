package org.dimdev.dimdoors.pockets.generator;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.api.util.math.Equation.EquationParseException;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
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
	public Pocket prepareAndPlacePocket(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
		Pocket pocket = DimensionalRegistry.getPocketDirectory(parameters.world().getRegistryKey()).newPocket(builder);
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
	public Vec3i getSize(PocketGenerationContext parameters) {
		Map<String, Double> variableMap = parameters.toVariableMap(new HashMap<>());
		return new Vec3i((int) widthEquation.apply(variableMap), (int) heightEquation.apply(variableMap), (int) lengthEquation.apply(variableMap));
	}

	@Override
	public PocketGenerator fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);

		try {
			width = nbt.getString("width");
			widthEquation = Equation.parse(width);
			height = nbt.getString("height");
			heightEquation = Equation.parse(height);
			length = nbt.getString("length");
			lengthEquation = Equation.parse(length);

		} catch (EquationParseException e) {
			LOGGER.error(e);
		}

		return this;
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);

		nbt.putString("width", width);
		nbt.putString("height", height);
		nbt.putString("length", length);

		return nbt;
	}

	@Override
	public LazyPocketGenerator cloneWithEmptyModifiers(BlockPos originalOrigin) {
		VoidGenerator generator = (VoidGenerator) super.cloneWithEmptyModifiers(originalOrigin);
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
