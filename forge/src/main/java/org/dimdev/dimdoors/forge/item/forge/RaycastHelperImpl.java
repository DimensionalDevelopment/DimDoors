package org.dimdev.dimdoors.forge.item.forge;

import net.minecraft.world.entity.player.Player;

public class RaycastHelperImpl {
    public static double reach(Player player) {
        return player.getReachDistance();
    }
}
