package org.dimdev.dimdoors.item.component;

import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.item.ModDataComponentTypes;

public class IdCounter {
    public static int get(ItemStack provider) {
    return provider.getOrDefault(ModDataComponentTypes.COUNT, 0);
    }

    public static void set(ItemStack provider, Integer value) {
    provider.set(ModDataComponentTypes.COUNT, value);
    }


    public static int increment(ItemStack provider) {
    var value = get(provider) + 1;

    set(provider, value);

    return value;
    }

    public static int count(ItemStack stack) {
    return get(stack);
    }

    public static int getAndIncrement(ItemStack provider) {
        var value = get(provider);

        set(provider, value + 1);

        return value;
    }
}
