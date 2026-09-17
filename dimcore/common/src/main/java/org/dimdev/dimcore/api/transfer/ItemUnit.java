package org.dimdev.dimcore.api.transfer;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Amount is an item count on both loaders. */
public record ItemUnit(Item item, DataComponentPatch components, long amount) implements Unit<ItemUnit> {
    public ItemUnit(Item item, long amount) {
        this(item, DataComponentPatch.EMPTY, amount);
    }

    public static ItemUnit of(ItemStack stack) {
        return new ItemUnit(stack.getItem(), stack.getComponentsPatch(), stack.getCount());
    }

    public ItemStack toStack() {
        ItemStack stack = new ItemStack(item, (int) Math.min(amount, Integer.MAX_VALUE));
        stack.applyComponents(components);
        return stack;
    }

    @Override
    public ItemUnit withAmount(long amount) {
        return new ItemUnit(item, components, amount);
    }

    @Override
    public boolean sameResource(ItemUnit other) {
        return item == other.item && components.equals(other.components);
    }
}
