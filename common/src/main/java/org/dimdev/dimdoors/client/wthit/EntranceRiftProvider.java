package org.dimdev.dimdoors.client.wthit;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;

import java.util.Objects;

// FIXME: is not actually client sided
public enum EntranceRiftProvider implements IBlockComponentProvider {
    INSTANCE;

    static final ResourceLocation ID = DimensionalDoors.id("entrance_rift_provider");

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
//    if (!config.getBoolean(ID)) {
//        return;
//    }
//    RiftBlockEntity<?> blockEntity = accessor.getBlockEntity();
//
//        tooltip.addLine(Component.literal("Closing: " + blockEntity.closing));
//
//        tooltip.addLine(Component.literal("Size: " + blockEntity.closing));

//    VirtualTarget destination = Objects.requireNonNull(blockEntity).getDestination();
//    if (destination != null) {
//        Component tKey = Component.translatable(destination.getType().getTranslationKey());
//        Component main = Component.translatable("dimdoors.destination").append(": ").append(tKey);
//        tooltip.addLine(main);
//    }
    }
}
