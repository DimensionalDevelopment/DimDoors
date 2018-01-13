package org.dimdev.dimdoors;

import org.dimdev.dimdoors.shared.commands.CommandPocket;
import org.dimdev.dimdoors.shared.commands.CommandDimTeleport;
import org.dimdev.dimdoors.shared.Config;
import org.dimdev.dimdoors.shared.CommonProxy;
import org.dimdev.dimdoors.shared.items.ModItems;
import org.dimdev.dimdoors.shared.world.gateways.GatewayGenerator;
import lombok.Getter;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

@Mod(modid = DimDoors.MODID, name = DimDoors.MODNAME, version = DimDoors.VERSION, dependencies = DimDoors.DEPENDENCIES)
public class DimDoors {

    public static final String MODID = "dimdoors";
    public static final String MODNAME = "Dimensional Doors";
    public static final String VERSION = "${version}";
    public static final String DEPENDENCIES = "required-after:forge@[14.23.0.2517,)"; //TODO make the forge version here change depending on a field in build.gradle

    @Mod.Instance(DimDoors.MODID)
    public static DimDoors instance;
    public static Logger log; // TODO: make non-static?

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

    @Mod.EventHandler
    public void onPreInitialization(FMLPreInitializationEvent event) {
        log = event.getModLog();
        proxy.onPreInitialization(event);
        Config.loadConfig(event);
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
        event.registerServerCommand(new CommandPocket());
    }

    public static void chat(Entity entity, String text) {
        entity.sendMessage(new TextComponentString("[DimDoors] " + text));
    }
}
