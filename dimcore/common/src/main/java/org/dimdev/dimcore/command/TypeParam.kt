package org.dimdev.dimcore.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.core.Registry
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.Type
import org.dimdev.dimcore.api.ext.cast

sealed class TypeParam<T : Any, A : Any>(val index: Int, val name: String, val default: A, val getter: (T) -> A) {
    class Value<T : Any, A : Any>(index: Int, name: String, val type: ArgumentType<A>, val clazz: Class<A>, default: A, getter: (T) -> A) : TypeParam<T, A>(index, name, default, getter) {
        fun node(prefix: String): RequiredArgumentBuilder<CommandSourceStack, A> = Commands.argument(prefix + name, type)
    }

    class Nested<T : Any, A : Any>(index: Int, name: String, val registry: Registry<out Type<A>>, default: A, getter: (T) -> A) : TypeParam<T, A>(index, name, default, getter)
}

class TypeArgs<T : Any>(val clazz: Class<T>, val params: List<TypeParam<T, *>>, val construct: (Values<T>) -> T) {
    class Values<T : Any>(
        private val ctx: CommandContext<CommandSourceStack>,
        private val prefix: String,
        private val path: TypeCommands.Path,
        private val current: T?
    ) {
        operator fun <A : Any> get(param: TypeParam<T, A>): A {
            if (param.index !in path.given) return current?.let(param.getter) ?: param.default

            return when (param) {
                is TypeParam.Value -> ctx.getArgument(prefix + param.name, param.clazz)
                is TypeParam.Nested -> path.chosen.getValue(param.index).build(ctx, "$prefix${param.name}.", current?.let(param.getter)).cast()
            }
        }
    }

    class Builder<T : Any>(private val clazz: Class<T>) {
        @PublishedApi internal val params = mutableListOf<TypeParam<T, *>>()
        private var construct: ((Values<T>) -> T)? = null

        inline fun <reified A : Any> param(name: String, type: ArgumentType<A>, default: A, noinline getter: (T) -> A): TypeParam<T, A> =
            TypeParam.Value(params.size, name, type, A::class.javaObjectType, default, getter).also(params::add)

        fun <A : Any> nested(name: String, registry: Registry<out Type<A>>, default: A, getter: (T) -> A): TypeParam<T, A> =
            TypeParam.Nested(params.size, name, registry, default, getter).also(params::add)

        fun float(name: String, default: Float, getter: (T) -> Float) = param(name, FloatArgumentType.floatArg(), default, getter)
        fun double(name: String, default: Double, getter: (T) -> Double) = param(name, DoubleArgumentType.doubleArg(), default, getter)
        fun int(name: String, default: Int, getter: (T) -> Int) = param(name, IntegerArgumentType.integer(), default, getter)
        fun long(name: String, default: Long, getter: (T) -> Long) = param(name, LongArgumentType.longArg(), default, getter)
        fun word(name: String, default: String, getter: (T) -> String) = param(name, StringArgumentType.word(), default, getter)
        fun vec3(name: String, default: Vec3, getter: (T) -> Vec3) = param(name, Vec3ValueArgument, default, getter)

        fun construct(block: (Values<T>) -> T) { construct = block }

        fun build(): TypeArgs<T> = TypeArgs(clazz, params.toList(), construct ?: error("No construct for ${clazz.simpleName}"))
    }

    companion object {
        inline operator fun <reified T : Any> invoke(block: Builder<T>.() -> Unit): TypeArgs<T> = Builder(T::class.java).apply(block).build()
    }
}

object TypeCommands {
    class Path(val given: Set<Int> = emptySet(), val chosen: Map<Int, Choice> = emptyMap()) {
        fun with(index: Int) = Path(given + index, chosen)
        fun with(index: Int, choice: Choice) = Path(given + index, chosen + (index to choice))
    }

    class Choice(private val spec: TypeArgs<*>, private val path: Path) {
        fun build(ctx: CommandContext<CommandSourceStack>, prefix: String, current: Any?): Any = construct(spec, ctx, prefix, path, current)
    }

