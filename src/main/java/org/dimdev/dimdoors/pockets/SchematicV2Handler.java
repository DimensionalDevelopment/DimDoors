package org.dimdev.dimdoors.pockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.google.common.collect.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;

import net.minecraft.nbt.*;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.schematic.v2.Schematic;

public class SchematicV2Handler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final SchematicV2Handler INSTANCE = new SchematicV2Handler();
    private final Map<Identifier, PocketTemplateV2> templates = Maps.newHashMap();
    private final Map<String, PocketGroup> pocketGroups = Maps.newHashMap();
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
			try {
				List<String> result = Files.readAllLines(Paths.get(SchematicV2Handler.class.getResource(String.format("/data/dimdoors/pockets/json/v2/%s.json", name)).toURI()));
				CompoundTag groupTag = StringNbtReader.parse(String.join("", result));
				PocketGroup type = new PocketGroup().fromTag(groupTag);
				type.init();
                this.pocketGroups.put(type.getGroup(), type);
            } catch (IOException | URISyntaxException | CommandSyntaxException e) {
                e.printStackTrace();
            }
        }

        LOGGER.info("Loaded schematics in {} seconds", System.currentTimeMillis() - startTime);
    }

    public void loadSchematic(Identifier templateID, String group, int size, String id) {
    	try {
			if (templates.containsKey(templateID)) return;
			Path basePath = Paths.get(SchematicV2Handler.class.getResource(String.format("/data/dimdoors/pockets/schematic/v2/%s/", group)).toURI());
			Path schemPath = basePath.resolve(id + ".schem");
			CompoundTag schemTag = NbtIo.readCompressed(Files.newInputStream(schemPath));
			Schematic schematic = Schematic.fromTag(schemTag);
			PocketTemplateV2 template = new PocketTemplateV2(schematic, size, id);
			templates.put(templateID, template);
		} catch (URISyntaxException | IOException e) {
			LOGGER.error("Could not load schematic!", e);
		}
	}

	public VirtualPocket getRandomPocketFromGroup(String group, PocketGenerationParameters parameters) {
    	return pocketGroups.get(group).getPocketList().getNextRandomWeighted(parameters);
	}

    public static SchematicV2Handler getInstance() {
        return INSTANCE;
    }

    public Map<Identifier, PocketTemplateV2> getTemplates() {
        return this.templates;
    }

    public Map<String, PocketGroup> getPocketGroups() {
        return this.pocketGroups;
    }
}
