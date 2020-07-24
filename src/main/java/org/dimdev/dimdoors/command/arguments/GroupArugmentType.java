package org.dimdev.dimdoors.command.arguments;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.dimdev.dimdoors.pockets.PocketTemplate;
import org.dimdev.dimdoors.pockets.SchematicHandler;

import net.minecraft.server.command.CommandSource;

public class GroupArugmentType implements ArgumentType<String> {
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readUnquotedString();
    }

    public static GroupArugmentType group() {
        return new GroupArugmentType();
    }

    public static <S> PocketTemplate getPocketTemplate(CommandContext<S> context, String name) {
        return context.getArgument(name, PocketTemplate.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(SchematicHandler.INSTANCE.getTemplateGroups().stream(), builder);
    }
}
