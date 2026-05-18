package org.dimdev.dimdoors.api.event;

import org.dimdev.dimdoors.api.util.SimpleEvent;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;

@FunctionalInterface
public interface RegisterDimensionalDoorProductionsCallback {
    SimpleEvent<RegisterDimensionalDoorProductionsCallback> EVENT = SimpleEvent.of(callbacks -> registrar ->
            callbacks.forEach(callback -> callback.register(registrar)));

    void register(DimensionalDoorBlockRegistrar registrar);
}
