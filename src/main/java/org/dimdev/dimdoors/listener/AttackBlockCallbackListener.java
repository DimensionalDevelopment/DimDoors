package org.dimdev.dimdoors.listener;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.item.ModItem;
import org.dimdev.dimdoors.network.c2s.HitBlockWithItemC2SPacket;

import java.io.IOException;

public class AttackBlockCallbackListener implements AttackBlockCallback {
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
		if (!world.isClient) return ActionResult.PASS;
		Item item = player.getStackInHand(hand).getItem();
		if (!(item instanceof ModItem)) {
			return ActionResult.PASS;
		}
		TypedActionResult<Boolean> result = ((ModItem) item).onAttackBlock(world, player, hand, pos, direction);
		if (result.getValue()) {
			try {
				ClientPlayNetworking.send(HitBlockWithItemC2SPacket.ID,
						new HitBlockWithItemC2SPacket(hand, pos, direction).write(PacketByteBufs.create()));
			} catch (IOException e) {
				LOGGER.error(e);
				return ActionResult.FAIL;
			}
		}

		return result.getResult();
	}
}
