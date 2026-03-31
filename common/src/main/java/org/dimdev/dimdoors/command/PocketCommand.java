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
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.ModRegistries;
import org.dimdev.dimdoors.api.util.*;
import org.dimdev.dimdoors.api.util.function.TriFunction;
import org.dimdev.dimdoors.item.RiftSignatureItem;
import org.dimdev.dimdoors.pockets.*;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.GlobalReference;
import org.dimdev.dimdoors.util.schematic.SchematicPlacer;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PocketCommand {
	private static final Logger LOGGER = LogManager.getLogger();

	// TODO: probably move somewhere else
	public static final Map<UUID, CommandSourceStack> logSetting = new HashMap<>();

    public static <T extends PocketCreator>  ArgumentBuilder<CommandSourceStack, ?> placeOption(String name, ResourceKey<Registry<T>> mapSupplier) {
        Function<CommandSourceStack, Registry<T>> registry = ctx -> ctx.getServer().registryAccess().registryOrThrow(mapSupplier);

        TriFunction<CommandSourceStack, ResourceLocation, PocketGenerationContext, @Nullable Pocket> function = (ctx, resourceLocation, context) -> {
            var t = registry.apply(ctx).get(resourceLocation);
            if(t != null) return t.prepareAndPlacePocket(context);
            return null;
        };

        return literal(name).then(
                argument("id", ResourceLocationArgument.id())
                        .requires(CommandSourceStack::isPlayer)
                        .suggests((ctx, builder) -> getSuggestions(registry.apply(ctx.getSource()).keySet(), builder))
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

                        var pocket = function.apply(context.getSource(), id, pocketGenerationContext);

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
                        .then(placeOption("virtual_pocket", ModRegistries.VIRTUAL_POCKET))
                        .then(placeOption("pocket_group", ModRegistries.POCKET_GROUP))
                        .then(placeOption("pocket_generator", ModRegistries.POCKET_GENERATOR))
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
		);
	}

    private static int load(CommandSourceStack source, PocketTemplate template) throws CommandSyntaxException {
		try {
			return WorldeditHelper.load(source, template);
		} catch (NoClassDefFoundError e) {
			return 0;
		}
	}

    public static CompletableFuture<Suggestions> getSuggestions(Set<ResourceLocation> paths, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(paths, builder);
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
