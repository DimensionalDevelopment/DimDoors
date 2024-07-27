package org.dimdev.dimdoors;

import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.proxy.IProxy;
import org.dimdev.dimdoors.client.DDGUIHandler;
import org.dimdev.dimdoors.shared.EventHandler;
import org.dimdev.dimdoors.shared.ModConfig;
import org.dimdev.dimdoors.shared.ModRecipes;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.commands.CommandDimTeleport;
import org.dimdev.dimdoors.shared.commands.CommandFabricConvert;
import org.dimdev.dimdoors.shared.commands.CommandPocket;
import org.dimdev.dimdoors.shared.commands.CommandSaveSchem;
import org.dimdev.dimdoors.shared.entities.EntityMonolith;
import org.dimdev.dimdoors.shared.items.ModItems;
import org.dimdev.dimdoors.shared.pockets.SchematicHandler;
import org.dimdev.dimdoors.shared.rifts.targets.*;
import org.dimdev.dimdoors.shared.sound.ModSounds;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;
import org.dimdev.dimdoors.shared.world.ModBiomes;
import org.dimdev.dimdoors.shared.world.ModDimensions;
import org.dimdev.dimdoors.shared.world.gateways.GatewayGenerator;

import java.io.File;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;
import static org.dimdev.dimdoors.DimDoors.*;
import static org.dimdev.dimdoors.shared.ModConfig.general;
import static org.dimdev.dimdoors.shared.rifts.targets.VirtualTarget.registry;

@Mod(modid = MODID, name = MODNAME, version = VERSION, acceptedMinecraftVersions = MCVERSIONS, dependencies = DEPENDENCIES)
public class DimDoors {

    public static final String MODID = "dimdoors";
    public static final String MODNAME = "Dimensional Doors";
    public static final String MCVERSIONS = "[1.12,1.13)";
    public static final String VERSION = "3.2.2";
    public static final String DEPENDENCIES = "required-after:forge@[14.23.5.2860,)";

    @Instance(MODID)
    public static DimDoors instance;
    public static Logger log;

    @SidedProxy(clientSide = "org.dimdev.dimdoors.proxy.ClientProxy",
            serverSide = "org.dimdev.dimdoors.proxy.ServerProxy")
    public static IProxy proxy;

    @Getter public static File configurationFolder;

    // Initialization
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Mod.EventHandler
    public void onPreInitialization(FMLPreInitializationEvent event) {
        log = event.getModLog();
        // Register event handlers
        EVENT_BUS.register(EventHandler.class);
        EVENT_BUS.register(ModBlocks.class);
        EVENT_BUS.register(ModItems.class);
        EVENT_BUS.register(ModRecipes.class);
        EVENT_BUS.register(ModSounds.class);
        EVENT_BUS.register(ModBiomes.class);
        EVENT_BUS.register(ModConfig.class);
        // Register rift destinations
        registry.put("available_link",RandomTarget.class);
        registry.put("escape",EscapeTarget.class);
        registry.put("global",GlobalReference.class);
        registry.put("limbo",LimboTarget.class);
        registry.put("local",LocalReference.class);
        registry.put("public_pocket",PublicPocketTarget.class);
        registry.put("pocket_entrance",PocketEntranceMarker.class);
        registry.put("pocket_exit",PocketExitMarker.class);
        registry.put("private",PrivatePocketTarget.class);
        registry.put("private_pocket_exit",PrivatePocketExitTarget.class);
        registry.put("relative",RelativeReference.class);
        // Register entities
        EntityRegistry.registerModEntity(getResource("mob_monolith"),EntityMonolith.class,
                "mob_monolith",0,instance,70,1,true);
        EntityRegistry.registerEgg(getResource("mob_monolith"),0,0xffffff);
        // Register tile entities
        TileEntity.register("dimdoors:entrance_rift",TileEntityEntranceRift.class);
        TileEntity.register("dimdoors:floating_rift",TileEntityFloatingRift.class);
        // Register dimensions
        ModDimensions.registerDimensions();
        // Register default targets
        Targets.registerDefaultTargets();
        //Register GUIhandler
        NetworkRegistry.INSTANCE.registerGuiHandler(this,new DDGUIHandler());
        // Make config folder and check if config needs to be regenerated TODO
        configurationFolder = new File(event.getModConfigurationDirectory(),"/DimDoors");
        configurationFolder.mkdirs();
        proxy.onPreInitialization(event);
    }

    @Mod.EventHandler
    public void onInitialization(FMLInitializationEvent event) {
        // Register loot tables
        LootTableList.register(getResource("dungeon_chest"));
        LootTableList.register(getResource("dispenser_projectiles"));
        LootTableList.register(getResource("dispenser_splash_potions"));
        LootTableList.register(getResource("dispenser_potion_arrows"));
        // Load schematics
        SchematicHandler.INSTANCE.loadSchematics();
        // Register world generators
        GameRegistry.registerWorldGenerator(new GatewayGenerator(), 0);
        proxy.onInitialization(event);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        // Register commands
        event.registerServerCommand(new CommandDimTeleport());
        event.registerServerCommand(new CommandPocket());
        event.registerServerCommand(new CommandSaveSchem());
        event.registerServerCommand(new CommandFabricConvert());
    }

    public static void sendTranslatedMessage(Entity entity, String text, Object... translationArgs) {
        // TODO: check if too long and split into several messages?
        if(entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            player.sendStatusMessage(new TextComponentTranslation(text,translationArgs),general.useStatusBar);
        }
    }

    public static void chat(Entity entity, String text, Object... translationArgs) {
        entity.sendMessage(new TextComponentTranslation(text,translationArgs));
    }

    public static ResourceLocation getResource(String path) {
        return new ResourceLocation(MODID,path);
    }
}