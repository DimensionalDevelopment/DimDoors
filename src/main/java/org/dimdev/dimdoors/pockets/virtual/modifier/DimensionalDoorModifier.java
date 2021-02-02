package org.dimdev.dimdoors.pockets.virtual.modifier;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.function.Function;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

import com.flowpowered.math.vector.Vector3i;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.math.StringEquationParser;
import org.dimdev.dimdoors.world.pocket.Pocket;

public class DimensionalDoorModifier implements Modifier {
	StringEquationParser.Equation x;
	StringEquationParser.Equation y;
	StringEquationParser.Equation z;

	@Override
	public Modifier fromTag(CompoundTag tag) {
		try {
			x = StringEquationParser.parse(tag.getString("x"));
			y = StringEquationParser.parse(tag.getString("y"));
			z = StringEquationParser.parse(tag.getString("z"));
		} catch (StringEquationParser.EquationParseException e) {
			e.printStackTrace();
		}
		return this;

	}

	@Override
	public ModifierType<? extends Modifier> getType() {
		return ModifierType.DIMENSIONAL_DOOR_MODIFIER_TYPE;
	}

	@Override
	public String getKey() {
		return "dimensional_doors";
	}

	@Override
	public void apply(Pocket pocket, PocketGenerationParameters parameters) {
		Vector3i size = pocket.getSize();
		Map<String, Double> variables = ImmutableMap.of("width", (double) size.getX(), "height", (double) size.getY(), "length", (double) size.getZ());

		BlockPos pos = new BlockPos(x.apply(variables), y.apply(variables), z.apply(variables));


	}
}
