package org.dimdev.dimdoors.shared.tools;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
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
        List<Schematic> schematics = generatePocketSchematics(8);

        // Save the schematics
        boolean isPublic = true;
        for (Schematic schematic : schematics) {
            NBTTagCompound schematicNBT = Schematic.saveToNBT(schematic);
            File saveFile = new File(schematicDir, (isPublic ? "public/" : "private/") + schematic.name + ".schem");
            saveFile.getParentFile().mkdirs();
            DataOutputStream schematicDataStream = new DataOutputStream(new FileOutputStream(saveFile));
            CompressedStreamTools.writeCompressed(schematicNBT, schematicDataStream);
            schematicDataStream.flush();
            schematicDataStream.close();
            isPublic = !isPublic;
        }
        // TODO: also generate JSON files
    }

    public static List<Schematic> generatePocketSchematics(int maxPocketSize) {
        List<Schematic> schematics = new ArrayList<>();
        for (int pocketSize = 0; pocketSize < maxPocketSize; pocketSize++) {
            schematics.add(generatePocketSchematic(
                    "public_pocket", // base name
                    pocketSize, // size
                    ModBlocks.ANCIENT_FABRIC.getDefaultState(), // outer wall
                    ModBlocks.FABRIC.getDefaultState(), // inner wall
                    ModBlocks.DIMENSIONAL_DOOR, // door
                    PocketExitDestination.builder().build(),
                    1)); // exit rift destination
            schematics.add(generatePocketSchematic(
                    "private_pocket", // base name
                    pocketSize, // size
                    ModBlocks.ANCIENT_FABRIC.getDefaultState().withProperty(BlockFabricAncient.COLOR, EnumDyeColor.WHITE), // outer wall
                    ModBlocks.FABRIC.getDefaultState().withProperty(BlockFabricAncient.COLOR, EnumDyeColor.WHITE), // inner wall
                    ModBlocks.PERSONAL_DIMENSIONAL_DOOR, // door
                    PrivatePocketExitDestination.builder().build(),
                    0)); // exit rift destination
        }
        return schematics;
    }

    private static Schematic generatePocketSchematic(String baseName, int pocketSize, IBlockState outerWallBlockState, IBlockState innerWallBlockState, BlockDimensionalDoor doorBlock, RiftDestination exitDest, float chaosWeight) {
        int size = (pocketSize + 1) * 16 - 1; // -1 so that the door can be centered

        // Set schematic info
        Schematic schematic = new Schematic();
        schematic.version = 1;
        schematic.author = "Robijnvogel"; //@todo set in build.gradle ${modID}
        schematic.name = baseName + "_" + pocketSize;
        schematic.creationDate = System.currentTimeMillis();
        schematic.requiredMods = new String[1];
        schematic.requiredMods[0] = DimDoors.MODID;
        schematic.width = (short) size;
        schematic.height = (short) size;
        schematic.length = (short) size;
        schematic.offset = new int[]{0, 0, 0}; // TODO: center pockets

        // Generate the pallette
        schematic.paletteMax = 4;
        schematic.pallette = new ArrayList<>();
        schematic.pallette.add(Blocks.AIR.getDefaultState());
        schematic.pallette.add(outerWallBlockState);
        schematic.pallette.add(innerWallBlockState);
        schematic.pallette.add(doorBlock.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)); //bottom
        schematic.pallette.add(doorBlock.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)); //top

        // Set block data
        schematic.blockData = new int[size][size][size]; //[x][y][z]
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    int layer = Collections.min(Arrays.asList(x, y, z, size - 1 - x, size - 1 - y, size - 1 - z));
                    if (layer == 0) {
                        schematic.blockData[x][y][z] = 1; // outer wall
                    } else if (layer < 5) {
                        schematic.blockData[x][y][z] = 2; // inner wall
                    } else {
                        schematic.blockData[x][y][z] = 0; // air
                    }
                }
            }
        }
        schematic.blockData[(size - 1) / 2][5][4] = 3; // door bottom
        schematic.blockData[(size - 1) / 2][6][4] = 4; // door top

        // Generate the rift TileEntities
        schematic.tileEntities = new ArrayList<>();
        TileEntityEntranceRift rift = (TileEntityEntranceRift) doorBlock.createTileEntity(null, doorBlock.getDefaultState());
        rift.setDestination(PocketEntranceDestination.builder()
                .ifDestination(exitDest)
                .build());
        rift.setProperties(LinkProperties.builder()
                .groups(Collections.singleton(1))
                .linksRemaining(1)
                .entranceWeight(chaosWeight)
                .floatingWeight(chaosWeight)
                .build());

        rift.setPlaceRiftOnBreak(true);
        NBTTagCompound tileNBT = rift.serializeNBT();
        tileNBT.setInteger("x", (size - 1) / 2);
        tileNBT.setInteger("y", 5);
        tileNBT.setInteger("z", 4);
        schematic.tileEntities.add(tileNBT);

        return schematic;
    }
}
