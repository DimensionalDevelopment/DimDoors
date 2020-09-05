package org.dimdev.dimdoors;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.command.ModCommands;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.rift.targets.EscapeTarget;
import org.dimdev.dimdoors.rift.targets.GlobalReference;
import org.dimdev.dimdoors.rift.targets.LimboTarget;
import org.dimdev.dimdoors.rift.targets.LocalReference;
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker;
import org.dimdev.dimdoors.rift.targets.PocketExitMarker;
import org.dimdev.dimdoors.rift.targets.PrivatePocketExitTarget;
import org.dimdev.dimdoors.rift.targets.PrivatePocketTarget;
import org.dimdev.dimdoors.rift.targets.PublicPocketTarget;
import org.dimdev.dimdoors.rift.targets.RandomTarget;
import org.dimdev.dimdoors.rift.targets.RelativeReference;
import org.dimdev.dimdoors.rift.targets.Targets;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.ModBiomes;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.feature.ModFeatures;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;

public class DimensionalDoorsInitializer implements ModInitializer {

    public static final Identifier MONOLITH_PARTICLE_PACKET = new Identifier("dimdoors", "monolith_particle_packet");

    private static MinecraftServer server;

    public static MinecraftServer getServer() {
        return server;
    }

    public static ServerWorld getWorld(RegistryKey<World> key) {
        return getServer().getWorld(key);
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(DimensionalDoorsInitializer::setServer);

        ModBlocks.init();
        ModItems.init();
        ModDimensions.init();
        ModEntityTypes.init();
        ModBiomes.init();
        ModBlockEntityTypes.init();
        ModCommands.init();
        ModSoundEvents.init();
        ModFeatures.init();

        Targets.registerDefaultTargets();
    }

    private static void setServer(MinecraftServer server) {
        DimensionalDoorsInitializer.server = server;
    }
}
