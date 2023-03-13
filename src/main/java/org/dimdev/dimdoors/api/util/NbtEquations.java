package org.dimdev.dimdoors.api.util;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.dimdev.dimdoors.api.util.math.Equation;

public class NbtEquations {
	private static final Logger LOGGER = LogManager.getLogger();

	public static CompoundTag solveNbtCompoundEquations(CompoundTag nbt, Map<String, Double> variableMap) {
		CompoundTag solved = new CompoundTag();
		for (String key : nbt.getAllKeys()) {
			if (nbt.getTagType(key) == NbtType.STRING && key.startsWith("equation_")) {
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
			} else if (nbt.getTagType(key) == Tag.TAG_COMPOUND) {
				solved.put(key, solveNbtCompoundEquations(nbt.getCompound(key), variableMap));
			} else if (nbt.getTagType(key) == NbtType.LIST) {
				solved.put(key, solveNbtListEquations((ListTag) nbt.get(key), variableMap));
			} else {
				solved.put(key, nbt.get(key));
			}
		}
		return solved;
	}

	public static ListTag solveNbtListEquations(ListTag nbtList, Map<String, Double> variableMap) {
		ListTag solved = new ListTag();
		for (Tag nbt : nbtList) {
			if (nbt.getId() == NbtType.LIST) {
				solved.add(solveNbtListEquations((ListTag) nbt, variableMap));
			} else if (nbt.getId() == Tag.TAG_COMPOUND) {
				solved.add(solveNbtCompoundEquations((CompoundTag) nbt, variableMap));
			} else {
				solved.add(nbt);
			}
		}
		return solved;
	}
}
