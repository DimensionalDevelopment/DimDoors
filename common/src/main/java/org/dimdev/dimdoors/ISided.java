package org.dimdev.dimdoors;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.Codec;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import org.dimdev.dimdoors.fluid.EternalFluid;
import org.dimdev.dimdoors.fluid.LeakFluid;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;

import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ISided extends IRegister, ICreativeTabHandler, INetworking {
    default Fluid createFlowingEternalFluid() {
        return new EternalFluid.Flowing();
    }

    default FlowingFluid createEternalFluid() {
        return new EternalFluid.Still();
    }

    default Fluid createFlowingLeakFluid() {
        return new LeakFluid.Flowing();
    }

    default FlowingFluid createLeakFluid() {
        return new LeakFluid.Still();
    }

    void onServerStarting(Consumer<MinecraftServer> consumer);
    void onServerStarted(Consumer<MinecraftServer> consumer);
    MinecraftServer getServer();
    void onPlayerQuit(Consumer<ServerPlayer> consumer);
    void onServerLevelTick(Consumer<ServerLevel> consumer);
    void onAttackBlock(AttackBlockCallback callback);
    void onUseItem(UseItemCallback callback);
    void onUseBlock(UseBlockCallback callback);
    void onBeforeBlockBreak(BlockBreakCallback callback);
    void onBeforeBlockPlace(BlockPlaceCallback callback);
    void registerEntityAttributes(EntityType<? extends LivingEntity> type, Supplier<AttributeSupplier.Builder> attributes);

    Path getConfigRoot();
    void initBuiltinPacks();

    public void registerServerLoader(String name, BiConsumer<HolderLookup.Provider, ResourceManager> consumer, boolean loadAfterTags);

    default public void registerServerLoader(String pocketLoader, BiConsumer<HolderLookup.Provider, ResourceManager> consumer) {
        registerServerLoader(pocketLoader, consumer, false);
    }

    public RecipeBookType getTesselatingRecipeBookType();

    boolean isModLoaded(String id);

    boolean isClient();

    long bucketAmount();

    void registerCommands(Consumer<CommandDispatcher<CommandSourceStack>> consumer);

    public <T> void createDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> codec);

    @FunctionalInterface
    interface AttackBlockCallback {
        InteractionResult attack(Player player, InteractionHand hand, BlockPos pos, Direction direction);
    }

    @FunctionalInterface
    interface UseItemCallback {
        InteractionResult use(Player player, InteractionHand hand);
    }

    @FunctionalInterface
    interface UseBlockCallback {
        InteractionResult use(Player player, InteractionHand hand, BlockHitResult hitResult);
    }

    @FunctionalInterface
    interface BlockBreakCallback {
        boolean shouldCancel(Level level, BlockPos pos, BlockState state, Player player);
    }

    @FunctionalInterface
    interface BlockPlaceCallback {
        boolean shouldCancel(Level level, BlockPos pos, BlockState state, Entity placer);
    }
}
