package org.dimdev.dimdoors.fabric.forge.item.fabric;

import net.minecraft.world.entity.player.Player;

public class RaycastHelperImpl {
    public static double reach(Player player) {
        return 4.5 + (player.isCreative() ? 0.5 : 0.0);
    }
}
