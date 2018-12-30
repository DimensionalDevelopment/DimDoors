package org.dimdev.dimdoors.proxy;


import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.dimdoors.client.ModelManager;
import org.dimdev.dimdoors.client.RenderMonolith;
import org.dimdev.dimdoors.client.TileEntityEntranceRiftRenderer;
import org.dimdev.dimdoors.client.TileEntityFloatingRiftRenderer;
import org.dimdev.dimdoors.shared.entities.EntityMonolith;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;

@SideOnly(Side.CLIENT)
public class ClientProxy implements IProxy {

    @Override
    public void onPreInitialization(FMLPreInitializationEvent event) {
        // Register client-side event handlers
        MinecraftForge.EVENT_BUS.register(ModelManager.class);

        // Register tile entity renderers
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEntranceRift.class, new TileEntityEntranceRiftRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFloatingRift.class, new TileEntityFloatingRiftRenderer());

        // Register monolith renderers
        RenderingRegistry.registerEntityRenderingHandler(EntityMonolith.class, manager -> new RenderMonolith(manager, 0.5f));

        // Activate stencil buffer if it isn't already enabled.
        Framebuffer framebuffer = Minecraft.getMinecraft().getFramebuffer();
        if(!framebuffer.isStencilEnabled()){
            framebuffer.enableStencil();
        }
    }

    @Override
    public void onInitialization(FMLInitializationEvent event) {
        ModelManager.registerItemModels();
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
    public void setCloudRenderer(WorldProvider provider, IRenderHandler renderer) {
        provider.setCloudRenderer(renderer);
    }

    @Override
    public void setSkyRenderer(WorldProvider provider, IRenderHandler renderer) {
        provider.setSkyRenderer(renderer);
    }
}
