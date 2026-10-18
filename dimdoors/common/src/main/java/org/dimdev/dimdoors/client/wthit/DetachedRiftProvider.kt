package org.dimdev.dimdoors.client.wthit

import mcp.mobius.waila.api.IBlockAccessor
import mcp.mobius.waila.api.IBlockComponentProvider
import mcp.mobius.waila.api.IDataProvider
import mcp.mobius.waila.api.IDataWriter
import mcp.mobius.waila.api.IPluginConfig
import mcp.mobius.waila.api.IServerAccessor
import mcp.mobius.waila.api.ITooltip
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimdoors.DimensionalDoors.Companion.id
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity
import org.dimdev.dimdoors.rift.targets.IdMarker

object DetachedRiftProvider : IBlockComponentProvider, IDataProvider<DetachedRiftBlockEntity> {
    private const val RIFT_ID = "riftId"

    override fun appendData(data: IDataWriter, accessor: IServerAccessor<DetachedRiftBlockEntity>, config: IPluginConfig) {
        (accessor.target.data.destination as? IdMarker)?.let { data.raw().putInt(RIFT_ID, it.id) }
    }

    override fun appendBody(tooltip: ITooltip, accessor: IBlockAccessor, config: IPluginConfig) {
        if (!config.getBoolean(ID)) return

        val raw = accessor.data.raw()
        if (raw.contains(RIFT_ID)) tooltip.addLine(Component.literal(raw.getInt(RIFT_ID).toString()))
    }


    @JvmField val ID: ResourceLocation = id("detached_rift_provider")
}
