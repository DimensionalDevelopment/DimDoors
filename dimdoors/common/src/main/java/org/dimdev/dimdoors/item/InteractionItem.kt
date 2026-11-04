package org.dimdev.dimdoors.item

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class InteractionItem(properties: Properties): Item(properties) {
    override fun hurtEnemy(stack: ItemStack, target: LivingEntity, attacker: LivingEntity): Boolean {
        return super.hurtEnemy(stack, target, attacker)
    }

    fun rightClickOnBlock(modifier: Modifier, level: Level, player: Player) {

    }

    fun leftClickOnBlock(modifier: Modifier, level: Level, player: Player) {

    }

    fun rightClickInAir(modifier: Modifier, level: Level, player: Player) {

    }

    fun leftClickInAir(modifier: Modifier, level: Level, player: Player) {

    }

    fun rightClickOnEntity(modifier: Modifier, level: Level, player: Player) {

    }

    fun leftClickOnEntity(modifier: Modifier, level: Level, player: Player) {

    }

    enum class Modifier {
        NONE, SHIFT, CONTROL
    }
}