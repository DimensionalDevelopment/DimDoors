package org.dimdev.dimdoors;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public interface ICreativeTabHandler {
    void appendStack(CreativeModeTab tab, ItemStack item);

    void modify(CreativeModeTab tab, ModifyTabCallback filler);

    @FunctionalInterface
    interface ModifyTabCallback {
        void accept(FeatureFlagSet flags, CreativeTabOutput output, boolean canUseGameMasterBlocks);
    }

    public interface CreativeTabOutput extends CreativeModeTab.Output {
        void acceptAfter(ItemStack after, ItemStack stack, CreativeModeTab.TabVisibility visibility);

        default void acceptAfter(ItemStack after, ItemStack stack) {
            acceptAfter(after, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        void acceptBefore(ItemStack before, ItemStack stack, CreativeModeTab.TabVisibility visibility);

        default void acceptBefore(ItemStack after, ItemStack stack) {
            acceptBefore(after, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        @Override
        default void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
            acceptAfter(ItemStack.EMPTY, stack, visibility);
        }
    }
}
