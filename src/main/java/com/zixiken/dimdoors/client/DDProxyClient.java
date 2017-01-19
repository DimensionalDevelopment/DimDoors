package com.zixiken.dimdoors.client;

import com.zixiken.dimdoors.DDProxyCommon;
import com.zixiken.dimdoors.tileentities.TileEntityDimDoor;
import com.zixiken.dimdoors.tileentities.TileEntityRift;
import com.zixiken.dimdoors.tileentities.TileEntityTransTrapdoor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@SuppressWarnings({"MethodCallSideOnly", "NewExpressionSideOnly"})
public class DDProxyClient extends DDProxyCommon {

    @Override
    public void onPreInitialization(FMLPreInitializationEvent event) {
        super.onPreInitialization(event);
        registerRenderers();
    }

    public void registerRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDimDoor.class, new RenderDimDoor());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTransTrapdoor.class, new RenderTransTrapdoor());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRift.class, new RenderRift());
    }

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public EntityPlayer getLocalPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public World getDefWorld() {
        return getWorldServer(0); //gets the client world dim 0 handler
    }

    @Override
    public WorldServer getWorldServer(int dimId) {
        return Minecraft.getMinecraft().getIntegratedServer().worldServerForDimension(dimId);
    }
}
