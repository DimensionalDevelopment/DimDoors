package org.dimdev.dimdoors.item;

import java.util.List;
import java.util.UUID;

import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.dimdev.dimdoors.util.EntityUtils;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.world.level.component.RiftKeyIdsComponent;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class RiftKeyItem extends Item {
	public RiftKeyItem(Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		RiftKeyIdsComponent component = RiftKeyIdsComponent.get(stack);
		if (context.isAdvanced() && !component.isEmpty()) {
			tooltip.add(LiteralText.EMPTY);
			tooltip.add(new TranslatableText("item.dimdoors.rift_key.ids"));
			for (UUID id : component.getIds()) {
				tooltip.add(new LiteralText(" " + id.toString()));
			}
		}
		super.appendTooltip(stack, world, tooltip, context);
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return RiftKeyIdsComponent.get(stack).isEmpty();
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 30;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (context.getWorld().isClient) {
			return ActionResult.CONSUME;
		}
		PlayerEntity player = context.getPlayer();
		BlockState state = context.getWorld().getBlockState(context.getBlockPos());
		if (player != null && state.getBlock() instanceof RiftProvider && player.isSneaky()) {
			RiftKeyIdsComponent component = RiftKeyIdsComponent.get(context.getStack());
			RiftBlockEntity riftBlockEntity = ((RiftProvider<?>) state.getBlock()).getRift(context.getWorld(), context.getBlockPos(), state);
			if (riftBlockEntity.isDetached()) {
				return super.useOnBlock(context);
			}
			EntranceRiftBlockEntity entranceRiftBlockEntity = ((EntranceRiftBlockEntity) riftBlockEntity);
			Rift rift = DimensionalRegistry.getRiftRegistry().getRift(new Location(entranceRiftBlockEntity.getWorld().getRegistryKey(), entranceRiftBlockEntity.getPos()));
			if (entranceRiftBlockEntity.isLocked()) {
				if (!component.remove(rift.getId())) {
					EntityUtils.chat(player, new TranslatableText("rifts.cantUnlock"));
				} else {
					entranceRiftBlockEntity.setLocked(false);
					entranceRiftBlockEntity.markDirty();
					EntityUtils.chat(player, new TranslatableText("rifts.unlocked"));
					return ActionResult.SUCCESS;
				}
			} else {
				entranceRiftBlockEntity.setLocked(true);
				component.addId(rift.getId());
				entranceRiftBlockEntity.markDirty();
				EntityUtils.chat(player, new TranslatableText("rifts.locked"));
				return ActionResult.SUCCESS;
			}
		}
		return super.useOnBlock(context);
	}
}
