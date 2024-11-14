package org.dimdev.dimdoors.item.component.neoforge;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.item.component.IdCounter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CounterComponentImplDeprecated implements IdCounter {
    private static final ResourceLocation IDENTIFIER = DimensionalDoors.id("counter");

    public static final ItemCapability<IdCounter, Void> INSTANCE = ItemCapability.createVoid(IDENTIFIER, IdCounter.class);

    private int counter = 0;

    public CounterComponentImplDeprecated() {
    }

    public int increment() {
        counter++;
        return counter;
    }

    public int count() {
        return counter;
    }

    public void reset() {
        counter = 0;
    }

    private CompoundTag writeToNbt() {
        var tag = new CompoundTag();
        tag.putInt("counter", counter);
        return tag;
    }

    private void readFromNbt(CompoundTag arg) {
        arg.putInt("counter", counter);
    }

    public static IdCounter get(ItemStack provider) {
        return provider.getDagetCapability(INSTANCE);
    }

    @Mod.EventBusSubscriber(modid = DimensionalDoors.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final CounterComponentImplDeprecated backend = new CounterComponentImplDeprecated();

        private final LazyOptional<CounterComponentImplDeprecated> optionalData = LazyOptional.of(() -> backend);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
            return CounterComponentImplDeprecated.INSTANCE.orEmpty(capability, optionalData);
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.backend.writeToNbt();
        }
        @Override
        public void deserializeNBT(CompoundTag arg) {
            this.backend.readFromNbt(arg);
        }

        @SubscribeEvent
        public static void attach(final AttachCapabilitiesEvent<ItemStack> event) {
            if(event.getObject().is(ModItems.RIFT_CONFIGURATION_TOOL.get())) {
                final CounterComponentImplDeprecated.Provider provider = new CounterComponentImplDeprecated.Provider();

                event.addCapability(CounterComponentImplDeprecated.IDENTIFIER, provider);
            }
        }

    }
}
