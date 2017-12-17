package com.zixiken.dimdoors;

import com.zixiken.dimdoors.shared.commands.PocketCommand;
import com.zixiken.dimdoors.shared.commands.CommandDimTeleport;
import com.zixiken.dimdoors.shared.DDConfig;
import com.zixiken.dimdoors.shared.DDProxyCommon;
import com.zixiken.dimdoors.shared.items.ModItems;
import com.zixiken.dimdoors.shared.world.gateways.GatewayGenerator;
import lombok.Getter;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod(modid = DimDoors.MODID, name = "Dimensional Doors",
     version = DimDoors.VERSION,
     dependencies = "required-after:forge@[14.23.0.2517,)")
public class DimDoors {

    public static final String MODID = "dimdoors";
    public static final String VERSION = "${version}";

    @Mod.Instance(DimDoors.MODID)
    public static DimDoors instance;

    public static Logger log; // TODO: make non-static?

    @SidedProxy(clientSide = "com.zixiken.dimdoors.client.DDProxyClient",
                serverSide = "com.zixiken.dimdoors.server.DDProxyServer")
    public static DDProxyCommon proxy;

    public static final CreativeTabs DIM_DOORS_CREATIVE_TAB = new CreativeTabs("dimensional_doors_creative_tab") {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(ModItems.DIMENSIONAL_DOOR);
        }
    };

    @Getter private GatewayGenerator gatewayGenerator;

    public static boolean disableRiftSetup = false; // TODO: Find a better system.

    @Mod.EventHandler
    public void onPreInitialization(FMLPreInitializationEvent event) {
        log = event.getModLog();
        proxy.onPreInitialization(event);
        DDConfig.loadConfig(event);
    }

    @Mod.EventHandler
    public void onInitialization(FMLInitializationEvent event) {
        proxy.onInitialization(event);
        gatewayGenerator = new GatewayGenerator();
        GameRegistry.registerWorldGenerator(gatewayGenerator, 0);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        registerCommands(event);
    }

    private void registerCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDimTeleport());
        event.registerServerCommand(new PocketCommand());
    }

    public static boolean isClient() {
        return proxy.isClient();
    }

    public static boolean isServer() {
        return !isClient();
    }

    public static void chat(EntityPlayer player, String text) {
        player.sendMessage(new TextComponentString("[DimDoors] " + text));
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
