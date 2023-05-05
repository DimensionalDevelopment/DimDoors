package org.dimdev.dimdoors.item;

import java.util.List;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.world.item.Item;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.sound.ModSoundEvents;

public class RiftStabilizerItem extends Item {
	public RiftStabilizerItem(Item.Properties settings) {
		super(settings);
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		HitResult hit = player.raycast(RaycastHelper.REACH_DISTANCE, 0, false);

		if (world.isClient) {
			if (RaycastHelper.hitsDetachedRift(hit, world)) {
				// TODO: not necessarily success, fix this and all other similar cases to make arm swing correct
				return new TypedActionResult<>(ActionResult.SUCCESS, stack);
			} else {
				player.sendMessage(Text.translatable("tools.rift_miss"), true);
				RiftBlockEntity.showRiftCoreUntil = System.currentTimeMillis() + DimensionalDoors.getConfig().getGraphicsConfig().highlightRiftCoreFor;
				return new TypedActionResult<>(ActionResult.FAIL, stack);
			}
		}

		if (RaycastHelper.hitsDetachedRift(hit, world)) {
			DetachedRiftBlockEntity rift = (DetachedRiftBlockEntity) world.getBlockEntity(new BlockPos(new Vec3i((int) hit.getPos().x, (int) hit.getPos().y, (int) hit.getPos().z)));
			if (!rift.stabilized && !rift.closing) {
				rift.setStabilized(true);
				world.playSound(null, player.getBlockPos(), ModSoundEvents.RIFT_CLOSE, SoundCategory.BLOCKS, 0.6f, 1); // TODO: different sound
				stack.damage(1, player, a -> {
				});
				player.sendMessage(Text.translatable(this.getTranslationKey() + ".stabilized"), true);
				return new TypedActionResult<>(ActionResult.SUCCESS, stack);
			} else {
				player.sendMessage(Text.translatable(this.getTranslationKey() + ".already_stabilized"), true);
			}
		}
		return new TypedActionResult<>(ActionResult.FAIL, stack);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack itemStack, World world, List<Text> list, TooltipContext tooltipContext) {
		list.add(Text.translatable(this.getTranslationKey() + ".info"));
	}
}
