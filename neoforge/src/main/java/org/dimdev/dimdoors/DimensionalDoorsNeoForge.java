package org.dimdev.dimdoors;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.*;
import org.dimdev.dimdoors.api.event.ChunkServedCallback;
import org.dimdev.dimdoors.compat.create.CreateCompat;
import org.dimdev.dimdoors.fluid.EternalFluid;
import org.dimdev.dimdoors.fluid.LeakFluid;
import org.dimdev.dimdoors.fluid.ModFluidTypes;
import org.dimdev.dimdoors.world.ModBiomeModifiers;
import org.dimdev.dimcore.NeoForgeSided;

import java.util.function.Consumer;

import static org.dimdev.dimdoors.DimensionalDoors.getSided;

@Mod(DimensionalDoors.MOD_ID)
public class DimensionalDoorsNeoForge extends NeoForgeSided<DimensionalDoorsNeoForge, DimensionalDoors> implements IDimensionalDoorsSided<DimensionalDoorsNeoForge> {
    public static final EnumProxy<RecipeBookType> TESSELLATING = new EnumProxy<>(RecipeBookType.class);

    public DimensionalDoorsNeoForge(IEventBus bus) {
        super(bus, DimensionalDoors.INSTANCE);

        registerRunnable(NeoForgeRegistries.Keys.FLUID_TYPES, ModFluidTypes::init);

        ModBiomeModifiers.init(bus);

        NeoForge.EVENT_BUS.<ChunkEvent.Load>addListener(load -> {
            if (!load.isNewChunk() && load.getLevel() instanceof ServerLevel level && load.getChunk() instanceof LevelChunk chunk)
                ChunkServedCallback.EVENT.invoker().onChunkServed(level, chunk);
        });
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
    public GameRules.Key<GameRules.BooleanValue> registerGameRule(String name, GameRules.Category category, boolean value) {
        return GameRules.register(name, category, GameRules.BooleanValue.create(value));
    }

    @Override
    public GameRules.Key<GameRules.IntegerValue> registerGameRule(String name, GameRules.Category category, int value) {
        return GameRules.register(name, category, GameRules.IntegerValue.create(value));
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

    @Override
    public void onServerStopped(Consumer<MinecraftServer> consumer) {
        NeoForge.EVENT_BUS.<ServerStoppedEvent>addListener(event -> consumer.accept(event.getServer()));
    }

    @Override
    public void onServerStopping(Consumer<MinecraftServer> consumer) {
        NeoForge.EVENT_BUS.<ServerStoppedEvent>addListener(event -> consumer.accept(event.getServer()));
    }
}
