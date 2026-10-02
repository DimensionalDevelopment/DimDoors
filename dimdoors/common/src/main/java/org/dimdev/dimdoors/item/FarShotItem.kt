//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package org.dimdev.dimdoors.item

import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.BowItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.Level
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided
import org.dimdev.dimdoors.entity.FarShotEnderPearlEntity
import org.dimdev.dimdoors.tag.ModEnchantmentTags
import java.util.function.Predicate

open class FarShotItem(properties: Properties) : BowItem(properties) {
    override fun createProjectile(
        level: Level,
        shooter: LivingEntity,
        weapon: ItemStack,
        ammo: ItemStack,
        isCrit: Boolean
    ): Projectile {
        val enchantments = weapon.enchantments
        val registry = level.registryAccess().asGetterLookup().lookupOrThrow(Registries.ENCHANTMENT)

        val hasFlaming = enchantments.getLevel(registry.getOrThrow(Enchantments.FLAME)) > 0
        val punchLevel = enchantments.getLevel(registry.getOrThrow(Enchantments.PUNCH))

        return FarShotEnderPearlEntity(level, shooter, hasFlaming, punchLevel)
    }

    override fun getAllSupportedProjectiles(): Predicate<ItemStack> {
        return PEARLS_ONLY
    }


    companion object {
        val PEARLS_ONLY: Predicate<ItemStack> = { stack -> stack.`is`(getSided().enderPearlsTag) }

        fun allowsEnchantment(enchantment: Holder<Enchantment>) = !enchantment.`is`(ModEnchantmentTags.BLOCKED_ON_FARSHOT)
    }
}
