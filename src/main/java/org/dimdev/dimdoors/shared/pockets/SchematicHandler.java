package org.dimdev.dimdoors.shared.pockets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.MalformedJsonException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.io.IOUtils;
import org.dimdev.ddutils.math.MathUtils;
import org.dimdev.ddutils.schem.Schematic;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.ModConfig;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.dimdev.dimdoors.DimDoors.log;
import static org.dimdev.dimdoors.shared.ModConfig.pockets;

/**
 * @author Robijnvogel
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class SchematicHandler { // TODO: parts of this should be moved to the org.dimdev.ddutils.schem package

    private static final String SAVED_POCKETS_GROUP_NAME = "saved_pockets";
    public static final SchematicHandler INSTANCE = new SchematicHandler();

    public Schematic loadSchematicFromByteArray(byte[] schematicBytecode) {
        Schematic schematic = null;
        try {
            schematic = Schematic.loadFromNBT(CompressedStreamTools.readCompressed(new ByteArrayInputStream(schematicBytecode)));
        } catch(IOException ex) {
            //this would be EXTREMELY unlikely, since this should have been checked earlier.
            log.error("Schematic file for this dungeon could not be read from byte array.",ex);
        }
        return schematic;
    }

    private List<PocketTemplate> templates;
    private Map<String, Map<String,Integer>> nameMap; // group -> name -> index in templates
    private List<Entry<PocketTemplate,Integer>> usageList = new ArrayList<>(); //template and nr of usages
    private final Map<PocketTemplate,Integer> usageMap = new HashMap<>(); //template -> index in usageList
    
    public void loadSchematics() {
        long startTime = System.currentTimeMillis();
        this.templates = new ArrayList<>();
        String[] names = {"default_dungeon_nether","default_dungeon_normal","default_private","default_public","default_blank"}; // TODO: don't hardcode
        for(String name : names) {
            try {
                URL resource = DimDoors.class.getResource("/assets/dimdoors/pockets/json/"+name+".json");
                if(Objects.nonNull(resource)) {
                    String jsonString = IOUtils.toString(resource,UTF_8);
                    this.templates.addAll(loadTemplatesFromJson(jsonString));
                }
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
        // Init json config folder
        File jsonFolder = new File(DimDoors.getConfigurationFolder(),"/jsons");
        if(!jsonFolder.exists()) jsonFolder.mkdirs();
        // Init schematics config folder
        File schematicFolder = new File(DimDoors.getConfigurationFolder(),"/schematics");
        if(!schematicFolder.exists()) schematicFolder.mkdirs();
        // Load config jsons and referenced schematics
        for(File file : Objects.requireNonNull(jsonFolder.listFiles())) {
            if(file.isDirectory() || !file.getName().endsWith(".json")) continue;
            try {
                String jsonString = IOUtils.toString(file.toURI(), UTF_8);
                this.templates.addAll(loadTemplatesFromJson(jsonString));
            } catch(MalformedJsonException e) {
                log.error("Malformed JSON file for schematic {}: {}",file.getName(),e);
            } catch(IOException e) {
                log.error("Error reading file {}. The following exception occurred: ",file.toURI(),e);
            }
        }
        // Load saved schematics
        File saveFolder = new File(DimDoors.getConfigurationFolder(), "/schematics/saved");
        if(saveFolder.exists()) {
            for(File file : Objects.requireNonNull(saveFolder.listFiles())) {
                String fileName = file.getName();
                if(file.isDirectory() || !fileName.endsWith(".schem")) continue;
                try {
                    byte[] schematicBytecode = IOUtils.toByteArray(Files.newInputStream(file.toPath()));
                    Schematic.loadFromNBT(CompressedStreamTools.readCompressed(new ByteArrayInputStream (schematicBytecode)));
                    PocketTemplate template = new PocketTemplate(SAVED_POCKETS_GROUP_NAME,fileName,
                            null,null,null,null,schematicBytecode,-1,0);
                    templates.add(template);
                } catch (MalformedJsonException e) {
                    log.error("Malformed JSON file for schematic {}: {}",fileName,e);
                } catch (IOException e) {
                    log.error("Error reading schematic {}: {}",fileName,e);
                }
            }
        }
        constructNameMap();
        log.info("Loaded {} templates in {} ms.",this.templates.size(),System.currentTimeMillis()-startTime);
    }

    private static List<PocketTemplate> loadTemplatesFromJson(String jsonString) {
        String schematicJarDirectory = "/assets/dimdoors/pockets/schematic/";
        File schematicFolder = new File(DimDoors.getConfigurationFolder(), "/schematics");
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(jsonString);
        JsonObject jsonTemplate = jsonElement.getAsJsonObject();
        //Generate and get templates (without a schematic) of all variations that are valid for the current "maxPocketSize"
        List<PocketTemplate> candidateTemplates = getAllValidVariations(jsonTemplate);
        String subDirectory = jsonTemplate.get("group").getAsString(); //get the subfolder in which the schematics are stored
        List<PocketTemplate> validTemplates = new ArrayList<>();
        for(PocketTemplate template : candidateTemplates) { //it's okay to "tap" this for-loop, even if validTemplates is empty.
            String extendedTemplateLocation = subDirectory.isEmpty() ?
                    template.getId() : subDirectory+"/"+template.getId()+".schem"; //transform the filename accordingly
            //Initialising the possible locations/formats for the schematic file
            InputStream schematicStream = DimDoors.class.getResourceAsStream(schematicJarDirectory + extendedTemplateLocation);
            File schematicFile = new File(schematicFolder, "/" + extendedTemplateLocation);
            //determine which location to load the schematic file from (and what format)
            DataInputStream schematicDataStream = null;
            boolean streamOpened = false;
            boolean isCustomFile = false;
            boolean isValidFormat = true;
            if(Objects.nonNull(schematicStream)) {
                schematicDataStream = new DataInputStream(schematicStream);
                streamOpened = true;
            } else if(schematicFile.exists()) {
                isCustomFile = true;
                try {
                    schematicDataStream = new DataInputStream(new FileInputStream(schematicFile));
                    streamOpened = true;
                } catch (FileNotFoundException ex) {
                    log.error("Schematic file {}.schem did not load correctly from config folder.",
                              template.getId(),ex);
                }
            } else log.error("Schematic \"{}.schem\" was not found in the jar or config directory.",template.getId());
            byte[] schematicBytecode = null;
            if(streamOpened) {
                try {
                    schematicBytecode = IOUtils.toByteArray(schematicDataStream);
                    schematicDataStream.close();
                } catch(IOException ex) {
                    log.error("Schematic file for {} could not be read into byte array.",template.getId(),ex);
                } finally {
                    try {
                        schematicDataStream.close();
                    } catch (IOException ex) {
                        log.error("Error occurred while closing schematicDataStream.",ex);
                    }
                }
            }
            if (isCustomFile) {
                Schematic schematic = null;
                try {
                    //noinspection ConstantConditions
                    schematic = Schematic.loadFromNBT(CompressedStreamTools.readCompressed(new ByteArrayInputStream(schematicBytecode)));
                } catch(Exception ex) {
                    log.error("Schematic file for {} could not be read as a valid schematic NBT file.",
                              template.getId(),ex);
                    isValidFormat = false;
                }
                if(Objects.nonNull(schematic)
                   && (schematic.width>(template.getSize()+1)*16 || schematic.length>(template.getSize()+1)*16)) {
                    log.warn("Schematic {} was bigger than specified in its json file and therefore wasn't loaded",
                             template.getId());
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
        for(JsonElement pocketElement : pockets) {
            JsonObject pocket = pocketElement.getAsJsonObject();
            int size = pocket.get("size").getAsInt();
            if (!ModConfig.pockets.loadAllSchematics && size>ModConfig.pockets.maxPocketSize) continue;
            String id = pocket.get("id").getAsString();
            String type = pocket.has("type") ? pocket.get("type").getAsString() : null;
            String name = pocket.has("name") ? pocket.get("name").getAsString() : null;
            String author = pocket.has("author") ? pocket.get("author").getAsString() : null;
            int baseWeight = pocket.has("baseWeight") ? pocket.get("baseWeight").getAsInt() : 100;
            pocketTemplates.add(new PocketTemplate(group,id,type,name,author,size,baseWeight));
        }
        return pocketTemplates.stream().sorted(Comparator.comparing(PocketTemplate::getId)).collect(Collectors.toList());
    }

    private void constructNameMap() {
        this.nameMap = new HashMap<>();
        //to prevent having to use too many getters
        String bufferedDirectory = null;
        Map<String, Integer> bufferedMap = null;
        for(PocketTemplate template : this.templates) {
            String dirName = template.getGroup();
            if(Objects.nonNull(dirName) && dirName.equals(bufferedDirectory)) //null check not needed
                bufferedMap.put(template.getId(),this.templates.indexOf(template));
            else {
                bufferedDirectory = dirName;
                if(this.nameMap.containsKey(dirName)) { //this will only happen if you have two json files referring to the same directory being loaded non-consecutively
                    bufferedMap = this.nameMap.get(dirName);
                    bufferedMap.put(template.getId(),this.templates.indexOf(template));
                } else {
                    bufferedMap = new HashMap<>();
                    bufferedMap.put(template.getId(),this.templates.indexOf(template));
                    this.nameMap.put(dirName,bufferedMap);
                }
            }
        }
    }

    public Set<String> getTemplateGroups() {
        return nameMap.keySet();
    }

    public Set<String> getTemplateNames(String group) {
        if (nameMap.containsKey(group)) return nameMap.get(group).keySet();
        return new HashSet<>();
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
        return Objects.nonNull(groupMap) && Objects.nonNull(groupMap.get(name)) ?
                this.templates.get(groupMap.get(name)) : null;
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
        Map<PocketTemplate,Float> weightedTemplates = new HashMap<>();
        int largestSize = 0;
        for(PocketTemplate template : this.templates) {
            if(template.getGroup().equals(group) && (maxSize==-1 || template.getSize()<=maxSize)) {
                if(getLargest && template.getSize()>largestSize) {
                    weightedTemplates = new HashMap<>();
                    largestSize = template.getSize();
                }
                weightedTemplates.put(template,template.getWeight(depth));
            }
        }
        if (weightedTemplates.isEmpty()) {
            log.warn("getRandomTemplate failed, no templates matching those criteria were found.");
            return null; // TODO: switch to exception system
        } return MathUtils.weightedRandom(weightedTemplates);
    }

    public PocketTemplate getPersonalPocketTemplate() {
        return getRandomTemplate("private",-1,pockets.privatePocketSize,true);
    }

    public PocketTemplate getPublicPocketTemplate() {
        return getRandomTemplate("public",-1,pockets.publicPocketSize,true);
    }

    public static void saveSchematic(Schematic schematic, String id) {
        NBTTagCompound schematicNBT = schematic.saveToNBT();
        File saveFolder = new File(DimDoors.getConfigurationFolder(),"/schematics/saved");
        if(!saveFolder.exists()) saveFolder.mkdirs();
        File saveFile = new File(saveFolder.getAbsolutePath()+"/"+id+".schem");
        try {
            saveFile.getParentFile().mkdirs();
            saveFile.createNewFile();
            DataOutputStream schematicDataStream = new DataOutputStream(Files.newOutputStream(saveFile.toPath()));
            CompressedStreamTools.writeCompressed(schematicNBT, schematicDataStream);
            schematicDataStream.flush();
            schematicDataStream.close();
        } catch(IOException ex) {
            log.error("Something went wrong while saving {} to disk.",saveFile.getAbsolutePath(),ex);
        }
    }

    public void saveSchematicForEditing(Schematic schematic, String id) {
        saveSchematic(schematic, id);
        if(!this.nameMap.containsKey(SAVED_POCKETS_GROUP_NAME)) this.nameMap.put(SAVED_POCKETS_GROUP_NAME,new HashMap<>());
        Map<String, Integer> savedDungeons = this.nameMap.get(SAVED_POCKETS_GROUP_NAME);
        if(savedDungeons.containsKey(id)) this.templates.remove((int) savedDungeons.remove(id));
        //create byte array
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] schematicBytecode = null;
        try {
            CompressedStreamTools.writeCompressed(schematic.saveToNBT(), byteStream);
            schematicBytecode = byteStream.toByteArray();
            byteStream.close();
        } catch (IOException ex) {
            log.error("Something went wrong while converting schematic {} to bytecode.",id,ex);
        }
        if(Objects.nonNull(schematicBytecode)) {
            this.templates.add(new PocketTemplate(SAVED_POCKETS_GROUP_NAME,id,null,null,null,schematic,
                                             schematicBytecode,-1,0));
            this.nameMap.get(SAVED_POCKETS_GROUP_NAME).put(id,this.templates.size()-1);
        }
    }
    
    private int getUsage(PocketTemplate template) {
        if (!this.usageMap.containsKey(template)) return -1;
        int index = this.usageMap.get(template);
        if(this.usageList.size()<=index) return -1;
        PocketTemplate listTemplate = this.usageList.get(index).getKey();
        if(listTemplate==template) return this.usageList.get(index).getValue();
        else {//should never happen, but you never really know.
           log.warn("Pocket Template usage list is desynced from the usage map, re-sorting and syncing now.");
            reSortUsages();
            return getUsage(template);
        }
    }
    
    public boolean notUsedOftenEnough(PocketTemplate template) {
        int maxNrOfCachedSchematics = pockets.cachedSchematics;
        int usageRank = this.usageMap.get(template);
        return usageRank>=maxNrOfCachedSchematics;
    }
    
    public void incrementUsage(PocketTemplate template) {  
        int startIndex;
        int newUsage;
        if(!this.usageMap.containsKey(template)) {
            this.usageList.add(new SimpleEntry<>(null,0)); //add a dummy entry at the end
            startIndex = this.usageList.size()-1;
            newUsage = 1;
        } else {
            startIndex = this.usageMap.get(template);
            newUsage = this.usageList.get(startIndex).getValue()+1;
        }
        int insertionIndex = findFirstEqualOrLessUsage(newUsage,0,startIndex);
        //shift all entries between the insertionIndex and the currentIndex to the right
        PocketTemplate currentTemplate;
        for(int i=startIndex; i>insertionIndex; i--) {
            this.usageList.set(i,this.usageList.get(i-1));
            currentTemplate = this.usageList.get(i).getKey();
            this.usageMap.put(currentTemplate, i);
        }            
        //insert the incremented entry at the correct place
        this.usageList.set(insertionIndex,new SimpleEntry<>(template,newUsage));
        this.usageMap.put(template,insertionIndex);
        if(insertionIndex<pockets.cachedSchematics) { //if the schematic of this template is supposed to get cached
            if(this.usageList.size()>pockets.cachedSchematics)  //if there are more used templates than there are schematics allowed to be cached
                this.usageList.get(pockets.cachedSchematics).getKey().setSchematic(null); //make sure that the number of cached schematics is limited
        }
    }
    
    //uses binary search
    private int findFirstEqualOrLessUsage(int usage, int indexMin, int indexMax) {
        if(this.usageList.get(indexMin).getValue()<=usage) return indexMin;
        int halfwayIndex = (indexMin+indexMax)/2;
        return this.usageList.get(halfwayIndex).getValue()>usage ?
                findFirstEqualOrLessUsage(usage,halfwayIndex+1,indexMax) :
                findFirstEqualOrLessUsage(usage,indexMin,halfwayIndex);
    }
    
    private void reSortUsages() {
        //sort the usageList
        this.usageList = mergeSortPairArrayByPairValue(this.usageList);
        //make sure that everything in the usageList is actually in the usageMap
        for(Entry<PocketTemplate, Integer> pair: this.usageList) this.usageMap.put(pair.getKey(), pair.getValue());
        //make sure that everything in the usageMap is actually in the usageList
        for(Entry<PocketTemplate, Integer> entry : this.usageMap.entrySet()) {
            PocketTemplate template = entry.getKey();
            int index = entry.getValue();
            PocketTemplate template2 = this.usageList.get(index).getKey();
            if(template!=template2) {
                entry.setValue(this.usageList.size());
                this.usageList.add(new SimpleEntry<>(template, 1));
            }
        }
    }
    
    //TODO make these a more common implementation for which PocketTemplate could be anything.
    private List<Entry<PocketTemplate,Integer>> mergeSortPairArrayByPairValue(List<Entry<PocketTemplate,Integer>> input) {
        if (input.size()<2) return input;
        else {
            List<Entry<PocketTemplate,Integer>> a = mergeSortPairArrayByPairValue(input.subList(0,input.size()/2));
            List<Entry<PocketTemplate,Integer>> b = mergeSortPairArrayByPairValue(input.subList(input.size()/2,input.size()));
            return mergePairArraysByPairValue(a,b);
        }
    }
    
    private List<Entry<PocketTemplate,Integer>> mergePairArraysByPairValue(List<Entry<PocketTemplate,Integer>> a,
            List<Entry<PocketTemplate,Integer>> b) {
        List<Entry<PocketTemplate,Integer>> output = new ArrayList<>();
        int aPointer = 0;
        int bPointer = 0;
        while(aPointer<a.size() || bPointer<b.size()) {
            if(aPointer>=a.size()) {
                output.addAll(b.subList(bPointer,b.size()));
                break;
            } 
            if(bPointer>=b.size()) {
                output.addAll(a.subList(aPointer,a.size()));
                break;
            }
            int aValue = a.get(aPointer).getValue();
            int bValue = b.get(bPointer).getValue();
            if(aValue>=bValue) {
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