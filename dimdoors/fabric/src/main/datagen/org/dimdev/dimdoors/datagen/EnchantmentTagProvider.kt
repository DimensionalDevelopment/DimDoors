package org.dimdev.dimdoors.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantments
import org.dimdev.dimdoors.enchantment.ModEnchants
import org.dimdev.dimdoors.tag.ModEnchantmentTags
import java.util.concurrent.CompletableFuture

class EnchantmentTagProvider(output: FabricDataOutput, completableFuture: CompletableFuture<HolderLookup.Provider>) : DimDoorsTagsProvider<Enchantment>(output, Registries.ENCHANTMENT, completableFuture) {
    override fun addTags(provider: HolderLookup.Provider) {
        tag(ModEnchantmentTags.DUNGEON_LOOT)
            .add(ModEnchants.RENDING_ENCHANTMENT)
            .add(ModEnchants.STRING_THEORY_ENCHANTMENT)
            .add(ModEnchants.TREPIDATION_ENCHANTMENT)
            .add(ModEnchants.TRANSCENDENT_ENCHANTMENT)

        tag(ModEnchantmentTags.BLOCKED_ON_FARSHOT).add(Enchantments.INFINITY, Enchantments.MULTISHOT)
    }
}
