package org.dimdev.dimdoors.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.CommandSource;
import org.dimdev.dimdoors.pockets.SchematicHandler;

import java.util.concurrent.CompletableFuture;

public class NameArugmentType implements ArgumentType<String> {
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readUnquotedString();
    }

    public static NameArugmentType name() {
        return new NameArugmentType();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String group = context.getArgument("group", String.class);

        return CommandSource.suggestMatching(SchematicHandler.INSTANCE.getTemplateNames(group), builder);
    }
}