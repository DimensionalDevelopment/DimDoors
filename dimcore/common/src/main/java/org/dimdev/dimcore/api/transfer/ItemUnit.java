package org.dimdev.dimcore.api.transfer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

/** Amount is an item count on both loaders. */
public record ItemUnit(Item item, DataComponentPatch components, long amount) implements Unit<ItemUnit> {
    public static final Codec<ItemUnit> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemUnit::item),
            DataComponentPatch.CODEC.fieldOf("components").forGetter(ItemUnit::components),
            Codec.LONG.fieldOf("amount").forGetter(ItemUnit::amount)
    ).apply(instance, ItemUnit::new));

    private static final ItemUnit EMPTY = new ItemUnit(Items.AIR, 0);

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

    public static ItemUnit empty() {
        return EMPTY;
    }
}