    private val specs = mutableMapOf<Type<*>, TypeArgs<*>>()

    fun <T : Any> register(type: Type<T>, args: TypeArgs<T>) { specs[type] = args }

    private fun <B : Any> spec(type: Type<B>): TypeArgs<out B>? = specs[type]?.cast()

    private fun <S : Any> construct(spec: TypeArgs<S>, ctx: CommandContext<CommandSourceStack>, prefix: String, path: Path, current: Any?): S =
        spec.construct(TypeArgs.Values(ctx, prefix, path, current?.takeIf(spec.clazz::isInstance)?.let(spec.clazz::cast)))

    fun <B : Any> branch(
        name: String,
        registry: Registry<out Type<B>>,
        current: (CommandContext<CommandSourceStack>) -> B? = { null },
        apply: (CommandContext<CommandSourceStack>, B) -> Int
    ): LiteralArgumentBuilder<CommandSourceStack> {
        val root = Commands.literal(name)

        registry.entrySet().forEach { (key, type) ->
            val spec = spec(type) ?: return@forEach
            root.then(literal(key.location().toString(), spec, current, apply))
        }

        return root
    }

    private fun <T : B, B : Any> literal(
        id: String,
        spec: TypeArgs<T>,
        current: (CommandContext<CommandSourceStack>) -> B?,
        apply: (CommandContext<CommandSourceStack>, B) -> Int
    ): LiteralArgumentBuilder<CommandSourceStack> {
        val exec = { path: Path -> Command<CommandSourceStack> { ctx -> apply(ctx, construct(spec, ctx, "", path, current(ctx))) } }

        val open = Commands.literal("{").executes(exec(Path())).also { open ->
            children(spec, "", Path(), 0, exec) { path -> listOf(Commands.literal("}").executes(exec(path))) }.forEach { open.then(it) }
        }

        return Commands.literal(id).executes(exec(Path())).then(open)
    }

    private fun children(
        spec: TypeArgs<*>,
        prefix: String,
        path: Path,
        from: Int,
        exec: (Path) -> Command<CommandSourceStack>,
        after: (Path) -> List<ArgumentBuilder<CommandSourceStack, *>>
    ): List<ArgumentBuilder<CommandSourceStack, *>> =
        spec.params.drop(from).map { named(spec, prefix, path, it, exec, after) } + after(path)

    private fun named(
        spec: TypeArgs<*>,
        prefix: String,
        path: Path,
        param: TypeParam<*, *>,
        exec: (Path) -> Command<CommandSourceStack>,
        after: (Path) -> List<ArgumentBuilder<CommandSourceStack, *>>
    ): ArgumentBuilder<CommandSourceStack, *> {
        val name = Commands.literal(param.name)
        val rest = { next: Path -> children(spec, prefix, next, param.index + 1, exec, after) }

        when (param) {
            is TypeParam.Value -> {
                val next = path.with(param.index)
                name.then(param.node(prefix).executes(exec(next)).also { node -> rest(next).forEach { node.then(it) } })
            }

            is TypeParam.Nested -> param.registry.entrySet().forEach { (key, type) ->
                val sub = specs[type] ?: return@forEach
                val outer = { inner: Path -> path.with(param.index, Choice(sub, inner)) }

                val open = Commands.literal("{").executes(exec(outer(Path()))).also { open ->
                    children(sub, "$prefix${param.name}.", Path(), 0, { exec(outer(it)) }) { inner ->
                        listOf(Commands.literal("}").executes(exec(outer(inner))).also { close -> rest(outer(inner)).forEach { close.then(it) } })
                    }.forEach { open.then(it) }
                }

                name.then(Commands.literal(key.location().toString()).executes(exec(outer(Path()))).also { literal ->
                    literal.then(open)
                    rest(outer(Path())).forEach { literal.then(it) }
                })
            }
        }

        return name
    }
}
