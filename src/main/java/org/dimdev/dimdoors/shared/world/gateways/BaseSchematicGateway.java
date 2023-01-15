package org.dimdev.dimdoors.shared.world.gateways;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.ddutils.schem.Schematic;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.dimdev.dimdoors.shared.pockets.PocketTemplate;

public abstract class BaseSchematicGateway extends BaseGateway {
    private Schematic schematic;

    public BaseSchematicGateway(String id) {
        String schematicJarDirectory = "/assets/dimdoors/gateways/";

        //Initialising the possible locations/formats for the schematic file
        InputStream schematicStream = DimDoors.class.getResourceAsStream(schematicJarDirectory + id + ".schem");

        //determine which location to load the schematic file from (and what format)
        DataInputStream schematicDataStream = null;
        boolean streamOpened = false;
        if (schematicStream != null) {
            schematicDataStream = new DataInputStream(schematicStream);
            streamOpened = true;
        } else {
            DimDoors.log.warn("Schematic '" + id + "' was not found in the jar or config directory, neither with the .schem extension, nor with the .schematic extension.");
        }

        NBTTagCompound schematicNBT;
        schematic = null;
        if (streamOpened) {
            try {
                schematicNBT = CompressedStreamTools.readCompressed(schematicDataStream);
                schematic = Schematic.loadFromNBT(schematicNBT);
                PocketTemplate.replacePlaceholders(schematic);
                schematicDataStream.close();
            } catch (IOException ex) {
                DimDoors.log.error("Schematic file for " + id + " could not be read as a valid schematic NBT file.", ex);
            } finally {
                try {
                    schematicDataStream.close();
                } catch (IOException ex) {
                    DimDoors.log.error("Error occurred while closing schematicDataStream", ex);
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