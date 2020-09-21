package org.dimdev.dimdoors.command.arguments;

import java.util.Collection;
import java.util.Objects;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.text.TranslatableText;

public class SchematicNamespaceArgumentType implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = ImmutableList.of("ruins", "blank", "nether", "private", "public");

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String value = Objects.requireNonNull(reader.readString());
        if (!EXAMPLES.contains(value)) {
            throw new SimpleCommandExceptionType(new TranslatableText("argument.dimdoors.schematic.invalidNamespace", String.join(", ", EXAMPLES), value)).create();
        }
        return value;
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static String getValue(CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }
}
