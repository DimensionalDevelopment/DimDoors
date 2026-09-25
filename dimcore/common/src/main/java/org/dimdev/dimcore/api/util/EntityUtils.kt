package org.dimdev.dimcore.api.util

import net.minecraft.network.chat.Component
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.TraceableEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import org.dimdev.dimcore.api.castOrNull
import java.util.*

object EntityUtils {
    @JvmStatic
    val Entity.owner: Entity
        get() {
            val entity = this
            return when {
                entity is Player -> return entity
                // Thrower
                entity is TraceableEntity -> entity.owner
                entity.controllingPassenger != null -> entity.controllingPassenger
                !entity.passengers.isEmpty() -> entity.passengers.last()

                // Owned Animals
                entity is Mob && entity.isLeashed -> entity.leashHolder
                entity is TamableAnimal && entity.owner != null -> entity.owner
                else -> null
            }?.owner ?: entity
        }
    val Entity.ownerPlayer: Player? get() = this.owner.castOrNull<Player>()

    val DamageSource.projectile: Projectile? get() = this.directEntity?.castOrNull() ?: this.entity?.castOrNull()

    val Entity.ownerPlayerUuid: UUID? get() = this.ownerPlayer?.uuid

    @JvmStatic @JvmOverloads fun chat(entity: Entity?, text: Component, actionBar: Boolean = false) {
        if (entity is Player) entity.displayClientMessage(text, actionBar)
    }
}
