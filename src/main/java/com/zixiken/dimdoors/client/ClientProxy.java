package com.zixiken.dimdoors.client;

import com.zixiken.dimdoors.CommonProxy;
import com.zixiken.dimdoors.tileentities.TileEntityDimDoor;
import com.zixiken.dimdoors.tileentities.TileEntityTransTrapdoor;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


public class ClientProxy extends CommonProxy {

    @Override
    public void onPreInitialization(FMLPreInitializationEvent event) {
        super.onPreInitialization(event);
        registerRenderers();
    }

    public void registerRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDimDoor.class, new RenderDimDoor());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTransTrapdoor.class, new RenderTransTrapdoor());
	}
}