package org.dimdev.dimdoors.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.pockets.theme.Converter;
import org.dimdev.dimdoors.pockets.theme.TaggedConverter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;

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

		BiConsumer<Identifier, JsonObject> consumer = (identifier, json)  -> {
			Path outputPath = getOutput(path, identifier);

			try {
				DataProvider.writeToPath(GSON, cache, json, outputPath);
			} catch (IOException var6) {
				LOGGER.error("Couldn't save decay pattern {}", outputPath, var6);
			}
		};

		generatePatterns(consumer);
	}

	protected void generatePatterns(BiConsumer<Identifier, JsonObject> consumer) {
		theme(new Identifier("dimdoors", "oak"),
				TaggedConverter.of(BlockTags.STAIRS, Blocks.OAK_STAIRS),
				TaggedConverter.of(BlockTags.LOGS, Blocks.OAK_LOG),
				TaggedConverter.of(BlockTags.SLABS, Blocks.OAK_SLAB),
				TaggedConverter.of(BlockTags.LEAVES, Blocks.OAK_LEAVES))
				.run(consumer);
	}

	private TaggedThemeBuilder theme(Identifier id, Converter... converters) {
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
		private final List<Converter> converters;
		private Identifier id;

		public TaggedThemeBuilder(Identifier id, Converter... converters) {
			this.id = id;

			this.converters = List.of(converters);
		}

		public void run(BiConsumer<Identifier, JsonObject> consumer) {
			JsonObject object = new JsonObject();

			JsonArray converters = new JsonArray();

			this.converters.stream().map(converter -> converter.toNbt(new NbtCompound())).map(ResourceUtil.NBT_TO_JSON).forEach(converters::add);

			object.add("converters", converters);

			consumer.accept(id, object);
		}
	}
}
