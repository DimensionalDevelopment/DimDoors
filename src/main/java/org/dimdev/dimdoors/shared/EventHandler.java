package org.dimdev.dimdoors.shared;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.dimdev.pocketlib.PocketRegistry;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.world.ModDimensions;

public final class EventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST) // don't let other mods do something based on the event
    public static void onLivingHurt(LivingHurtEvent event) {
        Entity entity = event.getEntity();
        if (entity.dimension == ModDimensions.LIMBO.getId() && event.getSource() == DamageSource.FALL) {
            event.setCanceled(true);// no fall damage in limbo
        }
    }

    @SubscribeEvent
    public static void onEntityEnterChunk(EntityEvent.EnteringChunk event) {
        // TODO: Pass to PocketLib
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            World world = entity.world;
            int dim = world.provider.getDimension();
            if (!world.isRemote
                && !player.isDead
                && ModDimensions.isDimDoorsPocketDimension(world)
                && !PocketRegistry.instance(dim).isPlayerAllowedToBeAt(player, player.getPosition())) {
                // TODO: make the world circular
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) { // TODO: what about non-players (EntityTravelToDimensionEvent)?
        // TODO: PocketLib compatibility
        if (!ModDimensions.isDimDoorsPocketDimension(event.fromDim) && ModDimensions.isDimDoorsPocketDimension(event.toDim)) {
            RiftRegistry.instance().setOverworldRift(event.player.getUniqueID(), null);
        }
    }
}
