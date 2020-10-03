package org.dimdev.dimdoors.command.arguments;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.text.TranslatableText;

public class SchematicNamespaceArgumentType implements ArgumentType<String> {
    private static final Collection<String> NAMESPACES = ImmutableList.of("ruins", "blank", "nether", "private", "public");

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String value = Objects.requireNonNull(reader.readString());
        if (!NAMESPACES.contains(value)) {
            throw new SimpleCommandExceptionType(new TranslatableText("argument.dimdoors.schematic.invalidNamespace", String.join(", ", NAMESPACES), value)).create();
        }
        return value;
    }

    @Override
    public Collection<String> getExamples() {
        return NAMESPACES;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(NAMESPACES, builder);
    }

    public static String getValue(CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }
}
