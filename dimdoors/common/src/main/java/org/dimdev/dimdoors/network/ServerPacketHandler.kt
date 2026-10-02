package org.dimdev.dimdoors.network

import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimdoors.api.item.ExtendedItem
import org.dimdev.dimdoors.network.packet.c2s.HitBlockWithItemC2SPacket
import org.dimdev.dimdoors.network.packet.s2c.ClearPocketS2CPacket
import org.dimdev.dimdoors.network.packet.s2c.PlayerInventorySlotUpdateS2CPacket
import org.dimdev.dimdoors.network.packet.s2c.SyncPocketAddonsS2CPacket
import org.dimdev.dimdoors.rift.registry.PocketRegistry.Companion.instance
import org.dimdev.dimdoors.world.ModDimensions
import org.dimdev.dimdoors.world.pocket.type.Pocket
import java.util.*

object ServerPacketHandler {

    private val DATA_MAP = mutableMapOf<UUID, PlayerSyncData>()

    fun <T : CustomPacketPayload> sendPacket(player: ServerPlayer, packet: T) = platform.sendPacket(player, packet)

    fun syncPocketAddonsIfNeeded(player: ServerPlayer, pocket: Pocket<*, *>) {
        val syncPacket = getSyncData(player).syncPocketAddonsIfNeeded(pocket)
        if (syncPacket != null) sendPacket(player, syncPacket)
    }

    fun clearPocketIfNeeded(player: ServerPlayer) {
        if (getSyncData(player).clearPocketIfNeeded()) sendPacket(player, ClearPocketS2CPacket)
    }

    private fun getSyncData(player: ServerPlayer): PlayerSyncData = DATA_MAP.computeIfAbsent(player.getUUID()) { PlayerSyncData() }

    fun clear() = DATA_MAP.clear()

    // TODO: attach this to some event to detect other kinds teleportation
    @JvmStatic
    fun sync(player: ServerPlayer, stack: ItemStack, hand: InteractionHand?) {
        if (hand == InteractionHand.OFF_HAND) {
            sendPacket(player, PlayerInventorySlotUpdateS2CPacket(45, stack))
        } else {
            sendPacket(player, PlayerInventorySlotUpdateS2CPacket(player.getInventory().selected, stack))
        }
    }

    fun onAttackBlock(player: ServerPlayer, packet: HitBlockWithItemC2SPacket): CustomPacketPayload? {
        player.getServer()!!.execute {
            val item = player.getItemInHand(packet.hand).item


            if (item is ExtendedItem) {
                item.onAttackBlock(
                    player.level(),
                    player,
                    packet.hand,
                    packet.pos,
                    packet.direction
                )
            }
        }

        return null
    }

    class PlayerSyncData {
        private var lastSyncedPocketWorld: ResourceKey<Level>? = null
        private var lastSyncedPocketId = Int.MIN_VALUE
        private var pocketSyncDirty = true

        fun syncPocketAddonsIfNeeded(pocket: Pocket<*, *>): SyncPocketAddonsS2CPacket? {
            if ((pocketSyncDirty || pocket.id != lastSyncedPocketId || (pocket.world.location() != lastSyncedPocketWorld!!.location()))) {
                pocketSyncDirty = false
                lastSyncedPocketId = pocket.id
                lastSyncedPocketWorld = pocket.world

                return SyncPocketAddonsS2CPacket(
                    pocket.world,
                    pocket.box,
                    pocket.getAddons { a -> a.type.isSyncable }
                )
            }

            return null
        }

        /** Resets to the "no pocket" state, reporting whether the player was in one.  */
        fun clearPocketIfNeeded(): Boolean {
            if (lastSyncedPocketWorld == null && lastSyncedPocketId == Int.MIN_VALUE) return false

            lastSyncedPocketWorld = null
            lastSyncedPocketId = Int.MIN_VALUE
            // Keeps the next syncPocketAddonsIfNeeded from reading the now-null world.
            pocketSyncDirty = true

            return true
        }

        fun markPocketSyncDirty(id: Int) {
            if (lastSyncedPocketId == id) pocketSyncDirty = true
        }

        companion object {
            fun getPocket(world: Level?, pos: BlockPos): Pocket<*, *>? {
                if (!ModDimensions.isPocketDimension(world)) return null

                val directory = instance.getPocketDirectory(world!!.dimension())

                return directory.getPocketAt(pos)
            }
        }
    }
}
