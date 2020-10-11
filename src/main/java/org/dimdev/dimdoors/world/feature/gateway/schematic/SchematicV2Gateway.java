package org.dimdev.dimdoors.world.feature.gateway.schematic;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.pockets.TemplateUtils;
import org.dimdev.dimdoors.util.schematic.v2.Schematic;
import org.dimdev.dimdoors.util.schematic.v2.SchematicPlacer;
import org.dimdev.dimdoors.world.feature.gateway.Gateway;

import net.minecraft.nbt.NbtIo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

public abstract class SchematicV2Gateway implements Gateway {
    private Schematic schematic;
    private final String id;
    public static final BiMap<String, SchematicV2Gateway> ID_SCHEMATIC_MAP = HashBiMap.create();

    public SchematicV2Gateway(String id) {
        ID_SCHEMATIC_MAP.putIfAbsent(id, this);
        this.id = id;
    }

    public void init() {
        String schematicJarDirectory = "/data/dimdoors/gateways/v2/";

        try (InputStream stream = DimensionalDoorsInitializer.class.getResourceAsStream(schematicJarDirectory + this.id + ".schem")) {
            if (stream == null) {
                throw new RuntimeException("Schematic '" + this.id + "' was not found in the jar or config directory, neither with the .schem extension, nor with the .schematic extension.");
            }
            try {
                this.schematic = Schematic.fromTag(NbtIo.readCompressed(stream));
            } catch (IOException ex) {
                throw new RuntimeException("Schematic file for " + this.id + " could not be read as a valid schematic NBT file.", ex);
            }
            TemplateUtils.replacePlaceholders(this.schematic);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public final void generate(StructureWorldAccess world, BlockPos pos) {
        SchematicPlacer.place(this.schematic, world, pos);
        this.generateRandomBits(world, pos);
    }

    /**
     * Generates randomized portions of the gateway structure (e.g. rubble, foliage)
     *
     * @param world - the world in which to generate the gateway
     * @param pos - the position at which the schematic is placed
     */
    protected void generateRandomBits(StructureWorldAccess world, BlockPos pos) {
    }
}
