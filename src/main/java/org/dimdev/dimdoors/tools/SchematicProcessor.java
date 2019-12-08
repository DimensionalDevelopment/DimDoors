//package org.dimdev.dimdoors.tools;
//
//import net.minecraft.nbt.CompressedStreamTools;
//import org.apache.commons.io.FileUtils;
//import org.dimdev.util.schem.Schematic;
//
//import java.io.*;
//
//public final class SchematicProcessor {
//
//    @SuppressWarnings("UseOfSystemOutOrSystemErr")
//    public static void main(String... args) throws IOException {
//        // Parse arguments
//        boolean testMode = false;
//        File schematicDir = new File("schematics/");
//        if (args.length > 2) {
//            System.err.println("Too many arguments!");
//            return;
//        } else if (args.length == 2) {
//            testMode = args[0].equals("true");
//            schematicDir = new File(args[1]);
//            if (!schematicDir.isDirectory()) {
//                System.err.print("The directory " + args[0] + " couldn't be found!");
//                return;
//            }
//        } else if (args.length == 1) {
//            testMode = args[0].equals("true");
//        }
//
//        Initializer.initialize();
//
//        if (testMode) {
//            File out = new File("out");
//            FileUtils.deleteDirectory(out);
//            out.mkdir();
//        }
//
//        process(schematicDir, testMode);
//    }
//
//
//    private static void process(File file, boolean testMode) throws IOException {
//        if (file.isDirectory()) {
//            for (File subFile : file.listFiles()) {
//                process(subFile, testMode);
//            }
//        } else {
//            Schematic schematic = Schematic.loadFromNBT(CompressedStreamTools.readCompressed(new FileInputStream(file)));
//            schematic = runTasks(schematic);
//
//            if (schematic != null) {
//                File outputFile = testMode ? new File("out", file.getName()) : file;
//                if (!testMode) {
//                    outputFile.delete();
//                }
//                CompressedStreamTools.writeCompressed(schematic.saveToNBT(), new FileOutputStream(outputFile));
//            }
//        }
//    }
//
//    // ***** ADD YOUR CODE BELOW THIS LINE *****
//
//    private static boolean schematicChanged = false;
//    private static boolean paletteChanged = false;
//
//    private static Schematic runTasks(Schematic schematic) {
//        // ADD TASKS HERE, DON'T FORGET TO SET schematicChanged and paletteChanged
//        //task1(schematic);
//        //task2(schematic);
//        //task3(schematic);
//        // ...
//
//        if (paletteChanged) {
//            schematic = rewriteSchematic(schematic); // To get rid of unused palette IDs
//            paletteChanged = false;
//        }
//        return schematicChanged ? schematic : null;
//    }
//
//    private static Schematic rewriteSchematic(Schematic schematic) {
//        Schematic copy = new Schematic(schematic.name, schematic.author, schematic.width, schematic.height, schematic.length);
//        copy.creationDate = schematic.creationDate;
//        copy.requiredMods = schematic.requiredMods;
//        copy.offset = schematic.offset;
//        for (int x = 0; x < schematic.width; x++) {
//            for (int y = 0; y < schematic.height; y++) {
//                for (int z = 0; z < schematic.length; z++) {
//                    copy.setBlockState(x, y, z, schematic.getBlockState(x, y, z));
//                }
//            }
//        }
//        copy.tileEntities = schematic.tileEntities;
//        copy.entities = schematic.entities;
//        return copy;
//    }
//}
