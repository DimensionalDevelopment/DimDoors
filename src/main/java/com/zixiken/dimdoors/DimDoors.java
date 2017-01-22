package com.zixiken.dimdoors;

import com.zixiken.dimdoors.items.ModItems;
import com.zixiken.dimdoors.shared.PocketSavedData;
import com.zixiken.dimdoors.shared.RiftRegistry;
import com.zixiken.dimdoors.shared.RiftSavedData;
import com.zixiken.dimdoors.shared.SchematicHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = DimDoors.MODID, name = "Dimensional Doors", version = DimDoors.VERSION)
public class DimDoors {

    public static final String VERSION = "3.0.0-a1";
    public static final String MODID = "dimdoors";

    @SidedProxy(clientSide = "com.zixiken.dimdoors.client.DDProxyClient",
            serverSide = "com.zixiken.dimdoors.server.DDProxyServer")
    public static DDProxyCommon proxy;

    @Mod.Instance(DimDoors.MODID)
    public static DimDoors instance;

    public static CreativeTabs dimDoorsCreativeTab = new CreativeTabs("dimDoorsCreativeTab") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return ModItems.itemDimDoor;
        }
    };

    @Mod.EventHandler
    public void onPreInitialization(FMLPreInitializationEvent event) {
        proxy.onPreInitialization(event);
        DDConfig.loadConfig(event);
    }

    @Mod.EventHandler
    public void onInitialization(FMLInitializationEvent event) {
        proxy.onInitialization(event);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        //@todo event.registerServerCommand( new DDCommand() ); //to register commands that this mod offers?
        RiftRegistry.Instance.reset();
        PocketSavedData.get(getDefWorld());
        RiftSavedData.get(getDefWorld());
        SchematicHandler.Instance.loadSchematics();
    }

    public static boolean isClient() {
        return proxy.isClient();
    }

    public static boolean isServer() {
        return !isClient();
    }

    public static World getDefWorld() {
        return proxy.getDefWorld(); //gets the server or client world dim 0 handler
    }
    
    public static void chat(EntityPlayer player, String text) {
        player.sendMessage(new TextComponentString("[DimDoors] " + text));
    }

    public static void log(Class classFiredFrom, String text) {
        FMLLog.info("[DimDoors] " + text + " (" + classFiredFrom.toString() + " )", 0);
    }
}
