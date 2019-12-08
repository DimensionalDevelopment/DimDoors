package org.dimdev.util.schem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.Map.Entry;

public class Schematic {
    private static final Logger LOGGER = LogManager.getLogger();
    public int version = 1;
    public String author = null;
    public String name = null;
    public long creationDate;
    public String[] requiredMods = {};
    public short width;
    public short height;
    public short length;
    public int[] offset = {0, 0, 0};
    public int paletteMax;
    public List<BlockState> palette = new ArrayList<>();
    public short[][][] blockData;
    public List<CompoundTag> tileEntities = new ArrayList<>();
    public List<CompoundTag> entities = new ArrayList<>();

    public Schematic() {
        paletteMax = -1;
    }

    public Schematic(short width, short height, short length) {
        this();
        this.width = width;
        this.height = height;
        this.length = length;
        blockData = new short[width][height][length];
        palette.add(Blocks.AIR.getDefaultState());
        paletteMax++;
        creationDate = System.currentTimeMillis();
    }

    public Schematic(String name, String author, short width, short height, short length) {
        this(width, height, length);
        this.name = name;
        this.author = author;
    }

    public static Schematic loadFromNBT(CompoundTag nbt) {
        Schematic schematic = new Schematic();
        schematic.version = nbt.getInt("Version");

        schematic.creationDate = System.currentTimeMillis();

        if (nbt.contains("Metadata")) {
            CompoundTag metadataCompound = nbt.getCompound("Metadata").getCompound(".");

            if (nbt.contains("Author")) {
                schematic.author = metadataCompound.getString("Author");
            }

            schematic.name = metadataCompound.getString("Name");

            if (nbt.contains("Date")) { //Date is not required
                schematic.creationDate = metadataCompound.getLong("Date");
            } else {
                schematic.creationDate = -1;
            }

            if (nbt.contains("RequiredMods")) { //RequiredMods is not required (ironically)
                ListTag requiredModsTagList = (ListTag) metadataCompound.get("RequiredMods");
                schematic.requiredMods = new String[requiredModsTagList.size()];
                for (int i = 0; i < requiredModsTagList.size(); i++) {
                    schematic.requiredMods[i] = requiredModsTagList.getString(i);
                }
            }
        }

        schematic.width = nbt.getShort("Width");
        schematic.height = nbt.getShort("Height");
        schematic.length = nbt.getShort("Length");

        if (nbt.contains("Offset")) { // Offset is not required
            schematic.offset = nbt.getIntArray("Offset");
        }

        CompoundTag paletteTag = nbt.getCompound("Palette"); //Palette is not required, however since we assume that the schematic contains at least some blocks, we can also assume that thee has to be a Palette
        Map<Integer, String> paletteMap = new HashMap<>();

        for (String key : paletteTag.getKeys()) {
            int paletteID = paletteTag.getInt(key);
            paletteMap.put(paletteID, key); //basically use the reversed order (key becomes value and value becomes key)
        }

        for (int i = 0; i < paletteMap.size(); i++) {
            String blockStateString = paletteMap.get(i);
            char lastBlockStateStringChar = blockStateString.charAt(blockStateString.length() - 1);
            String id;
            String state;
            if (lastBlockStateStringChar == ']') {
                String[] blockAndStateStrings = blockStateString.split("\\[");
                id = blockAndStateStrings[0];
                state = blockAndStateStrings[1];
                state = state.substring(0, state.length() - 1); //remove the "]" at the end
            } else {
                id = blockStateString;
                state = "";
            }

            Block block = Registry.BLOCK.get(new Identifier(id));

            BlockState blockstate = block.getDefaultState();

            if (!state.isEmpty()) {
                String[] properties = state.split(",");
                blockstate = getBlockStateWithProperties(block, properties);
            }

            schematic.palette.add(blockstate); //@todo, can we assume that a schematic file always has all palette integers used from 0 to pallettemax-1?
        }

        if (nbt.contains("PaletteMax")) {
            schematic.paletteMax = nbt.getInt("PaletteMax");
        } else {
            schematic.paletteMax = schematic.palette.size() - 1;
        }

        byte[] blockDataIntArray = nbt.getByteArray("BlockData");
        schematic.blockData = new short[schematic.width][schematic.height][schematic.length];
        for (int x = 0; x < schematic.width; x++) {
            for (int y = 0; y < schematic.height; y++) {
                for (int z = 0; z < schematic.length; z++) {
                    schematic.blockData[x][y][z] = blockDataIntArray[x + z * schematic.width + y * schematic.width * schematic.length]; //according to the documentation on https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-1.md
                }
            }
        }

        if (nbt.contains("TileEntities")) {
            for (Tag tag : (ListTag) nbt.get("TileEntities")) {
                schematic.tileEntities.add((CompoundTag) tag);
            }
        }

        if (nbt.contains("Entities")) {
            for (Tag tag : (ListTag) nbt.get("Entities")) {
                schematic.entities.add((CompoundTag) tag);
            }
        }

        return schematic;
    }

