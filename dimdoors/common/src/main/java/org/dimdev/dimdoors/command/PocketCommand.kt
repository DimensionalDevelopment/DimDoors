package org.dimdev.dimdoors.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.commands.arguments.coordinates.BlockPosArgument
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.network.chat.Component.literal
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
import org.dimdev.dimdoors.rift.registry.LinkProperties
import org.dimdev.dimdoors.rift.registry.PocketRegistry.Companion.instance
import org.dimdev.dimdoors.rift.targets.RiftReference
import org.dimdev.dimdoors.world.ModDimensions
import org.dimdev.dimdoors.world.pocket.VirtualLocation.Companion.fromLocation
import java.util.concurrent.CompletableFuture
import kotlin.jvm.optionals.getOrNull

object PocketCommand {
    private val LOGGER: Logger = LogManager.getLogger()

    fun <T : PocketCreator> ArgumentBuilder<CommandSourceStack, *>.placeOption(
        name: String,
        resourceKey: ResourceKey<Registry<T>>
    ): ArgumentBuilder<CommandSourceStack, *> {
        return this.literal(name) {
            argument("id", ResourceLocationArgument.id()) {
                requires { obj -> obj.isPlayer }
                suggests { ctx, builder -> getSuggestions(ctx.source.registryAccess(), resourceKey, builder) }
                execute {
                    placePocket(
                        source,
                        ResourceLocationArgument.getId(this, "id"),
                        resourceKey,
                        null,
                        null
                    )
                }

                argument("target", EntityArgument.entity()) {
                    execute {
                        val source = source ?: return@execute 0

                        placePocket(
                            source,
                            ResourceLocationArgument.getId(this, "id"),
                            resourceKey,
                            EntityArgument.getEntity(this, "target"),
                            null
                        )
                    }

                    argument("source_pos", BlockPosArgument.blockPos()) {
                        execute {
                            placePocket(
                                source!!,
                            ResourceLocationArgument.getId(this, "id"),
                            resourceKey,
                            null,
                            BlockPosArgument.getLoadedBlockPos(this, "source_pos"))

                        }
                    }
                }
            }
        }
    }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>): LiteralCommandNode<CommandSourceStack> = dispatcher.register("pocket") {
            requires { source -> source.hasPermission(2) }


            placeOption("virtual_pocket", ModRegistryKeys.VIRTUAL_POCKET)
            placeOption("pocket_group", ModRegistryKeys.POCKET_GROUPS)
            placeOption("pocket_generator", ModRegistryKeys.POCKET_GENERATOR)

                literal("dump") {
                    requires { src -> src.hasPermission(4) }
                    execute {
                        val source = this.source ?: return@execute 0
                        source.sendSuccess({ literal("Dumping pocket data") }, false)

                        CompletableFuture.runAsync {
                            runCatching { PocketLoader.dump() }.onFailure { LOGGER.error("Error dumping pocket data", it) }
                        }.thenRun {
                            source.server.execute {
                                source.sendSuccess({ literal("Dumped pocket data") }, false)
                            }
                        }
                        Command.SINGLE_SUCCESS
                    }
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
        val creator = source.registryAccess().registry(idFunction).map<T> { a -> a.get(id) }.getOrNull() ?: run {
            source.sendFailure(literal("Unknown pocket id: $id"))
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
            source.sendFailure(literal("Source position must be a raw rift, a door/trapdoor/portal that can host a rift, or replaceable space."))
            return 0
        }

        val pocketLevel = sourceLevel.server.getLevel(ModDimensions.DUNGEON) ?: run {
            source.sendFailure(literal("Could not resolve the dungeon pocket world."))
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

        val pocket = runCatching { PocketCreator.create(creator, pocketGenerationContext) }.getOrElse { e ->
            LOGGER.error("Failed to generate pocket {} via command.", id, e)
            source.sendFailure(literal("Failed to generate pocket $id. Check the server log."))
            return 0
        }

        if (pocket == null) {
            source.sendFailure(literal("Pocket generation returned no pocket for $id."))
            return 0
        }

        val entrance = instance.getPocketEntrance(pocket)
        if (entrance == null) {
            source.sendFailure(literal("Pocket $id generated without a registered entrance."))
            return 0
        }

        if(RiftSignatureItem.getOrCreateRift(sourceLevel, sourcePos) == null) {
            source.sendFailure(literal("Could not create or convert a rift at " + sourcePos.toShortString() + "."))
            return 0
        }

        TemplateUtils.linkRifts(contextLocation, entrance)
        if (targetEntity != null
            && !(contextLocation.blockEntity as EntranceRiftBlockEntity<*>).teleport(targetEntity)
        ) { // This line does not feel safe but theoretically any block entity errors would happen inside linkRifts
            source.sendFailure(literal("Failed to teleport entity through created rift."))
            return 0
        }

        source.sendSuccess({
            literal(
                ("Linked ${sourcePos.toShortString()} to $id in ${pocketLevel.dimension().location()} at ${entrance.blockPos.toShortString()}")
            )
        }, false)
        return Command.SINGLE_SUCCESS
    }

    private fun normalizeSourcePos(level: ServerLevel, sourcePos: BlockPos): BlockPos {
        val sourceState = level.getBlockState(sourcePos)
        if (sourceState.hasProperty(DoorBlock.HALF) && sourceState.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER
        ) {
            return sourcePos.below()
        }
        return sourcePos
    }

    private fun canUseSource(player: ServerPlayer, sourcePos: BlockPos, sourceState: BlockState): Boolean = (sourceState.canBeReplaced() || sourceState.block is RiftVariantProvider)
            && player.mayUseItemAt(sourcePos, Direction.UP, ItemStack.EMPTY)

    fun <T : PocketCreator> getSuggestions(
        access: RegistryAccess,
        resourceKey: ResourceKey<Registry<T>>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        return SharedSuggestionProvider.suggest(
            access.registry<T>(resourceKey)
                .map(Registry<T>::keySet).stream()
                .flatMap(MutableSet<ResourceLocation>::stream)
                .map(ResourceLocation::toString), builder
        )
    }
}
