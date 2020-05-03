package org.dimdev.dimdoors.shared;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.world.ModDimensions;

import static net.minecraft.util.DamageSource.OUT_OF_WORLD;

public final class EventHandler {

    World world = Minecraft.getMinecraft().world;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                boolean inVoid = event.player.posY <= ModConfig.general.KillFallHeightForPocketPublicAndDungeon;
                boolean inDimension = event.player.dimension == 686 || event.player.dimension == 685 || event.player.dimension == 687;
                while (inDimension) {
                    while (inVoid) {
                        for(int i = 0; i < 2; i++){
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

        if (entity.dimension == ModDimensions.LIMBO.getId() && event.getSource() == DamageSource.FALL) {
            event.setCanceled(true);// no fall damage in limbo
        }
        //Used only in pocket, personal, and dungeon dimensions
        if (entity.dimension == ModDimensions.PRIVATE.getId() || entity.dimension == ModDimensions.DUNGEON.getId() || entity.dimension == ModDimensions.PUBLIC.getId()) {

            if (event.getSource() == OUT_OF_WORLD) {
                    entity.setPortal(entity.getPosition().add(10, -100, 10));
                    entity.changeDimension(ModDimensions.LIMBO.getId());
            }
        }
    }
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onDimensionChange (PlayerEvent.PlayerChangedDimensionEvent event){
            // TODO: Make this work with other mods (such as Dimensional Industry)
            if (!ModDimensions.isDimDoorsPocketDimension(event.fromDim) && ModDimensions.isDimDoorsPocketDimension(event.toDim)) {
                RiftRegistry.instance().setOverworldRift(event.player.getUniqueID(), null);
            }
        }
    }

