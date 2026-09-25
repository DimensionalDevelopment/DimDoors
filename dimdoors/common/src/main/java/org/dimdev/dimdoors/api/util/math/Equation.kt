package org.dimdev.dimdoors.api.util.math

import com.mojang.serialization.Codec
import net.minecraft.util.Mth
import org.dimdev.dimdoors.DimensionalDoors.Companion.LOGGER
import kotlin.math.*

private val Boolean.double: Double get() = if(this) 1.0 else 0.0

interface Equation {
    fun apply(variableMap: MutableMap<String, Double>): Double

    fun asString(): String {
        return this.visit(StringBuilder()).toString()
    }

    fun visit(builder: StringBuilder): StringBuilder?

    fun asBoolean(vararg pairs: Pair<String, Double>) = asBoolean(mutableMapOf(*pairs))

    fun asBoolean(variableMap: MutableMap<String, Double>) = toBoolean(this.apply(variableMap))

    object StringEquationParser {
        @Throws(EquationParseException::class)
        fun parse(equationString: String): Equation {
            var equationString = equationString
            equationString = equationString.replace("\\s".toRegex(), "")

            for (parser in parseRules) parser.tryParse(equationString)?.let { return it }
            throw EquationParseException("\"$equationString\" could not be parsed")
        }

        private fun interface EquationParser {
            @Throws(EquationParseException::class)
            fun tryParse(toParse: String): Equation?
        }

        private class VariableReplacer : EquationParser {
            override fun tryParse(toParse: String): Equation? {
                return when {
                    !toParse.matches("[a-zA-Z_][a-zA-Z0-9_]*".toRegex()) -> null
                    else -> newEquation({ map ->
                        if (map.containsKey(toParse)) map[toParse]
                        LOGGER.error("Variable \"$toParse\" was not passed to equation! Returning 0 as fallback.")
                        0.0
                    }) { stringBuilder -> stringBuilder.append(toParse) }
                }
            }
        }

        private class SplitterParser : EquationParser {
            private val operations = mutableMapOf<String, Pair<Array<String>, (MutableMap<String, Double>, Array<Equation>) -> Double>>()

            fun add(
                function: (MutableMap<String, Double>, Array<Equation>) -> Double,
                vararg symbols: String
            ): SplitterParser {
                val symbolList = mutableListOf(*symbols)
                symbolList.reverse()
                operations[symbolList[0]] = symbolList.toTypedArray<String>() to function
                return this
            }

            @Throws(EquationParseException::class)
            override fun tryParse(toParse: String): Equation? {
                for (i in toParse.length - 1 downTo 1) for ((key, op) in operations) {
                    if (!toParse.startsWith(key, i) || depth(toParse, i) != 0) continue
                    val (symbols, function) = op
                    val cuts = mutableListOf(i to key.length)
                    for (s in symbols.drop(1)) cuts.add((find(toParse, s, cuts.last().first) ?: break) to s.length)
                    if (cuts.size < symbols.size) continue
                    val equations = (listOf(0 to 0) + cuts.asReversed() + (toParse.length to 0))
                        .zipWithNext { (a, len), (b, _) -> Equation.parse(toParse.substring(a + len, b)) }.toTypedArray()
                    return newEquation({ function(it, equations) }) { sb ->
                        symbols.indices.forEach { equations[it].visit(sb)!!.append(symbols[symbols.size - 1 - it]) }
                        equations.last().visit(sb)!!
                    }
                }
                return null
            }

            private fun find(s: String, symbol: String, end: Int) = (end - 1 downTo 1).firstOrNull { s.startsWith(symbol, it) && depth(s, it, end) == 0 }
            private fun depth(s: String, i: Int, end: Int = s.length) = s.substring(i, end).let { it.count { c -> c == ')' } - it.count { c -> c == '(' } }
        }

        private class FunctionParser(
            functionString: String,
            private val minArguments: Int,
            private val maxArguments: Int,
            private val function: (MutableMap<String, Double>, Array<Equation>) -> Double) : EquationParser {

            private val functionString: String = "$functionString("

            @Throws(EquationParseException::class)
            override fun tryParse(toParse: String): Equation? {
                if (!toParse.startsWith(this.functionString) || !toParse.endsWith(")")) return null
                val arguments = toParse.substring(this.functionString.length, toParse.length - 1).split(",".toRegex()).toTypedArray()
                if (arguments.size == 1 && arguments[0] == "" && this.minArguments == 0) {
                    return newEquation(
                            { stringDoubleMap -> this.function.invoke(stringDoubleMap, emptyArray()) },
                            { stringBuilder -> stringBuilder.append(functionString).append(")") })
                }

                if (this.minArguments > arguments.size || (this.maxArguments < arguments.size && this.maxArguments != -1)) return null
                val argumentEquations = arguments.map { Equation.parse(it) }.toTypedArray()

                return newEquation(
                        { stringDoubleMap-> this.function.invoke(stringDoubleMap, argumentEquations) },
                         { stringBuilder ->
                            stringBuilder.append(functionString)
                            argumentEquations[0].visit(stringBuilder)
                            for (i in 1..<argumentEquations.size) {
                                stringBuilder.append(",")
                                argumentEquations[i].visit(stringBuilder)
                            }
                            stringBuilder.append(")")
                        })
            }
        }

