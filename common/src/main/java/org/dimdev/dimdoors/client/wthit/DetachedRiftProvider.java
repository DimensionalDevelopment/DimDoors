package org.dimdev.dimdoors.client.wthit;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;

import java.util.Objects;

public enum DetachedRiftProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final ResourceLocation ID = DimensionalDoors.id("detached_rift_provider");

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
//        if (!config.getBoolean(ID)) {
//            return;
//        }
        DetachedRiftBlockEntity blockEntity = (DetachedRiftBlockEntity) accessor.getBlockEntity();
        VirtualTarget destination = Objects.requireNonNull(blockEntity).getDestination();
        if (destination != null) {
            Component tKey = Component.translatable(destination.getType().getTranslationKey());
            Component main = Component.translatable("dimdoors.destination").append(": ").append(tKey);
            tooltip.addLine(main);
            tooltip.addLine(Component.nullToEmpty("Size:" + blockEntity.size));
        }
    }
}
