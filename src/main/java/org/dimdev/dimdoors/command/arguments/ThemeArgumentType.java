package org.dimdev.dimdoors.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.theme.Theme;

import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ThemeArgumentType implements ArgumentType<Theme> {
	public static final DynamicCommandExceptionType UNKNOWN_POCKET_TEMPLATE = new DynamicCommandExceptionType(s -> new TranslatableText("commands.pocket.unknownTheme", s));

	@Override
	public Theme parse(StringReader reader) throws CommandSyntaxException {
		String strValue = reader.readString();
		Identifier value = Identifier.tryParse(strValue);
		if (!getThemes().containsKey(value)) {
			throw UNKNOWN_POCKET_TEMPLATE.create(value.toString());
		}
		return getThemes().get(value);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestMatching(getExamples(), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return getThemes()
				.keySet()
				.parallelStream()
				.collect(Collectors.toCollection(TreeSet::new));
	}

	private Map<String, Theme> getThemes() {
		return PocketLoader.getInstance().getThemes();
	}

	public static Theme getValue(CommandContext<?> context, final String name) {
		return context.getArgument(name, Theme.class);
	}
}
