package org.dimdev.dimdoors.item;

import java.util.List;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class RiftKeyItem extends Item {
	public RiftKeyItem(Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context); // TODO
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return super.hasGlint(stack); // TODO
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 30;
	}
}
