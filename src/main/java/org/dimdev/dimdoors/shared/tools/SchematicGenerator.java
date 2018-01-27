package org.dimdev.dimdoors.shared.tools;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.ddutils.schem.Schematic;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.BlockDimensionalDoor;
import org.dimdev.dimdoors.shared.blocks.BlockFabric;
import org.dimdev.dimdoors.shared.blocks.BlockFabricAncient;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.rifts.RiftDestination;
import org.dimdev.dimdoors.shared.rifts.destinations.PocketEntranceMarker;
import org.dimdev.dimdoors.shared.rifts.destinations.PocketExitMarker;
import org.dimdev.dimdoors.shared.rifts.destinations.PrivatePocketExitDestination;
import org.dimdev.dimdoors.shared.rifts.registry.LinkProperties;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SchematicGenerator {

    // Run "gradlew generatePocketSchematics" to generate the pocket schematics
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public static void main(String... args) throws IOException {

        // Parse arguments
        File schematicDir;
        if (args.length > 1) {
            System.err.println("Too many arguments!");
            return;
        } else if (args.length == 1) {
            schematicDir = new File(args[0]);
            if (!schematicDir.isDirectory()) {
                System.err.print("The directory " + args[0] + " couldn't be found!");
                return;
            }
        } else {
            schematicDir = new File("schematics/");
        }

        // Register blocks and tile entities to be able to run this without starting Minecraft
        Initializer.initialize();

        // Generate the schematics
        List<Schematic> schematics = generatePocketSchematics();

        // Save the schematics
        String[] saveFolders = {"public/", "private/", "blank/", "blank/", "blank/"};
        int i = 0;
        for (Schematic schematic : schematics) {
            NBTTagCompound schematicNBT = schematic.saveToNBT();
            File saveFile = new File(schematicDir, saveFolders[i++ % saveFolders.length] + schematic.name + ".schem");
            saveFile.getParentFile().mkdirs();
            DataOutputStream schematicDataStream = new DataOutputStream(new FileOutputStream(saveFile));
            CompressedStreamTools.writeCompressed(schematicNBT, schematicDataStream);
            schematicDataStream.flush();
            schematicDataStream.close();
        }
        // TODO: also generate JSON files
    }

    public static List<Schematic> generatePocketSchematics() {
        List<Schematic> schematics = new ArrayList<>();
        for (int pocketSize = 0; pocketSize < 8; pocketSize++) { // Changing to 8 to 16 would cause out of memory (256^3*4 bytes = 64MB/schematic)
            schematics.add(generateBlankWithDoor(
                    "public_pocket", // base name
                    pocketSize, // size
                    ModBlocks.ANCIENT_FABRIC.getDefaultState(), // outer wall
                    ModBlocks.FABRIC.getDefaultState(), // inner wall
                    ModBlocks.DIMENSIONAL_DOOR, // door
                    PocketExitMarker.builder().build(),// exit rift destination
                    LinkProperties.builder()
                            .groups(Collections.singleton(1))
                            .linksRemaining(1)
                            .entranceWeight(1)
                            .floatingWeight(1)
                            .build()));

            schematics.add(generateBlankWithDoor(
                    "private_pocket", // base name
                    pocketSize, // size
                    ModBlocks.ANCIENT_FABRIC.getDefaultState().withProperty(BlockFabricAncient.COLOR, EnumDyeColor.WHITE), // outer wall
                    ModBlocks.FABRIC.getDefaultState().withProperty(BlockFabricAncient.COLOR, EnumDyeColor.WHITE), // inner wall
                    ModBlocks.PERSONAL_DIMENSIONAL_DOOR, // door
                    PrivatePocketExitDestination.builder().build(),// exit rift destination
                    null));

            schematics.add(generateBlank(
                    "blank_pocket",
                    pocketSize,
                    ModBlocks.ANCIENT_FABRIC.getDefaultState(),
                    ModBlocks.FABRIC.getDefaultState()));

            schematics.add(generateFrame(
                    "void_pocket",
                    pocketSize,
                    ModBlocks.FABRIC.getDefaultState().withProperty(BlockFabric.COLOR, EnumDyeColor.LIGHT_BLUE)));

            schematics.add(generateResizableFrame(
                    "resizable_pocket",
                    pocketSize,
                    ModBlocks.FABRIC.getDefaultState().withProperty(BlockFabric.COLOR, EnumDyeColor.ORANGE)));
        }
        return schematics;
    }

    private static Schematic generateBlank(String baseName, int pocketSize, IBlockState outerWall, IBlockState innerWall) {
        short size = (short) ((pocketSize + 1) * 16 - 1); // -1 so that the door can be centered

        // Set schematic info
        Schematic schematic = new Schematic(baseName + "_" + pocketSize, "DimDoors", size, size, size);
        schematic.requiredMods = new String[]{DimDoors.MODID};

        // Set block data
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    int layer = Collections.min(Arrays.asList(x, y, z, size - 1 - x, size - 1 - y, size - 1 - z));
                    if (layer == 0) {
                        schematic.setBlockState(x, y, z, outerWall);
                    } else if (layer < 5) {
                        schematic.setBlockState(x, y, z, innerWall);
                    }
                }
            }
        }

        return schematic;
    }

    private static Schematic generateBlankWithDoor(String baseName, int pocketSize, IBlockState outerWall, IBlockState innerWall, BlockDimensionalDoor doorBlock, RiftDestination exitDest, LinkProperties link) {
        short size = (short) ((pocketSize + 1) * 16 - 1); // -1 so that the door can be centered

        // Make the schematic
        Schematic schematic = generateBlank(baseName, pocketSize, outerWall, innerWall);

        // Add the door
        schematic.setBlockState((size - 1) / 2, 5, 4, doorBlock.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER));
        schematic.setBlockState((size - 1) / 2, 6, 4, doorBlock.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER));

        // Set the rift entities
        schematic.tileEntities = new ArrayList<>();
        TileEntityEntranceRift rift = (TileEntityEntranceRift) doorBlock.createTileEntity(null, doorBlock.getDefaultState());
        rift.setDestination(PocketEntranceMarker.builder()
                .ifDestination(exitDest)
                .build());
        rift.setProperties(link);

        rift.setPlaceRiftOnBreak(true);
        NBTTagCompound tileNBT = rift.serializeNBT();
        tileNBT.setInteger("x", (size - 1) / 2);
        tileNBT.setInteger("y", 5);
        tileNBT.setInteger("z", 4);
        schematic.tileEntities.add(tileNBT);

        return schematic;
    }

    private static Schematic generateFrame(String baseName, int chunkSize, IBlockState frame) {
        short size = (short) ((chunkSize + 1) * 16 - 1); // -1 so that the door can be centered

        // Set schematic info
        Schematic schematic = new Schematic(baseName + "_" + chunkSize, "DimDoors", size, size, size);
        schematic.requiredMods = new String[]{DimDoors.MODID};

        // Set block data
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    int sides = 0;
                    if (x == 0 || x == size - 1) sides++;
                    if (y == 0 || y == size - 1) sides++;
                    if (z == 0 || z == size - 1) sides++;

                    if (sides >= 2) {
                        schematic.setBlockState(x, y, z, frame);
                    }
                }
            }
        }

        return schematic;
    }

    private static Schematic generateResizableFrame(String baseName, int chunkSize, IBlockState frame) {
        short size = (short) ((chunkSize + 1) * 16);

        // Set schematic info
        Schematic schematic = new Schematic(baseName + "_" + chunkSize, "DimDoors", size, size, size);
        schematic.requiredMods = new String[]{DimDoors.MODID};

        // Set block data
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    int sides = 0;
                    if (x % 16 == 0) sides++;
                    if (y % 16 == 0) sides++;
                    if (z % 16 == 0) sides++;
                    int cubeSides = 3;
                    int cubeSize = Math.max(x, Math.max(y, z));
                    if (cubeSize - x != 0) cubeSides--;
                    if (cubeSize - y != 0) cubeSides--;
                    if (cubeSize - z != 0) cubeSides--;

                    if (sides >= 2 && cubeSides >= 2) {
                        schematic.setBlockState(x, y, z, frame);
                    }
                }
            }
        }

        return schematic;
    }
}
