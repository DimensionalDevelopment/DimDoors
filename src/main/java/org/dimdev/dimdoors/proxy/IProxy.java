package org.dimdev.dimdoors.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IProxy {

    void onPreInitialization(FMLPreInitializationEvent event);

    void onInitialization(FMLInitializationEvent event);

    boolean isClient();

    EntityPlayer getLocalPlayer();

    void setCloudRenderer(WorldProvider provider, IRenderHandler renderer);

    void setSkyRenderer(WorldProvider provider, IRenderHandler renderer);
}
