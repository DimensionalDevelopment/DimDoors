package org.dimdev.dimdoors.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IProxy {

    public void onPreInitialization(FMLPreInitializationEvent event);

    public void onInitialization(FMLInitializationEvent event);

    public boolean isClient();

    public EntityPlayer getLocalPlayer();

    public void setCloudRenderer(WorldProvider provider, IRenderHandler renderer);

    public void setSkyRenderer(WorldProvider provider, IRenderHandler renderer);
}
