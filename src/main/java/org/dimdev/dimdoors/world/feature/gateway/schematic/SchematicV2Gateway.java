package org.dimdev.dimdoors.world.feature.gateway.schematic;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimcore.schematic.v2.Schematic;
import org.dimdev.dimcore.schematic.v2.SchematicPlacer;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.world.feature.gateway.Gateway;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

public class SchematicV2Gateway implements Gateway {
    private static final Logger LOGGER = LogManager.getLogger();
    private Schematic schematic;
    private final String id;
    public static final BiMap<SchematicV2Gateway, String> SCHEMATIC_ID_MAP = HashBiMap.create();
    public static final BiMap<String, SchematicV2Gateway> ID_SCHEMATIC_MAP = HashBiMap.create();

    public SchematicV2Gateway(String id) {
        SCHEMATIC_ID_MAP.putIfAbsent(this, id);
        ID_SCHEMATIC_MAP.putIfAbsent(id, this);
        this.id = id;
    }

    public void init() {
        String schematicJarDirectory = "/data/dimdoors/gateways/v2/";
        InputStream schematicStream = DimensionalDoorsInitializer.class.getResourceAsStream(schematicJarDirectory + this.id + ".schem");

        DataInputStream schematicDataStream = null;
        boolean streamOpened = false;
        if (schematicStream != null) {
            schematicDataStream = new DataInputStream(schematicStream);
            streamOpened = true;
        } else {
            LOGGER.warn("Schematic '" + this.id + "' was not found in the jar or config directory, neither with the .schem extension, nor with the .schematic extension.");
        }

        CompoundTag tag;
        this.schematic = null;
        if (streamOpened) {
            try {
                tag = NbtIo.readCompressed(schematicDataStream);
                this.schematic = Schematic.fromTag(tag);
                schematicDataStream.close();
            } catch (IOException ex) {
                LOGGER.error("Schematic file for " + this.id + " could not be read as a valid schematic NBT file.", ex);
            } finally {
                try {
                    schematicDataStream.close();
                } catch (IOException ex) {
                    LOGGER.error("Error occured while closing schematicDataStream", ex);
                }
            }
        }
    }

    public void generate(StructureWorldAccess world, BlockPos pos) {
        SchematicPlacer.place(this.schematic, world, pos);
    }

    /**
     * Generates randomized portions of the gateway structure (e.g. rubble, foliage)
     *
     * @param world - the world in which to generate the gateway
     * @param x     - the x-coordinate at which to center the gateway; usually where the door is placed
     * @param y     - the y-coordinate of the block on which the gateway may be built
     * @param z     - the z-coordinate at which to center the gateway; usually where the door is placed
     */
    protected void generateRandomBits(StructureWorldAccess world, int x, int y, int z) {
    }
}
