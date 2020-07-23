package org.dimdev.dimdoors.world.gateway;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimcore.schematic.Schematic;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.World;

public abstract class BaseSchematicGateway extends BaseGateway {
    private static final Logger LOGGER = LogManager.getLogger();
    private Schematic schematic;

    public BaseSchematicGateway(String id) {
        String schematicJarDirectory = "/data/dimdoors/gateways/";

        //Initialising the possible locations/formats for the schematic file
        InputStream schematicStream = DimensionalDoorsInitializer.class.getResourceAsStream(schematicJarDirectory + id + ".schem");

        //determine which location to load the schematic file from (and what format)
        DataInputStream schematicDataStream = null;
        boolean streamOpened = false;
        if (schematicStream != null) {
            schematicDataStream = new DataInputStream(schematicStream);
            streamOpened = true;
        } else {
            LOGGER.warn("Schematic '" + id + "' was not found in the jar or config directory, neither with the .schem extension, nor with the .schematic extension.");
        }

        CompoundTag schematicNBT;
        schematic = null;
        if (streamOpened) {
            try {
                schematicNBT = NbtIo.readCompressed(schematicDataStream);
                schematic = Schematic.fromTag(schematicNBT);
                //PocketTemplate.replacePlaceholders(schematic);
                schematicDataStream.close();
            } catch (IOException ex) {
                LOGGER.error("Schematic file for " + id + " could not be read as a valid schematic NBT file.", ex);
            } finally {
                try {
                    schematicDataStream.close();
                } catch (IOException ex) {
                    LOGGER.error("Error occured while closing schematicDataStream", ex);
                }
            }
        }
    }

    @Override
    public void generate(World world, int x, int y, int z) {
        schematic.place(world, x, y, z);
        generateRandomBits(world, x, y, z);
    }

    /**
     * Generates randomized portions of the gateway structure (e.g. rubble, foliage)
     *
     * @param world - the world in which to generate the gateway
     * @param x     - the x-coordinate at which to center the gateway; usually where the door is placed
     * @param y     - the y-coordinate of the block on which the gateway may be built
     * @param z     - the z-coordinate at which to center the gateway; usually where the door is placed
     */
    protected void generateRandomBits(World world, int x, int y, int z) {
    }
}