package org.dimdev.dimdoors.command.arguments

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimdoors.pockets.PocketLoader
import org.dimdev.dimdoors.pockets.PocketTemplate
import java.util.concurrent.CompletableFuture
import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.Collectors

class PocketTemplateArgumentType : ArgumentType<PocketTemplate?> {
    @Throws(CommandSyntaxException::class)
    override fun parse(reader: StringReader): PocketTemplate {
        val id = ResourceLocation.read(reader)
        return id.let(PocketLoader.getTemplates()::get) ?: throw UNKNOWN_POCKET_TEMPLATE.create(id)
    }

    override fun <S> listSuggestions(
        context: CommandContext<S?>?,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions?> {
        return SharedSuggestionProvider.suggest(getExamples(), builder)
    }

    override fun getExamples() = PocketLoader.getTemplates().keys.map(ResourceLocation::toString)

    companion object {
        val UNKNOWN_POCKET_TEMPLATE: DynamicCommandExceptionType = DynamicCommandExceptionType(Function { s: Any? ->
            Component.translatable(
                "commands.pocket.unknownPocketTemplate",
                s
            )
        })

        fun getValue(context: CommandContext<*>, name: String?): PocketTemplate {
            return context.getArgument(name, PocketTemplate::class.java)
        }
    }
}
