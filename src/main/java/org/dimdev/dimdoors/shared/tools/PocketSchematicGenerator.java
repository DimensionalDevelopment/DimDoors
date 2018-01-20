package org.dimdev.dimdoors.shared.tools;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;
import org.dimdev.ddutils.schem.Schematic;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.server.ServerProxy;
import org.dimdev.dimdoors.shared.blocks.BlockDimensionalDoor;
import org.dimdev.dimdoors.shared.blocks.BlockFabric;
import org.dimdev.dimdoors.shared.blocks.BlockFabricAncient;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.rifts.RiftDestination;
import org.dimdev.dimdoors.shared.rifts.destinations.PocketEntranceDestination;
import org.dimdev.dimdoors.shared.rifts.destinations.PocketExitDestination;
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

/**
 * @author Robijnvogel
 */
public final class PocketSchematicGenerator {

    // Run "gradlew generatePocketSchematics" to generate the pocket schematics
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public static void main(String... args) throws IOException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        // Register blocks and tile entities to be able to run this without starting Minecraft
        Bootstrap.register();
        ModMetadata md = new ModMetadata();
        md.modId = DimDoors.MODID;
        ModContainer mc = new DummyModContainer(md);
        Loader.instance().setupTestHarness(mc);
        Loader.instance().setActiveModContainer(mc);
        ModBlocks.registerBlocks(new RegistryEvent.Register<Block>(GameData.BLOCKS, RegistryManager.ACTIVE.getRegistry(GameData.BLOCKS)));
        new ServerProxy().registerTileEntities();
        new ServerProxy().registerRiftDestinations();
        Loader.instance().setActiveModContainer(null);

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

        // Generate the schematics
        List<Schematic> schematics = generatePocketSchematics();

        // Save the schematics
        String[] saveFolders = {"public/", "private/", "blank/", "blank/"};
        int i = 0;
        for (Schematic schematic : schematics) {
            NBTTagCompound schematicNBT = Schematic.saveToNBT(schematic);
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
        for (int pocketSize = 0; pocketSize < 8; pocketSize++) {
            schematics.add(generateBlankWithDoor(
                    "public_pocket", // base name
                    pocketSize, // size
                    ModBlocks.ANCIENT_FABRIC.getDefaultState(), // outer wall
                    ModBlocks.FABRIC.getDefaultState(), // inner wall
                    ModBlocks.DIMENSIONAL_DOOR, // door
                    PocketExitDestination.builder().build(),// exit rift destination
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

            schematics.add(generateBlank("blank_pocket",
                    pocketSize,
                    ModBlocks.ANCIENT_FABRIC.getDefaultState(),
                    ModBlocks.FABRIC.getDefaultState()));

            schematics.add(generateFrame("void_pocket",
                    pocketSize,
                    ModBlocks.FABRIC.getDefaultState().withProperty(BlockFabric.COLOR, EnumDyeColor.LIGHT_BLUE)));
        }
        return schematics;
    }

    private static Schematic generateBlank(String baseName, int pocketSize, IBlockState outerWall, IBlockState innerWall) {
        short size = (short) ((pocketSize + 1) * 16 - 1); // -1 so that the door can be centered

        // Set schematic info
        Schematic schematic = new Schematic(baseName + "_" + pocketSize, "DimDoors", size, size, size);
        schematic.requiredMods = new String[] { DimDoors.MODID };

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
        rift.setDestination(PocketEntranceDestination.builder()
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
        schematic.requiredMods = new String[] { DimDoors.MODID };

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
}
