package org.dimdev.dimdoors.pockets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimcore.schematic.Schematic;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.util.math.MathUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

/**
 * @author Robijnvogel
 */
public class SchematicHandler { // TODO: parts of this should be moved to the org.dimdev.ddutils.schem package
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String SAVED_POCKETS_GROUP_NAME = "saved_pockets";
    public static final SchematicHandler INSTANCE = new SchematicHandler();

    private static String getFolder() {
        return "config"; // TODO
    }

    public Schematic loadSchematicFromByteArray(byte[] schematicBytecode) {
        Schematic schematic = null;
        try {
            schematic = Schematic.fromTag(NbtIo.readCompressed(new ByteArrayInputStream(schematicBytecode)));
        } catch (IOException ex) {
            //this would be EXTREMELY unlikely, since this should have been checked earlier.
            LOGGER.error("Schematic file for this dungeon could not be read from byte array.", ex);
        }
        return schematic;
    }

    private List<PocketTemplate> templates;
    private Map<String, Map<String, Integer>> nameMap; // group -> name -> index in templates
    private List<Entry<PocketTemplate, Integer>> usageList = new ArrayList<>(); //template and nr of usages
    private final Map<PocketTemplate, Integer> usageMap = new HashMap<>(); //template -> index in usageList

    public void loadSchematics() {
        long startTime = System.currentTimeMillis();

        this.templates = new ArrayList<>();

        String[] names = {"default_dungeon_nether", "default_dungeon_normal", "default_private", "default_public", "default_blank"}; // TODO: don't hardcode
        for (String name : names) {
            try {
                URL resource = DimensionalDoorsInitializer.class.getResource("/data/dimdoors/pockets/json/" + name + ".json");
                String jsonString = IOUtils.toString(resource, StandardCharsets.UTF_8);
                this.templates.addAll(loadTemplatesFromJson(jsonString));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Init json config folder
        File jsonFolder = new File(getFolder(), "/jsons");
        if (!jsonFolder.exists()) {
            jsonFolder.mkdirs();
        }
        // Init schematics config folder
        File schematicFolder = new File(getFolder(), "/schematics");
        if (!schematicFolder.exists()) {
            schematicFolder.mkdirs();
        }

        // Load config jsons and referenced schematics
        for (File file : jsonFolder.listFiles()) {
            if (file.isDirectory() || !file.getName().endsWith(".json")) continue;
            try {
                String jsonString = IOUtils.toString(file.toURI(), StandardCharsets.UTF_8);
                this.templates.addAll(loadTemplatesFromJson(jsonString));
            } catch (IOException e) {
                LOGGER.error("Error reading file " + file.toURI() + ". The following exception occured: ", e);
            }
        }

        // Load saved schematics
        File saveFolder = new File(getFolder(), "/schematics/saved");
        if (saveFolder.exists()) {
            for (File file : saveFolder.listFiles()) {
                if (file.isDirectory() || !file.getName().endsWith(".schem")) continue;
                try {
                    byte[] schematicBytecode = IOUtils.toByteArray(new FileInputStream(file));
                    Schematic.fromTag(NbtIo.readCompressed(new ByteArrayInputStream(schematicBytecode)));
                    PocketTemplate template = new PocketTemplate(SAVED_POCKETS_GROUP_NAME, file.getName(), null, null, null, null, schematicBytecode, -1, 0);
                    this.templates.add(template);
                } catch (IOException e) {
                    LOGGER.error("Error reading schematic " + file.getName() + ": " + e);
                }
            }
        }

        this.constructNameMap();

        LOGGER.info("Loaded " + this.templates.size() + " templates in " + (System.currentTimeMillis() - startTime) + " ms.");
    }

    private static List<PocketTemplate> loadTemplatesFromJson(String jsonString) {
        String schematicJarDirectory = "/data/dimdoors/pockets/schematic/";
        File schematicFolder = new File(getFolder(), "/schematics");

        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(jsonString);
        JsonObject jsonTemplate = jsonElement.getAsJsonObject();

        //Generate and get templates (without a schematic) of all variations that are valid for the current "maxPocketSize"
        List<PocketTemplate> candidateTemplates = getAllValidVariations(jsonTemplate);

        String subDirectory = jsonTemplate.get("group").getAsString(); //get the subfolder in which the schematics are stored

        List<PocketTemplate> validTemplates = new ArrayList<>();
        for (PocketTemplate template : candidateTemplates) { //it's okay to "tap" this for-loop, even if validTemplates is empty.
            String extendedTemplatelocation = subDirectory.equals("") ? template.getId() : subDirectory + "/" + template.getId() + ".schem"; //transform the filename accordingly

            //Initialising the possible locations/formats for the schematic file
            InputStream schematicStream = DimensionalDoorsInitializer.class.getResourceAsStream(schematicJarDirectory + extendedTemplatelocation);
            File schematicFile = new File(schematicFolder, "/" + extendedTemplatelocation);

            //determine which location to load the schematic file from (and what format)
            DataInputStream schematicDataStream = null;
            boolean streamOpened = false;
            boolean isCustomFile = false;
            boolean isValidFormat = true;
            if (schematicStream != null) {
                schematicDataStream = new DataInputStream(schematicStream);
                streamOpened = true;
            } else if (schematicFile.exists()) {
                isCustomFile = true;
                try {
                    schematicDataStream = new DataInputStream(new FileInputStream(schematicFile));
                    streamOpened = true;
                } catch (FileNotFoundException ex) {
                    LOGGER.error("Schematic file " + template.getId() + ".schem did not load correctly from config folder.", ex);
                }
            } else {
                LOGGER.error("Schematic \"" + template.getId() + ".schem\" was not found in the jar or config directory.");
            }

            byte[] schematicBytecode = null;
            if (streamOpened) {
                try {
                    schematicBytecode = IOUtils.toByteArray(schematicDataStream);
                    schematicDataStream.close();
                } catch (IOException ex) {
                    LOGGER.error("Schematic file for " + template.getId() + " could not be read into byte array.", ex);
                } finally {
                    try {
                        schematicDataStream.close();
                    } catch (IOException ex) {
                        LOGGER.error("Error occured while closing schematicDataStream.", ex);
                    }
                }
            }

            if (isCustomFile) {
                Schematic schematic = null;
                try {
                    schematic = Schematic.fromTag(NbtIo.readCompressed(new ByteArrayInputStream(schematicBytecode)));
                } catch (Exception ex) {
                    LOGGER.error("Schematic file for " + template.getId() + " could not be read as a valid schematic NBT file.", ex);
                    isValidFormat = false;
                }

                if (schematic != null
                        && (schematic.sizeX > (template.getSize() + 1) * 16 || schematic.sizeZ > (template.getSize() + 1) * 16)) {
                    LOGGER.warn("Schematic " + template.getId() + " was bigger than specified in its json file and therefore wasn't loaded");
                    isValidFormat = false;
                }
            }

            if (streamOpened && isValidFormat) {
                template.setSchematicBytecode(schematicBytecode);
                validTemplates.add(template);
            }
        }

        return validTemplates;
    }

    private static List<PocketTemplate> getAllValidVariations(JsonObject jsonTemplate) {
        List<PocketTemplate> pocketTemplates = new ArrayList<>();

        final String group = jsonTemplate.get("group").getAsString();

        final JsonArray pockets = jsonTemplate.getAsJsonArray("pockets");

        //convert the variations arraylist to a list of pocket templates
        for (JsonElement pocketElement : pockets) {
            JsonObject pocket = pocketElement.getAsJsonObject();
            int size = pocket.get("size").getAsInt();
            if (!ModConfig.INSTANCE.getPocketsConfig().loadAllSchematics && size > ModConfig.INSTANCE.getPocketsConfig().maxPocketSize) continue;
            String id = pocket.get("id").getAsString();
            String type = pocket.has("type") ? pocket.get("type").getAsString() : null;
            String name = pocket.has("name") ? pocket.get("name").getAsString() : null;
            String author = pocket.has("author") ? pocket.get("author").getAsString() : null;
            int baseWeight = pocket.has("baseWeight") ? pocket.get("baseWeight").getAsInt() : 100;
            pocketTemplates.add(new PocketTemplate(group, id, type, name, author, null, null, size, baseWeight));
        }

        return pocketTemplates.stream().sorted(Comparator.comparing(PocketTemplate::getId)).collect(Collectors.toList());
    }

    private void constructNameMap() {
        this.nameMap = new HashMap<>();
        //to prevent having to use too many getters
        String bufferedDirectory = null;
        Map<String, Integer> bufferedMap = null;

        for (PocketTemplate template : this.templates) {
            String dirName = template.getGroup();
            if (dirName != null && dirName.equals(bufferedDirectory)) { //null check not needed
                bufferedMap.put(template.getId(), this.templates.indexOf(template));
            } else {
                bufferedDirectory = dirName;
                if (this.nameMap.containsKey(dirName)) { //this will only happen if you have two json files referring to the same directory being loaded non-consecutively
                    bufferedMap = this.nameMap.get(dirName);
                    bufferedMap.put(template.getId(), this.templates.indexOf(template));
                } else {
                    bufferedMap = new HashMap<>();
                    bufferedMap.put(template.getId(), this.templates.indexOf(template));
                    this.nameMap.put(dirName, bufferedMap);
                }
            }
        }
    }

    public Set<String> getTemplateGroups() {
        return this.nameMap.keySet();
    }

    public Set<String> getTemplateNames(String group) {
        if (this.nameMap.containsKey(group)) {
            return this.nameMap.get(group).keySet();
        } else {
            return new HashSet<>();
        }
    }

    /**
     * Gets a loaded PocketTemplate by its group and name.
     *
     * @param group Template group
     * @param name  Template name
     * @return The dungeon template with that group and name, or null if it wasn't found
     */
    public PocketTemplate getTemplate(String group, String name) {
        Map<String, Integer> groupMap = this.nameMap.get(group);
        if (groupMap == null) {
            return null;
        }
        Integer index = groupMap.get(name);
        if (index == null) {
            return null;
        }
        return this.templates.get(index);
    }

    /**
     * Gets a random template matching certain criteria.
     *
     * @param group      The template group to choose from.
     * @param maxSize    Maximum size the template can be.
     * @param getLargest Setting this to true will always get the largest template size in that group,
     *                   but still randomly out of the templates with that size (ex. for private and public pockets)
     * @return A random template matching those criteria, or null if none were found
     */
    public PocketTemplate getRandomTemplate(String group, int depth, int maxSize, boolean getLargest) { // TODO: multiple groups
        // TODO: cache this for faster calls:
        Map<PocketTemplate, Float> weightedTemplates = new HashMap<>();
        int largestSize = 0;
         for (PocketTemplate template : this.templates) {
            if (template.getGroup().equals(group) && (maxSize == -1 || template.getSize() <= maxSize)) {
                if (getLargest && template.getSize() > largestSize) {
                    weightedTemplates = new HashMap<>();
                    largestSize = template.getSize();
                }
                weightedTemplates.put(template, template.getWeight(depth));
            }
        }
        if (weightedTemplates.isEmpty()) {
            LOGGER.warn("getRandomTemplate failed, no templates matching those criteria were found.");
            return null; // TODO: switch to exception system
        }

        return MathUtil.weightedRandom(weightedTemplates);
    }

    public PocketTemplate getPersonalPocketTemplate() {
        return this.getRandomTemplate("private", -1, ModConfig.INSTANCE.getPocketsConfig().privatePocketSize, true);
    }

    public PocketTemplate getPublicPocketTemplate() {
        return this.getRandomTemplate("public", -1, ModConfig.INSTANCE.getPocketsConfig().publicPocketSize, true);
    }

    public static void saveSchematic(Schematic schematic, String id) {
        CompoundTag schematicNBT = schematic.saveToNBT();
        File saveFolder = new File(getFolder(), "/schematics/saved");
        if (!saveFolder.exists()) {
            saveFolder.mkdirs();
        }

        File saveFile = new File(saveFolder.getAbsolutePath() + "/" + id + ".schem");
        try {
            saveFile.getParentFile().mkdirs();
            saveFile.createNewFile();
            DataOutputStream schematicDataStream = new DataOutputStream(new FileOutputStream(saveFile));
            NbtIo.writeCompressed(schematicNBT, schematicDataStream);
            schematicDataStream.flush();
            schematicDataStream.close();
        } catch (IOException ex) {
            LOGGER.error("Something went wrong while saving " + saveFile.getAbsolutePath() + " to disk.", ex);
        }
    }

    public void saveSchematicForEditing(Schematic schematic, String id) {
        saveSchematic(schematic, id);

        if (!this.nameMap.containsKey(SAVED_POCKETS_GROUP_NAME)) {
            this.nameMap.put(SAVED_POCKETS_GROUP_NAME, new HashMap<>());
        }

        Map<String, Integer> savedDungeons = this.nameMap.get(SAVED_POCKETS_GROUP_NAME);
        if (savedDungeons.containsKey(id)) {
            this.templates.remove((int) savedDungeons.remove(id));
        }

        //create byte array
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] schematicBytecode = null;
        try {
            NbtIo.writeCompressed(schematic.saveToNBT(), byteStream);
            schematicBytecode = byteStream.toByteArray();
            byteStream.close();
        } catch (IOException ex) {
            LOGGER.error("Something went wrong while converting schematic " + id + " to bytecode.", ex);
        }

        if (schematicBytecode != null) {
            this.templates.add(new PocketTemplate(SAVED_POCKETS_GROUP_NAME, id, null, null, null, schematic, schematicBytecode, -1, 0));
            this.nameMap.get(SAVED_POCKETS_GROUP_NAME).put(id, this.templates.size() - 1);
        }
    }

    private int getUsage(PocketTemplate template) {
        if (!this.usageMap.containsKey(template)) return -1;
        int index = this.usageMap.get(template);
        if (this.usageList.size() <= index) return -1;
        PocketTemplate listTemplate = this.usageList.get(index).getKey();
        if (listTemplate == template) {
            int usage = this.usageList.get(index).getValue();
            return usage;
        } else {//should never happen, but you never really know.
            LOGGER.warn("Pocket Template usage list is desynched from the usage map, re-sorting and synching now.");
            this.reSortUsages();
            return this.getUsage(template);
        }
    }

    public boolean isUsedOftenEnough(PocketTemplate template) {
        int maxNrOfCachedSchematics = ModConfig.INSTANCE.getPocketsConfig().cachedSchematics;
        int usageRank = this.usageMap.get(template);
        return usageRank < maxNrOfCachedSchematics;
    }

    public void incrementUsage(PocketTemplate template) {
        int startIndex;
        int newUsage;
        if (!this.usageMap.containsKey(template)) {
            this.usageList.add(new SimpleEntry<>(null, 0)); //add a dummy entry at the end
            startIndex = this.usageList.size() - 1;
            newUsage = 1;
        } else {
            startIndex = this.usageMap.get(template);
            newUsage = this.usageList.get(startIndex).getValue() + 1;
        }

        int insertionIndex = this.findFirstEqualOrLessUsage(newUsage, 0, startIndex);
        //shift all entries inbetween the insertionIndex and the currentIndex to the right
        PocketTemplate currentTemplate;
        for (int i = startIndex; i > insertionIndex; i--) {
            this.usageList.set(i, this.usageList.get(i - 1));
            currentTemplate = this.usageList.get(i).getKey();
            this.usageMap.put(currentTemplate, i);
        }
        //insert the incremented entry at the correct place
        this.usageList.set(insertionIndex, new SimpleEntry(template, newUsage));
        this.usageMap.put(template, insertionIndex);

        if (insertionIndex < ModConfig.INSTANCE.getPocketsConfig().cachedSchematics) { //if the schematic of this template is supposed to get cached
            if (this.usageList.size() > ModConfig.INSTANCE.getPocketsConfig().cachedSchematics) { //if there are more used templates than there are schematics allowed to be cached
                this.usageList.get(ModConfig.INSTANCE.getPocketsConfig().cachedSchematics).getKey().setSchematic(null); //make sure that the number of cached schematics is limited
            }
        }
    }

    //uses binary search
    private int findFirstEqualOrLessUsage(int usage, int indexMin, int indexMax) {

        if (this.usageList.get(indexMin).getValue() <= usage) {
            return indexMin;
        }
        int halfwayIndex = (indexMin + indexMax) / 2;
        if (this.usageList.get(halfwayIndex).getValue() > usage) {
            return this.findFirstEqualOrLessUsage(usage, halfwayIndex + 1, indexMax);
        } else {
            return this.findFirstEqualOrLessUsage(usage, indexMin, halfwayIndex);
        }
    }

    private void reSortUsages() {
        //sort the usageList
        this.usageList = this.mergeSortPairArrayByPairValue(this.usageList);
        //make sure that everything in the usageList is actually in the usageMap
        for (Entry<PocketTemplate, Integer> pair : this.usageList) {
            this.usageMap.put(pair.getKey(), pair.getValue());
        }

        //make sure that everything in the usageMap is actually in the usageList
        for (Entry<PocketTemplate, Integer> entry : this.usageMap.entrySet()) {
            PocketTemplate template = entry.getKey();
            int index = entry.getValue();
            PocketTemplate template2 = this.usageList.get(index).getKey();
            if (index >= this.usageList.size() || template != template2) {
                entry.setValue(this.usageList.size());
                this.usageList.add(new SimpleEntry(template, 1));
            }
        }
    }

    //TODO make these a more common implementation for which PocketTemplate could be anything.
    private List<Entry<PocketTemplate, Integer>> mergeSortPairArrayByPairValue(List<Entry<PocketTemplate, Integer>> input) {
        if (input.size() < 2) {
            return input;
        } else {
            List<Entry<PocketTemplate, Integer>> a = this.mergeSortPairArrayByPairValue(input.subList(0, input.size() / 2));
            List<Entry<PocketTemplate, Integer>> b = this.mergeSortPairArrayByPairValue(input.subList(input.size() / 2, input.size()));
            return this.mergePairArraysByPairValue(a, b);
        }
    }

    private List<Entry<PocketTemplate, Integer>> mergePairArraysByPairValue(List<Entry<PocketTemplate, Integer>> a, List<Entry<PocketTemplate, Integer>> b) {
        List<Entry<PocketTemplate, Integer>> output = new ArrayList<>();
        int aPointer = 0;
        int bPointer = 0;
        while (aPointer < a.size() || bPointer < b.size()) {
            if (aPointer >= a.size()) {
                output.addAll(b.subList(bPointer, b.size()));
                break;
            }
            if (bPointer >= b.size()) {
                output.addAll(a.subList(aPointer, a.size()));
                break;
            }
            int aValue = a.get(aPointer).getValue();
            int bValue = b.get(bPointer).getValue();
            if (aValue >= bValue) {
                output.add(a.get(aPointer));
                aPointer++;
            } else {
                output.add(b.get(bPointer));
                bPointer++;
            }
        }
        return output;
    }
}
