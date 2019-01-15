package org.dimdev.dimdoors.shared;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.world.ModDimensions;

public final class EventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        Entity entity = event.getEntity();
        if (entity.dimension == ModDimensions.LIMBO.getId() && event.getSource() == DamageSource.FALL) {
            event.setCanceled(true);// no fall damage in limbo
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        // TODO: Make this work with other mods (such as Dimensional Industry)
        if (!ModDimensions.isDimDoorsPocketDimension(event.fromDim) && ModDimensions.isDimDoorsPocketDimension(event.toDim)) {
            RiftRegistry.instance().setOverworldRift(event.player.getUniqueID(), null);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onGetBreakSpeed(BreakSpeed event) {
        if(event.getEntityPlayer().isCreative()) {
            return;
        }
        if(!isPermitted(ForgeRegistries.BLOCKS.getKey(event.getEntityPlayer().getEntityWorld().getBlockState(event.getPos()).getBlock()).toString(), new String[0]/*event.getEntityPlayer().getCurrentPocket().getRules().getBreakBlockArray()*/, true/*event.getEntityPlayer().getCurrentPocket().getRules().getBreakBlockWhitelist()*/)) {
            event.setCanceled(true);
        }
        return;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickItem(RightClickItem event) {
        if(event.getEntityPlayer().isCreative()) {
            return;
        }
        if(!isPermitted(ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem()).toString(), new String[0]/*event.getEntityPlayer().getCurrentPocket().getRules().getUseItemArray()*/, true/*event.getEntityPlayer().getCurrentPocket().getRules().getUseItemWhitelist()*/)) {
            event.setCanceled(true);
        }
        return;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(RightClickBlock event) {
        if(event.getEntityPlayer().isCreative()) {
            return;
        }
        if(!isPermitted(ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem()).toString(), new String[0]/*event.getEntityPlayer().getCurrentPocket().getRules().getUseItemArray()*/, true/*event.getEntityPlayer().getCurrentPocket().getRules().getUseItemWhitelist()*/)) {
            event.setUseItem(Event.Result.DENY); //Only prevent item interaction, block interaction might still be interesting
        }
        if(!isPermitted(ForgeRegistries.BLOCKS.getKey(event.getWorld().getBlockState(event.getPos()).getBlock()).toString(), new String[0]/*event.getEntityPlayer().getCurrentPocket().getRules().getInteractBlockArray()*/, true/*event.getEntityPlayer().getCurrentPocket().getRules().getInteractBlockWhitelist()*/)) {
            event.setUseBlock(Event.Result.DENY); //Only prevent block interaction, item interaction might still be interesting
        }
        return;
    }


    private static boolean isPermitted(String itemOrBlockName, String[] rules, boolean whitelist) {
        for (String rule : rules) {
            if (itemOrBlockName.matches(rule)) {
                return whitelist;
            }
        }
        return !whitelist;
    }
}
