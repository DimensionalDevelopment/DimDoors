package org.dimdev.dimdoors.util.math;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class StringEquationParser {
	private static final Logger LOGGER = LogManager.getLogger();
	private final static List<EquationParser> parseRules = new ArrayList<>();
	static {
		// Parenthesis
		parseRules.add(toParse -> {
			if (!toParse.startsWith("(") || !toParse.endsWith(")")) return Optional.empty();
			return Optional.of(parse(toParse.substring(1, toParse.length() - 1)));
		});

		//+, -
		Map<String, TriFunction<Map<String, Double>, Equation, Equation, Double>> sumOperations = new HashMap<>();
		sumOperations.put("+", (stringDoubleMap, first, second) -> first.apply(stringDoubleMap) + second.apply(stringDoubleMap));
		sumOperations.put("-", (stringDoubleMap, first, second) -> first.apply(stringDoubleMap) - second.apply(stringDoubleMap));
		parseRules.add(new SplitterParser(sumOperations));

		//*, /, %
		Map<String, TriFunction<Map<String, Double>, Equation, Equation, Double>> dotOperations = new HashMap<>();
		dotOperations.put("*", (stringDoubleMap, first, second) -> first.apply(stringDoubleMap) * second.apply(stringDoubleMap));
		dotOperations.put("/", (stringDoubleMap, first, second) -> first.apply(stringDoubleMap) / second.apply(stringDoubleMap));
		dotOperations.put("%", (stringDoubleMap, first, second) -> first.apply(stringDoubleMap) % second.apply(stringDoubleMap));
		parseRules.add(new SplitterParser(dotOperations));

		//x^y
		Map<String, TriFunction<Map<String, Double>, Equation, Equation, Double>> exponentOperations = new HashMap<>();
		exponentOperations.put("^", (stringDoubleMap, first, second) -> Math.pow(first.apply(stringDoubleMap), second.apply(stringDoubleMap)));
		parseRules.add(new SplitterParser(exponentOperations));

		// try to parse as Double
		parseRules.add(toParse -> {
			try {
				Double result = Double.parseDouble(toParse);
				return Optional.of(stringDoubleMap -> result);
			} catch (NumberFormatException e) {
				return Optional.empty();
			}
		});

		//variable replacer
		parseRules.add(new VariableReplacer());
	}



	public static Equation parse(String equationString) throws EquationParseException {
		equationString = equationString.replaceAll("\\s","");
		for (EquationParser parser : parseRules) {
			Optional<Equation> equation = parser.tryParse(equationString);
			if (equation.isPresent()) return equation.get();
		}
		throw new EquationParseException("\"" + equationString + "\" could not be parsed");
	}


	public interface Equation extends Function<Map<String, Double>, Double> { }

	private interface EquationParser {
		Optional<Equation> tryParse(String toParse) throws EquationParseException;
	}

	private static class VariableReplacer implements EquationParser {
		@Override
		public Optional<Equation> tryParse(String toParse) {
			if (!toParse.matches("[a-zA-Z_][a-zA-Z0-9_]*")) return Optional.empty();
			return Optional.of(stringDoubleMap -> {
				if (stringDoubleMap != null && stringDoubleMap.containsKey(toParse)) return stringDoubleMap.get(toParse);
				LOGGER.error("Variable \"" + toParse + "\" was not passed to equation! Returning 0 as fallback.");
				return 0d;
			});
		}
	}

	private static class SplitterParser implements EquationParser {
		private final Map<String, TriFunction<Map<String, Double>, Equation, Equation, Double>> operations;

		public SplitterParser(Map<String, TriFunction<Map<String, Double>, Equation, Equation, Double>> operations) {
			this.operations = operations;
		}

		@Override
		public Optional<Equation> tryParse(String toParse) throws EquationParseException {
			int depth = 0;
			for (int i = toParse.length() - 1; i >= 1 ; i--) {
				String substring = toParse.substring(i);
				if (substring.startsWith(")")) depth++;
				else if (substring.startsWith("(")) depth--;
				for(String symbol : operations.keySet()) {
					if (substring.startsWith(symbol) && depth == 0) {
						final TriFunction<Map<String, Double>, Equation, Equation, Double> operation = operations.get(symbol);
						final Equation first = parse(toParse.substring(0,i));
						final Equation second = parse(toParse.substring(i+1));
						return Optional.of(stringDoubleMap -> operation.apply(stringDoubleMap, first, second));
					}
				}
			}
			return Optional.empty();
		}
	}

	private static class FunctionParser implements EquationParser {
		private final String functionString;
		private final BiFunction<Map<String, Double>, Equation, Double> function;

		public FunctionParser(String functionString, BiFunction<Map<String, Double>, Equation, Double> function) {
			this.functionString = functionString + "(";
			this.function = function;
		}

		@Override
		public Optional<Equation> tryParse(String toParse) throws EquationParseException {
			if (!toParse.startsWith(functionString) || !toParse.endsWith(")")) return Optional.empty();
			final Equation equation = parse(toParse.substring(functionString.length(), toParse.length()-1));
			return Optional.of(stringDoubleMap -> function.apply(stringDoubleMap, equation));
		}
	}

	public static class EquationParseException extends Exception {
		public EquationParseException(String message) {
			super(message);
		}
	}

	private interface TriFunction<T, U, V, R> {
		R apply(T t, U u, V v);
	}
}
