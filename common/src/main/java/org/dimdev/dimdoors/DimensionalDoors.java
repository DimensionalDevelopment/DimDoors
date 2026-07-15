package org.dimdev.dimdoors;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback;
import org.dimdev.dimdoors.api.util.LocationCondition.LocationConditionType;
import org.dimdev.dimdoors.api.util.LocationValue.LocationValueWithType;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.client.IDimDoorsClientSided;
import org.dimdev.dimdoors.client.ModRecipeBookTypes;
import org.dimdev.dimdoors.command.ModCommands;
import org.dimdev.dimdoors.compat.sable.SableCompat;
import org.dimdev.dimdoors.criteria.ModCriteria;
import org.dimdev.dimdoors.enchantment.ModEnchantmentEffects;
import org.dimdev.dimdoors.enchantment.TranscendentProjectiles;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.entity.stat.ModStats;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.item.ModArmorMaterials;
import org.dimdev.dimdoors.item.ModDataComponentTypes;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar;
import org.dimdev.dimdoors.item.door.DoorRiftDataLoader;
import org.dimdev.dimdoors.item.door.data.condition.Condition;
import org.dimdev.dimdoors.item.loot.ModItemLootConditions;
import org.dimdev.dimdoors.listener.AttackBlockCallbackListener;
import org.dimdev.dimdoors.listener.UseDoorItemOnBlockCallbackListener;
import org.dimdev.dimdoors.listener.pocket.*;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.dimdev.dimdoors.network.packet.c2s.HitBlockWithItemC2SPacket;
import org.dimdev.dimdoors.network.packet.c2s.NetworkHandlerInitializedC2SPacket;
import org.dimdev.dimdoors.network.packet.s2c.*;
import org.dimdev.dimdoors.particle.ModParticleTypes;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.recipe.ModRecipeSerializers;
import org.dimdev.dimdoors.recipe.ModRecipeTypes;
import org.dimdev.dimdoors.rift.registry.RegistryVertex;
import org.dimdev.dimdoors.rift.targets.Targets;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.screen.ModScreenHandlerTypes;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.util.schematic.SchemFixer;
import org.dimdev.dimdoors.world.ModBiomes;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.ModStructureProccessors;
import org.dimdev.dimdoors.world.carvers.ModCarvers;
import org.dimdev.dimdoors.world.decay.Decay;
import org.dimdev.dimdoors.world.decay.conditions.DecayConditionType;
import org.dimdev.dimdoors.world.decay.pattern.DecayPatternType;
import org.dimdev.dimdoors.world.decay.results.DecayResultType;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.BlankChunkGenerator;
import org.dimdev.dimdoors.world.pocket.PocketChunkClaims;
import org.dimdev.dimdoors.world.pocket.PocketChunkLoadingManager;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import static org.dimdev.dimdoors.block.door.WaterLoggableDoorBlock.WATERLOGGED;

public class DimensionalDoors implements ModCommon<IDimensionalDoorsSided<?>> {
    public static final DimensionalDoors INSTANCE = new DimensionalDoors();

    public static final String MOD_ID = "dimdoors";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static DimensionalDoorItemRegistrar dimensionalDoorItemRegistrar;
    private static DimensionalDoorBlockRegistrar dimensionalDoorBlockRegistrar;
    private static IDimensionalDoorsSided<?> sided;

