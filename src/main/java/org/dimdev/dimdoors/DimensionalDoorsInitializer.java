package org.dimdev.dimdoors;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.command.arguments.CoordinateArgument;
import net.minecraft.command.arguments.DimensionArgumentType;
import net.minecraft.command.arguments.RotationArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.commands.CommandDimTeleport;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.pockets.SchematicHandler;
import org.dimdev.dimdoors.rift.targets.*;
import org.dimdev.dimdoors.world.ModBiomes;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.util.TeleportUtil;

public class DimensionalDoorsInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        ModBlocks.init();
        ModItems.init();
        ModDimensions.init();
        ModEntityTypes.init();
        ModBiomes.init();

        registerCommands();

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

        Targets.registerDefaultTargets();

        SchematicHandler.INSTANCE.loadSchematics();
    }

    private void registerCommands() {
        CommandRegistry.INSTANCE.register(false, CommandDimTeleport::register);

    }
}
