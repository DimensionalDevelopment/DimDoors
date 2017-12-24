package com.zixiken.dimdoors.server;

import com.zixiken.dimdoors.shared.DDProxyCommon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.DimensionManager;

/**
 * @author Robijnvogel
 */
public class DDProxyServer extends DDProxyCommon {

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public EntityPlayer getLocalPlayer() {
        return null;
    }

    @Override
    public WorldServer getWorldServer(int dim) {
        return DimensionManager.getWorld(0).getMinecraftServer().getWorld(dim);
    }

    @Override
    public void setCloudRenderer(WorldProvider provider, IRenderHandler renderer) {}

    @Override
    public void setSkyRenderer(WorldProvider provider, IRenderHandler renderer) {}
}
