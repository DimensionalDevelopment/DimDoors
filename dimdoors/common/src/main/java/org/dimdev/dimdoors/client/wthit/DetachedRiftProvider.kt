package org.dimdev.dimdoors.client.wthit

import mcp.mobius.waila.api.IBlockAccessor
import mcp.mobius.waila.api.IBlockComponentProvider
import mcp.mobius.waila.api.IPluginConfig
import mcp.mobius.waila.api.ITooltip
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimdoors.DimensionalDoors.Companion.id
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity
import org.dimdev.dimdoors.rift.targets.IdMarker

object DetachedRiftProvider : IBlockComponentProvider {
    override fun appendBody(tooltip: ITooltip, accessor: IBlockAccessor, config: IPluginConfig) {
        if (!config.getBoolean(ID)) return

        val blockEntity = accessor.getBlockEntity<DetachedRiftBlockEntity>()
        val destination = requireNotNull(blockEntity).data.destination
        if (destination is IdMarker) tooltip.addLine(Component.literal(destination.id.toString()))
    }


    @JvmField val ID: ResourceLocation = id("detached_rift_provider")
}
