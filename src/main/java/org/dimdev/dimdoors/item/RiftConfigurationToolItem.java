package org.dimdev.dimdoors.item;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.dimdev.dimdoors.api.item.ExtendedItem;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.item.component.CounterComponent;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.rift.targets.IdMarker;

import static net.fabricmc.api.EnvType.CLIENT;

public class RiftConfigurationToolItem extends Item implements ExtendedItem {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final String ID = "rift_configuration_tool";

	RiftConfigurationToolItem() {
		super(new Item.Properties().stacksTo(1).durability(16));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		HitResult hit = player.pick(RaycastHelper.REACH_DISTANCE, 0, false);

		if (world.isClientSide) {
			return InteractionResultHolder.fail(stack);
		} else {
			CounterComponent counter = CounterComponent.get(stack);

			if (RaycastHelper.hitsRift(hit, world)) {
				RiftBlockEntity rift = (RiftBlockEntity) world.getBlockEntity(((BlockHitResult) hit).getBlockPos());

				if (rift.getDestination() instanceof IdMarker && ((IdMarker) rift.getDestination()).getId() >= 0) {
					EntityUtils.chat(player, Component.nullToEmpty("Id: " + ((IdMarker) rift.getDestination()).getId()));
				} else {
					int id = counter.increment();

					ServerPacketHandler.get((ServerPlayer) player).sync(stack, hand);

					EntityUtils.chat(player, Component.nullToEmpty("Rift stripped of data and set to target id: " + id));

					rift.setDestination(new IdMarker(id));
				}

				return InteractionResultHolder.success(stack);
			} else {
				EntityUtils.chat(player, Component.nullToEmpty("Current Count: " + counter.count()));
			}
		}

		return InteractionResultHolder.success(stack);
	}

	@Override
	public InteractionResultHolder<Boolean> onAttackBlock(Level world, Player player, InteractionHand hand, BlockPos pos, Direction direction) {
		if (world.isClientSide) {
			if (player.isShiftKeyDown()) {
				if (CounterComponent.get(player.getItemInHand(hand)).count() != 0 || world.getBlockEntity(pos) instanceof RiftBlockEntity) {
					return InteractionResultHolder.success(true);
				}
				return InteractionResultHolder.fail(false);
			}
		} else {
			ItemStack stack = player.getItemInHand(hand);
			if (player.isShiftKeyDown()) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof RiftBlockEntity) {
					RiftBlockEntity rift = (RiftBlockEntity) blockEntity;
					if (!(rift.getDestination() instanceof IdMarker) || ((IdMarker) rift.getDestination()).getId() != -1) {
						rift.setDestination(new IdMarker(-1));
						EntityUtils.chat(player, Component.nullToEmpty("Rift stripped of data and set to invalid id: -1"));
						return InteractionResultHolder.success(false);
					}
				} else if (CounterComponent.get(stack).count() != 0) {
					CounterComponent.get(stack).reset();

					ServerPacketHandler.get((ServerPlayer) player).sync(stack, hand);

					EntityUtils.chat(player, Component.nullToEmpty("Counter has been reset."));
					return InteractionResultHolder.success(false);
				}
			}
		}
		return InteractionResultHolder.pass(false);
	}

	@Override
	@Environment(CLIENT)
	public void appendHoverText(ItemStack itemStack, Level world, List<Component> list, TooltipFlag tooltipContext) {
		if (I18n.exists(this.getDescriptionId() + ".info")) {
			list.add(MutableComponent.create(new TranslatableContents(this.getDescriptionId() + ".info")));
		}
	}
}
