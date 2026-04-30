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
import net.minecraft.network.chat.Component;
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
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.Path;
import org.dimdev.dimdoors.api.util.SimpleTree;
import org.dimdev.dimdoors.block.RiftVariantProvider;
import org.dimdev.dimdoors.item.RiftSignatureItem;
import org.dimdev.dimdoors.pockets.PocketCreator;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.TemplateUtils;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.GlobalReference;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PocketCommand {
    private static final Logger LOGGER = LogManager.getLogger();

    // TODO: probably move somewhere else
//    public static final Map<UUID, CommandSourceStack> logSetting = new HashMap<>();

    public static ArgumentBuilder<CommandSourceStack, ?> placeOption(String name, Supplier<SimpleTree<String, ? extends PocketCreator>> mapSupplier, Function<ResourceLocation, PocketCreator> idFunction) {
        return literal(name).then(
                argument("id", ResourceLocationArgument.id())
                        .requires(CommandSourceStack::isPlayer)
                        .suggests((ctx, builder) -> getSuggestions(mapSupplier.get().keySet(), builder))
                        .executes(context -> placePocket(
                                context.getSource(),
                                ResourceLocationArgument.getId(context, "id"),
                                idFunction,
                                context.getSource().getPlayerOrException(),
                                null
                        ))
                        .then(argument("locator", EntityArgument.entity())
                                .executes(context -> placePocket(
                                        context.getSource(),
                                        ResourceLocationArgument.getId(context, "id"),
                                        idFunction,
                                        EntityArgument.getEntity(context, "locator"),
                                        null
                                ))
                        )
                        .then(argument("source_pos", BlockPosArgument.blockPos())
                                .executes(context -> placePocket(
                                        context.getSource(),
                                        ResourceLocationArgument.getId(context, "id"),
                                        idFunction,
                                        null,
                                        BlockPosArgument.getLoadedBlockPos(context, "source_pos")
                                ))
                        ));
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("pocket")
                        .requires(source -> source.hasPermission(2))
                        .then(placeOption("virtual_pocket", PocketLoader::getVirtualPockets, PocketLoader::getVirtual))
                        .then(placeOption("pocket_group", PocketLoader::getPocketGroups, PocketLoader::getGroup))
                        .then(placeOption("pocket_generator", PocketLoader::getPocketGenerators, PocketLoader::getGenerator)
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
                                                            }).thenRun(() -> {
                                                                ctx.getSource().getServer().execute(() -> {
                                                                    ctx.getSource().sendSuccess(() -> Component.literal("Dumped pocket data"), false);
                                                                });
                                                            });
                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                        )
                        ));
    }

    private static int placePocket(CommandSourceStack source, ResourceLocation id, Function<ResourceLocation, PocketCreator> idFunction, @Nullable Entity locatorEntity, @Nullable BlockPos selectedSourcePos) throws CommandSyntaxException {
        PocketCreator creator = idFunction.apply(id);
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
        } else if (locatorEntity != null) {
            sourceLevel = (ServerLevel) locatorEntity.level();
            sourcePos = normalizeSourcePos(sourceLevel, locatorEntity.blockPosition());
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

        Location contextLocation = new Location(sourceLevel, sourcePos);
        PocketGenerationContext pocketGenerationContext = new PocketGenerationContext(
                pocketLevel,
                VirtualLocation.fromLocation(contextLocation),
                new GlobalReference(contextLocation),
                LinkProperties.NONE,
                pocketLevel.registryAccess()
        );

        Pocket pocket;
        try {
            pocket = creator.prepareAndPlacePocket(pocketGenerationContext);
        } catch (RuntimeException e) {
            LOGGER.error("Failed to generate pocket {} via command.", id, e);
            source.sendFailure(Component.literal("Failed to generate pocket " + id + ". Check the server log."));
            return 0;
        }

        if (pocket == null) {
            source.sendFailure(Component.literal("Pocket generation returned no pocket for " + id + "."));
            return 0;
        }

        Location entrance = DimensionalRegistry.getRiftRegistry().getPocketEntrance(pocket);
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

    public static CompletableFuture<Suggestions> getSuggestions(Set<Path<String>> paths, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(paths.stream().flatMap(path -> path.reduce(String::concat).stream()), builder);
    }
}
