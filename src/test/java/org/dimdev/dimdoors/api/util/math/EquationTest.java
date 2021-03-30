package org.dimdev.dimdoors.api.util.math;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class EquationTest {

	@Test
	public void parseAndApply() throws Equation.EquationParseException {
		Map<String, Double> empty = new HashMap<>();
		double expected;
		String equation;

		assertThrows(Equation.EquationParseException.class, () -> Equation.parse("."));

		expected = 15d;
		equation = Double.toString(expected);
		assertEquals(expected, Equation.parse(equation).apply(empty), 0);


		expected = 4d;
		equation = "2 + 2";
		assertEquals(expected, Equation.parse(equation).apply(empty), 0);


		expected = 1;
		equation = "1 ? 1 : 0";
		assertEquals(expected, Equation.parse(equation).apply(empty), 0);

		expected = 0;
		equation = "0 ? 1 : 0";
		assertEquals(expected, Equation.parse(equation).apply(empty), 0);

		expected = 1;
		equation = (Math.random() + 1d) + "? 1 : 0";
		assertEquals(expected, Equation.parse(equation).apply(empty), 0);

		expected = 1;
		equation = (Math.random() - 1d) + "? 1 : 0";
		assertEquals(expected, Equation.parse(equation).apply(empty), 0);
	}

	public void parseAndAsBoolean() throws Equation.EquationParseException {
		Map<String, Double> empty = new HashMap<>();


	}
}
