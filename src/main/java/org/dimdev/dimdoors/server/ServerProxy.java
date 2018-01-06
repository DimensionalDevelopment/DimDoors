package org.dimdev.dimdoors.server;

import org.dimdev.dimdoors.shared.CommonProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.IRenderHandler;

/**
 * @author Robijnvogel
 */
public class ServerProxy extends CommonProxy {

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public EntityPlayer getLocalPlayer() {
        return null;
    }

    @Override
    public void setCloudRenderer(WorldProvider provider, IRenderHandler renderer) {}

    @Override
    public void setSkyRenderer(WorldProvider provider, IRenderHandler renderer) {}
}
