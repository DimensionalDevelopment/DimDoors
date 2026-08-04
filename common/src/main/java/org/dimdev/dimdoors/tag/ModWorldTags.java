package org.dimdev.dimdoors.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModWorldTags {
    public static final TagKey<DimensionType> MONOLITHS_CAN_EXIST = of("monoliths_can_exist");
    public static final TagKey<DimensionType> UNRAVELLED_FABRIC_CAN_UNRAVEL = of("unravelled_fabric_can_unravel");


    private static TagKey<DimensionType> of(String id) {
        return TagKey.create(Registries.DIMENSION_TYPE, DimensionalDoors.id(id));
    }
}
