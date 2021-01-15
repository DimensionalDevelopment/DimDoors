package org.dimdev.dimdoors.pockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.util.schematic.v2.Schematic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

public class SchematicV2Handler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
    private static final SchematicV2Handler INSTANCE = new SchematicV2Handler();
    private final List<PocketTemplateV2> templates = Lists.newArrayList();
    private final Multimap<String, PocketTemplateV2> templateMap = HashMultimap.create();
    private final List<PocketType> pocketTypes = Lists.newArrayList();
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
                PocketType type = PocketType.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow(false, System.err::println).getFirst();
                this.pocketTypes.add(type);
                this.loadResourceSchematics(type);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }

        LOGGER.info("Loaded schematics in {} seconds", System.currentTimeMillis() - startTime);
    }

    private void loadResourceSchematics(PocketType type) throws URISyntaxException, IOException {
        String group = type.getGroup();
        Path basePath = Paths.get(SchematicV2Handler.class.getResource(String.format("/data/dimdoors/pockets/schematic/v2/%s/", group)).toURI());
        for (PocketType.PocketEntry entry : type.getEntries()) {
            Path schemPath = basePath.resolve(entry.getName() + ".schem");
            CompoundTag schemTag = NbtIo.readCompressed(Files.newInputStream(schemPath));
            Schematic schematic = Schematic.fromTag(schemTag);
            for (int i = 0; i < entry.getWeight(); i++) {
                this.templateMap.put(group, new PocketTemplateV2(schematic, group, entry.getSize(), entry.getName(), entry.getWeight()));
            }
        }
    }

    public PocketTemplateV2 getRandomPublicPocket() {
        Collection<PocketTemplateV2> publicPockets = this.templateMap.get("public");
        int index = RANDOM.nextInt(publicPockets.size());
        return Lists.newArrayList(publicPockets).get(0);
    }

    public PocketTemplateV2 getRandomPrivatePocket() {
        Collection<PocketTemplateV2> publicPockets = this.templateMap.get("private");
        int index = RANDOM.nextInt(publicPockets.size());
        return Lists.newArrayList(publicPockets).get(0);
    }

    public static SchematicV2Handler getInstance() {
        return INSTANCE;
    }

    public List<PocketTemplateV2> getTemplates() {
        return this.templates;
    }

    public List<PocketType> getPocketTypes() {
        return this.pocketTypes;
    }
}
