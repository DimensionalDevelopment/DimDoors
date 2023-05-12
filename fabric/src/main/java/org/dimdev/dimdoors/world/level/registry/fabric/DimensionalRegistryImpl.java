package org.dimdev.dimdoors.world.level.registry.fabric;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.server.dedicated.DedicatedServer;
import org.dimdev.dimdoors.DimensionalDoorsComponents;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;

import static org.dimdev.dimdoors.DimensionalDoors.getServer;

public class DimensionalRegistryImpl extends DimensionalRegistry implements ComponentV3 {
    public static DimensionalRegistry instance() {
        return DimensionalDoorsComponents.DIMENSIONAL_REGISTRY_COMPONENT_KEY.get(((DedicatedServer) getServer()).getProperties());
    }
}
