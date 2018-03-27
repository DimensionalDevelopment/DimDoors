package org.dimdev.dimdoors;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import org.dimdev.dimdoors.shared.ModConfig;
import org.dimdev.dimdoors.shared.commands.CommandFabricConvert;
import org.dimdev.dimdoors.shared.commands.CommandPocket;
import org.dimdev.dimdoors.shared.commands.CommandDimTeleport;
import org.dimdev.dimdoors.shared.CommonProxy;
import org.dimdev.dimdoors.shared.commands.CommandSaveSchem;
import org.dimdev.dimdoors.shared.items.ModItems;
import org.dimdev.dimdoors.shared.world.gateways.GatewayGenerator;
import lombok.Getter;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

import java.io.File;

import static org.dimdev.dimdoors.DimDoors.*;

@Mod(modid = MODID, name = MODNAME, version = VERSION, acceptedMinecraftVersions = MCVERSIONS, dependencies = DEPENDENCIES)
public class DimDoors {

    public static final String MODID = "dimdoors";
    public static final String MODNAME = "Dimensional Doors";
    public static final String MCVERSIONS = "[1.12,1.13)";
    public static final String VERSION = "${version}";
    public static final String DEPENDENCIES = "required-after:forge@[14.21.0.2320,);after:csb_ench_table";
    // after:csb_ench_table as a workaround for https://github.com/crazysnailboy/EnchantingTable/issues/7

    @Mod.Instance(DimDoors.MODID)
    public static DimDoors instance;
    public static Logger log;

    @SidedProxy(clientSide = "org.dimdev.dimdoors.client.ClientProxy",
                serverSide = "org.dimdev.dimdoors.server.ServerProxy")
    public static CommonProxy proxy;

    public static final CreativeTabs DIM_DOORS_CREATIVE_TAB = new CreativeTabs("dimensional_doors_creative_tab") {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(ModItems.DIMENSIONAL_DOOR);
        }
    };

    @Getter private GatewayGenerator gatewayGenerator;
    @Getter public static File configurationFolder;

    @Mod.EventHandler
    public void onPreInitialization(FMLPreInitializationEvent event) {
        log = event.getModLog();
        proxy.onPreInitialization(event);
        configurationFolder = new File(event.getModConfigurationDirectory(), "/DimDoors");
        if (configurationFolder.exists()) {
            configurationFolder.mkdirs();
        }
    }

    @Mod.EventHandler
    public void onInitialization(FMLInitializationEvent event) {
        proxy.onInitialization(event);
        gatewayGenerator = new GatewayGenerator(); //TODO put this in a proxy and structure this modularly instead of writing whole new classes per gateway type...
        GameRegistry.registerWorldGenerator(gatewayGenerator, 0);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        registerCommands(event);
    }

    private void registerCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDimTeleport());
        event.registerServerCommand(new CommandPocket());
        event.registerServerCommand(new CommandSaveSchem());
        event.registerServerCommand(new CommandFabricConvert());
    }

    public static void sendTranslatedMessage(Entity entity, String text, Object... translationArgs) {
        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            player.sendStatusMessage(new TextComponentTranslation(text, translationArgs), ModConfig.general.useStatusBar);
        }
    }

    public static void chat(Entity entity, String text, Object... translationArgs) {
        entity.sendMessage(new TextComponentTranslation(text, translationArgs));
    }
}
