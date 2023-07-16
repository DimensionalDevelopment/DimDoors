package org.dimdev.dimdoors.world.level.component.forge;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
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
import org.dimdev.dimdoors.world.level.component.ChunkLazilyGeneratedComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChunkLazilyGeneratedComponentImpl {
    public static final ResourceLocation IDENTIFIER = DimensionalDoors.id("chunk_lazily_generated");
    public static final Capability<ChunkLazilyGeneratedComponent> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public static ChunkLazilyGeneratedComponent get(LevelChunk  chunk) {
        return chunk.getCapability(INSTANCE).resolve().get();
    }

    @Mod.EventBusSubscriber(modid = DimensionalDoors.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final ChunkLazilyGeneratedComponent backend = new ChunkLazilyGeneratedComponent();
        private final LazyOptional<ChunkLazilyGeneratedComponent> optionalData = LazyOptional.of(() -> backend);
        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
            return ChunkLazilyGeneratedComponentImpl.INSTANCE.orEmpty(capability, optionalData);
        }

        @Override
        public CompoundTag serializeNBT() {
            var nbt = new CompoundTag();
            this.backend.writeToNbt(nbt);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag arg) {
            this.backend.readFromNbt(arg);
        }

        @SubscribeEvent
        public static void attach(final AttachCapabilitiesEvent<ChunkAccess> event) {
            final Provider provider = new Provider();

            event.addCapability(ChunkLazilyGeneratedComponentImpl.IDENTIFIER, provider);
        }
    }
}
