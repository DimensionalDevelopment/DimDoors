package org.dimdev.dimdoors;

import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.command.ModCommands;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.rift.targets.*;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.ModBiomes;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.ModFeatures;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;

public class DimensionalDoorsInitializer implements ModInitializer {

    public static final Identifier MONOLITH_PARTICLE_PACKET = new Identifier("dimdoors", "monolith_particle_packet");

    @Override
    public void onInitialize() {
        ModBlocks.init();
        ModItems.init();
        ModDimensions.init();
        ModEntityTypes.init();
        ModBiomes.init();
        ModFeatures.init();
        ModBlockEntityTypes.init();
        ModCommands.init();
        ModSoundEvents.init();

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
    }
}
