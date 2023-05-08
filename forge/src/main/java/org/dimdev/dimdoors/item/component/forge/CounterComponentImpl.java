package org.dimdev.dimdoors.item.component.forge;

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
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.item.component.CounterComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CounterComponentImpl implements CounterComponent {
    private static final ResourceLocation IDENTIFIER = DimensionalDoors.id("counter");

    public static final Capability<CounterComponentImpl> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    private int counter = 0;

    public CounterComponentImpl() {
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

    public static CounterComponentImpl get(ItemStack provider) {
        return provider.getCapability(INSTANCE).resolve().get();
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final CounterComponentImpl backend = new CounterComponentImpl();

        private final LazyOptional<CounterComponentImpl> optionalData = LazyOptional.of(() -> backend);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
            return CounterComponentImpl.INSTANCE.orEmpty(capability, optionalData);
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.backend.writeToNbt();
        }
        @Override
        public void deserializeNBT(CompoundTag arg) {
            this.backend.readFromNbt(arg);
        }

        public static void attach(final AttachCapabilitiesEvent<ItemStack> event) {
            if(event.getObject().is(ModItems.RIFT_CONFIGURATION_TOOL.get())) {
                final CounterComponentImpl.Provider provider = new CounterComponentImpl.Provider();

                event.addCapability(CounterComponentImpl.IDENTIFIER, provider);
            }
        }

    }
}
