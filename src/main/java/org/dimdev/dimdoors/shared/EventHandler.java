package org.dimdev.dimdoors.shared;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
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
    public static void onStartUsingItem(LivingEntityUseItemEvent.Start event) {
        if(!(event.getEntityLiving() instanceof EntityPlayer) || !(event.getEntityLiving().getEntityWorld().provider instanceof WorldProviderPocket) || !PocketRegistry.instance(event.getEntityLiving().dimension).isWithinPocketBounds(event.getEntityLiving().getPosition()) || ((EntityPlayer)event.getEntityLiving()).isCreative()) {
            return;
        }
        String itemName = ForgeRegistries.ITEMS.getKey(event.getItem().getItem()).toString();
        String itemMeta = Integer.toString(event.getItem().getMetadata());
        PocketRule rule = PocketRegistry.instance(event.getEntityLiving().dimension).getPocketAt(event.getEntityLiving().getPosition()).getRules().getUseItemRule();
        if (rule.matches(itemName, itemMeta)) {
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
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) { //TODO: check wether having different template config on the client/server causes desync //TODO: make it try to stack instead of moving stacks to empty slots
        if (!(event.getEntityLiving() instanceof EntityPlayer) || !(event.getEntityLiving().getEntityWorld().provider instanceof WorldProviderPocket) || !PocketRegistry.instance(event.getEntityLiving().dimension).isWithinPocketBounds(event.getEntityLiving().getPosition()) || ((EntityPlayer) event.getEntityLiving()).isCreative()) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        PocketRule rule = PocketRegistry.instance(event.getEntityLiving().dimension).getPocketAt(event.getEntityLiving().getPosition()).getRules().getBanItemRule();
        if (!player.getHeldItemOffhand().isEmpty() && rule.matches(ForgeRegistries.ITEMS.getKey(player.getHeldItemOffhand().getItem()).toString(), Integer.toString(player.getHeldItemOffhand().getMetadata()))) { //TODO: message player about item being blacklisted in this pocket //TODO: move logic to BannedItemHandler
            int target = firstEmptyInventorySlot(player);
            if (target != -1) {
                swapWithOffhand(player, target);
            }
            else {
                target = firstValidInventorySlot(player,rule);
                if (target != -1) {
                    swapWithOffhand(player, target);
                }
                else {
                    target = firstEmptyHotbarSlot(player);
                    if (target != -1) {
                        swapWithOffhand(player, target);
                    }
                    else {
                        player.dropItem(player.getHeldItemOffhand(), false);
                        player.inventory.offHandInventory.set(0, ItemStack.EMPTY);
                    }
                }
            }
        }

        if (!player.getHeldItemMainhand().isEmpty() && rule.matches(ForgeRegistries.ITEMS.getKey(player.getHeldItemMainhand().getItem()).toString(), Integer.toString(player.getHeldItemMainhand().getMetadata()))) { //TODO: message player about item being blacklisted in this pocket
            int source = player.inventory.currentItem;
            int target = nextValidHotbarSlot(player, rule);
            if (target != -1) {
                while (player.getHeldItemMainhand() != player.inventory.getStackInSlot(target)) {
                    player.inventory.changeCurrentItem(-1);
                }
            }
            else {
                target = firstEmptyInventorySlot(player);
                if (target != -1) {
                    swapSlots(player, source, target);
                }
                else {
                    target = firstEmptyHotbarSlot(player);
                    if (target != -1) {
                        swapSlots(player, source, target);
                    }
                    else {
                        player.dropItem(player.inventory.removeStackFromSlot(source), false);
                    }
                }
            }
        }
    }

    private static int firstEmptyHotbarSlot(EntityPlayer player) {
        for (int i = 0; i < 9; i++) {
            if (player.inventory.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    private static int nextValidHotbarSlot(EntityPlayer player, PocketRule rule) {
        for (int i = 0; i < 9; i++) {
            int j = (player.inventory.currentItem + i) % 9;
            if (player.inventory.getStackInSlot(j).isEmpty() || !rule.matches(ForgeRegistries.ITEMS.getKey(player.inventory.getStackInSlot(j).getItem()).toString(), Integer.toString(player.inventory.getStackInSlot(j).getMetadata()))) {
                return j;
            }
        }
        return -1;
    }

    private static int firstEmptyInventorySlot(EntityPlayer player) {
        for (int i = 9; i < player.inventory.mainInventory.size(); i++) {
            if (player.inventory.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    private static int firstValidInventorySlot(EntityPlayer player, PocketRule rule) {
        for (int i = 9; i < player.inventory.mainInventory.size(); i++) {
            if (player.inventory.getStackInSlot(i).isEmpty() || !rule.matches(ForgeRegistries.ITEMS.getKey(player.inventory.getStackInSlot(i).getItem()).toString(), Integer.toString(player.inventory.getStackInSlot(i).getMetadata()))) {
                return i;
            }
        }
        return -1;
    }

    private static void swapSlots(EntityPlayer player, int slot1, int slot2) {
        ItemStack temp = player.inventory.getStackInSlot(slot2);
        player.inventory.setInventorySlotContents(slot2, player.inventory.getStackInSlot(slot1));
        player.inventory.setInventorySlotContents(slot1, temp);
    }

    private static void swapWithOffhand(EntityPlayer player, int slot) {
        ItemStack temp = player.inventory.getStackInSlot(slot);
        player.inventory.setInventorySlotContents(slot, player.getHeldItemOffhand());
        player.inventory.offHandInventory.set(0, temp);
    }
}
