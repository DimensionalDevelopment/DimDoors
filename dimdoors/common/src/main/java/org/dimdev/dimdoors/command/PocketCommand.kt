package org.dimdev.dimdoors.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.commands.arguments.coordinates.BlockPosArgument
import net.minecraft.commands.arguments.coordinates.Coordinates
import net.minecraft.commands.arguments.selector.EntitySelector
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.api.util.Location.Companion.ofWorld
import org.dimdev.dimdoors.block.RiftVariantProvider
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity
import org.dimdev.dimdoors.item.RiftSignatureItem
import org.dimdev.dimdoors.pockets.PocketCreator
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.pockets.PocketLoader
import org.dimdev.dimdoors.pockets.TemplateUtils
import org.dimdev.dimdoors.pockets.generator.PocketGenerator
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.rift.registry.LinkProperties
import org.dimdev.dimdoors.rift.registry.PocketRegistry.Companion.instance
import org.dimdev.dimdoors.rift.targets.RiftReference
import org.dimdev.dimdoors.world.ModDimensions
import org.dimdev.dimdoors.world.pocket.VirtualLocation.Companion.fromLocation
import org.dimdev.dimdoors.world.pocket.type.Pocket
import java.util.concurrent.CompletableFuture
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier
import kotlin.jvm.optionals.getOrNull

object PocketCommand {
    private val LOGGER: Logger = LogManager.getLogger()

