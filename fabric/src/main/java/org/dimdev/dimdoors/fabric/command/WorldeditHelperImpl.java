package org.dimdev.dimdoors.fabric.command;

import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.session.SessionOwner;
import net.minecraft.server.level.ServerPlayer;

public class WorldeditHelperImpl {
    public static SessionOwner getSessionOwner(ServerPlayer player) {
        return FabricAdapter.adaptPlayer(player);
    }
}