    public CompoundTag saveToNBT() {
        CompoundTag nbt = new CompoundTag();

        nbt.putInt("Version", version);
        CompoundTag metadataCompound = new CompoundTag();

        if (author != null) {
            metadataCompound.putString("Author", author);
        }

        metadataCompound.putString("Name", name);
        if (creationDate != -1) metadataCompound.putLong("Date", creationDate);
        ListTag requiredModsTagList = new ListTag();

        for (String requiredMod : requiredMods) {
            requiredModsTagList.add(StringTag.of(requiredMod));
        }

        metadataCompound.put("RequiredMods", requiredModsTagList);
        nbt.put("Metadata", metadataCompound);

        nbt.putShort("Width", width);
        nbt.putShort("Height", height);
        nbt.putShort("Length", length);
        nbt.putIntArray("Offset", offset);
        nbt.putInt("PaletteMax", paletteMax);

        CompoundTag paletteNBT = new CompoundTag();

        for (int i = 0; i < palette.size(); i++) {
            BlockState state = palette.get(i);
            String blockStateString = getBlockStateStringFromState(state);
            paletteNBT.putInt(blockStateString, i);
        }

        nbt.put("Palette", paletteNBT);

        byte[] blockDataIntArray = new byte[width * height * length];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    blockDataIntArray[x + z * width + y * width * length] = (byte) blockData[x][y][z]; //according to the documentation on https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-1.md
                }
            }
        }

        nbt.putByteArray("BlockData", blockDataIntArray);

        ListTag tileEntitiesTagList = new ListTag();
        tileEntitiesTagList.addAll(tileEntities);
        nbt.put("TileEntities", tileEntitiesTagList);

        ListTag entitiesTagList = new ListTag();
        entitiesTagList.addAll(entities);
        nbt.put("Entities", entitiesTagList);

        return nbt;
    }

    static BlockState getBlockStateWithProperties(Block block, String[] properties) {
        Map<String, String> propertyAndBlockStringsMap = new HashMap<>();

        for (String property : properties) {
            String[] propertyAndBlockStrings = property.split("=");
            propertyAndBlockStringsMap.put(propertyAndBlockStrings[0], propertyAndBlockStrings[1]);
        }

        StateManager<Block, BlockState> stateManager = block.getStateManager();
        BlockState chosenState = block.getDefaultState();

        for (Entry<String, String> entry : propertyAndBlockStringsMap.entrySet()) {
            Property<?> property = stateManager.getProperty(entry.getKey());

            if (property != null) {
                Comparable<?> value = null;
                for (Comparable<?> object : property.getValues()) {
                    if (object.toString().equals(entry.getValue())) {
                        value = object;
                        break;
                    }
                }

                if (value != null) {
                    // property is Property<?>, value is Comparable<?>, and the ?s refer to the same type because
                    // IProperty<T>.getAllowedValues() returns Collection<T>, but the compiler doesn't keep track of
                    // this, so casting to raw types:
                    //noinspection rawtypes
                    chosenState = chosenState.with(property, (Comparable) value);
                }
            }
        }

        return chosenState;
    }

    private static String getBlockStateStringFromState(BlockState state) {
        Block block = state.getBlock();
        String blockNameString = Registry.BLOCK.getId(block).toString();
        StringBuilder blockStateString = new StringBuilder();
        BlockState defaultState = block.getDefaultState();

        if (state == defaultState) {
            return blockNameString;
        } else {
            for (Property<?> property : state.getProperties()) {
                String value = state.get(property).toString();
                String defaultValue = defaultState.get(property).toString();

                if (!defaultValue.equals(value)) {
                    String firstHalf = property.getName();
                    String secondHalf = state.get(property).toString();
                    String propertyString = firstHalf + "=" + secondHalf;
                    blockStateString.append(propertyString).append(",");
                }
            }

            blockStateString = new StringBuilder(blockStateString.substring(0, blockStateString.length() - 1)); //removes the last comma
            return blockNameString + "[" + blockStateString + "]";
        }
    }

    public static Schematic createFromWorld(World world, BlockPos from, BlockPos to) {
        BlockPos dimensions = to.subtract(from).add(1, 1, 1);
        Schematic schematic = new Schematic((short) dimensions.getX(), (short) dimensions.getY(), (short) dimensions.getZ());

        Set<String> mods = new HashSet<>();

        for (int x = 0; x < dimensions.getX(); x++) {
            for (int y = 0; y < dimensions.getY(); y++) {
                for (int z = 0; z < dimensions.getZ(); z++) {
                    BlockPos pos = new BlockPos(from.getX() + x, from.getY() + y, from.getZ() + z);

                    BlockState state = world.getBlockState(pos);
                    String id = getBlockStateStringFromState(state);
                    if (id.contains(":")) mods.add(id.split(":")[0]);
                    schematic.setBlockState(x, y, z, state);

                    BlockEntity BlockEntity = world.getChunk(pos).getBlockEntity(pos);
                    if (BlockEntity != null) {
                        CompoundTag BlockEntityNBT = BlockEntity.toTag(new CompoundTag());
                        BlockEntityNBT.putInt("x", BlockEntityNBT.getInt("x") - from.getX());
                        BlockEntityNBT.putInt("y", BlockEntityNBT.getInt("y") - from.getY());
                        BlockEntityNBT.putInt("z", BlockEntityNBT.getInt("z") - from.getZ());

                        schematic.tileEntities.add(BlockEntityNBT);
                    }
                }
            }
        }

        for (Entity entity : world.getEntities((Entity) null, getBoundingBox(from, to), entity -> !(entity instanceof PlayerEntity))) {
            CompoundTag entityTag = entity.toTag(new CompoundTag());

            ListTag posTag = (ListTag) entityTag.get("Pos");
            ListTag relativePosTag = new ListTag();
            relativePosTag.add(DoubleTag.of(posTag.getDouble(0) - from.getX()));
            relativePosTag.add(DoubleTag.of(posTag.getDouble(1) - from.getY()));
            relativePosTag.add(DoubleTag.of(posTag.getDouble(2) - from.getZ()));
            entityTag.put("Pos", relativePosTag);

            schematic.entities.add(entityTag);
        }

        schematic.requiredMods = mods.toArray(new String[0]);
        schematic.creationDate = System.currentTimeMillis();

        return schematic;
    }

    private static Box getBoundingBox(Vec3i from, Vec3i to) {
        return new Box(new BlockPos(from.getX(), from.getY(), from.getZ()), new BlockPos(to.getX(), to.getY(), to.getZ()));
    }

    public void place(World world, int xBase, int yBase, int zBase) {
        // Place the schematic's blocks
        setBlocks(world, xBase, yBase, zBase);

        // Set BlockEntity data
        for (CompoundTag BlockEntityNBT : tileEntities) {
            Vec3i schematicPos = new BlockPos(BlockEntityNBT.getInt("x"), BlockEntityNBT.getInt("y"), BlockEntityNBT.getInt("z"));
            BlockPos pos = new BlockPos(xBase, yBase, zBase).add(schematicPos);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity != null) {
                String id = BlockEntityNBT.getString("id");
                String blockBlockEntityId = BlockEntityType.getId(blockEntity.getType()).toString();
                if (id.equals(blockBlockEntityId)) {
                    blockEntity.fromTag(BlockEntityNBT);

                    // Correct the position
                    blockEntity.setWorld(world, pos);
                    blockEntity.markDirty();
                } else {
                    throw new RuntimeException("Schematic contained BlockEntity " + id + " at " + pos + " but the BlockEntity of that block (" + world.getBlockState(pos) + ") must be " + blockBlockEntityId);
                }
            } else {
                throw new RuntimeException("Schematic contained BlockEntity info at " + pos + " but the block there (" + world.getBlockState(pos) + ") has no BlockEntity.");
            }
        }

        // Spawn entities
        for (CompoundTag entityNBT : entities) {
            // Correct the position and UUID
            ListTag posNBT = (ListTag) entityNBT.get("Pos");
            ListTag newPosNBT = new ListTag();
            newPosNBT.add(DoubleTag.of(posNBT.getDouble(0) + xBase));
            newPosNBT.add(DoubleTag.of(posNBT.getDouble(1) + yBase));
            newPosNBT.add(DoubleTag.of(posNBT.getDouble(2) + zBase));
            CompoundTag adjustedEntityTag = entityNBT.copy();
            adjustedEntityTag.put("Pos", newPosNBT);
            adjustedEntityTag.putUuid("UUID", UUID.randomUUID());

            world.spawnEntity(EntityType.getEntityFromTag(adjustedEntityTag, world).orElseThrow(() -> new RuntimeException("missing entity type")));
        }
    }

    public BlockState getBlockState(int x, int y, int z) {
        return palette.get(blockData[x][y][z]);
    }

    public void setBlockState(int x, int y, int z, BlockState state) {
        if (palette.contains(state)) {
            blockData[x][y][z] = (short) palette.indexOf(state); // TODO: optimize this (there must be some efficient list implementations)
        } else {
            palette.add(state);
            blockData[x][y][z] = (short) ++paletteMax;
        }
    }

    private void setBlocks(World world, int xBase, int yBase, int zBase) {
        LOGGER.debug("Setting chunk blockstates");

        long setTime = 0;
        long relightTime = 0;

        for (int chunkX = 0; chunkX <= (width >> 4) + 1; chunkX++) {
            for (int chunkZ = 0; chunkZ <= (length >> 4) + 1; chunkZ++) {
                long setStart = System.nanoTime();
                // Get the chunk only once for efficiency
                Chunk chunk = world.getChunk((xBase >> 4) + chunkX, (zBase >> 4) + chunkZ);
                ChunkSection[] sections = chunk.getSectionArray();

                for (int storageY = 0; storageY <= (height >> 4) + 1; storageY++) {
                    // Get the storage only once for eficiency
                    ChunkSection storage = sections[(yBase >> 4) + storageY];
                    boolean setAir = storage != null;

                    for (int x = 0; x < 16; x++) {
                        for (int y = 0; y < 16; y++) {
                            for (int z = 0; z < 16; z++) {
                                int sx = (chunkX << 4) + x - (xBase & 0x0F);
                                int sy = (storageY << 4) + y - (yBase & 0x0F);
                                int sz = (chunkZ << 4) + z - (zBase & 0x0F);

                                if (sx >= 0 && sy >= 0 && sz >= 0 && sx < width && sy < height && sz < length) {
                                    BlockState state = palette.get(blockData[sx][sy][sz]);
                                    if (!state.getBlock().equals(Blocks.AIR)) {
                                        if (storage == null) {
                                            storage = new ChunkSection((yBase >> 4) + storageY << 4);
                                            sections[(yBase >> 4) + storageY] = storage;
                                        }

                                        storage.setBlockState(x, y, z, state);
                                    } else if (setAir) {
                                        storage.setBlockState(x, y, z, state);
                                    }
                                }
                            }
                        }
                    }
                }

                setTime += System.nanoTime() - setStart;
                long relightStart = System.nanoTime();
                // TODO: relight
                relightTime += System.nanoTime() - relightStart;
            }
        }

        // TODO: update region
        LOGGER.debug("Set block states in " + setTime / 1000000 + " ms and relit chunks/cubes in " + relightTime / 1000000);
    }
}
