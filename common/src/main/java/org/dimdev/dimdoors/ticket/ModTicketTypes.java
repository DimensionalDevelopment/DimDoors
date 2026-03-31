package org.dimdev.dimdoors.ticket;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import net.fabricmc.fabric.impl.biome.modification.BuiltInRegistryKeys;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.TicketType;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModTicketTypes {
    public static final Registrar<TicketType<?>> REGISTRY = DeferredRegister.create(DimensionalDoors.MOD_ID, .)
}
