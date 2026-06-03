package org.dimdev.dimdoors.client.wthit;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.pockets.virtual.reference.IdReference;
import org.dimdev.dimdoors.rift.targets.IdMarker;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;

import java.util.Objects;

public enum DetachedRiftProvider implements IBlockComponentProvider {
    INSTANCE;

    static final Identifier ID = DimensionalDoors.id("detached_rift_provider");

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
//        if (!config.getBoolean(ID)) {
//            return;
//        }
        DetachedRiftBlockEntity blockEntity = accessor.getBlockEntity();
        VirtualTarget destination = Objects.requireNonNull(blockEntity).getDestination();
        if (destination instanceof IdMarker id) {
            tooltip.addLine(Component.literal(String.valueOf(id.getId())));
        }
    }
}
