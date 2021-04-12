package org.dimdev.dimdoors.api.util;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.math.Equation;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class TagEquations {
	private static final Logger LOGGER = LogManager.getLogger();

	public static NbtCompound solveCompoundTagEquations(NbtCompound tag, Map<String, Double> variableMap) {
		NbtCompound solved = new NbtCompound();
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
				solved.put(key, solveListTagEquations((NbtList) tag.get(key), variableMap));
			} else {
				solved.put(key, tag.get(key));
			}
		}
		return solved;
	}

	public static NbtList solveListTagEquations(NbtList listTag, Map<String, Double> variableMap) {
		NbtList solved = new NbtList();
		for (NbtElement tag : listTag) {
			if (tag.getType() == NbtType.LIST) {
				solved.add(solveListTagEquations((NbtList) tag, variableMap));
			} else if (tag.getType() == NbtType.COMPOUND) {
				solved.add(solveCompoundTagEquations((NbtCompound) tag, variableMap));
			} else {
				solved.add(tag);
			}
		}
		return solved;
	}
}
