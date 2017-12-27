package org.dimdev.dimdoors.shared;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 *
 * @author Robijnvogel
 */
public interface IDDProxy {

    public boolean isClient();

    public void onPreInitialization(FMLPreInitializationEvent event);

    public void onInitialization(FMLInitializationEvent event);

    public EntityPlayer getLocalPlayer();

    public WorldServer getWorldServer(int dimId);

    public void setCloudRenderer(WorldProvider provider, IRenderHandler renderer);

    public void setSkyRenderer(WorldProvider provider, IRenderHandler renderer);
}
