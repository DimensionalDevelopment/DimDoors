package org.dimdev.dimdoors.item.loot

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancements.critereon.EntityPredicate
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders
import org.dimdev.dimdoors.util.CodecUtils.nullable

data class EntityNearBy(val range: NumberProvider, val predicate: EntityPredicate?, val interval: Int) : LootItemCondition {
    override fun getType(): LootItemConditionType = ModItemLootConditions.ENTITY_NEARBY.value()

    override fun getReferencedContextParams() = mutableSetOf(
            LootContextParams.THIS_ENTITY,
            LootContextParams.ENCHANTMENT_LEVEL,
            LootContextParams.ORIGIN
    )

    override fun test(lootContext: LootContext): Boolean {
        val entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY)
        if (entity !is LivingEntity || !entity.isAlive) return false

        if (interval > 0 && entity.tickCount % interval != 0) return false

        val searchRange = range.getFloat(lootContext).toDouble()
        if (searchRange <= 0.0) return false

        val area = entity.boundingBox.inflate(searchRange)

        return !lootContext.level
            .getEntitiesOfClass(Entity::class.java, area) {
                e -> e !== entity && e.isAlive && predicate?.matches(lootContext.level, entity.position(), e) ?: true
            }.isEmpty()
    }

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec { instance -> instance.group(
                    NumberProviders.CODEC.fieldOf("range").forGetter(EntityNearBy::range),
                    EntityPredicate.CODEC.optionalFieldOf("predicate").nullable().forGetter(EntityNearBy::predicate),
                    Codec.INT.optionalFieldOf("interval", 40).forGetter(EntityNearBy::interval)
                ).apply(instance, ::EntityNearBy);
        }

        @JvmStatic
        fun nearby(range: NumberProvider, predicate: EntityPredicate?, interval: Int) = LootItemCondition.Builder { EntityNearBy(range, predicate, interval) }
    }
}
