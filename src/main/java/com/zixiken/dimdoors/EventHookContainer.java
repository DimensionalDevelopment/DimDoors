package com.zixiken.dimdoors;

import com.zixiken.dimdoors.items.ItemDoorBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHookContainer {

    @SubscribeEvent
    public void onPlayerEvent(PlayerInteractEvent event) {
        // Handle all door placement here

        World world = event.getEntity().world;
        ItemStack stack = event.getEntityPlayer().inventory.getCurrentItem();
        if (stack != null && ItemDoorBase.tryToPlaceDoor(stack, event.getEntityPlayer(), world, event.getPos(), event.getFace())) // Cancel the event so that we don't get two doors from vanilla doors
        {
            event.setCanceled(true);
        }
    }
}
