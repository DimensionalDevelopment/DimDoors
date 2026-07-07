package org.dimdev.dimdoors.world.pocket.type.addon;

import net.minecraft.resources.ResourceLocation;

public interface AddonProvider {
    <C extends PocketAddon> C getAddon(ResourceLocation id);

    boolean hasAddon(PocketAddon.PocketAddonType<?, ?> id);

    <C extends PocketAddon> boolean addAddon(C addon);
}
