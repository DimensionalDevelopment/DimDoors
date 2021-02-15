package org.dimdev.dimdoors.pockets;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.*;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.*;
import net.minecraft.util.Identifier;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.WeightedList;
import org.dimdev.dimdoors.util.schematic.v2.Schematic;

public class SchematicV2Handler {
    private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
    private static final SchematicV2Handler INSTANCE = new SchematicV2Handler();
    private final Map<String, PocketGenerator> pocketGeneratorMap = Maps.newHashMap();
    private final Map<String, VirtualPocket> pocketGroups = Maps.newHashMap();
	private final Map<Identifier, PocketTemplateV2> templates = Maps.newHashMap();
    private boolean loaded = false;

    private SchematicV2Handler() {
    }

    public void load() {
        if (this.loaded) {
            throw new UnsupportedOperationException("Attempted to load pockets twice!");
        }
        this.loaded = true;
        long startTime = System.currentTimeMillis();

		try {
			Path path = Paths.get(SchematicV2Handler.class.getResource("/data/dimdoors/pockets/generators").toURI());
			loadJson(path, new String[0], this::loadPocketGenerator);
			LOGGER.info("Loaded pockets in {} seconds", System.currentTimeMillis() - startTime);
		} catch (URISyntaxException e) {
			LOGGER.error(e);
		}

		startTime = System.currentTimeMillis();
		try {
			Path path = Paths.get(SchematicV2Handler.class.getResource("/data/dimdoors/pockets/groups").toURI());
			loadJson(path, new String[0], this::loadPocketGroup);
			LOGGER.info("Loaded pocket groups in {} seconds", System.currentTimeMillis() - startTime);
		} catch (URISyntaxException e) {
			LOGGER.error(e);
		}
    }

    public Tag readNbtFromJson(String id) {
		try {
			Path path = Paths.get(SchematicV2Handler.class.getResource("/data/dimdoors/pockets/json/" + id + ".json").toURI());
			if (!Files.isRegularFile(path)) {
				return null;
			}
			try {
				JsonElement json = GSON.fromJson(String.join("", Files.readAllLines(path)), JsonElement.class);
				return JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, json);
			} catch (IOException e) {
				LOGGER.error(e);
			}
		} catch (URISyntaxException e) {
			LOGGER.error(e);
		}
		return null;
	}

    public <T> T readNbtSerializableFromJson(String id, Function<Tag, T> reader) {
    	Tag tag = readNbtFromJson(id);
    	if (tag == null) return null;
    	return reader.apply(tag);
	}

	// TODO: fix, some weird "/" stuff
    private void loadJson(Path path, String[] idParts, BiConsumer<String, Tag> loader) {
		if (Files.isDirectory(path)) {
			try {
				for (Path directoryPath : Files.newDirectoryStream(path)) {
					String[] directoryIdParts = Arrays.copyOf(idParts, idParts.length + 1);
					String fileName = directoryPath.getFileName().toString();
					if (fileName.endsWith("/")) fileName = fileName.substring(0, fileName.length()-1); // https://bugs.openjdk.java.net/browse/JDK-8153248
					if (Files.isRegularFile(directoryPath)) fileName = fileName.substring(0, fileName.lastIndexOf('.')); // cut extension
					directoryIdParts[directoryIdParts.length - 1] = fileName;
					loadJson(directoryPath, directoryIdParts, loader);
				}
			} catch (IOException e) {
				LOGGER.error("Could not load pocket data in path {}", path.toAbsolutePath());
				LOGGER.error("Stacktrace: ", e);
			}
		} else if(Files.isRegularFile(path) && path.getFileName().toString().endsWith(".json")) {
			String id = String.join("/", idParts);
			try {
				JsonElement json = GSON.fromJson(String.join("", Files.readAllLines(path)), JsonElement.class);
				loader.accept(id, JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, json));
			} catch (IOException e) {
				LOGGER.error("Could not load pocket data in path {}", path.toAbsolutePath());
				LOGGER.error("Stacktrace: ", e);
			} catch (RuntimeException e) {
				LOGGER.error("Error parsing schematic json");
				LOGGER.error("Erroring path: {}", path.toAbsolutePath());
				throw e;
			}
		}
	}

	private void loadPocketGroup(String id, Tag tag) {
		VirtualPocket group = VirtualPocket.deserialize(tag);
		pocketGroups.put(id, group);
	}

	private void loadPocketGenerator(String id, Tag tag) {
    	if (tag == null || tag.getType() != NbtType.COMPOUND) {
    		LOGGER.error("Could not load PocketGenerator " + id + " since the json does not represent a CompoundTag!");
    		return;
		}
		PocketGenerator gen =  PocketGenerator.deserialize((CompoundTag) tag);
		if (gen != null) pocketGeneratorMap.put(id, gen);
	}

    public void loadSchematic(Identifier templateID, String id) {
    	try {
			if (templates.containsKey(templateID)) return;
			Path schemPath = Paths.get(SchematicV2Handler.class.getResource(String.format("/data/dimdoors/pockets/schematic/%s.schem", id.replaceAll("\\.", "/"))).toURI());
			CompoundTag schemTag = NbtIo.readCompressed(Files.newInputStream(schemPath));
			Schematic schematic = Schematic.fromTag(schemTag);
			PocketTemplateV2 template = new PocketTemplateV2(schematic, id);
			templates.put(templateID, template);
		} catch (URISyntaxException | IOException e) {
			LOGGER.error("Could not load schematic!", e);
		}
	}

	public WeightedList<PocketGenerator, PocketGenerationParameters> getPocketsMatchingTags(List<String> required, List<String> blackList, boolean exact) {
    	return new WeightedList<>(pocketGeneratorMap.values().stream().filter(pocketGenerator -> pocketGenerator.checkTags(required, blackList, exact)).collect(Collectors.toList()));
	}

	public VirtualPocket getGroup(String group) {
    	return pocketGroups.get(group);
	}

    public static SchematicV2Handler getInstance() {
        return INSTANCE;
    }

    public Map<Identifier, PocketTemplateV2> getTemplates() {
        return this.templates;
    }

    public Map<String, VirtualPocket> getPocketGroups() {
        return this.pocketGroups;
    }

    public PocketGenerator getGenerator(String id) {
    	return pocketGeneratorMap.get(id);
	}
}
