package org.dimdev.dimdoors.command.forge;

import com.sk89q.worldedit.neoforge.NeoForgeAdapter;
import com.sk89q.worldedit.session.SessionOwner;
import net.minecraft.server.level.ServerPlayer;

public class WorldeditHelperImpl {
    public static SessionOwner getSessionOwner(ServerPlayer player) {
        return NeoForgeAdapter.adaptPlayer(player);
    }
}
