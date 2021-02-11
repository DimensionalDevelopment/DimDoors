package org.dimdev.dimdoors.util;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.util.math.Equation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import net.fabricmc.fabric.api.util.NbtType;

public class TagEquations {
	private static final Logger LOGGER = LogManager.getLogger();

	public static CompoundTag solveCompoundTagEquations(CompoundTag tag, Map<String, Double> variableMap) {
		CompoundTag solved = new CompoundTag();
		for (String key : tag.getKeys()) {
			if (tag.getType(key) == NbtType.STRING && key.startsWith("equation_")) {
				try {
					double solution = Equation.parse(tag.getString(key)).apply(variableMap);
					key = key.substring(9);
					if (key.startsWith("int_")) {
						key = key.substring(4);
						solved.putInt(key, (int) solution);
					} else if (key.startsWith("boolean_")) {
						key = key.substring(8);
						solved.putBoolean(key, Equation.toBoolean(solution));
					} else if (key.startsWith("double_")) {
						key = key.substring(7);
						solved.putDouble(key, solution);
					} else {
						solved.putDouble(key, solution);
					}
				} catch (Equation.EquationParseException e) {
					LOGGER.error(e);
				}
			} else if (tag.getType(key) == NbtType.COMPOUND) {
				solved.put(key, solveCompoundTagEquations(tag.getCompound(key), variableMap));
			} else if (tag.getType(key) == NbtType.LIST) {
				solved.put(key, solveListTagEquations((ListTag) tag.get(key), variableMap));
			} else {
				solved.put(key, tag.get(key));
			}
		}
		return solved;
	}

	public static ListTag solveListTagEquations(ListTag listTag, Map<String, Double> variableMap) {
		ListTag solved = new ListTag();
		for (Tag tag : listTag) {
			if (tag.getType() == NbtType.LIST) {
				solved.add(solveListTagEquations((ListTag) tag, variableMap));
			} else if (tag.getType() == NbtType.COMPOUND) {
				solved.add(solveCompoundTagEquations((CompoundTag) tag, variableMap));
			} else {
				solved.add(tag);
			}
		}
		return solved;
	}
}