    public static ResourceLocation id(String id) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
    }

    public static MinecraftServer getServer() {
        return sided.getServer();
    }

    public static ServerLevel getWorld(ResourceKey<Level> world) {
        return getServer().getLevel(world);
    }

    private static ModConfig config;

    public static ModConfig getConfig() {
        return config;
    }

    public static void setConfig(ModConfig config) {
        DimensionalDoors.config = config;
    }

    public static void saveConfig() {
        config.save(sided.getConfigRoot());
    }

    public static void reloadConfig() {
        config = ModConfig.load(sided.getConfigRoot());
    }

    public void init(IDimensionalDoorsSided<?> sided) {
        DimensionalDoors.sided = sided;

        reloadConfig();

        registerRegistries();

        ModRecipeBookTypes.init();

        registerRun(Registries.DATA_COMPONENT_TYPE, () -> ModDataComponentTypes.register());
        registerRun(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, () -> ModEnchantmentEffects.init());
        registerRun(Registries.LOOT_CONDITION_TYPE, () -> ModItemLootConditions.init());

        registerRun(Registries.CHUNK_GENERATOR, () -> sided.register(Registries.CHUNK_GENERATOR, "blank", BlankChunkGenerator.CODEC));
        registerRun(Registries.RECIPE_TYPE, () -> ModRecipeTypes.init());
        registerRun(Registries.RECIPE_SERIALIZER, () -> ModRecipeSerializers.init());
        registerRun(Registries.MENU, () -> ModScreenHandlerTypes.init());
        registerRun(Registries.SOUND_EVENT, () -> ModSoundEvents.init());
        registerRun(Registries.FLUID, () -> ModFluids.init());
        registerRun(Registries.ENTITY_TYPE, () -> ModEntityTypes.init());
        registerRun(Registries.ARMOR_MATERIAL, () -> ModArmorMaterials.init());
        registerRun(Registries.BLOCK, () -> ModBlocks.init());
        registerRun(Registries.ITEM, () -> ModItems.init());
        registerRun(Registries.BLOCK_ENTITY_TYPE, () -> ModBlockEntityTypes.init());
        registerRun(Registries.CARVER, () -> ModCarvers.init());
        registerRun(Registries.BIOME, () -> ModBiomes.init());
        registerRun(Registries.CUSTOM_STAT, () -> ModStats.init());
        registerRun(Registries.PARTICLE_TYPE, () -> ModParticleTypes.init());
        registerRun(Registries.TRIGGER_TYPE, () -> ModCriteria.init());
        registerRun(Registries.STRUCTURE_PROCESSOR, () -> ModStructureProccessors.init());
//        registerRunDataValue(() -> Fray.init()); TODO: Finish Fray
        registerRunDataValue(() -> PocketChunkClaims.init());
        registerRunDataValue(() -> TranscendentProjectiles.init());


        ModCommands.init();
        ModDimensions.init(sided);
        dimensionalDoorItemRegistrar = new DimensionalDoorItemRegistrar(sided);
        dimensionalDoorBlockRegistrar = new DimensionalDoorBlockRegistrar(sided, dimensionalDoorItemRegistrar);
        sided.checkCompat();
        registerRun(Registries.BLOCK, dimensionalDoorBlockRegistrar::init);
        registerRun(Registries.ITEM, () -> {
            dimensionalDoorItemRegistrar.init();
            sided.modify(ModItems.DIMENSIONAL_DOORS, (flags, output, canUseGameMasterBlocks) -> {
                for (Item item : dimensionalDoorItemRegistrar.getAutogeneratedItems()) {
                    output.acceptAfter(
                            ModBlocks.REALITY_SPONGE.asItem().getDefaultInstance(),
                            item.getDefaultInstance()
                    );
                }
            });
        });

//        ModRecipeBookTypes.init();

        sided.registerServerLoader("pocket_loader", PocketLoader::reload);
        sided.registerServerLoader("decay_loader", Decay.DecayLoader::reload, true);
//        sided.registerServerLoader("door_data_loader", DoorRiftDataLoader::reload);

        sided.registerClientPacket(PlayerInventorySlotUpdateS2CPacket.TYPE, PlayerInventorySlotUpdateS2CPacket.STREAM_CODEC, (packet) -> ClientPacketListener.onPlayerInventorySlotUpdate(packet));
        sided.registerClientPacket(SyncPocketAddonsS2CPacket.TYPE, SyncPocketAddonsS2CPacket.STREAM_CODEC, (packet) -> ClientPacketListener.onSyncPocketAddons(packet));
        sided.registerClientPacket(MonolithAggroParticlesPacket.TYPE, MonolithAggroParticlesPacket.STREAM_CODEC, (packet) -> ClientPacketListener.onMonolithAggroParticles(packet));
        sided.registerClientPacket(MonolithTeleportParticlesPacket.TYPE, MonolithTeleportParticlesPacket.STREAM_CODEC, (packet) -> ClientPacketListener.onMonolithTeleportParticles(packet));
        sided.registerClientPacket(RenderBreakBlockS2CPacket.TYPE, RenderBreakBlockS2CPacket.STREAM_CODEC, (packet) -> ClientPacketListener.onRenderBreakBlock(packet));
        sided.registerServerPacket(HitBlockWithItemC2SPacket.TYPE, HitBlockWithItemC2SPacket.STREAM_CODEC, (packet, player) -> ServerPacketHandler.onAttackBlock(player, packet));
        sided.registerServerPacket(NetworkHandlerInitializedC2SPacket.TYPE, NetworkHandlerInitializedC2SPacket.STREAM_CODEC, (packet, player) -> ServerPacketHandler.onNetworkHandlerInitialized(player));
        sided.onServerStarting(Decay.DecayLoader::populate);

        sided.addPack(PackType.SERVER_DATA, "default", "Default", true);
        sided.addPack(PackType.SERVER_DATA, "classic", "Classic", true);

        registerListeners();
//        SchemFixer.run();
    }

    private static void registerRun(ResourceKey<? extends Registry<?>> key, Runnable runnable) {
        sided.registerRunnable(key, runnable);
    }

    private static void registerRunDataValue(Runnable runnable) {
        sided.registerRunDataValue(runnable);
    }

    public static void registerRegistries() {
        Targets.registerDefaultTargets();
        VirtualTarget.VirtualTargetType.register();
        ImplementedVirtualPocket.VirtualPocketType.register();
        RegistryVertex.RegistryVertexType.register();
        Modifier.ModifierType.register();
        PocketGenerator.PocketGeneratorType.register();
        AbstractPocket.AbstractPocketType.register();
        PocketAddon.PocketAddonType.register();
        Condition.ConditionType.register();
        DecayConditionType.register();
        DecayResultType.register();
        DecayPatternType.register();
        LocationConditionType.register();
        LocationValueWithType.register();

        ModRegistries.register();
    }

    private static void registerListeners() {
//        sided.onPlayerQuit(player -> PocketCommand.logSetting.remove(player.getUUID()));

        sided.onServerStarted(server -> {
            DimensionalRegistry.init(server);
            PocketChunkLoadingManager.reconcileAll(server);
        });

        sided.onAttackBlock(new AttackBlockCallbackListener());
        sided.onAttackBlock(new PocketAttackBlockCallbackListener());

        sided.onBeforeBlockBreak(new PlayerBlockBreakEventBeforeListener());

        sided.onUseItem(new UseItemCallbackListener());
        UseItemOnBlockCallback.EVENT.register(new UseItemOnBlockCallbackListener());
        sided.onUseBlock(new UseBlockCallbackListener());
        sided.onBeforeBlockPlace((level, pos, state, placer) -> shouldCancelBlockModification(level, pos, placer));

        // placing doors on rifts
        UseItemOnBlockCallback.EVENT.register(new UseDoorItemOnBlockCallbackListener());

        sided.onServerLevelTick(Decay::tick);
    }

    private static boolean shouldCancelBlockModification(Level level, BlockPos pos, Entity actor) {
        return !(actor instanceof Player player && player.isCreative())
                && PocketListenerUtil.getAddon(PocketAddon.PocketAddonType.PREVENT_BLOCK_MODIFICATION_ADDON, level, pos).isPresent();
    }

    public static DimensionalDoorItemRegistrar getDimensionalDoorItemRegistrar() {
        return dimensionalDoorItemRegistrar;
    }

    public static DimensionalDoorBlockRegistrar getDimensionalDoorBlockRegistrar() {
        return dimensionalDoorBlockRegistrar;
    }

    public static void afterBlockBreak(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        if (player.isCreative() && !DimensionalDoors.getConfig().getDoorsConfig().placeRiftsInCreativeMode) {
            return;
        }

        if (blockEntity instanceof EntranceRiftBlockEntity riftBlockEntity) {
            if (state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
                pos = pos.below();
            }

            var detachedState = ModBlocks.DETACHED_RIFT.defaultBlockState();
            if(state.hasProperty(WATERLOGGED)) detachedState = detachedState.setValue(WATERLOGGED, state.getValue(WATERLOGGED));

            world.setBlockAndUpdate(pos, detachedState);
            world.getBlockEntity(pos, ModBlockEntityTypes.DETACHED_RIFT).ifPresent(rift -> rift.setData((riftBlockEntity).getData()));
        }
    }

    public static IDimensionalDoorsSided<?> getSided() {
        return sided;
    }
}
