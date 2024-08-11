package org.dimdev.dimdoors.item.component;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.RotatedLocation;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final Holder<DataComponentType<IdCounter>> ID_COUNTER = DATA_COMPONENTS.register("id_counter", () -> DataComponentType.<IdCounter>builder().persistent(IdCounter.CODEC).networkSynchronized(IdCounter.STREAM_CODEC).build());
    public static final Holder<DataComponentType<RotatedLocation>> DESTINATION = DATA_COMPONENTS.register("destination", () -> DataComponentType.<RotatedLocation>builder().persistent(RotatedLocation.CODEC).networkSynchronized(RotatedLocation.STREAM_CODEC).build());
}
