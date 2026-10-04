package org.dimdev.dimdoors.listener.pocket

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import org.dimdev.dimdoors.network.client.ClientPacketListener
import org.dimdev.dimdoors.rift.registry.PocketRegistry.Companion.instance
import org.dimdev.dimdoors.world.ModDimensions
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddonType

object PocketListenerUtil {
    @JvmStatic
    fun <T : PocketAddon> getAddon(
        holder: PocketAddonType<T, *>,
        world: Level,
        pos: BlockPos
    ): T? {
        return if (world.isClientSide) ClientPacketListener.getAddonClient(
            holder,
            world,
            pos
        ) else getAddonCommon(holder, world, pos)
    }

    fun <T : PocketAddon> getAddonCommon(
        clazz: PocketAddonType<T, *>,
        world: Level,
        pos: BlockPos
    ): T? {
        if (world.isClientSide) throw UnsupportedOperationException("Cannot call this method on the Client.")
        if (!ModDimensions.isPocketDimension(world)) return null
        val pocket = instance.getPocketDirectory(world.dimension()).getPocketAt(pos) ?: return null
        return pocket.getAddon(clazz)
    }
}