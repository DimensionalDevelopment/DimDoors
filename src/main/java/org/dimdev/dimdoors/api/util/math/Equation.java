package org.dimdev.dimdoors.api.util.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import net.minecraft.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.math.MathHelper;

@FunctionalInterface
public interface Equation {
	double FALSE = 0d;
	double TRUE = 1d;

	double apply(Map<String, Double> variableMap);

	default boolean asBoolean(Map<String, Double> variableMap) {
		return toBoolean(this.apply(variableMap));
	}

	static Equation parse(String equationString) throws EquationParseException {
		return StringEquationParser.INSTANCE.parse(equationString);
	}

	static double toDouble(boolean value) {
		return value ? TRUE : FALSE;
	}

	static boolean toBoolean(double value) {
		return value != FALSE;
	}

	class StringEquationParser {
		private static final Logger LOGGER = LogManager.getLogger();
		public static StringEquationParser INSTANCE = new StringEquationParser();
		private final static List<EquationParser> parseRules = new ArrayList<>();

		static {
			// Parenthesis
			parseRules.add(toParse -> {
				if (!toParse.startsWith("(") || !toParse.endsWith(")")) return Optional.empty();
				return Optional.of(Equation.parse(toParse.substring(1, toParse.length() - 1)));
			});

			// try to parse as Double
			parseRules.add(toParse -> {
				try {
					Double result = Double.parseDouble(toParse);
					return Optional.of(stringDoubleMap -> result);
				} catch (NumberFormatException e) {
					return Optional.empty();
				}
			});

			// some logic first
			//   ?  :
			parseRules.add(new SplitterParser()
					.add((variableMap, equations) -> equations[0].asBoolean(variableMap) ? equations[1].apply((variableMap)) : equations[2].apply(variableMap), "?", ":"));

			// ||
			parseRules.add(new SplitterParser()
					.add((variableMap, equations) -> toDouble(equations[0].asBoolean(variableMap) || equations[1].asBoolean((variableMap))), "||"));

			// &&
			parseRules.add(new SplitterParser()
					.add((variableMap, equations) -> toDouble(equations[0].asBoolean(variableMap) && equations[1].asBoolean((variableMap))), "&&"));

			// ==, <=, >=, <, >
			parseRules.add(new SplitterParser()
					.add((variableMap, equations) -> toDouble(equations[0].apply(variableMap) == equations[1].apply((variableMap))), "==")
					.add((variableMap, equations) -> toDouble(equations[0].apply(variableMap) <= equations[1].apply((variableMap))), "<=")
					.add((variableMap, equations) -> toDouble(equations[0].apply(variableMap) >= equations[1].apply((variableMap))), ">=")
					.add((variableMap, equations) -> toDouble(equations[0].apply(variableMap) < equations[1].apply((variableMap))), "<")
					.add((variableMap, equations) -> toDouble(equations[0].apply(variableMap) > equations[1].apply((variableMap))), ">"));

			// +, -
			parseRules.add(new SplitterParser()
					.add((variableMap, equations) -> equations[0].apply(variableMap) + equations[1].apply((variableMap)), "+")
					.add((variableMap, equations) -> equations[0].apply(variableMap) - equations[1].apply((variableMap)), "-"));

			// *, /, %
			parseRules.add(new SplitterParser()
					.add((variableMap, equations) -> equations[0].apply(variableMap) * equations[1].apply((variableMap)), "*")
					.add((variableMap, equations) -> equations[0].apply(variableMap) / equations[1].apply((variableMap)), "/")
					.add((variableMap, equations) -> equations[0].apply(variableMap) % equations[1].apply((variableMap)), "%"));

			// x^y
			parseRules.add(new SplitterParser()
					.add((variableMap, equations) -> Math.pow(equations[0].apply(variableMap), equations[1].apply(variableMap)), "^"));

			// H with H(0) = 1: https://en.wikipedia.org/wiki/Heaviside_step_function
			parseRules.add(new FunctionParser("H", 1, 1, ((stringDoubleMap, equations) -> equations[0].apply(stringDoubleMap) >= 0 ? 1d : 0d)));

			// floor
			parseRules.add(new FunctionParser("floor", 1, 1, ((stringDoubleMap, equations) -> Math.floor(equations[0].apply(stringDoubleMap)))));

			// ceil
			parseRules.add(new FunctionParser("ceil", 1, 1, ((stringDoubleMap, equations) -> Math.ceil(equations[0].apply(stringDoubleMap)))));

			// max
			parseRules.add(new FunctionParser("max", 2, -1, ((stringDoubleMap, equations) -> {
				Double max = equations[0].apply(stringDoubleMap);
				for (int i = 1; i < equations.length; i++) {
					max = Math.max(max, equations[i].apply(stringDoubleMap));
				}
				return max;
			})));

			// min
			parseRules.add(new FunctionParser("min", 2, -1, ((stringDoubleMap, equations) -> {
				Double min = equations[0].apply(stringDoubleMap);
				for (int i = 1; i < equations.length; i++) {
					min = Math.min(min, equations[i].apply(stringDoubleMap));
				}
				return min;
			})));

			// clamp
			parseRules.add(new FunctionParser("clamp", 3, 3, (stringDoubleMap, equations) -> MathHelper.clamp(equations[0].apply(stringDoubleMap), equations[1].apply(stringDoubleMap), equations[2].apply(stringDoubleMap))));

			// rand
			parseRules.add(new FunctionParser("random", 0,0, ((stringDoubleMap, equations) -> Math.random())));

			// variable replacer
			parseRules.add(new VariableReplacer());
		}

