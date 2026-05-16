package org.dimdev.dimdoors.painting;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.PaintingVariant;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.datagen.DimDoorsDynamicRegistryProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModPaintings {
    public static ResourceKey<PaintingVariant> LIMBO = key("limbo");
    public static ResourceKey<PaintingVariant> EYES = key("eyes");
    public static ResourceKey<PaintingVariant> PORTAL = key("portal");
    public static ResourceKey<PaintingVariant> FREEDOM = key("freedom");
    public static ResourceKey<PaintingVariant> GATEWAY_AT_NIGHT = key("gateway_at_night");

    public static final List<ResourceKey<PaintingVariant>> PAINTINGS_TO_DECAY_INTO;

    private static ResourceKey<PaintingVariant> key(String name) {
        return ResourceKey.create(Registries.PAINTING_VARIANT, DimensionalDoors.id(name));
    }

    private static void addIntoList(List<ResourceKey<PaintingVariant>> list, ResourceKey<PaintingVariant> key, int width, int height) {
        list.set((width -1) + ((height - 1) * 4), key);
    }

    static {
        var list = new ArrayList<ResourceKey<PaintingVariant>>(16);

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                list.add(key("placeholder_" + (x + 1) + "_" + (y + 1)));
            }
        }

//        list.set(6, FREEDOM);
        addIntoList(list, LIMBO, 4, 2);
        addIntoList(list, FREEDOM, 2, 2);
        addIntoList(list, PORTAL, 2, 4);

        PAINTINGS_TO_DECAY_INTO = Collections.unmodifiableList(list);
    }
}
