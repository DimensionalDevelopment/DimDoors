package org.dimdev.dimdoors.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.client.ToolTipHelper;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class RiftRemoverItem extends Item {
	public static final String ID = "rift_remover";
	public static final ResourceLocation REMOVED_RIFT_LOOT_TABLE = DimensionalDoors.id("removed_rift");

	public RiftRemoverItem(Item.Properties settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
		ToolTipHelper.processTranslation(list, this.getDescription() + ".info");
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		HitResult hit = player.pick(RaycastHelper.REACH_DISTANCE, 0, false);

		if (world.isClientSide) {
			if (!RaycastHelper.hitsDetachedRift(hit, world)) {
				player.displayClientMessage(Component.translatable("tools.rift_miss"), true);
				RiftBlockEntity.showRiftCoreUntil = System.currentTimeMillis() + DimensionalDoors.getConfig().getGraphicsConfig().highlightRiftCoreFor;
			}
			return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
		}

		if (RaycastHelper.hitsDetachedRift(hit, world)) {
			// casting to BlockHitResult is mostly safe since RaycastHelper#hitsDetachedRift already checks hit type
			DetachedRiftBlockEntity rift = (DetachedRiftBlockEntity) world.getBlockEntity(((BlockHitResult) hit).getBlockPos());
			if (!Objects.requireNonNull(rift).closing) {
				rift.setClosing(true);
				world.playSound(null, player.blockPosition(), ModSoundEvents.RIFT_CLOSE.get(), SoundSource.BLOCKS, 0.6f, 1);
				stack.hurtAndBreak(10, player, a -> a.broadcastBreakEvent(hand));
				LootParams ctx = new LootParams.Builder((ServerLevel) world).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(((BlockHitResult) hit).getBlockPos())).withOptionalParameter(LootContextParams.THIS_ENTITY, player).create(LootContextParamSets.ALL_PARAMS);
				((ServerLevel) world).getServer().getLootData().getLootTable(REMOVED_RIFT_LOOT_TABLE).getRandomItems(ctx).forEach(stack1 -> {
					Containers.dropItemStack(world, ((BlockHitResult) hit).getBlockPos().getX(), ((BlockHitResult) hit).getBlockPos().getY(), ((BlockHitResult) hit).getBlockPos().getZ(), stack1);
				});

				player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".closing"), true);
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
			} else {
				player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".already_closing"), true);
			}
		}
		return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
	}
}
