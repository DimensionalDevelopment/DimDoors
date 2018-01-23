package org.dimdev.dimdoors.client;

import org.dimdev.dimdoors.shared.CommonProxy;
import org.dimdev.dimdoors.shared.entities.EntityMonolith;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void onPreInitialization(FMLPreInitializationEvent event) {
        super.onPreInitialization(event);
        registerRenderers();
    }

    @Override
    public void afterItemsRegistered() {
        // Model variants can't be registered from onInitialization because that's too late (models have
        // already been loaded by minecraft), but they can't be registered from the onPreInitialization
        // event because that's too early (items haven't been registered yet, so RegistryDelegate.name == null.
        // causing all item variants to be added to the same item (RegistryDelegate.equals compares the names
        // of the delegates only).
        ModelManager.registerModelVariants();
        // ModelManager.addCustomStateMappers(); // TODO: fix this
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
    public void setCloudRenderer(WorldProvider provider, IRenderHandler renderer) {
        provider.setCloudRenderer(renderer);
    }

    @Override
    public void setSkyRenderer(WorldProvider provider, IRenderHandler renderer) {
        provider.setSkyRenderer(renderer);
    }
}
