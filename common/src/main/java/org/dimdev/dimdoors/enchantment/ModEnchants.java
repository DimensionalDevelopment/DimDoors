package org.dimdev.dimdoors.enchantment;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.PlaySoundEffect;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.enchantment.effect.TranscendentProjectileEffect;
import org.dimdev.dimdoors.item.loot.EntityNearBy;
import org.dimdev.dimdoors.tag.ModEntityTypeTags;
import org.dimdev.dimdoors.tag.ModItemTags;

public class ModEnchants {
    public static final ResourceKey<Enchantment> STRING_THEORY_ENCHANTMENT = ResourceKey.create(Registries.ENCHANTMENT, DimensionalDoors.id("string_theory"));
    public static final ResourceKey<Enchantment> TREPIDATION_ENCHANTMENT = ResourceKey.create(Registries.ENCHANTMENT, DimensionalDoors.id("trepidation"));
    public static final ResourceKey<Enchantment> TRANSCENDENT_ENCHANTMENT = ResourceKey.create(Registries.ENCHANTMENT, DimensionalDoors.id("transcendent"));
    public static final ResourceKey<Enchantment> RENDING_ENCHANTMENT = ResourceKey.create(Registries.ENCHANTMENT, DimensionalDoors.id("rending"));

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> items = context.lookup(Registries.ITEM);

        register(context, STRING_THEORY_ENCHANTMENT, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE), 10, 4,
                        Enchantment.dynamicCost(1, 11),
                        Enchantment.dynamicCost(12, 11), 1,
                        EquipmentSlotGroup.ARMOR))
                .exclusiveWith(enchantments.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE)));

        register(context, TREPIDATION_ENCHANTMENT, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE), 2, 3,
                        Enchantment.dynamicCost(5, 8),
                        Enchantment.dynamicCost(25, 8), 4,
                        EquipmentSlotGroup.CHEST))
                .withEffect(
                        EnchantmentEffectComponents.TICK,
                        new PlaySoundEffect(
                                Holder.direct(SoundEvents.WARDEN_HEARTBEAT),
                                ConstantFloat.of(0.8F),
                                ConstantFloat.of(1.0F)
                        ),
                        EntityNearBy.nearby(
                                EnchantmentLevelProvider.forEnchantmentLevel(LevelBasedValue.perLevel(5.0F, 5.0F)),
                                EntityPredicate.Builder.entity().of(ModEntityTypeTags.TREPIDATION_DETECTED).build(),
                                40
                        )
                ));

        register(context, TRANSCENDENT_ENCHANTMENT, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ModItemTags.TRANSCENDENT_ENCHANTABLE), 2, 1,
                        Enchantment.dynamicCost(15, 10),
                        Enchantment.dynamicCost(45, 10), 4,
                        EquipmentSlotGroup.MAINHAND))
                .withEffect(
                        EnchantmentEffectComponents.PROJECTILE_SPAWNED,
                        TranscendentProjectileEffect.INSTANCE
                ));

        register(context, RENDING_ENCHANTMENT, Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(ItemTags.MINING_ENCHANTABLE), 2, 2,
                Enchantment.dynamicCost(15, 9),
                Enchantment.dynamicCost(45, 9), 4,
                EquipmentSlotGroup.MAINHAND)));
    }

    private static void register(BootstrapContext<Enchantment> bootstrapContext, ResourceKey<Enchantment> resourceKey, Enchantment.Builder builder) {
        bootstrapContext.register(resourceKey, builder.build(resourceKey.location()));
    }
}
