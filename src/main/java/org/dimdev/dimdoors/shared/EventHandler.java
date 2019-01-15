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
import org.dimdev.pocketlib.PocketRegistry;
import org.dimdev.pocketlib.WorldProviderPocket;

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
        if(!(event.getEntityPlayer().getEntityWorld().provider instanceof WorldProviderPocket) || !PocketRegistry.instance(event.getEntityPlayer().dimension).isWithinPocketBounds(event.getPos()) || event.getEntityPlayer().isCreative()) {
            return;
        }
        if(PocketRegistry.instance(event.getEntityPlayer().dimension).getPocketAt(event.getPos()).getRules().getBreakBlockRule().matches(ForgeRegistries.BLOCKS.getKey(event.getEntityPlayer().getEntityWorld().getBlockState(event.getPos()).getBlock()).toString())) {
            event.setCanceled(true);
        }
        return;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickItem(RightClickItem event) {
        if(!(event.getEntityPlayer().getEntityWorld().provider instanceof WorldProviderPocket) || !PocketRegistry.instance(event.getEntityPlayer().dimension).isWithinPocketBounds(event.getPos()) || event.getEntityPlayer().isCreative()) {
            return;
        }
        if(PocketRegistry.instance(event.getEntityPlayer().dimension).getPocketAt(event.getPos()).getRules().getUseItemOnAirRule().matches(ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem()).toString())) {
            event.setCanceled(true);
        }
        return;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(RightClickBlock event) {
        if(!(event.getEntityPlayer().getEntityWorld().provider instanceof WorldProviderPocket) || !PocketRegistry.instance(event.getEntityPlayer().dimension).isWithinPocketBounds(event.getPos()) || event.getEntityPlayer().isCreative()) {
            return;
        }
        if(PocketRegistry.instance(event.getEntityPlayer().dimension).getPocketAt(event.getPos()).getRules().getUseItemOnBlockRule().matches(ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem()).toString())) {
            event.setUseItem(Event.Result.DENY); //Only prevent item interaction, block interaction might still be interesting
        }
        if(PocketRegistry.instance(event.getEntityPlayer().dimension).getPocketAt(event.getPos()).getRules().getInteractBlockRule().matches(ForgeRegistries.BLOCKS.getKey(event.getWorld().getBlockState(event.getPos()).getBlock()).toString())) {
            event.setUseBlock(Event.Result.DENY); //Only prevent block interaction, item interaction might still be interesting
        }
        return;
    }
}
