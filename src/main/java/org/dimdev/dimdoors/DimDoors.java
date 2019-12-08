package org.dimdev.dimdoors;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.Entity;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.rift.targets.*;
import org.dimdev.dimdoors.world.ModDimensions;

import java.io.File;

public class DimDoors implements ModInitializer {
    public static void sendTranslatedMessage(Entity entity, String s, Object... args) {
        // TODO
    }

    public static String getConfigurationFolder() {
        return null; // TODO
    }

    @Override
    public void onInitialize() {
        ModBlocks.init();
        ModItems.init();
        ModDimensions.registerDimensions();

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
