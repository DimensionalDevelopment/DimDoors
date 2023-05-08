package org.dimdev.dimdoors.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RiftStabilizerItem extends Item {
	public RiftStabilizerItem(Item.Properties settings) {
		super(settings);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		HitResult hit = player.pick(RaycastHelper.REACH_DISTANCE, 0, false);

		if (world.isClientSide) {
			if (RaycastHelper.hitsDetachedRift(hit, world)) {
				// TODO: not necessarily success, fix this and all other similar cases to make arm swing correct
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
			} else {
				player.displayClientMessage(Component.translatable("tools.rift_miss"), true);
				RiftBlockEntity.showRiftCoreUntil = System.currentTimeMillis() + DimensionalDoors.getConfig().getGraphicsConfig().highlightRiftCoreFor;
				return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
			}
		}

		if (RaycastHelper.hitsDetachedRift(hit, world)) {
			DetachedRiftBlockEntity rift = (DetachedRiftBlockEntity) world.getBlockEntity(new BlockPos(new Vec3i((int) hit.getLocation().x, (int) hit.getLocation().y, (int) hit.getLocation().z)));
			if (!rift.stabilized && !rift.closing) {
				rift.setStabilized(true);
				world.playSound(null, player.blockPosition(), ModSoundEvents.RIFT_CLOSE.get(), SoundSource.BLOCKS, 0.6f, 1); // TODO: different sound
				stack.hurtAndBreak(1, player, a -> {});
				player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".stabilized"), true);
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
			} else {
				player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".already_stabilized"), true);
			}
		}
		return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
	}

	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
		list.add(Component.translatable(this.getDescriptionId() + ".info"));
	}
}
