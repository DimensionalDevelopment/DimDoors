package org.dimdev.dimdoors.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.IRenderHandler;
import org.dimdev.dimdoors.shared.fluids.ModFluids;

public class CommonProxy implements IProxy {

    @Override
    public void onPreInitialization(FMLPreInitializationEvent event) {
        ModFluids.registerFluids();
    }

    @Override
    public void onInitialization(FMLInitializationEvent event) {}

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
