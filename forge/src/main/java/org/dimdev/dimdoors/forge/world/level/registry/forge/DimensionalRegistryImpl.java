package org.dimdev.dimdoors.forge.world.level.registry.forge;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DimensionalRegistryImpl {
    public static final ResourceLocation IDENTIFIER = DimensionalDoors.id("dimensional_registry");

    public static final Capability<DimensionalRegistry> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    @Mod.EventBusSubscriber(modid = DimensionalDoors.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final LazyOptional<DimensionalRegistry> optionalData = LazyOptional.empty();
        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
            return DimensionalRegistryImpl.INSTANCE.orEmpty(capability, optionalData);
        }

        @Override
        public CompoundTag serializeNBT() {
            var nbt = new CompoundTag();
            DimensionalRegistry.writeToNbt(nbt);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag arg) {
            DimensionalRegistry.readFromNbt(arg);
        }

        @SubscribeEvent
        public static void attach(final AttachCapabilitiesEvent<Level> event) {
            if(DimensionalRegistry.isValidWorld(event.getObject())) {
                final DimensionalRegistryImpl.Provider provider = new DimensionalRegistryImpl.Provider();

                event.addCapability(DimensionalRegistryImpl.IDENTIFIER, provider);
            }
        }
    }
}
