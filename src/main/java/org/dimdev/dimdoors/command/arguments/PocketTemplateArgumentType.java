package org.dimdev.dimdoors.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.api.util.Path;
import org.dimdev.dimdoors.api.util.SimpleTree;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.PocketTemplate;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PocketTemplateArgumentType implements ArgumentType<PocketTemplate> {

	@Override
	public PocketTemplate parse(StringReader reader) throws CommandSyntaxException {
		Path<String> value = Path.stringPath(Objects.requireNonNull(reader.readString()));
		if (!getPocketTemplates().containsKey(value)) {
			// TODO: throw
		}
		return getPocketTemplates().get(value);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestMatching(getExamples(), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return getPocketTemplates().keySet().parallelStream().map(path -> path.reduce(String::concat)).map(id -> "\"" + id + "\"").collect(Collectors.toCollection(TreeSet::new));
	}

	private SimpleTree<String, PocketTemplate> getPocketTemplates() {
		return PocketLoader.getInstance().getTemplates();
	}

	public static PocketTemplate getValue(CommandContext<?> context, final String name) {
		return context.getArgument(name, PocketTemplate.class);
	}
}
