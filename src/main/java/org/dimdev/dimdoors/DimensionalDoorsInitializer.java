package org.dimdev.dimdoors;

import java.io.IOException;

import org.dimdev.dimdoors.util.schematic.v2.SchematicTest;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.command.ModCommands;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.pockets.SchematicHandler;
import org.dimdev.dimdoors.pockets.SchematicV2Handler;
import org.dimdev.dimdoors.rift.targets.Targets;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.ModBiomes;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.feature.ModFeatures;
import org.jetbrains.annotations.NotNull;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

public class DimensionalDoorsInitializer implements ModInitializer {
    public static final Identifier MONOLITH_PARTICLE_PACKET = new Identifier("dimdoors", "monolith_particle_packet");

    private static MinecraftServer server;

    @NotNull
    public static MinecraftServer getServer() {
        if (server != null) {
            return server;
        }
        throw new UnsupportedOperationException("Accessed server too early!");
    }

    public static ServerWorld getWorld(RegistryKey<World> key) {
        return getServer().getWorld(key);
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register((minecraftServer) -> {
            server = minecraftServer;
            if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
                try {
                    SchematicTest.test();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        ModBlocks.init();
        ModItems.init();
        ModFeatures.init();
        ModBiomes.init();
        ModDimensions.init();
        ModEntityTypes.init();
        ModBlockEntityTypes.init();
        ModCommands.init();
        ModSoundEvents.init();

        ModConfig.deserialize();

        Targets.registerDefaultTargets();

        SchematicV2Handler.getInstance().load();
        SchematicHandler.INSTANCE.loadSchematics();
    }
}
