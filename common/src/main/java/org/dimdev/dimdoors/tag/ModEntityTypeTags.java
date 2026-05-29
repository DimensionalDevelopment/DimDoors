package org.dimdev.dimdoors.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModEntityTypeTags {
    public static final TagKey<EntityType<?>> TREPIDATION_DETECTED = of("trepidation_detected");

    private static TagKey<EntityType<?>> of(String id) {
        return TagKey.create(Registries.ENTITY_TYPE, DimensionalDoors.id(id));
    }
}