    fun <T : PocketCreator> placeOption(
        name: String,
        resourceKey: ResourceKey<Registry<T>>
    ): ArgumentBuilder<CommandSourceStack?, *> {
        return Commands.literal(name).then(
            Commands.argument<ResourceLocation?>("id", ResourceLocationArgument.id())
                .requires { obj -> obj.isPlayer }
                .suggests { ctx, builder -> getSuggestions(ctx.source.registryAccess(), resourceKey, builder) }
                .execute { placePocket(source, ResourceLocationArgument.getId(this, "id"), resourceKey, null, null) }
                .then(
                    Commands.argument<EntitySelector?>("target", EntityArgument.entity())
                        .executes(Command { context: CommandContext<CommandSourceStack?>? ->
                            PocketCommand.placePocket<T?>(
                                context!!.getSource()!!,
                                ResourceLocationArgument.getId(context, "id"),
                                resourceKey,
                                EntityArgument.getEntity(context, "target"),
                                null
                            )
                        })
                )
                .then(
                    Commands.argument<Coordinates?>("source_pos", BlockPosArgument.blockPos())
                        .executes(Command { context: CommandContext<CommandSourceStack?>? ->
                            PocketCommand.placePocket<T?>(
                                context!!.getSource()!!,
                                ResourceLocationArgument.getId(context, "id"),
                                resourceKey,
                                null,
                                BlockPosArgument.getLoadedBlockPos(context, "source_pos")
                            )
                        })
                )
        )
    }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register("pocket") {
            requires(Predicate { source: CommandSourceStack? -> source!!.hasPermission(2) })
            then(PocketCommand.placeOption<VirtualPocket>("virtual_pocket", ModRegistryKeys.VIRTUAL_POCKET))
            then(PocketCommand.placeOption<VirtualPocket>("pocket_group", ModRegistryKeys.POCKET_GROUPS))
            then(PocketCommand.placeOption<PocketGenerator<*>>("pocket_generator", ModRegistryKeys.POCKET_GENERATOR))
            .then(Commands.literal("dump")
                .requires(Predicate { src: CommandSourceStack? -> src!!.hasPermission(4) })
                .executes(Command { ctx: CommandContext<CommandSourceStack?>? ->
                    ctx!!.getSource()!!
                        .sendSuccess(Supplier { Component.literal("Dumping pocket data") }, false)
                    CompletableFuture.runAsync(Runnable {
                        try {
                            PocketLoader.dump()
                        } catch (e: Exception) {
                            LOGGER.error("Error dumping pocket data", e)
                        }
                    }).thenRun(Runnable {
                                ctx.getSource()!!.getServer().execute(Runnable {
                                    ctx.getSource()!!
                                        .sendSuccess(Supplier { Component.literal("Dumped pocket data") }, false)
                                })
                            })
                            Command.SINGLE_SUCCESS
                        })
                )
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun <T : PocketCreator> placePocket(
        source: CommandSourceStack,
        id: ResourceLocation,
        idFunction: ResourceKey<Registry<T>>,
        targetEntity: Entity?,
        selectedSourcePos: BlockPos?
    ): Int {
        val creator = source.registryAccess().registry(idFunction).map<T>({ a-> a.get(id) }).getOrNull()

        if (creator == null) {
            source.sendFailure(Component.literal("Unknown pocket id: " + id))
            return 0
        }

        val player = source.playerOrException
        val sourceLevel: ServerLevel?
        val sourcePos: BlockPos

        when {
            selectedSourcePos != null -> {
                sourceLevel = player.serverLevel()
                sourcePos = normalizeSourcePos(sourceLevel, selectedSourcePos)
            }
            targetEntity != null -> {
                sourceLevel = targetEntity.level() as ServerLevel
                sourcePos = normalizeSourcePos(sourceLevel, targetEntity.blockPosition())
            }
            else -> {
                sourceLevel = player.serverLevel()
                sourcePos = normalizeSourcePos(sourceLevel, player.blockPosition())
            }
        }

        val sourceState = sourceLevel.getBlockState(sourcePos)
        if (!canUseSource(player, sourcePos, sourceState)) {
            source.sendFailure(Component.literal("Source position must be a raw rift, a door/trapdoor/portal that can host a rift, or replaceable space."))
            return 0
        }

        val pocketLevel = sourceLevel.getServer().getLevel(ModDimensions.DUNGEON)
        if (pocketLevel == null) {
            source.sendFailure(Component.literal("Could not resolve the dungeon pocket world."))
            return 0
        }

        val contextLocation = ofWorld(sourceLevel, sourcePos)
        val pocketGenerationContext = PocketGenerationContext(
            pocketLevel,
            fromLocation(contextLocation),
            RiftReference(contextLocation),
            LinkProperties.NONE,
            pocketLevel.registryAccess()
        )

        val pocket: Pocket<*, *>?
        try {
            pocket = PocketCreator.create(creator, pocketGenerationContext)
        } catch (e: RuntimeException) {
            LOGGER.error("Failed to generate pocket {} via command.", id, e)
            source.sendFailure(Component.literal("Failed to generate pocket " + id + ". Check the server log."))
            return 0
        }

        if (pocket == null) {
            source.sendFailure(Component.literal("Pocket generation returned no pocket for " + id + "."))
            return 0
        }

        val entrance = instance.getPocketEntrance(pocket)
        if (entrance == null) {
            source.sendFailure(Component.literal("Pocket " + id + " generated without a registered entrance."))
            return 0
        }

        val rift = RiftSignatureItem.getOrCreateRift(sourceLevel, sourcePos)
        if (rift.isEmpty()) {
            source.sendFailure(Component.literal("Could not create or convert a rift at " + sourcePos.toShortString() + "."))
            return 0
        }

        TemplateUtils.linkRifts(contextLocation, entrance)
        if (targetEntity != null
            && !(contextLocation.blockEntity as EntranceRiftBlockEntity<*>).teleport(targetEntity)
        ) { // This line does not feel safe but theoretically any block entity errors would happen inside linkRifts
            source.sendFailure(Component.literal("Failed to teleport entity through created rift."))
            return 0
        }

        source.sendSuccess(Supplier {
            Component.literal(
                ("Linked " + sourcePos.toShortString()
                        + " to " + id
                        + " in " + pocketLevel.dimension().location()
                        + " at " + entrance.blockPos.toShortString())
            )
        }, false)
        return Command.SINGLE_SUCCESS
    }

    private fun normalizeSourcePos(level: ServerLevel, sourcePos: BlockPos): BlockPos {
        val sourceState = level.getBlockState(sourcePos)
        if (sourceState.hasProperty<DoubleBlockHalf?>(DoorBlock.HALF) && sourceState.getValue<DoubleBlockHalf?>(
                DoorBlock.HALF
            ) == DoubleBlockHalf.UPPER
        ) {
            return sourcePos.below()
        }
        return sourcePos
    }

    private fun canUseSource(player: ServerPlayer, sourcePos: BlockPos, sourceState: BlockState): Boolean {
        return (sourceState.canBeReplaced() || sourceState.getBlock() is RiftVariantProvider)
                && player.mayUseItemAt(sourcePos, Direction.UP, ItemStack.EMPTY)
    }

    fun <T : PocketCreator?> getSuggestions(
        access: RegistryAccess,
        resourceKey: ResourceKey<Registry<T?>?>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions?> {
        return SharedSuggestionProvider.suggest(
            access.registry<T?>(resourceKey)
                .map<MutableSet<ResourceLocation?>?>(Function { obj: Registry<T?>? -> obj!!.keySet() }).stream()
                .flatMap<ResourceLocation?> { obj: MutableSet<ResourceLocation?>? -> obj!!.stream() }
                .map<String?> { obj: ResourceLocation? -> obj.toString() }, builder
        )
    }
}
