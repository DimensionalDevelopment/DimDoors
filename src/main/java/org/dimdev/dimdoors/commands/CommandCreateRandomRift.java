package org.dimdev.dimdoors.commands;

import org.dimdev.dimdoors.core.DimLink;
import org.dimdev.dimdoors.core.LinkType;
import org.dimdev.dimdoors.core.NewDimData;
import org.dimdev.dimdoors.core.PocketManager;
import org.dimdev.dimdoors.dungeon.DungeonData;
import org.dimdev.dimdoors.helpers.DungeonHelper;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.world.PocketBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class CommandCreateRandomRift extends DDCommandBase {
    private static CommandCreateRandomRift instance = null;
    private static final Random random = new Random();

    private CommandCreateRandomRift() {
        super("dd-random", "<dungeon name>");
    }

    public static CommandCreateRandomRift instance() {
        if (instance == null)
            instance = new CommandCreateRandomRift();

        return instance;
    }

    @Override
    protected DDCommandResult processCommand(EntityPlayer sender, String[] command) {
        NewDimData dimension;
        DungeonHelper dungeonHelper = DungeonHelper.instance();

        if (command.length > 1) {
            return DDCommandResult.TOO_MANY_ARGUMENTS;
        }

        DimLink link;
        DungeonData result;
        int x = MathHelper.floor_double(sender.posX);
        int y = MathHelper.floor_double(sender.posY);
        int z = MathHelper.floor_double(sender.posZ);
        int orientation = MathHelper.floor_double((sender.rotationYaw + 180.0F) * 4.0F / 360.0F - 0.5D) & 3;

        if (command.length == 0) {
            dimension = PocketManager.getDimensionData(sender.worldObj);
            link = dimension.createLink(x, y + 1, z, LinkType.DUNGEON, orientation);

            sender.worldObj.setBlock(x, y + 1, z, DimDoors.blockRift, 0, 3);
            sendChat(sender, "Created a rift to a random dungeon.");
        } else {
            result = getRandomDungeonByPartialName(command[0], dungeonHelper.getRegisteredDungeons());
            if (result == null) {
                result = getRandomDungeonByPartialName(command[0], dungeonHelper.getUntaggedDungeons());
            }

            // Check if we found any matches
            if (result != null) {
                dimension = PocketManager.getDimensionData(sender.worldObj);
                link = dimension.createLink(x, y + 1, z, LinkType.DUNGEON, orientation);

                if (PocketBuilder.generateSelectedDungeonPocket(link, DimDoors.properties, result)) {
                    // Create a rift to our selected dungeon and notify the player
                    sender.worldObj.setBlock(x, y + 1, z, DimDoors.blockRift, 0, 3);
                    sendChat(sender, "Created a rift to \"" + result.schematicName() + "\" dungeon (Dimension ID = " + link.destination().getDimension() + ").");
                } else {
                    // Dungeon generation failed somehow. Notify the user and remove the useless link.
                    dimension.deleteLink(link);
                    sendChat(sender, "Dungeon generation failed unexpectedly!");
                }
            } else {
                //No matches!
                return new DDCommandResult("Error: The specified dungeon was not found. Use 'list' to see a list of the available dungeons.");
            }
        }
        return DDCommandResult.SUCCESS;
    }

    private static DungeonData getRandomDungeonByPartialName(String query, Collection<DungeonData> dungeons) {
        // Search for all dungeons that contain the lowercase query string.
        String dungeonName;
        String normalQuery = query.toLowerCase();
        ArrayList<DungeonData> matches = new ArrayList<DungeonData>();

        for (DungeonData dungeon : dungeons) {
            // We need to extract the file's name. Comparing against schematicPath could
            // yield false matches if the query string is contained within the path.
            dungeonName = dungeon.schematicName().toLowerCase();
            if (dungeonName.contains(normalQuery)) {
                matches.add(dungeon);
            }
        }
        if (matches.isEmpty()) {
            return null;
        }
        return matches.get(random.nextInt(matches.size()));
    }
}