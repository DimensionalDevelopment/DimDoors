package org.dimdev.dimdoors;

import com.chocohead.mm.api.ClassTinkerers;
import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.impl.content.registry.util.ImmutableCollectionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.inventory.RecipeBookType;
import org.dimdev.dimdoors.api.event.ChunkServedCallback;
import org.dimdev.dimdoors.mixin.RecipeBookSettingsAccessor;
import org.dimdev.limlib.FabricSided;


import java.util.function.*;

import static org.dimdev.dimdoors.DimensionalDoors.MOD_ID;

public class DimensionalDoorsFabric extends FabricSided<DimensionalDoorsFabric, DimensionalDoors> implements IDimensionalDoorsSided<DimensionalDoorsFabric> {
    private final Supplier<RecipeBookType> TESSELLATING = Suppliers.memoize(() -> {
        var type = ClassTinkerers.getEnum(RecipeBookType.class, "TESSELLATING");
        ImmutableCollectionUtils.getAsMutableMap(RecipeBookSettingsAccessor::getTagFields, RecipeBookSettingsAccessor::setTagFields)
                .putIfAbsent(type, Pair.of("isTessellatingGui", "isTessellatingFilteringCraftable"));
            return type;

    });

    public DimensionalDoorsFabric() {
        super(DimensionalDoors.INSTANCE);
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        getTesselatingRecipeBookType();

        PlayerBlockBreakEvents.AFTER.register(DimensionalDoors::afterBlockBreak);
        ServerChunkEvents.CHUNK_LOAD.register((serverLevel, levelChunk) -> ChunkServedCallback.EVENT.invoker().onChunkServed(serverLevel, levelChunk));

    }

    @Override
    public RecipeBookType getTesselatingRecipeBookType() {
        return TESSELLATING.get();
    }

    @Override
    public void onServerStopped(Consumer<MinecraftServer> consumer) {
        ServerLifecycleEvents.SERVER_STOPPED.register(consumer::accept);
    }

    @Override
    public void onServerStopping(Consumer<MinecraftServer> consumer) {
        ServerLifecycleEvents.SERVER_STOPPING.register(consumer::accept);
    }
}