		public Equation parse(String equationString) throws EquationParseException {
			equationString = equationString.replaceAll("\\s", "");
			for (EquationParser parser : parseRules) {
				Optional<Equation> equation = parser.tryParse(equationString);
				if (equation.isPresent()) return equation.get();
			}
			throw new EquationParseException("\"" + equationString + "\" could not be parsed");
		}

		@FunctionalInterface
		private interface EquationParser {
			Optional<Equation> tryParse(String toParse) throws EquationParseException;
		}

		private static class VariableReplacer implements EquationParser {
			@Override
			public Optional<Equation> tryParse(String toParse) {
				if (!toParse.matches("[a-zA-Z_][a-zA-Z0-9_]*")) return Optional.empty();
				return Optional.of(stringDoubleMap -> {
					if (stringDoubleMap != null && stringDoubleMap.containsKey(toParse))
						return stringDoubleMap.get(toParse);
					LOGGER.error("Variable \"" + toParse + "\" was not passed to equation! Returning 0 as fallback.");
					return 0d;
				});
			}
		}

		private static class SplitterParser implements EquationParser {
			private final Map<String, Pair<String[], BiFunction<Map<String, Double>, Equation[], Double>>> operations;

			public SplitterParser() {
				this.operations = new HashMap<>();
			}

			public SplitterParser add(BiFunction<Map<String, Double>, Equation[], Double> function, String... symbols) {
				List<String> symbolList = Arrays.asList(symbols);
				Collections.reverse(symbolList);
				operations.put(symbolList.get(0), new Pair<>(symbolList.toArray(new String[0]), function));
				return this;
			}

			@Override
			public Optional<Equation> tryParse(String toParse) throws EquationParseException {
				int depth = 0;
				for (int i = toParse.length() - 1; i >= 1; i--) {
					String substring = toParse.substring(i);
					if (substring.startsWith(")")) depth++;
					else if (substring.startsWith("(")) depth--;
					for (String currentSymbol : this.operations.keySet()) {
						if (depth == 0 && substring.startsWith(currentSymbol)) {
							final Pair<String[], BiFunction<Map<String, Double>, Equation[], Double>> operation = this.operations.get(currentSymbol);
							final String[] symbols = operation.getLeft();
							List<Pair<Integer, Integer>> partIndices = new ArrayList<>(symbols.length + 1);
							partIndices.add(new Pair<>(i + currentSymbol.length(), toParse.length()));

							int symbolPointer = 1;
							if (symbolPointer < symbols.length) currentSymbol = symbols[symbolPointer];
							int endIndex = i;
							int innerDepth = 0;
							for (int j = i - 1; j >= 1 && symbolPointer < symbols.length; j--) {
								String innerSubstring = toParse.substring(j);

								if (innerSubstring.startsWith(")")) innerDepth++;
								else if (innerSubstring.startsWith("(")) innerDepth--;
								if (innerDepth == 0 && innerSubstring.startsWith(currentSymbol)) {

									partIndices.add(new Pair<>(j + currentSymbol.length(), endIndex));

									endIndex = j;
									symbolPointer++;
									if (symbolPointer < symbols.length) currentSymbol = symbols[symbolPointer];
								}
							}
							if (symbolPointer < symbols.length) continue;
							partIndices.add(new Pair<>(0, endIndex));

							Equation[] equations = new Equation[partIndices.size()];
							for (int j = 0; j < partIndices.size(); j++) {
								Pair<Integer, Integer> pair = partIndices.get(j);
								equations[partIndices.size() - j - 1] = Equation.parse(toParse.substring(pair.getLeft(), pair.getRight()));
							}

							return Optional.of(stringDoubleMap -> operation.getRight().apply(stringDoubleMap, equations));
						}
					}
				}
				return Optional.empty();
			}
		}

		private static class FunctionParser implements EquationParser {
			private final String functionString;
			private final int minArguments;
			private final int maxArguments;
			private final BiFunction<Map<String, Double>, Equation[], Double> function;

			public FunctionParser(String functionString, int minArguments, int maxArguments, BiFunction<Map<String, Double>, Equation[], Double> function) {
				this.functionString = functionString + "(";
				this.minArguments = minArguments;
				this.maxArguments = maxArguments;
				this.function = function;
			}

			@Override
			public Optional<Equation> tryParse(String toParse) throws EquationParseException {
				if (!toParse.startsWith(this.functionString) || !toParse.endsWith(")")) return Optional.empty();
				String[] arguments = toParse.substring(this.functionString.length(), toParse.length() - 1).split(",", -1);
				if (arguments.length == 1 && arguments[0].equals("") && this.minArguments == 0) {
					return Optional.of(stringDoubleMap -> this.function.apply(stringDoubleMap, new Equation[0]));
				}
				if (this.minArguments > arguments.length || (this.maxArguments < arguments.length && this.maxArguments != -1))
					return Optional.empty();
				final Equation[] argumentEquations = new Equation[arguments.length];
				for (int i = 0; i < arguments.length; i++) {
					argumentEquations[i] = Equation.parse(arguments[i]);
				}
				return Optional.of(stringDoubleMap -> this.function.apply(stringDoubleMap, argumentEquations));
			}
		}

		@FunctionalInterface
		private interface TriFunction<T, U, V, R> {
			R apply(T t, U u, V v);
		}
	}

	class EquationParseException extends Exception {
		public EquationParseException(String message) {
			super(message);
		}
	}
}
