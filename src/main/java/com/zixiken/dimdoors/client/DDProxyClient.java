package com.zixiken.dimdoors.client;

import com.zixiken.dimdoors.client.sound.DDSounds;
import com.zixiken.dimdoors.shared.DDProxyCommon;
import com.zixiken.dimdoors.shared.ModelManager;
import com.zixiken.dimdoors.shared.entities.MobMonolith;
import com.zixiken.dimdoors.shared.entities.RenderMobObelisk;
import com.zixiken.dimdoors.shared.tileentities.TileEntityVerticalEntranceRift;
import com.zixiken.dimdoors.shared.tileentities.TileEntityFloatingRift;
import com.zixiken.dimdoors.shared.tileentities.TileEntityHorizontalEntranceRift;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class DDProxyClient extends DDProxyCommon {

    @Override
    public void onPreInitialization(FMLPreInitializationEvent event) {
        super.onPreInitialization(event);
        MinecraftForge.EVENT_BUS.register(DDSounds.class);

        ModelManager.registerModelVariants();
        ModelManager.addCustomStateMappers();

        registerRenderers();
    }

    @Override
    public void onInitialization(FMLInitializationEvent event) {
        super.onInitialization(event);
        ModelManager.registerModels();
    }

    public void registerRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVerticalEntranceRift.class, new RenderVerticalEntranceRift());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHorizontalEntranceRift.class, new RenderHorizontalEntranceRift());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFloatingRift.class, new RenderRift());
        RenderingRegistry.registerEntityRenderingHandler(MobMonolith.class, manager -> new RenderMobObelisk(manager, 0.5f));
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
    public WorldServer getWorldServer(int dimId) {
        return Minecraft.getMinecraft().getIntegratedServer().getWorld(dimId);
    }
}
