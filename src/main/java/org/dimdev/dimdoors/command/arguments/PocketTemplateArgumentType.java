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
import org.dimdev.dimdoors.api.util.Path;
import org.dimdev.dimdoors.api.util.SimpleTree;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.PocketTemplate;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PocketTemplateArgumentType implements ArgumentType<PocketTemplate> {
	public static final DynamicCommandExceptionType UNKNOWN_POCKET_TEMPLATE = new DynamicCommandExceptionType(s -> new TranslatableText("commands.pocket.unknownPocketTemplate", s));

	@Override
	public PocketTemplate parse(StringReader reader) throws CommandSyntaxException {
		String strValue = reader.readString();
		Path<String> value = Path.stringPath(strValue);
		if (!getPocketTemplates().containsKey(value)) {
			throw UNKNOWN_POCKET_TEMPLATE.create(value.toString());
		}
		return getPocketTemplates().get(value);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestMatching(getExamples(), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return getPocketTemplates()
				.keySet()
				.parallelStream()
				.map(path -> path.reduce(String::concat))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(id -> "\"" + id + "\"")
				.collect(Collectors.toCollection(TreeSet::new));
	}

	private SimpleTree<String, PocketTemplate> getPocketTemplates() {
		return PocketLoader.getInstance().getTemplates();
	}

	public static PocketTemplate getValue(CommandContext<?> context, final String name) {
		return context.getArgument(name, PocketTemplate.class);
	}
}