        private val parseRules = mutableListOf<EquationParser>().apply {
            fun MutableList<EquationParser>.equation(equationParser: (String) -> Equation?) {
                add(equationParser)
            }

            fun MutableList<EquationParser>.splitter(block: SplitterParser.() -> Unit) {
                add(SplitterParser().apply(block))
            }

            fun SplitterParser.boolean(name: String, function: (Boolean, Boolean) -> Boolean) {
                add({ variables, equations ->
                    var var1 = equations[0].asBoolean(variables)
                    var var2 = equations[1].asBoolean(variables)

                    return@add function.invoke(var1, var2).double
                }, name)
            }

            fun SplitterParser.doubleBoolean(name: String, function: (Double, Double) -> Boolean) {
                add({ variables, equations ->
                    var var1 = equations[0].apply(variables)
                    var var2 = equations[1].apply(variables)

                    return@add function.invoke(var1, var2).double
                }, name)
            }

            fun SplitterParser.double(name: String, function: (Double, Double) -> Double) {
                add({ variables, equations ->
                    var var1 = equations[0].apply(variables)
                    var var2 = equations[1].apply(variables)

                    return@add function.invoke(var1, var2)
                }, name)
            }

            fun MutableList<EquationParser>.function(
                functionString: String,
                minArguments: Int,
                maxArguments: Int,
                function: (MutableMap<String, Double>, Array<Equation>) -> Double
            ) {
                add(FunctionParser(functionString, minArguments, maxArguments, function))
            }

            fun MutableList<EquationParser>.function(
                functionString: String,
                minArguments: Int,
                maxArguments: Int,
                function: ((Int) -> Double) -> Double
            ) {
                function(
                    functionString,
                    minArguments,
                    maxArguments
                ) { variables, equations -> function.invoke { equations[it].apply(variables) } }
            }

            // Parenthesis
            equation { toParse: String ->
                if (!toParse.startsWith("(") || !toParse.endsWith(")")) return@equation null
                val equation: Equation = Equation.parse(toParse.substring(1, toParse.length - 1))

                newEquation(
                    { variableMap -> equation.apply(variableMap) },
                    { stringBuilder ->
                        equation.visit(stringBuilder.append("("))!!.append(")")
                    })
            }

            equation { toParse ->
                try {
                    val result = toParse.toDouble()
                    return@equation newEquation(
                        { result },
                        { builder -> builder.append(toParse) }
                    )
                } catch (_: NumberFormatException) {
                    return@equation null
                }
            }

            // some logic first
            //   ?  :
            splitter {
                add({ variableMap, equations ->
                    if (equations[0].asBoolean(variableMap)) equations[1].apply((variableMap)) else equations[2].apply(
                        variableMap
                    )
                }, "?", ":")
            }

            // ||
            splitter {
                boolean("||") { a, b -> a || b }
            }

            // &&
            splitter {
                boolean("&&") { a, b -> a && b }
            }

            // ==, <=, >=, <, >
            splitter {
                doubleBoolean("==") { a, b -> a == b }
                doubleBoolean("<=") { a, b -> a <= b }
                doubleBoolean(">=") { a, b -> a >= b }
                doubleBoolean("<") { a, b -> a < b }
                doubleBoolean(">") { a, b -> a > b }
            }

            // +, -
            splitter {
                double("+") { a, b -> a + b }
                double("-") { a, b -> a - b }
            }

            // *, /, %
            splitter {
                double("*") { a, b -> a * b }
                double("/") { a, b -> a / b }
                double("%") { a, b -> a % b }
            }

            //TODO: ensure x^y is right associative

            // x^y
            splitter {
                add(
                    { variableMap, equations -> equations[0].apply(variableMap).pow(equations[1].apply(variableMap)) },
                    "^"
                )
            }

            // H with H(0) = 1: https://en.wikipedia.org/wiki/Heaviside_step_function
            function(
                "H",
                1,
                1
            ) { func -> if (func.invoke(0) >= 0) 1.0 else 0.0 }

            // floor
            function("floor", 1, 1) { func -> floor(func.invoke(0)) }

            // ceil
            function("ceil", 1, 1) { func -> ceil(func.invoke(0)) }

            // max
            function("max", 2, -1) { stringDoubleMap, equations -> equations.maxOf { it.apply(stringDoubleMap) } }

            // min
            function("max", 2, -1) { stringDoubleMap, equations -> equations.minOf { it.apply(stringDoubleMap) } }

            // clamp
            function("clamp", 3, 3) { func -> Mth.clamp(
                func.invoke(1),
                func.invoke(1),
                func.invoke(2))
            }

            // rand
            function("random", 0, 0) { _, _ -> Math.random() }

            // variable replacer
            add(VariableReplacer())
        }
    }

    class EquationParseException(message: String?) : Exception(message)
    companion object {
        @JvmStatic
        @Throws(EquationParseException::class)

        fun parse(equationString: String): Equation = StringEquationParser.parse(equationString)

        fun parseOrCrash(equationString: String): Equation {
            try {
                return parse(equationString)
            } catch (e: EquationParseException) {
                throw RuntimeException(e)
            }
        }

        @JvmStatic
        fun toBoolean(value: Double): Boolean {
            return value != FALSE
        }

        fun newEquation(
            apply: (MutableMap<String, Double>) -> Double,
            visit: (StringBuilder) -> StringBuilder
        ): Equation {
            return object : Equation {
                override fun apply(variableMap: MutableMap<String, Double>): Double = variableMap.let(apply)

                override fun visit(builder: StringBuilder): StringBuilder = builder.let(visit)
            }
        }

        val CODEC= Codec.withAlternative(Codec.STRING, Codec.INT, Int::toString).xmap(::parseOrCrash, Equation::asString)


        val ZERO: Equation = parseOrCrash("0")
        val FIVE: Equation = parseOrCrash("5")
        val ONE: Equation = parseOrCrash("1")

        const val FALSE: Double = 0.0
    }
}
