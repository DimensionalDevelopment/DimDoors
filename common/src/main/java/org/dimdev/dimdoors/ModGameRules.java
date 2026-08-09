package org.dimdev.dimdoors;

import net.minecraft.world.level.GameRules;

public class ModGameRules {
    public static final GameRules.Key<GameRules.BooleanValue> RIFT_SIGNATURE_WORKS_IN_PRIVATE_POCKETS = DimensionalDoors.getSided().registerGameRule("pocketsRiftSignaturesWorkInPrivatePockets", GameRules.Category.MISC, true);

    public static void init() {
    }
}
