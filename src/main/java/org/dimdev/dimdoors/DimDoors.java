package org.dimdev.dimdoors;

import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
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

import static org.dimdev.dimdoors.DimDoors.*;

@Mod(modid = MODID, name = MODNAME, version = VERSION, acceptedMinecraftVersions = MCVERSIONS, dependencies = DEPENDENCIES)
public class DimDoors {

    public static final String MODID = "dimdoors";
    public static final String MODNAME = "Dimensional Doors";
    public static final String MCVERSIONS = "[1.12,1.13)";
    public static final String VERSION = "${version}";
    public static final String DEPENDENCIES = "required-after:forge@[14.21.0.2320,)";

    @Mod.Instance(DimDoors.MODID)
    public static DimDoors instance;
    public static Logger log;

    @SidedProxy(clientSide = "org.dimdev.dimdoors.proxy.ClientProxy",
                serverSide = "org.dimdev.dimdoors.proxy.ServerProxy")
    public static IProxy proxy;

    @Getter public static File configurationFolder;

    // Initialization

    @Mod.EventHandler
    public void onPreInitialization(FMLPreInitializationEvent event) {
        log = event.getModLog();

        // Register event handlers
        MinecraftForge.EVENT_BUS.register(EventHandler.class);
        MinecraftForge.EVENT_BUS.register(ModBlocks.class);
        MinecraftForge.EVENT_BUS.register(ModItems.class);
        MinecraftForge.EVENT_BUS.register(ModRecipes.class);
        MinecraftForge.EVENT_BUS.register(ModSounds.class);
        MinecraftForge.EVENT_BUS.register(ModBiomes.class);
        MinecraftForge.EVENT_BUS.register(ModConfig.class);

        // Register rift destinations
        VirtualTarget.registry.put("available_link", RandomTarget.class);
        VirtualTarget.registry.put("escape", EscapeTarget.class);
        VirtualTarget.registry.put("global", GlobalReference.class);
        VirtualTarget.registry.put("limbo", LimboTarget.class);
        VirtualTarget.registry.put("local", LocalReference.class);
        VirtualTarget.registry.put("public_pocket", PublicPocketTarget.class);
        VirtualTarget.registry.put("pocket_entrance", PocketEntranceMarker.class);
        VirtualTarget.registry.put("pocket_exit", PocketExitMarker.class);
        VirtualTarget.registry.put("private", PrivatePocketTarget.class);
        VirtualTarget.registry.put("private_pocket_exit", PrivatePocketExitTarget.class);
        VirtualTarget.registry.put("relative", RelativeReference.class);

        // Register entities
        EntityRegistry.registerModEntity(new ResourceLocation(DimDoors.MODID, "mob_monolith"), EntityMonolith.class, "monoliths", 0, DimDoors.instance, 70, 1, true);
        EntityRegistry.registerEgg(new ResourceLocation(DimDoors.MODID, "mob_monolith"), 0, 0xffffff);

        // Register tile entities
        GameRegistry.registerTileEntity(TileEntityEntranceRift.class, "dimdoors:entrance_rift");
        GameRegistry.registerTileEntity(TileEntityFloatingRift.class, "dimdoors:floating_rift");

        // Register dimensions
        ModDimensions.registerDimensions();

        // Register default targets
        Targets.registerDefaultTargets();
        
        //Register GUIhandler
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new DDGUIHandler());

        // Make config folder and check if config needs to be regenerated TODO
        configurationFolder = new File(event.getModConfigurationDirectory(), "/DimDoors");
        configurationFolder.mkdirs();

        proxy.onPreInitialization(event);
    }

    @Mod.EventHandler
    public void onInitialization(FMLInitializationEvent event) {
        // Register loot tables
        LootTableList.register(new ResourceLocation(DimDoors.MODID, "dungeon_chest"));
        LootTableList.register(new ResourceLocation(DimDoors.MODID, "dispenser_projectiles"));
        LootTableList.register(new ResourceLocation(DimDoors.MODID, "dispenser_splash_potions"));
        LootTableList.register(new ResourceLocation(DimDoors.MODID, "dispenser_potion_arrows"));

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
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            player.sendStatusMessage(new TextComponentTranslation(text, translationArgs), ModConfig.general.useStatusBar);
        }
    }

    public static void chat(Entity entity, String text, Object... translationArgs) {
        entity.sendMessage(new TextComponentTranslation(text, translationArgs));
    }
}
