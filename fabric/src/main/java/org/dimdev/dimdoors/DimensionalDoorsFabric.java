package org.dimdev.dimdoors;

import com.chocohead.mm.api.ClassTinkerers;
import com.google.common.base.Suppliers;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.impl.content.registry.util.ImmutableCollectionUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.function.TriConsumer;
import org.dimdev.dimdoors.api.event.ChunkServedCallback;
import org.dimdev.dimdoors.fabric.mixin.RecipeBookSettingsAccessor;
import org.dimdev.dimdoors.network.packet.s2c.*;
import org.dimdev.dimdoors.util.DataValue;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.*;

import static org.dimdev.dimdoors.DimensionalDoors.MOD_ID;
import static org.dimdev.dimdoors.DimensionalDoors.id;

public class DimensionalDoorsFabric extends FabricSidedImpl<DimensionalDoorsFabric, DimensionalDoors> implements IDimensionalDoorsSided<DimensionalDoorsFabric> {
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
    }

    @Override
    public RecipeBookType getTesselatingRecipeBookType() {
        return TESSELLATING.get();
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }
}
