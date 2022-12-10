package org.dimdev.dimdoors.api.util.math;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

		//expected = 1;
		equation = (Math.random() - 1d) + "? 1 : 0";
		assertEquals(expected, Equation.parse(equation).apply(empty), 0);
	}

	@Test
	public void parseAndAsString() throws Equation.EquationParseException {

		String equation = "20*4-13/5^1^3*max(1,2,3,4,5)";
		assertEquals(equation, Equation.parse(equation).asString());

		equation = "9/1?1:max(0,0)";
		assertEquals(equation, Equation.parse(equation).asString());

		equation = "someVariable";
		assertEquals(equation, Equation.parse(equation).asString());

		equation = "5*test?one:two";
		assertEquals(equation, Equation.parse(equation).asString());
	}
}
