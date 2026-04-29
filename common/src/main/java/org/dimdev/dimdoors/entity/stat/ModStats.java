package org.dimdev.dimdoors.entity.stat;

import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModStats {
    public static final ResourceLocation DEATHS_IN_POCKETS = register("deaths_in_pocket");
    public static final ResourceLocation TIMES_SENT_TO_LIMBO = register("times_sent_to_limbo");
    public static final ResourceLocation TIMES_TELEPORTED_BY_MONOLITH = register("times_teleported_by_monolith");
    public static final ResourceLocation TIMES_BEEN_TO_DUNGEON = register("times_been_to_dungeon");

    private static ResourceLocation register(String string) {
        ResourceLocation resourceLocation = DimensionalDoors.id(string);

        DimensionalDoors.getSided().registerCustomStat(string, resourceLocation);
//        CUSTOM.get(resourceLocation, statFormatter);
        return resourceLocation;
    }

    public static void init() {
    }
}
