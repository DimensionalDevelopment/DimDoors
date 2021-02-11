package org.dimdev.dimdoors.item;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.network.c2s.HitBlockS2CPacket;
import org.dimdev.dimdoors.network.s2c.PlayerInventorySlotUpdateS2CPacket;
import org.dimdev.dimdoors.rift.targets.IdMarker;
import org.dimdev.dimdoors.util.EntityUtils;
import org.dimdev.dimdoors.world.level.Counter;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import static net.fabricmc.api.EnvType.CLIENT;

public class RiftConfigurationToolItem extends Item {
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
			Counter counter = Counter.get(stack);

			if (RaycastHelper.hitsRift(hit, world)) {
				RiftBlockEntity rift = (RiftBlockEntity) world.getBlockEntity(((BlockHitResult) hit).getBlockPos());

				if (rift.getDestination() instanceof IdMarker && ((IdMarker) rift.getDestination()).getId() >= 0) {
					EntityUtils.chat(player, Text.of("Id: " + ((IdMarker) rift.getDestination()).getId()));
				} else {
					int id = counter.increment();
					this.sync(stack, player, hand);
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

	public static ActionResult onAttackBlockCallback(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
		if (world.isClient && player.isSneaking() && player.getStackInHand(hand).getItem() instanceof RiftConfigurationToolItem) {
			if (Counter.get(player.getStackInHand(hand)).count() != -1 || world.getBlockEntity(pos) instanceof RiftBlockEntity) {
				HitBlockS2CPacket packet = new HitBlockS2CPacket(hand, pos, direction);
				try {
					PacketByteBuf buf = PacketByteBufs.create();
					packet.write(buf);
					ClientPlayNetworking.send(HitBlockS2CPacket.ID, buf);
				} catch (IOException e) {
					LOGGER.error(e);
					return ActionResult.FAIL;
				}
			}
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

	public static void receiveHitBlock(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
		HitBlockS2CPacket packet = new HitBlockS2CPacket();
		try {
			packet.read(buf);
			server.execute(() -> serverThreadReceiveHitBlock(player, packet.getHand(), packet.getPos()));
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	private static void serverThreadReceiveHitBlock(ServerPlayerEntity player, Hand hand, BlockPos pos) {
		ItemStack stack = player.getStackInHand(hand);
		if (player.isSneaking() && stack.getItem() instanceof RiftConfigurationToolItem) {
			BlockEntity blockEntity = player.getServerWorld().getBlockEntity(pos);
			if (blockEntity instanceof RiftBlockEntity) {
				RiftBlockEntity rift = (RiftBlockEntity) blockEntity;
				if (!(rift.getDestination() instanceof IdMarker) || ((IdMarker) rift.getDestination()).getId() != -1) {
					rift.setDestination(new IdMarker(-1));
					EntityUtils.chat(player, Text.of("Rift stripped of data and set to invalid id: -1"));
				}
			} else if (Counter.get(stack).count() != -1) {
				Counter.get(stack).set(-1);
				((RiftConfigurationToolItem) stack.getItem()).sync(stack, player, hand);

				EntityUtils.chat(player, Text.of("Counter has been reset."));
			}
		}
	}


	@Override
	@Environment(CLIENT)
	public void appendTooltip(ItemStack itemStack, World world, List<Text> list, TooltipContext tooltipContext) {
		if (I18n.hasTranslation(this.getTranslationKey() + ".info")) {
			list.add(new TranslatableText(this.getTranslationKey() + ".info"));
		}
	}

	@Override
	public ItemStack getDefaultStack() {
		ItemStack defaultStack = super.getDefaultStack();
		Counter.get(defaultStack).set(-1);
		return defaultStack;
	}

	private void sync(ItemStack stack, PlayerEntity player, Hand hand) {
		ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
		PlayerInventorySlotUpdateS2CPacket packet;
		if (hand == Hand.OFF_HAND) {
			packet = new PlayerInventorySlotUpdateS2CPacket(45, stack);
		} else {
			packet = new PlayerInventorySlotUpdateS2CPacket(serverPlayer.getInventory().selectedSlot, stack);
		}
		PacketByteBuf buf = PacketByteBufs.create();
		try {
			packet.write(buf);
			ServerPlayNetworking.send(serverPlayer, PlayerInventorySlotUpdateS2CPacket.ID, buf);
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}
}
