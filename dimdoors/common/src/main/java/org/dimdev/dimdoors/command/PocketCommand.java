package org.dimdev.dimdoors.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.ModRegistryKeys;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.block.RiftVariantProvider;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.item.RiftSignatureItem;
import org.dimdev.dimdoors.pockets.PocketCreator;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.TemplateUtils;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.registry.PocketRegistry;
import org.dimdev.dimdoors.rift.targets.RiftReference;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PocketCommand {
    private static final Logger LOGGER = LogManager.getLogger();

    // TODO: probably move somewhere else
//    public static final Map<UUID, CommandSourceStack> logSetting = new HashMap<>();

    public static <T extends PocketCreator> ArgumentBuilder<CommandSourceStack, ?> placeOption(String name, ResourceKey<Registry<T>> resourceKey) {
        return literal(name).then(
                argument("id", ResourceLocationArgument.id())
                        .requires(CommandSourceStack::isPlayer)
                        .suggests((ctx, builder) -> getSuggestions(ctx.getSource().registryAccess(), resourceKey, builder))
                        .executes(context -> placePocket(
                                context.getSource(),
                                ResourceLocationArgument.getId(context, "id"),
                                resourceKey,
                                null,
                                null
                        ))
                        .then(argument("target", EntityArgument.entity())
                                .executes(context -> placePocket(
                                        context.getSource(),
                                        ResourceLocationArgument.getId(context, "id"),
                                        resourceKey,
                                        EntityArgument.getEntity(context, "target"),
                                        null
                                ))
                        )
                        .then(argument("source_pos", BlockPosArgument.blockPos())
                                .executes(context -> placePocket(
                                        context.getSource(),
                                        ResourceLocationArgument.getId(context, "id"),
                                        resourceKey,
                                        null,
                                        BlockPosArgument.getLoadedBlockPos(context, "source_pos")
                                ))
                        ));
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("pocket")
                        .requires(source -> source.hasPermission(2))
                        .then(placeOption("virtual_pocket", ModRegistryKeys.VIRTUAL_POCKET))
                        .then(placeOption("pocket_group", ModRegistryKeys.POCKET_GROUPS))
                        .then(placeOption("pocket_generator", ModRegistryKeys.POCKET_GENERATOR)
                                .then(
                                                literal("dump")
                                                        .requires(src -> src.hasPermission(4))
                                                        .executes(ctx -> {
                                                            ctx.getSource().sendSuccess(() -> Component.literal("Dumping pocket data"), false);
                                                            CompletableFuture.runAsync(() -> {
                                                                try {
                                                                    PocketLoader.dump();
                                                                } catch (Exception e) {
                                                                    LOGGER.error("Error dumping pocket data", e);
                                                                }
                                                            }).thenRun(() -> ctx.getSource().getServer().execute(() -> ctx.getSource().sendSuccess(() -> Component.literal("Dumped pocket data"), false)));
                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                        )
                        ));
    }

    private static <T extends PocketCreator> int placePocket(CommandSourceStack source, ResourceLocation id, ResourceKey<Registry<T>> idFunction, @Nullable Entity targetEntity, @Nullable BlockPos selectedSourcePos) throws CommandSyntaxException {
        PocketCreator creator = source.registryAccess().registry(idFunction).map(a -> a.get(id)).orElse(null);
        if (creator == null) {
            source.sendFailure(Component.literal("Unknown pocket id: " + id));
            return 0;
        }

        ServerPlayer player = source.getPlayerOrException();
        ServerLevel sourceLevel;
        BlockPos sourcePos;

        if (selectedSourcePos != null) {
            sourceLevel = player.serverLevel();
            sourcePos = normalizeSourcePos(sourceLevel, selectedSourcePos);
        } else if (targetEntity != null) {
            sourceLevel = (ServerLevel) targetEntity.level();
            sourcePos = normalizeSourcePos(sourceLevel, targetEntity.blockPosition());
        } else {
            sourceLevel = player.serverLevel();
            sourcePos = normalizeSourcePos(sourceLevel, player.blockPosition());
        }

        BlockState sourceState = sourceLevel.getBlockState(sourcePos);
        if (!canUseSource(player, sourcePos, sourceState)) {
            source.sendFailure(Component.literal("Source position must be a raw rift, a door/trapdoor/portal that can host a rift, or replaceable space."));
            return 0;
        }

        ServerLevel pocketLevel = sourceLevel.getServer().getLevel(ModDimensions.DUNGEON);
        if (pocketLevel == null) {
            source.sendFailure(Component.literal("Could not resolve the dungeon pocket world."));
            return 0;
        }

        Location contextLocation = Location.ofWorld(sourceLevel, sourcePos);
        PocketGenerationContext pocketGenerationContext = new PocketGenerationContext(
                pocketLevel,
                VirtualLocation.fromLocation(contextLocation),
                new RiftReference(contextLocation),
                LinkProperties.NONE,
                pocketLevel.registryAccess()
        );

        Pocket<?, ?> pocket;
        try {
            pocket = PocketCreator.create(creator, pocketGenerationContext);
        } catch (RuntimeException e) {
            LOGGER.error("Failed to generate pocket {} via command.", id, e);
            source.sendFailure(Component.literal("Failed to generate pocket " + id + ". Check the server log."));
            return 0;
        }

        if (pocket == null) {
            source.sendFailure(Component.literal("Pocket generation returned no pocket for " + id + "."));
            return 0;
        }

        Location entrance = PocketRegistry.getInstance().getPocketEntrance(pocket);
        if (entrance == null) {
            source.sendFailure(Component.literal("Pocket " + id + " generated without a registered entrance."));
            return 0;
        }

        BlockPos linkedSourcePos = sourcePos;
        var rift = RiftSignatureItem.getOrCreateRift(sourceLevel, linkedSourcePos);
        if (rift.isEmpty()) {
            source.sendFailure(Component.literal("Could not create or convert a rift at " + linkedSourcePos.toShortString() + "."));
            return 0;
        }

        TemplateUtils.linkRifts(contextLocation, entrance);
        if (targetEntity != null
            && !((EntranceRiftBlockEntity) contextLocation.getBlockEntity()).teleport(targetEntity)) { // This line does not feel safe but theoretically any block entity errors would happen inside linkRifts
            source.sendFailure(Component.literal("Failed to teleport entity through created rift."));
            return 0;
        }

        source.sendSuccess(() -> Component.literal(
                "Linked " + linkedSourcePos.toShortString()
                        + " to " + id
                        + " in " + pocketLevel.dimension().location()
                        + " at " + entrance.getBlockPos().toShortString()
        ), false);
        return Command.SINGLE_SUCCESS;
    }

    private static BlockPos normalizeSourcePos(ServerLevel level, BlockPos sourcePos) {
        BlockState sourceState = level.getBlockState(sourcePos);
        if (sourceState.hasProperty(DoorBlock.HALF) && sourceState.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            return sourcePos.below();
        }
        return sourcePos;
    }

    private static boolean canUseSource(ServerPlayer player, BlockPos sourcePos, BlockState sourceState) {
        return (sourceState.canBeReplaced() || sourceState.getBlock() instanceof RiftVariantProvider)
                && player.mayUseItemAt(sourcePos, Direction.UP, ItemStack.EMPTY);
    }

    public static <T extends PocketCreator> CompletableFuture<Suggestions> getSuggestions(RegistryAccess access, ResourceKey<Registry<T>> resourceKey, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(access.registry(resourceKey).map(Registry::keySet).stream().flatMap(Collection::stream).map(ResourceLocation::toString), builder);
    }
}
