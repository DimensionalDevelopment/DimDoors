package com.zixiken.dimdoors;

import com.zixiken.dimdoors.items.ItemDoorBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHookContainer {
	@SubscribeEvent
	public void onPlayerEvent(PlayerInteractEvent event) {
		// Handle all door placement here
		if (event.action == Action.LEFT_CLICK_BLOCK) return;

		World world = event.entity.worldObj;
		ItemStack stack = event.entityPlayer.inventory.getCurrentItem();
		if (stack != null && ItemDoorBase.tryToPlaceDoor(stack, event.entityPlayer, world, event.pos, event.face))
				// Cancel the event so that we don't get two doors from vanilla doors
				event.setCanceled(true);
	}
}