//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.dimdev.dimdoors.item;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.entity.FarShotEnderPearlEntity;
import org.dimdev.dimdoors.tag.ModEnchantmentTags;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class FarShotItem extends BowItem {
    public static final Predicate<ItemStack> PEARLS_ONLY = (p_43017_) -> p_43017_.is(DimensionalDoors.getSided().getEnderPearlsTag());

    public FarShotItem(Properties properties) {
        super(properties);
    }

    protected @NotNull Projectile createProjectile(@NotNull Level level, @NotNull LivingEntity shooter, @NotNull ItemStack weapon, @NotNull ItemStack ammo, boolean isCrit) {
        var enchantments = weapon.getEnchantments();
        var registry = level.registryAccess().asGetterLookup().lookupOrThrow(Registries.ENCHANTMENT);

        var hasFlaming = enchantments.getLevel(registry.getOrThrow(Enchantments.FLAME)) > 0;
        var punchLevel = enchantments.getLevel(registry.getOrThrow(Enchantments.PUNCH));

        return new FarShotEnderPearlEntity(level, shooter, hasFlaming, punchLevel);
    }

    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return PEARLS_ONLY;
    }


    public static boolean allowsEnchantment(Holder<Enchantment> enchantment) {
        return !enchantment.is(ModEnchantmentTags.BLOCKED_ON_FARSHOT);
    }
}
