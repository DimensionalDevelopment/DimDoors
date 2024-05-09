package org.dimdev.dimdoors.item.forge;

import net.minecraft.world.entity.player.Player;

public class RaycastHelperImpl {
    public static double reach(Player player) {
        return player.getBlockReach();
    }
}
