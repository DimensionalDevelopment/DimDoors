package com.zixiken.dimdoors.client;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.config.DDProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientOnlyHooks {
    private DDProperties properties;

    private ISound limboMusic;

    public ClientOnlyHooks(DDProperties properties) {
        this.properties = properties;
        this.limboMusic = PositionedSoundRecord.create(new ResourceLocation(DimDoors.MODID + ":creepy"));
    }


    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onSoundEffectResult(PlaySoundEvent event)
    {
        ResourceLocation playingSound = event.sound.getSoundLocation();
        if (playingSound != null && playingSound.getResourceDomain().equals("minecraft") && (playingSound.getResourcePath().equals("music.game") || playingSound.getResourcePath().equals("music.game.creative"))) {
            if (FMLClientHandler.instance().getClient().thePlayer.worldObj.provider.getDimensionId() == DimDoors.properties.LimboDimensionID) {
                ResourceLocation sound = new ResourceLocation(DimDoors.MODID + ":creepy");

                if (!Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(limboMusic)) {
                    event.result = limboMusic;
                } else {
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.world.provider.getDimensionId() == DimDoors.properties.LimboDimensionID &&
                event.world.isRemote && !Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(limboMusic)) {
            Minecraft.getMinecraft().getSoundHandler().playSound(limboMusic);
        }
    }
}
