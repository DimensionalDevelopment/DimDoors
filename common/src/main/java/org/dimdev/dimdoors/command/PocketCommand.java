package org.dimdev.dimdoors.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.*;
import org.dimdev.dimdoors.item.RiftSignatureItem;
import org.dimdev.dimdoors.pockets.*;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.GlobalReference;
import org.dimdev.dimdoors.util.schematic.SchematicPlacer;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PocketCommand {
	private static final Logger LOGGER = LogManager.getLogger();

	// TODO: probably move somewhere else
	public static final Map<UUID, CommandSourceStack> logSetting = new HashMap<>();

    public static ArgumentBuilder<CommandSourceStack, ?> placeOption(String name, Supplier<SimpleTree<String, ? extends PocketCreator>> mapSupplier, Function<ResourceLocation, PocketCreator> idFunction) {
        BiFunction<ResourceLocation, PocketGenerationContext, @Nullable Pocket> function = (resourceLocation, context) -> {
            var t = idFunction.apply(resourceLocation);
            if(t != null) return t.prepareAndPlacePocket(context);
            return null;
        };

        return literal(name).then(
                argument("id", ResourceLocationArgument.id())
                        .requires(CommandSourceStack::isPlayer)
                        .suggests((ctx, builder) -> getSuggestions(mapSupplier.get().keySet(), builder))
                        .executes(new Command<CommandSourceStack>() {
                    @Override
                    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
                        var id = ResourceLocationArgument.getId(context, "id");

                        var player = context.getSource().getPlayerOrException();
                        var level = player.serverLevel();
                        var pos = player.blockPosition();

                        var location = new Location(level, pos);

                        var rift = RiftSignatureItem.getOrCreateRift(level, pos);

                        if(rift.isEmpty()) return 0;

                        var pocketGenerationContext = new PocketGenerationContext(level, VirtualLocation.fromLocation(location), new GlobalReference(location), LinkProperties.NONE, level.registryAccess());

                        var pocket = function.apply(id, pocketGenerationContext);

                        if(pocket == null) return 0;

                        TemplateUtils.linkRifts(location, DimensionalRegistry.getRiftRegistry().getPocketEntrance(pocket));

                        return 1;
                    }
                }));
    }

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				literal("pocket")
						.requires(source -> source.hasPermission(2))
                        .then(placeOption("virtual_pocket", PocketLoader::getVirtualPockets, PocketLoader::getVirtual))
                        .then(placeOption("pocket_group", PocketLoader::getPocketGroups, PocketLoader::getGroup))
                        .then(placeOption("pocket_generator", PocketLoader::getPocketGenerators, PocketLoader::getGenerator)

//                        .then(
//                                literal("place")
//                                        .then(placeOption("virtualPockets", () -> PocketLoader.getVirtualPockets(), (id) -> PocketLoader.getVirtual(id));
//                                                argument("virtualPockets", ResourceLocationArgument.id())
//                                                        .suggests((commandContext, suggestionsBuilder) -> {
//                                                            return
//                                                        }).then()
//                                        )
//                                        .then(
//                                                argument("pocketGroups", ResourceLocationArgument.id())
//                                                        .suggests((commandContext, suggestionsBuilder) -> {
//                                                            return SuggestionsBuilder
//                                                        }).then()
//                                        )
//                                        .then(
//                                                argument("pocketGenerators", ResourceLocationArgument.id())
//                                                        .suggests((commandContext, suggestionsBuilder) -> {
//                                                            return SuggestionsBuilder
//                                                        }).then()
//                                        )
//                        )
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

    private static int load(CommandSourceStack source, PocketTemplate template) throws CommandSyntaxException {
		try {
			return WorldeditHelper.load(source, template);
		} catch (NoClassDefFoundError e) {
			return 0;
		}
	}

    public static CompletableFuture<Suggestions> getSuggestions(Set<Path<String>> paths, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(paths.stream().flatMap(path -> path.reduce(String::concat).stream()), builder);
    }


    private static int place(ServerPlayer source, PocketTemplate template, BlockPlacementType blockPlacementType) throws CommandSyntaxException {
		SchematicPlacer.place(
				template.getSchematic(),
				source.serverLevel(),
				source.blockPosition()
        );

		String id = template.getId().toString();
		source.displayClientMessage(Component.translatable("commands.pocket.placedSchem", id, "" + source.blockPosition().getX() + ", " + source.blockPosition().getY() + ", " + source.blockPosition().getZ(), source.level().dimension().location().toString()), true);
		return Command.SINGLE_SUCCESS;
	}
}
