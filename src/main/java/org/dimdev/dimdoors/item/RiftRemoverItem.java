package org.dimdev.dimdoors.item;

import java.util.List;
import java.util.Objects;

import net.minecraft.util.hit.BlockHitResult;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.client.ToolTipHelper;
import org.dimdev.dimdoors.sound.ModSoundEvents;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class RiftRemoverItem extends Item {
	public static final String ID = "rift_remover";

	public RiftRemoverItem(Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack itemStack, World world, List<Text> list, TooltipContext tooltipContext) {
		ToolTipHelper.processTranslation(list, this.getTranslationKey() + ".info");
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		HitResult hit = player.raycast(RaycastHelper.REACH_DISTANCE, 0, false);

		if (world.isClient) {
			if (!RaycastHelper.hitsDetachedRift(hit, world)) {
				player.sendMessage(new TranslatableText("tools.rift_miss"), true);
				RiftBlockEntity.showRiftCoreUntil = System.currentTimeMillis() + DimensionalDoorsInitializer.getConfig().getGraphicsConfig().highlightRiftCoreFor;
			}
			return new TypedActionResult<>(ActionResult.FAIL, stack);
		}

		if (RaycastHelper.hitsDetachedRift(hit, world)) {
			// casting to BlockHitResult is mostly safe since RaycastHelper#hitsDetachedRift already checks hit type
			DetachedRiftBlockEntity rift = (DetachedRiftBlockEntity) world.getBlockEntity(((BlockHitResult) hit).getBlockPos());
			if (!Objects.requireNonNull(rift).closing) {
				rift.setClosing(true);
				world.playSound(null, player.getBlockPos(), ModSoundEvents.RIFT_CLOSE, SoundCategory.BLOCKS, 0.6f, 1);
				stack.damage(10, player, a -> a.sendToolBreakStatus(hand));
				player.sendMessage(new TranslatableText(this.getTranslationKey() + ".closing"), true);
				return new TypedActionResult<>(ActionResult.SUCCESS, stack);
			} else {
				player.sendMessage(new TranslatableText(this.getTranslationKey() + ".already_closing"), true);
			}
		}
		return new TypedActionResult<>(ActionResult.FAIL, stack);
	}
}
