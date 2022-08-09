package org.dimdev.dimdoors.shared;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.dimdev.ddutils.TeleportUtils;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.sound.ModSounds;
import org.dimdev.dimdoors.shared.world.ModDimensions;

public final class EventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        Entity entity = event.getEntity();
        if (entity.dimension == ModDimensions.getLimboDim() && (event.getSource() == DamageSource.FALL)) {
            event.setCanceled(true);// no fall damage in limbo
        } else if(entity instanceof EntityPlayer && ModDimensions.isDimDoorsDimension(entity.dimension) && event.getSource() == DamageSource.OUT_OF_WORLD) {
            event.setCanceled(true);//no void damage for players in dim doors dimensions
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if(entity instanceof EntityPlayer && (ModDimensions.isDimDoorsDimension(entity.dimension) || ModConfig.limbo.universalLimbo)) {
            EntityPlayer player = (EntityPlayer)entity;
            player.extinguish();
            if(!player.getActivePotionEffects().isEmpty()) player.clearActivePotions();
            player.setHealth(entity.getMaxHealth());
            player.getFoodStats().setFoodLevel(20);
            player.getFoodStats().setFoodSaturationLevel(6f);
            teleportToLimbo(player);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        // TODO: Make this work with other mods (such as Dimensional Industry)
        if (!ModDimensions.isDimDoorsPocketDimension(event.fromDim) && ModDimensions.isDimDoorsPocketDimension(event.toDim)) {
            RiftRegistry.instance().setOverworldRift(event.player.getUniqueID(), null);
        }
    }

    /**
     * Players can no longer fall out of Limbo. Should fix some death related issues and increases the use cases for eternal fabric
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (ModDimensions.isDimDoorsDimension(player.dimension) && player.posY<-50) {
            teleportToLimbo(player);
        }
    }

    private static void teleportToLimbo(EntityPlayer player) {
        double x = player.posX + MathHelper.clamp(player.world.rand.nextDouble(), 100, 100);
        double z = player.posZ + MathHelper.clamp(player.world.rand.nextDouble(), -100, 100);
        TeleportUtils.teleport(player, ModDimensions.getLimboDim(),x,700,z,player.rotationYaw,player.rotationPitch);
        player.world.playSound(null, player.getPosition(), ModSounds.CRACK, SoundCategory.HOSTILE, 13, 1);
    }
}
