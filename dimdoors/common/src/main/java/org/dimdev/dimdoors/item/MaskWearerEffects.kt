package org.dimdev.dimdoors.item

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.Registries
import net.minecraft.server.TickTask
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantments
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimcore.api.event.PlayerTeleportEvents
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity
import org.dimdev.dimdoors.entity.mask.MaskEvents
import org.dimdev.dimdoors.entity.mask.MaskType
import org.dimdev.dimdoors.rift.targets.LimboTarget
import org.dimdev.dimdoors.world.DataValues
import org.dimdev.dimdoors.world.ModDimensions
import kotlin.math.min

object MaskWearerEffects {
    const val MASK_DAMAGE_INTERVAL_BASE = 100
    const val MASK_DAMAGE_AMOUNT = 1f
    const val MAX_BODY_STACKS = 4
    private const val CYCLOPS_MAX_STEPS = 64

    fun createMaskStack(type: MaskType): ItemStack = ItemStack(ModItems.MASK).apply { set(ModDataComponentTypes.MASK_TYPE, type) }

    fun createWornMask(registries: RegistryAccess, type: MaskType): ItemStack = createMaskStack(type).apply {
        val enchantments = registries.lookupOrThrow(Registries.ENCHANTMENT)
        enchant(enchantments.getOrThrow(Enchantments.BINDING_CURSE), 1)
        enchant(enchantments.getOrThrow(Enchantments.VANISHING_CURSE), 1)
        set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false)
    }

    @JvmStatic
    fun wornType(player: Player): MaskType? = player.getItemBySlot(EquipmentSlot.HEAD).takeIf { it.`is`(ModItems.MASK) }?.get(ModDataComponentTypes.MASK_TYPE)

    @JvmStatic
    fun isWearing(player: ServerPlayer, type: MaskType) = wornType(player) == type && effectsActive(player, type)

    fun effectsActive(player: ServerPlayer, type: MaskType) = !ModDimensions.isRootDimension(player.level()) && MaskEvents.ALLOW_WORN_EFFECTS.invoker().invoke(player, type)

    fun equip(player: ServerPlayer, type: MaskType) {
        val head = player.getItemBySlot(EquipmentSlot.HEAD)

        if (head.`is`(ModItems.MASK)) {
            DataValues.MASK_BODY_STACKS.update(player, 0) { min((it ?: 0) + 1, MAX_BODY_STACKS) }
            return
        }

        player.setItemSlot(EquipmentSlot.HEAD, createWornMask(player.registryAccess(), type))
        if (!head.isEmpty && !player.inventory.add(head)) player.drop(head, false)
    }

    fun removeMasks(player: ServerPlayer) {
        val type = wornType(player) ?: return
        player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY)
        DataValues.MASK_BODY_STACKS.remove(player)
        MaskEvents.MASKS_REMOVED.invoker().invoke(player, type)
    }

    @JvmStatic
    fun cure(player: ServerPlayer) {
        removeMasks(player)
        DataValues.FRAY_VALUE.set(player, 0)
    }

    private fun burn(player: ServerPlayer) {
        val level = player.serverLevel()
        level.sendParticles(ParticleTypes.FLAME, player.x, player.eyeY, player.z, 20, 0.25, 0.25, 0.25, 0.02)
        level.playSound(null, player.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1f, 1f)
        removeMasks(player)
    }

    fun tick(player: ServerPlayer) {
        val type = wornType(player) ?: return

        if (ModDimensions.isLimboDimension(player.level())) return burn(player)
        if (!effectsActive(player, type)) return

        val stacks = DataValues.MASK_BODY_STACKS.getOrDefault(player, 0)
        if (player.tickCount % (MASK_DAMAGE_INTERVAL_BASE / (1 + stacks)) == 0) player.hurt(player.damageSources().wither(), MASK_DAMAGE_AMOUNT)

        if (player.tickCount % 20 == 0) {
            when (type) {
                MaskType.ECHO -> MobEffects.DIG_SLOWDOWN to 1
                MaskType.ENLIGHTENED -> MobEffects.GLOWING to 0
                MaskType.SCULKING -> MobEffects.BLINDNESS to 0
                else -> null
            }?.let { (effect, amplifier) -> player.addEffect(MobEffectInstance(effect, 60, amplifier, true, false)) }

            if (stacks >= MAX_BODY_STACKS) player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0, true, false))
        }

        if (type == MaskType.CYCLOP) tickCyclops(player)

        MaskEvents.WORN_TICK.invoker().invoke(player, type)
    }

    private fun tickCyclops(player: ServerPlayer) {
        val timer = DataValues.MASK_CYCLOPS_TIMER.getOrDefault(player, 0)
        if (timer > 0) return DataValues.MASK_CYCLOPS_TIMER.set(player, timer - 1)

        DataValues.MASK_CYCLOPS_TIMER.set(player, 200 + player.random.nextInt(201))

        val level = player.serverLevel()
        val direction = Direction.Plane.HORIZONTAL.getRandomDirection(player.random)
        var box = player.boundingBox
        var steps = 0

        while (steps < CYCLOPS_MAX_STEPS) {
            val next = box.move(direction.stepX.toDouble(), 0.0, direction.stepZ.toDouble())

            val rift = BlockPos.betweenClosed(BlockPos.containing(next.minX, next.minY, next.minZ), BlockPos.containing(next.maxX, next.maxY, next.maxZ))
                .firstNotNullOfOrNull { level.getBlockEntity(it) as? EntranceRiftBlockEntity<*> }
            if (rift != null) {
                rift.teleport(player)
                return
            }

            if (!level.noCollision(player, next)) break
            box = next
            steps++
        }

        if (steps > 0) player.connection.teleport(player.x + direction.stepX * steps, player.y, player.z + direction.stepZ * steps, player.yRot, player.xRot)
    }

    fun registerEvents() {
        platform.onPlayerTick(::tick)

        PlayerTeleportEvents.ALLOW.register { player, level, _ ->
            if (wornType(player) == null || ModDimensions.isLimboDimension(level)) return@register true

            player.server.tell(TickTask(player.server.tickCount) { LimboTarget.teleportToLimbo(player) })
            false
        }
    }
}
