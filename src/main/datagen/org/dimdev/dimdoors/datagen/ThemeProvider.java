package org.dimdev.dimdoors.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.pockets.theme.Theme;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThemeProvider implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	private final DataGenerator generator;

	public ThemeProvider(DataGenerator generator) {
		this.generator  = generator;
	}

	@Override
	public void run(DataCache cache) throws IOException {
		Path path = this.generator.getOutput();

		BiConsumer<Identifier, JsonElement> consumer = (identifier, json)  -> {
			Path outputPath = getOutput(path, identifier);

			try {
				DataProvider.writeToPath(GSON, cache, json, outputPath);
			} catch (IOException var6) {
				LOGGER.error("Couldn't save decay pattern {}", outputPath, var6);
			}
		};

		generatePatterns(consumer);
	}

	protected void generatePatterns(BiConsumer<Identifier, JsonElement> consumer) {
		theme(new Identifier("dimdoors", "oak"),
				of(BlockTags.STAIRS, Blocks.OAK_STAIRS),
				of(BlockTags.LOGS, Blocks.OAK_LOG),
				of(BlockTags.SLABS, Blocks.OAK_SLAB),
				of(BlockTags.LEAVES, Blocks.OAK_LEAVES),
				of(ModBlocks.BLACK_FABRIC, Blocks.QUARTZ_BLOCK))
				.run(consumer);
	}

	private Pair<Theme.Entry, Block> of(Object obj, Block block) {
		return Pair.of(Theme.Entry.of(obj), block);
	}

	private TaggedThemeBuilder theme(Identifier id, Pair<Theme.Entry, Block>... converters) {
		return new TaggedThemeBuilder(id, converters);
	}



	@Override
	public String getName() {
		return "Theme";
	}

	private static Path getOutput(Path rootOutput, Identifier lootTableId) {
		return rootOutput.resolve("data/" + lootTableId.getNamespace() + "/pockets/themes/" + lootTableId.getPath() + ".json");
	}

	public static class TaggedThemeBuilder {
		private final Theme theme ;
		private Identifier id;

		public TaggedThemeBuilder(Identifier id, Pair<Theme.Entry, Block>... converters) {
			this.id = id;

			this.theme = new Theme(Stream.of(converters).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		}

		public void run(BiConsumer<Identifier, JsonElement> consumer) {
			JsonElement object = JsonOps.INSTANCE.withEncoder(Theme.CODEC).andThen(DataResult::result).andThen(Optional::get).apply(theme);
			consumer.accept(id, object);
		}
	}
}
