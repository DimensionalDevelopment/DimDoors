package org.dimdev.dimdoors.world.decay;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class DecayInventoryHelper {
    private DecayInventoryHelper() {}

    public static List<ItemStack> takeContents(Level level, BlockPos pos) {
        return takeContents(level.getBlockEntity(pos));
    }

    public static List<ItemStack> takeContents(@Nullable BlockEntity blockEntity) {
        if (!(blockEntity instanceof Container container)) {
            return List.of();
        }

        List<ItemStack> contents = new ArrayList<>();

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.removeItemNoUpdate(slot);

            if (!stack.isEmpty()) {
                contents.add(stack);
            }
        }

        container.setChanged();
        return contents;
    }

    public static void transferOrDrop(Level level, BlockPos pos, List<ItemStack> contents) {
        if (contents.isEmpty()) {
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        Container container = blockEntity instanceof Container target ? target : null;
        List<ItemStack> leftovers = new ArrayList<>();

        for (ItemStack stack : contents) {
            ItemStack remainder = stack.copy();

            if (container != null) {
                remainder = insert(container, remainder);
            }

            if (!remainder.isEmpty()) {
                leftovers.add(remainder);
            }
        }

        if (container != null) {
            container.setChanged();
        }

        drop(level, pos, leftovers);
    }

    public static void drop(Level level, BlockPos pos, List<ItemStack> contents) {
        for (ItemStack stack : contents) {
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
    }

    private static ItemStack insert(Container container, ItemStack stack) {
        for (int slot = 0; slot < container.getContainerSize() && !stack.isEmpty(); slot++) {
            if (!container.canPlaceItem(slot, stack)) {
                continue;
            }

            ItemStack existing = container.getItem(slot);

            if (existing.isEmpty()) {
                int moved = Math.min(stack.getCount(), Math.min(container.getMaxStackSize(), stack.getMaxStackSize()));
                ItemStack placed = stack.copy();
                placed.setCount(moved);
                container.setItem(slot, placed);
                stack.shrink(moved);
                continue;
            }

            if (!ItemStack.isSameItemSameComponents(existing, stack)) {
                continue;
            }

            int max = Math.min(container.getMaxStackSize(), existing.getMaxStackSize());
            int space = max - existing.getCount();

            if (space <= 0) {
                continue;
            }

            int moved = Math.min(space, stack.getCount());
            existing.grow(moved);
            container.setItem(slot, existing);
            stack.shrink(moved);
        }

        return stack;
    }
}
