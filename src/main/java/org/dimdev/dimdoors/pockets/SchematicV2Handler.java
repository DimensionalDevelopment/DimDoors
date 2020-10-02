package org.dimdev.dimdoors.pockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dimdev.dimcore.schematic.v2.Schematic;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

public class SchematicV2Handler {
    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
    private final List<PocketTemplateV2> templates = Lists.newArrayList();
    private Map<String, Map<String, Integer>> nameMap;

    public void load() {
        long startTime = System.currentTimeMillis();
        Set<String> names = ImmutableSet.of("default_private", "default_public");
        for (String name : names) {
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(SchematicV2Handler.class.getResource(String.format("/data/dimdoors/pockets/json/v2/%s.json", name)).toURI()))) {
                List<String> result = new ArrayList<>();
                while(true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    result.add(line);
                }
                JsonObject json = GSON.fromJson(String.join("", result), JsonObject.class);
                PocketType type = PocketType.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow(false, System.err::println).getFirst();
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadResourceSchematics(PocketType type) throws URISyntaxException, IOException {
        String group = type.getGroup();
        Path basePath = Paths.get(SchematicV2Handler.class.getResource(String.format("/data/dimdoors/pockets/schematic/v2/%s/", group)).toURI());
        for (PocketType.PocketEntry entry : type.getEntries()) {
            Path schemPath = basePath.resolve(entry.getName() + ".schem");
            CompoundTag schemTag = NbtIo.readCompressed(Files.newInputStream(schemPath));
            Schematic schematic = Schematic.fromTag(schemTag);
            this.templates.add(new PocketTemplateV2(schematic, group, entry.getSize(), entry.getName()));
        }
    }
}
