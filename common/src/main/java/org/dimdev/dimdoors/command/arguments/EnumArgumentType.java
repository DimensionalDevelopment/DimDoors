package org.dimdev.dimdoors.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class EnumArgumentType<T extends Enum<T>> implements ArgumentType<T> {
	public static final DynamicCommandExceptionType UNKNOWN_VALUE = new DynamicCommandExceptionType(str -> Component.translatable("commands.generic.unknownValue", str));
	private final Map<String, T> values;
	private final Set<String> valueList;

	public EnumArgumentType(Class<T> enumClass) {
		values = new HashMap<>();
		for (T enumConstant : enumClass.getEnumConstants()) {
			values.put(enumConstant.name().toLowerCase(), enumConstant);
		}
		valueList = values.keySet();
	}

	@Override
	public T parse(StringReader reader) throws CommandSyntaxException {
		String str = reader.readString();
		return Optional.ofNullable(str)
				.map(values::get)
				.orElseThrow(() -> UNKNOWN_VALUE.create(str));
	}

	@Override
	public Collection<String> getExamples() {
		return valueList;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggest(valueList, builder);
	}
}
