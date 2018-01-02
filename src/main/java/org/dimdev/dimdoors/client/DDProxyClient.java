package org.dimdev.dimdoors.client;

import org.dimdev.dimdoors.shared.DDProxyCommon;
import org.dimdev.dimdoors.shared.entities.EntityMonolith;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.IRenderHandler;
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
        // ModelManager.addCustomStateMappers(); // TODO: fix this
        ModelManager.registerModelVariants();
        registerRenderers();
    }

    @Override
    public void onInitialization(FMLInitializationEvent event) {
        super.onInitialization(event);
        ModelManager.registerModels();
    }

    public void registerRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEntranceRift.class, new TileEntityEntranceRiftRenderer());
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
    public WorldServer getWorldServer(int dim) {
        return Minecraft.getMinecraft().getIntegratedServer().getWorld(dim);
    }

    @Override
    public void setCloudRenderer(WorldProvider provider, IRenderHandler renderer) {
        provider.setCloudRenderer(renderer);
    }

    @Override
    public void setSkyRenderer(WorldProvider provider, IRenderHandler renderer) {
        provider.setSkyRenderer(renderer);
    }
}
