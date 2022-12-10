package org.dimdev.dimdoors.client.wthit;

import java.util.Objects;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;

// FIXME: is not actually client sided
public enum EntranceRiftProvider implements IBlockComponentProvider {
	INSTANCE;

	private static final Identifier ID = DimensionalDoors.id("entrance_rift_provider");

	@Override
	public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
		if (!config.getBoolean(ID)) {
			return;
		}
		EntranceRiftBlockEntity blockEntity = ((EntranceRiftBlockEntity) accessor.getBlockEntity());
		VirtualTarget destination = Objects.requireNonNull(blockEntity).getDestination();
		if (destination != null) {
			Text tKey = MutableText.of(new TranslatableTextContent(destination.getType().getTranslationKey()));
			Text main = MutableText.of(new TranslatableTextContent("dimdoors.destination")).append(": ").append(tKey);
			tooltip.addLine(main);
		}
	}
}
