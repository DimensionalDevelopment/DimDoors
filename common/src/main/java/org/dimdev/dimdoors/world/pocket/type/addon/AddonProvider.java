package org.dimdev.dimdoors.world.pocket.type.addon;

public interface AddonProvider {
    boolean hasAddon(PocketAddon.PocketAddonType<?, ?> id);

    <C extends PocketAddon> boolean addAddon(C addon);
}
