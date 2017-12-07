package com.zixiken.dimdoors;

import com.zixiken.dimdoors.shared.commands.PocketCommand;
import com.zixiken.dimdoors.shared.commands.TeleportCommand;
import com.zixiken.dimdoors.shared.DDConfig;
import com.zixiken.dimdoors.shared.DDProxyCommon;
import com.zixiken.dimdoors.shared.PocketRegistry;
import com.zixiken.dimdoors.shared.items.ModItems;
import com.zixiken.dimdoors.shared.RiftRegistry;
import com.zixiken.dimdoors.shared.SchematicHandler;
import com.zixiken.dimdoors.shared.util.DefaultSchematicGenerator;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@Mod(modid = DimDoors.MODID, name = "Dimensional Doors", version = DimDoors.VERSION, dependencies = "required-after:forge@[14.23.0.2517,)")
public class DimDoors {

    public static final String MODID = "dimdoors";
    public static final String VERSION = "${version}";

    @SidedProxy(clientSide = "com.zixiken.dimdoors.client.DDProxyClient",
                serverSide = "com.zixiken.dimdoors.server.DDProxyServer")
    public static DDProxyCommon proxy;

    @Mod.Instance(DimDoors.MODID)
    public static DimDoors instance;

    public static CreativeTabs dimDoorsCreativeTab = new CreativeTabs("dimensional_doors_creative_tab") {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(ModItems.DIMENSIONAL_DOOR);
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
        registerCommands(event);
        RiftRegistry.INSTANCE.reset();
        PocketRegistry.INSTANCE.reset();

        //DefaultSchematicGenerator.tempGenerateDefaultSchematics();
    }

    private void registerCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(new TeleportCommand());
        event.registerServerCommand(new PocketCommand());
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

    public static void warn(String text) {
        warn(null, text);
    }

    public static void warn(Class<?> classFiredFrom, String text) {
        if(classFiredFrom != null) {
            FMLLog.log.warn("[DimDoors] " + text + " (" + classFiredFrom + " )", 0);
        } else {
            FMLLog.log.warn("[DimDoors] " + text, 0);
        }
    }

    public static void log(String text) {
        log(null, text);
    }

    public static void log(Class<?> classFiredFrom, String text) {
        if(classFiredFrom != null) {
            FMLLog.log.info("[DimDoors] " + text + " (" + classFiredFrom + " )", 0);
        } else {
            FMLLog.log.info("[DimDoors] " + text, 0);
        }
    }

    // TODO: I18n is deprecated, convert to TextComponentTranslation
    public static void translateAndAdd(String key, List<String> list) {
        for (int i = 0; i < 10; i++) {
            if (I18n.canTranslate(key + Integer.toString(i))) {
                String line = I18n.translateToLocal(key + Integer.toString(i));
                list.add(line);
            } else {
                break;
            }
        }
    }
}
