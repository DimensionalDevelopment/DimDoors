package org.dimdev.dimdoors.painting;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.DimensionalDoors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ModPaintings {
    public static ResourceKey<PaintingVariant> LIMBO = key("limbo");

    public static final List<ResourceKey<PaintingVariant>> PLACEHOLDERS;

    public static void bootstrap(BootstrapContext<PaintingVariant> context) {
        register(context, LIMBO, 4, 2);

        for (int x = 1; x < 5; x++) {
            for (int y = 1; y < 5; y++) {
                placeholder(context, x, y);
            }
        }
    }

    private static void placeholder(BootstrapContext<PaintingVariant> context, int x, int y) {
        register(context, key("placeholder_" + x + "_" + y), x, y);
    }

    private static ResourceKey<PaintingVariant> key(String name) {
        return ResourceKey.create(Registries.PAINTING_VARIANT, DimensionalDoors.id(name));
    }

    private static void register(BootstrapContext<PaintingVariant> bootstrapContext, ResourceKey<PaintingVariant> resourceKey, int width, int height) {
        bootstrapContext.register(resourceKey, new PaintingVariant(width, height, resourceKey.location()));
    }

    static {
        var list = new ArrayList<ResourceKey<PaintingVariant>>();

        for (int x = 1; x < 5; x++) {
            for (int y = 1; y < 5; y++) {
                list.add(key("placeholder_" + x + "_" + y));
            }
        }

        PLACEHOLDERS = Collections.unmodifiableList(list);
    }
}
