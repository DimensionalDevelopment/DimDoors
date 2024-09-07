package org.dimdev.dimdoors.command;

import com.sk89q.worldedit.forge.ForgeAdapter;
import com.sk89q.worldedit.session.SessionOwner;
import net.minecraft.server.level.ServerPlayer;

public class WorldeditHelperImpl {
    public static SessionOwner getSessionOwner(ServerPlayer player) {
        return ForgeAdapter.adaptPlayer(player);
    }
}
