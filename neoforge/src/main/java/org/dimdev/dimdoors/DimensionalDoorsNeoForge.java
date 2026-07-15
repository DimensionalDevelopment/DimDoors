package org.dimdev.dimdoors;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.Codec;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.registries.*;
import net.neoforged.neoforge.registries.callback.AddCallback;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.function.TriConsumer;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.dimdev.dimdoors.api.event.ChunkServedCallback;
import org.dimdev.dimdoors.compat.create.CreateCompat;
import org.dimdev.dimdoors.fluid.EternalFluid;
import org.dimdev.dimdoors.fluid.LeakFluid;
import org.dimdev.dimdoors.fluid.neoforge.ModFluidTypes;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.dimdev.dimdoors.network.packet.c2s.HitBlockWithItemC2SPacket;
import org.dimdev.dimdoors.network.packet.c2s.NetworkHandlerInitializedC2SPacket;
import org.dimdev.dimdoors.network.packet.s2c.*;
import org.dimdev.dimdoors.util.DataValue;
import org.dimdev.dimdoors.world.ModBiomeModifiers;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;
import java.util.function.*;

import static org.dimdev.dimdoors.DimensionalDoors.getSided;

@Mod(DimensionalDoors.MOD_ID)
public class DimensionalDoorsNeoForge extends NeoForgeSidedImpl<DimensionalDoorsNeoForge, DimensionalDoors> implements IDimensionalDoorsSided<DimensionalDoorsNeoForge> {
    public static final EnumProxy<RecipeBookType> TESSELLATING = new EnumProxy<>(RecipeBookType.class);

    public DimensionalDoorsNeoForge(IEventBus bus) {
        super(bus, DimensionalDoors.INSTANCE);

        registerRunnable(NeoForgeRegistries.Keys.FLUID_TYPES, ModFluidTypes::init);
        common.init(this);
    }

    @Override
    public String getModId() {
        return DimensionalDoors.MOD_ID;
    }

    @Override
    public Fluid createFlowingEternalFluid() {
        return new EternalFluid.Flowing() {
            @Override
            public FluidType getFluidType() {
                return ModFluidTypes.ETERNAL;
            }
        };
    }

    @Override
    public FlowingFluid createEternalFluid() {
        return new EternalFluid.Still() {
            @Override
            public FluidType getFluidType() {
                return ModFluidTypes.ETERNAL;
            }
        };
    }

    @Override
    public Fluid createFlowingLeakFluid() {
        return new LeakFluid.Flowing() {
            @Override
            public FluidType getFluidType() {
                return ModFluidTypes.LEAK;
            }
        };
    }

    @Override
    public FlowingFluid createLeakFluid() {
        return new LeakFluid.Still() {
            @Override
            public FluidType getFluidType() {
                return ModFluidTypes.LEAK;
            }
        };
    }

    @Override
    public RecipeBookType getTesselatingRecipeBookType() {
        return TESSELLATING.getValue();
    }

    @Override
    public void checkCompat() {
        IDimensionalDoorsSided.super.checkCompat();
        if(getSided().isModLoaded("create")) {
            CreateCompat.init();
        }
    }
}
