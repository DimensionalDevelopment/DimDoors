package org.dimdev.dimdoors.client.wthit;

import java.util.Objects;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;

// FIXME: is not actually client sided
public enum EntranceRiftProvider implements IBlockComponentProvider {
	INSTANCE;

	private static final ResourceLocation ID = DimensionalDoors.resource("entrance_rift_provider");

	@Override
	public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
		if (!config.getBoolean(ID)) {
			return;
		}
		EntranceRiftBlockEntity blockEntity = ((EntranceRiftBlockEntity) accessor.getBlockEntity());
		VirtualTarget destination = Objects.requireNonNull(blockEntity).getDestination();
		if (destination != null) {
			Component tKey = MutableComponent.create(new TranslatableContents(destination.getType().getTranslationKey()));
			Component main = MutableComponent.create(new TranslatableContents("dimdoors.destination")).append(": ").append(tKey);
			tooltip.addLine(main);
		}
	}
}
