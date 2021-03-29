package org.dimdev.dimdoors.client.wthit;

import java.util.List;
import java.util.Objects;

import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

// FIXME: is not actually client sided
public enum EntranceRiftProvider implements IComponentProvider {
	INSTANCE;

	private static final Identifier ID = new Identifier("dimdoors", "entrance_rift_provider");

	@Override
	public void appendBody(List<Text> tooltip, IDataAccessor accessor, IPluginConfig config) {
		if (!config.get(ID, true)) {
			return;
		}
		EntranceRiftBlockEntity blockEntity = ((EntranceRiftBlockEntity) accessor.getBlockEntity());
		VirtualTarget destination = Objects.requireNonNull(blockEntity).getDestination();
		if (destination != null) {
			TranslatableText tKey = new TranslatableText(destination.getType().getTranslationKey());
			Text main = new TranslatableText("dimdoors.destination").append(": ").append(tKey);
			tooltip.add(main);
		}
	}
}
