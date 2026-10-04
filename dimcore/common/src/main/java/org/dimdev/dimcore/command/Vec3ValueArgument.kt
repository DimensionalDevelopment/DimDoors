package org.dimdev.dimcore.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.arguments.coordinates.Vec3Argument
import net.minecraft.world.phys.Vec3
import java.util.concurrent.CompletableFuture

object Vec3ValueArgument : ArgumentType<Vec3> {
    private val EXAMPLES = listOf("0 0 0", "1 0.5 0.5", "-2.5 64 10")

    override fun parse(reader: StringReader): Vec3 {
        val x = reader.readDouble()
        expectSpace(reader)
        val y = reader.readDouble()
        expectSpace(reader)
        val z = reader.readDouble()
        return Vec3(x, y, z)
    }

    private fun expectSpace(reader: StringReader) {
        if (!reader.canRead() || reader.peek() != ' ') throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(reader)
        reader.skip()
    }

    override fun <S> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val typed = builder.remaining.split(' ')
        val filled = List(3) { typed.getOrNull(it)?.takeIf(String::isNotEmpty) ?: "1.0" }
        return builder.suggest(filled.joinToString(" ")).buildFuture()
    }

    override fun getExamples(): Collection<String> = EXAMPLES
}
