package com.zixiken.dimdoors.client;

import com.zixiken.dimdoors.shared.DDProxyCommon;
import com.zixiken.dimdoors.shared.entities.EntityMonolith;
import com.zixiken.dimdoors.shared.entities.RenderMonolith;
import com.zixiken.dimdoors.shared.tileentities.TileEntityVerticalEntranceRift;
import com.zixiken.dimdoors.shared.tileentities.TileEntityFloatingRift;
import com.zixiken.dimdoors.shared.tileentities.TileEntityHorizontalEntranceRift;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DDProxyClient extends DDProxyCommon {

    @Override
    public void onPreInitialization(FMLPreInitializationEvent event) {
        super.onPreInitialization(event);
        //ModelManager.addCustomStateMappers();
        ModelManager.registerModelVariants();
        registerRenderers();
    }

    @Override
    public void onInitialization(FMLInitializationEvent event) {
        super.onInitialization(event);
        ModelManager.registerModels();
    }

    public void registerRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVerticalEntranceRift.class, new TileEntityVerticalEntranceRiftRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHorizontalEntranceRift.class, new TileEntityHorizontalEntranceRiftRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFloatingRift.class, new TileEntityFloatingRiftRenderer());
        RenderingRegistry.registerEntityRenderingHandler(EntityMonolith.class, manager -> new RenderMonolith(manager, 0.5f));
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
