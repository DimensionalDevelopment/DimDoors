package org.dimdev.dimdoors.pockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.google.common.collect.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.util.WeightedSet;
import org.dimdev.dimdoors.util.schematic.v2.Schematic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

public class SchematicV2Handler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
    private static final SchematicV2Handler INSTANCE = new SchematicV2Handler();
    private final Map<Identifier, PocketTemplateV2> templates = Maps.newHashMap();
    private final Map<String, WeightedSet<VirtualPocket>> templateMap = Maps.newHashMap(); //TODO: un-ugly-fy
    private final List<PocketGroup> pocketTypes = Lists.newArrayList();
    private static final Random RANDOM = new Random(new Random().nextLong());
    private boolean loaded = false;

    private SchematicV2Handler() {
    }

    public void load() {
        if (this.loaded) {
            throw new UnsupportedOperationException("Attempted to load schematics twice!");
        }
        this.loaded = true;
        long startTime = System.currentTimeMillis();
        Set<String> names = ImmutableSet.of("default_private", "default_public");
        for (String name : names) {
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(SchematicV2Handler.class.getResource(String.format("/data/dimdoors/pockets/json/v2/%s.json", name)).toURI()))) {
                List<String> result = new ArrayList<>();
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    result.add(line);
                }
                JsonObject json = GSON.fromJson(String.join("", result), JsonObject.class);
                PocketGroup type = PocketGroup.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow(false, System.err::println).getFirst();
                this.pocketTypes.add(type);
                this.loadResourceSchematics(type);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }

        LOGGER.info("Loaded schematics in {} seconds", System.currentTimeMillis() - startTime);
    }

    /*
     TODO: Change, is currently ugly.
      Maybe request schematic from within VirtualSchematicPocket#init() and add default void init() to VirtualPocket?
     */
    private void loadResourceSchematics(PocketGroup type) throws URISyntaxException, IOException {
        String group = type.getGroup();
        WeightedSet<VirtualPocket> weightedPockets = new WeightedSet<>();
        templateMap.put(group, weightedPockets);
        Path basePath = Paths.get(SchematicV2Handler.class.getResource(String.format("/data/dimdoors/pockets/schematic/v2/%s/", group)).toURI());
        for (VirtualPocket virtualPocket : type.getEntries()) {
			weightedPockets.add(virtualPocket, virtualPocket.getWeight());
        	if (virtualPocket instanceof VirtualSchematicPocket) {
        		VirtualSchematicPocket schemPocket = (VirtualSchematicPocket) virtualPocket;
				Path schemPath = basePath.resolve(schemPocket.getName() + ".schem");
				CompoundTag schemTag = NbtIo.readCompressed(Files.newInputStream(schemPath));
				Schematic schematic = Schematic.fromTag(schemTag);
				PocketTemplateV2 template = new PocketTemplateV2(schematic, group, schemPocket.getSize(), schemPocket.getName(), schemPocket.getWeight());
				Identifier templateID = new Identifier("dimdoors", schemPocket.getName());
				templates.put(templateID, template);
				schemPocket.setTemplateID(templateID);
			}
        }
    }

    public VirtualPocket getRandomPublicPocket() {
		return getRandomPocketFromGroup("public");
    }

    public VirtualPocket getRandomPrivatePocket() {
        return getRandomPocketFromGroup("private");
    }

	public VirtualPocket getRandomPocketFromGroup(String group) {
		return templateMap.get(group).getRandomWeighted();
	}

    public static SchematicV2Handler getInstance() {
        return INSTANCE;
    }

    public Map<Identifier, PocketTemplateV2> getTemplates() {
        return this.templates;
    }

    public List<PocketGroup> getPocketTypes() {
        return this.pocketTypes;
    }
}
