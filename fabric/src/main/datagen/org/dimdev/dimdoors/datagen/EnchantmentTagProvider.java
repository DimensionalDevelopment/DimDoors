package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import org.dimdev.dimdoors.enchantment.ModEnchants;
import org.dimdev.dimdoors.tag.ModEnchantmentTags;

import java.util.concurrent.CompletableFuture;

public class EnchantmentTagProvider extends FabricTagProvider.EnchantmentTagProvider {
    public EnchantmentTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        tag(ModEnchantmentTags.DUNGEON_LOOT)
                .add(ModEnchants.RENDING_ENCHANTMENT)
                .add(ModEnchants.STRING_THEORY_ENCHANTMENT)
                .add(ModEnchants.TREPIDATION_ENCHANTMENT)
                .add(ModEnchants.TRANSCENDENT_ENCHANTMENT);
    }
}
