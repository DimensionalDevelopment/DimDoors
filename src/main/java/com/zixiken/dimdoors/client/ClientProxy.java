package com.zixiken.dimdoors.client;

import com.zixiken.dimdoors.CommonProxy;
import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.ticking.MobMonolith;
import com.zixiken.dimdoors.tileentities.TileEntityDimDoor;
import com.zixiken.dimdoors.tileentities.TileEntityTransTrapdoor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class ClientProxy extends CommonProxy {

    @Override
	public void registerRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDimDoor.class, new RenderDimDoor());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTransTrapdoor.class, new RenderTransTrapdoor());

        RenderingRegistry.registerEntityRenderingHandler(MobMonolith.class, new IRenderFactory<MobMonolith>() {
            @Override
            public Render<? super MobMonolith> createRenderFor(RenderManager manager) {
                return new RenderMobObelisk(manager);
            }
        });
	}

    @Override
    public void registerSidedHooks() {
        ClientOnlyHooks hooks = new ClientOnlyHooks(DDProperties.instance());
        MinecraftForge.EVENT_BUS.register(hooks);
        MinecraftForge.TERRAIN_GEN_BUS.register(hooks);
        PocketManager.getDimwatcher().registerReceiver(new PocketManager.ClientDimWatcher());
        PocketManager.getLinkWatcher().registerReceiver(new PocketManager.ClientLinkWatcher());
    }

	@Override
	public EntityPlayer getMessagePlayer(MessageContext ctx) {
		return Minecraft.getMinecraft().thePlayer;
	}
}