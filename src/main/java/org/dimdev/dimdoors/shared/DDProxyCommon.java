package org.dimdev.dimdoors.shared;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.entities.EntityMonolith;
import org.dimdev.dimdoors.shared.items.ModItems;
import org.dimdev.dimdoors.shared.rifts.*;
import org.dimdev.dimdoors.shared.sound.ModSounds;
import org.dimdev.dimdoors.shared.tileentities.*;
import org.dimdev.dimdoors.shared.world.DimDoorDimensions;
import org.dimdev.dimdoors.shared.world.ModBiomes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class DDProxyCommon implements IDDProxy {

    @Override
    public void onPreInitialization(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(EventHandler.class);
        MinecraftForge.EVENT_BUS.register(ModBlocks.class);
        MinecraftForge.EVENT_BUS.register(ModItems.class);
        MinecraftForge.EVENT_BUS.register(CraftingManager.class); // TODO: ModRecipes?
        MinecraftForge.EVENT_BUS.register(ModSounds.class);
        MinecraftForge.EVENT_BUS.register(ModBiomes.class);

        registerTileEntities();
        DimDoorDimensions.registerDimensions();

        EntityRegistry.registerModEntity(new ResourceLocation(DimDoors.MODID, "mob_monolith"), EntityMonolith.class, "monolith", 0, DimDoors.instance, 70, 1, true);
        EntityRegistry.registerEgg(new ResourceLocation(DimDoors.MODID, "mob_monolith"), 0, 0xffffff);
        registerRiftDestinations();
    }

    public void registerRiftDestinations() {
        RiftDestination.destinationRegistry.put("available_link", AvailableLinkDestination.class);
        RiftDestination.destinationRegistry.put("escape", EscapeDestination.class);
        RiftDestination.destinationRegistry.put("global", GlobalDestination.class);
        RiftDestination.destinationRegistry.put("limbo", LimboDestination.class);
        RiftDestination.destinationRegistry.put("local", LocalDestination.class);
        RiftDestination.destinationRegistry.put("new_public", NewPublicDestination.class);
        RiftDestination.destinationRegistry.put("pocket_entrance", PocketEntranceDestination.class);
        RiftDestination.destinationRegistry.put("pocket_exit", PocketExitDestination.class);
        RiftDestination.destinationRegistry.put("private", PrivateDestination.class);
        RiftDestination.destinationRegistry.put("private_pocket_exit", PrivatePocketExitDestination.class);
        RiftDestination.destinationRegistry.put("relative", RelativeDestination.class);
    }

    @Override
    public void onInitialization(FMLInitializationEvent event) {
        SchematicHandler.INSTANCE.loadSchematics();
    }

    public void registerTileEntities() { // TODO: new registry system
        GameRegistry.registerTileEntity(TileEntityEntranceRift.class, "TileEntityEntranceRift");
        GameRegistry.registerTileEntity(TileEntityFloatingRift.class, "TileEntityFloatingRift");
    }
}
