package org.dimdev.dimdoors.shared;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.world.ModDimensions;

import static net.minecraft.util.DamageSource.OUT_OF_WORLD;

public final class EventHandler {

    static World world = Minecraft.getMinecraft().world;


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            boolean inVoid = event.player.posY <= ModConfig.general.KillFallHeightForPocketPublicAndDungeon;
            boolean inDimension = event.player.dimension == 686 || event.player.dimension == 685 || event.player.dimension == 687;
            while (inDimension) {
                while (inVoid) {
                    for (int i = 0; i < 1; i++) {
                        event.player.attackEntityFrom(OUT_OF_WORLD, 1);
                    }
                    break;
                }
                break;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        Entity entity = event.getEntity();
        //Used only in pocket, personal, and dungeon dimensions
        if (entity.dimension == ModDimensions.PRIVATE.getId() || entity.dimension == ModDimensions.DUNGEON.getId() || entity.dimension == ModDimensions.PUBLIC.getId()) {

            if (event.getSource() == OUT_OF_WORLD) {
                entity.setPortal(entity.getPosition().add(10, 700, 10));
                entity.changeDimension(ModDimensions.LIMBO.getId());
                ((EntityPlayerMP) entity).connection.setPlayerLocation(0, 700, 0, entity.rotationYaw, entity.rotationPitch);

            }
        }
        if (entity.dimension == ModDimensions.LIMBO.getId() && (event.getSource() == DamageSource.FALL || event.getSource() == OUT_OF_WORLD)) {
            event.setCanceled(true);// no fall damage in limbo
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDimensionChange (PlayerEvent.PlayerChangedDimensionEvent event) {
        // TODO: Make this work with other mods (such as Dimensional Industry)
        if (!ModDimensions.isDimDoorsPocketDimension(event.fromDim) && ModDimensions.isDimDoorsPocketDimension(event.toDim)) {
            RiftRegistry.instance().setOverworldRift(event.player.getUniqueID(), null);
        }
    }
}