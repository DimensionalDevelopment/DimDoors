package org.dimdev.dimdoors.client.wthit;

import java.util.List;

import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.rift.registry.LinkProperties;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;

public enum EntranceRiftProvider implements IComponentProvider {
	INSTANCE;

	@Override
	public void appendBody(List<Text> tooltip, IDataAccessor accessor, IPluginConfig config) {
		EntranceRiftBlockEntity blockEntity = ((EntranceRiftBlockEntity) accessor.getBlockEntity());
		LinkProperties properties = blockEntity.getProperties();
		if (properties != null) {
			tooltip.add(new TranslatableText("dimdoors.linkProperties.oneWay", properties.oneWay));
			tooltip.add(new TranslatableText("dimdoors.linkProperties.linksRemaining", properties.linksRemaining));
		}
		if (blockEntity.getColor() != null) {
			TranslatableText colorText = new TranslatableText("dimdoors.color");
			LiteralText actualColorText = new LiteralText(Integer.toHexString(blockEntity.getColor().toIntNoAlpha()));
			actualColorText.getStyle().withColor(TextColor.fromRgb(blockEntity.getColor().toIntNoAlpha()));
			colorText.append(actualColorText);
			tooltip.add(new TranslatableText("dimdoors.color", colorText));
		}
		tooltip.add(new LiteralText("Foo"));
	}
}
