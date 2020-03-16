package org.dimdev.dimdoors;

import net.fabricmc.api.ModInitializer;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.pockets.SchematicHandler;
import org.dimdev.dimdoors.rift.targets.*;
import org.dimdev.dimdoors.world.ModBiomes;
import org.dimdev.dimdoors.world.ModDimensions;

public class DimensionalDoorsInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        ModBlocks.init();
        ModItems.init();
        ModDimensions.init();
        ModEntityTypes.init();
        ModBiomes.init();

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
}
