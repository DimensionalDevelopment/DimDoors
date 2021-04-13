package org.dimdev.dimdoors.item;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.item.ExtendedItem;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.rift.targets.IdMarker;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.item.component.CounterComponent;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.api.Environment;

import static net.fabricmc.api.EnvType.CLIENT;

public class RiftConfigurationToolItem extends Item implements ExtendedItem {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final String ID = "rift_configuration_tool";

	RiftConfigurationToolItem() {
		super(new Item.Settings().group(ModItems.DIMENSIONAL_DOORS).maxCount(1).maxDamage(16));
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		HitResult hit = player.raycast(RaycastHelper.REACH_DISTANCE, 0, false);

		if (world.isClient) {
			return TypedActionResult.fail(stack);
		} else {
			CounterComponent counter = CounterComponent.get(stack);

			if (RaycastHelper.hitsRift(hit, world)) {
				RiftBlockEntity rift = (RiftBlockEntity) world.getBlockEntity(((BlockHitResult) hit).getBlockPos());

				if (rift.getDestination() instanceof IdMarker && ((IdMarker) rift.getDestination()).getId() >= 0) {
					EntityUtils.chat(player, Text.of("Id: " + ((IdMarker) rift.getDestination()).getId()));
				} else {
					int id = counter.increment();

					ServerPacketHandler.get((ServerPlayerEntity) player).sync(stack, hand);

					EntityUtils.chat(player, Text.of("Rift stripped of data and set to target id: " + id));

					rift.setDestination(new IdMarker(id));
				}

				return TypedActionResult.success(stack);
			} else {
				EntityUtils.chat(player, Text.of("Current Count: " + counter.count()));
			}
		}

		return TypedActionResult.success(stack);
	}

	@Override
	public TypedActionResult<Boolean> onAttackBlock(World world, PlayerEntity player, Hand hand, BlockPos pos, Direction direction) {
		if (world.isClient) {
			if (player.isSneaking()) {
				if (CounterComponent.get(player.getStackInHand(hand)).count() != 0 || world.getBlockEntity(pos) instanceof RiftBlockEntity) {
					return TypedActionResult.success(true);
				}
				return TypedActionResult.fail(false);
			}
		} else {
			ItemStack stack = player.getStackInHand(hand);
			if (player.isSneaking()) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof RiftBlockEntity) {
					RiftBlockEntity rift = (RiftBlockEntity) blockEntity;
					if (!(rift.getDestination() instanceof IdMarker) || ((IdMarker) rift.getDestination()).getId() != -1) {
						rift.setDestination(new IdMarker(-1));
						EntityUtils.chat(player, Text.of("Rift stripped of data and set to invalid id: -1"));
						return TypedActionResult.success(false);
					}
				} else if (CounterComponent.get(stack).count() != 0) {
					CounterComponent.get(stack).reset();

					ServerPacketHandler.get((ServerPlayerEntity) player).sync(stack, hand);

					EntityUtils.chat(player, Text.of("Counter has been reset."));
					return TypedActionResult.success(false);
				}
			}
		}
		return TypedActionResult.pass(false);
	}

	@Override
	@Environment(CLIENT)
	public void appendTooltip(ItemStack itemStack, World world, List<Text> list, TooltipContext tooltipContext) {
		if (I18n.hasTranslation(this.getTranslationKey() + ".info")) {
			list.add(new TranslatableText(this.getTranslationKey() + ".info"));
		}
	}
}
