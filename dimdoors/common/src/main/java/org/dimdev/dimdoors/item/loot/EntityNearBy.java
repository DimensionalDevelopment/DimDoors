package org.dimdev.dimdoors.item.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public record EntityNearBy(NumberProvider range, Optional<EntityPredicate> predicate, int interval) implements LootItemCondition {
    public static final MapCodec<EntityNearBy> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.fieldOf("range").forGetter(EntityNearBy::range),
            EntityPredicate.CODEC.optionalFieldOf("predicate").forGetter(EntityNearBy::predicate),
            Codec.INT.optionalFieldOf("interval", 40).forGetter(EntityNearBy::interval)
    ).apply(instance, EntityNearBy::new));

    public static LootItemCondition.Builder nearby(NumberProvider range, EntityPredicate predicate, int interval) {
        return () -> new EntityNearBy(range, Optional.ofNullable(predicate), interval);
    }

    @Override
    public @NotNull LootItemConditionType getType() {
        return ModItemLootConditions.ENTITY_NEARBY;
    }

    @Override
    public @NotNull Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.THIS_ENTITY, LootContextParams.ENCHANTMENT_LEVEL, LootContextParams.ORIGIN);
    }

    @Override
    public boolean test(LootContext lootContext) {
        Entity entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof LivingEntity wearer) || !wearer.isAlive()) {
            return false;
        }

        if (interval > 0 && wearer.tickCount % interval != 0) {
            return false;
        }

        double searchRange = range.getFloat(lootContext);
        if (searchRange <= 0.0) {
            return false;
        }

        AABB area = wearer.getBoundingBox().inflate(searchRange);

        return !lootContext.getLevel().getEntitiesOfClass(
                Entity.class,
                area,
                e -> e != wearer
                        && e.isAlive()
                        && predicate.map(value -> value.matches(lootContext.getLevel(), wearer.position(), e)).orElse(true)
        ).isEmpty();
    }
}
