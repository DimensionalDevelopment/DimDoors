package org.dimdev.dimdoors.api.util;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.math.Equation;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class NbtEquations {
	private static final Logger LOGGER = LogManager.getLogger();

	public static NbtCompound solveNbtCompoundEquations(NbtCompound nbt, Map<String, Double> variableMap) {
		NbtCompound solved = new NbtCompound();
		for (String key : nbt.getKeys()) {
			if (nbt.getType(key) == NbtType.STRING && key.startsWith("equation_")) {
				try {
					double solution = Equation.parse(nbt.getString(key)).apply(variableMap);
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
			} else if (nbt.getType(key) == NbtType.COMPOUND) {
				solved.put(key, solveNbtCompoundEquations(nbt.getCompound(key), variableMap));
			} else if (nbt.getType(key) == NbtType.LIST) {
				solved.put(key, solveNbtListEquations((NbtList) nbt.get(key), variableMap));
			} else {
				solved.put(key, nbt.get(key));
			}
		}
		return solved;
	}

	public static NbtList solveNbtListEquations(NbtList nbtList, Map<String, Double> variableMap) {
		NbtList solved = new NbtList();
		for (NbtElement nbt : nbtList) {
			if (nbt.getType() == NbtType.LIST) {
				solved.add(solveNbtListEquations((NbtList) nbt, variableMap));
			} else if (nbt.getType() == NbtType.COMPOUND) {
				solved.add(solveNbtCompoundEquations((NbtCompound) nbt, variableMap));
			} else {
				solved.add(nbt);
			}
		}
		return solved;
	}
}
