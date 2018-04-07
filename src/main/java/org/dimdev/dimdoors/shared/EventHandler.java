package org.dimdev.dimdoors.shared;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
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
}
