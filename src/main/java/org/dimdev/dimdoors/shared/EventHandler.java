package org.dimdev.dimdoors.shared;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.dimdev.dimdoors.shared.pockets.PocketRule;
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
        IBlockState blockState = event.getEntityPlayer().getEntityWorld().getBlockState(event.getPos());
        String blockName = ForgeRegistries.BLOCKS.getKey(blockState.getBlock()).toString();
        String blockMeta = Integer.toString(blockState.getBlock().getMetaFromState(blockState));
        PocketRule rule = PocketRegistry.instance(event.getEntityPlayer().dimension).getPocketAt(event.getPos()).getRules().getBreakBlockRule();
        if(rule.matches(blockName, blockMeta)) {
            event.setCanceled(true);
        }
        return;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickItem(RightClickItem event) {
        if(!(event.getEntityPlayer().getEntityWorld().provider instanceof WorldProviderPocket) || !PocketRegistry.instance(event.getEntityPlayer().dimension).isWithinPocketBounds(event.getPos()) || event.getEntityPlayer().isCreative()) {
            return;
        }
        String itemName = ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem()).toString();
        String itemMeta = Integer.toString(event.getItemStack().getMetadata());
        PocketRule rule = PocketRegistry.instance(event.getEntityPlayer().dimension).getPocketAt(event.getPos()).getRules().getUseItemRule();
        if(rule.matches(itemName, itemMeta)) {
            event.setCanceled(true);
        }
        return;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onUseBow(ArrowNockEvent event) {
        String itemName = ForgeRegistries.ITEMS.getKey(event.getBow().getItem()).toString();
        String itemMeta = Integer.toString(event.getBow().getMetadata());
        PocketRule rule = PocketRegistry.instance(event.getEntityPlayer().dimension).getPocketAt(event.getEntityPlayer().getPosition()).getRules().getUseItemRule();
        if(rule.matches(itemName, itemMeta)) {
            event.setCanceled(true);
        }
        return;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(RightClickBlock event) {
        if(!(event.getEntityPlayer().getEntityWorld().provider instanceof WorldProviderPocket) || !PocketRegistry.instance(event.getEntityPlayer().dimension).isWithinPocketBounds(event.getPos()) || event.getEntityPlayer().isCreative()) {
            return;
        }
        String itemName = ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem()).toString();
        String itemMeta = Integer.toString(event.getItemStack().getMetadata());
        PocketRule itemRule = PocketRegistry.instance(event.getEntityPlayer().dimension).getPocketAt(event.getPos()).getRules().getUseItemRule();
        if(itemRule.matches(itemName, itemMeta)) {
            event.setUseItem(Event.Result.DENY); //Only prevent item interaction, block interaction might still be interesting
        }
        IBlockState blockState = event.getEntityPlayer().getEntityWorld().getBlockState(event.getPos());
        String blockName = ForgeRegistries.BLOCKS.getKey(blockState.getBlock()).toString();
        String blockMeta = Integer.toString(blockState.getBlock().getMetaFromState(blockState));
        PocketRule blockRule = PocketRegistry.instance(event.getEntityPlayer().dimension).getPocketAt(event.getPos()).getRules().getInteractBlockRule();
        if(blockRule.matches(blockName, blockMeta)) {
            event.setUseBlock(Event.Result.DENY); //Only prevent block interaction, item interaction might still be interesting
        }
        return;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onUseBucket(FillBucketEvent event) {
        if(!(event.getEntityPlayer().getEntityWorld().provider instanceof WorldProviderPocket) || !PocketRegistry.instance(event.getEntityPlayer().dimension).isWithinPocketBounds(event.getTarget().getBlockPos()) || event.getEntityPlayer().isCreative()) {
            return;
        }
        String itemName = ForgeRegistries.ITEMS.getKey(event.getEmptyBucket().getItem()).toString();
        PocketRule itemRule = PocketRegistry.instance(event.getEntityPlayer().dimension).getPocketAt(event.getEntityPlayer().getPosition()).getRules().getUseItemRule();

        IBlockState blockState = event.getEntityPlayer().getEntityWorld().getBlockState(event.getTarget().getBlockPos());
        String blockName = ForgeRegistries.BLOCKS.getKey(blockState.getBlock()).toString();
        String blockMeta = Integer.toString(blockState.getBlock().getMetaFromState(blockState));
        PocketRule blockRule = PocketRegistry.instance(event.getEntityPlayer().dimension).getPocketAt(event.getTarget().getBlockPos()).getRules().getInteractBlockRule();

        if(itemRule.matches(itemName, "0")) {
            event.setResult(Event.Result.DENY);
        }
        else if(blockRule.matches(blockName, blockMeta)) {
            event.setResult(Event.Result.DENY);
        }
        return;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onUseBonemeal(BonemealEvent event) { //is this even worth it?
        if(!(event.getEntityPlayer().getEntityWorld().provider instanceof WorldProviderPocket) || !PocketRegistry.instance(event.getEntityPlayer().dimension).isWithinPocketBounds(event.getPos()) || event.getEntityPlayer().isCreative()) {
            return;
        }
        PocketRule itemRule = PocketRegistry.instance(event.getEntityPlayer().dimension).getPocketAt(event.getEntityPlayer().getPosition()).getRules().getUseItemRule();

        IBlockState blockState = event.getEntityPlayer().getEntityWorld().getBlockState(event.getPos());
        String blockName = ForgeRegistries.BLOCKS.getKey(blockState.getBlock()).toString();
        String blockMeta = Integer.toString(blockState.getBlock().getMetaFromState(blockState));
        PocketRule blockRule = PocketRegistry.instance(event.getEntityPlayer().dimension).getPocketAt(event.getPos()).getRules().getInteractBlockRule();

        if(itemRule.matches("minecraft:dye", "0")) { //TODO: make sure item name is correct in 1.13
            event.setResult(Event.Result.DENY);
        }
        else if(blockRule.matches(blockName, blockMeta)) {
            event.setResult(Event.Result.DENY);
        }
        return;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onUseHoe(UseHoeEvent event) { //is this even worth it?
        if(!(event.getEntityPlayer().getEntityWorld().provider instanceof WorldProviderPocket) || !PocketRegistry.instance(event.getEntityPlayer().dimension).isWithinPocketBounds(event.getPos()) || event.getEntityPlayer().isCreative()) {
            return;
        }
        IBlockState blockState = event.getEntityPlayer().getEntityWorld().getBlockState(event.getPos());
        String blockName = ForgeRegistries.BLOCKS.getKey(blockState.getBlock()).toString();
        String blockMeta = Integer.toString(blockState.getBlock().getMetaFromState(blockState));
        PocketRule blockRule = PocketRegistry.instance(event.getEntityPlayer().dimension).getPocketAt(event.getPos()).getRules().getInteractBlockRule();
        if(blockRule.matches(blockName, blockMeta)) {
            event.setResult(Event.Result.DENY);
        }
        return;
    }
}
