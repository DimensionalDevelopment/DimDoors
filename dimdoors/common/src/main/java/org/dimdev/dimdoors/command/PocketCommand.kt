package org.dimdev.dimdoors.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.commands.arguments.coordinates.BlockPosArgument
import net.minecraft.core.*
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Component.literal
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.Music
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimcore.api.Type
import org.dimdev.dimcore.command.TypeCommands
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.api.util.Location
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
import org.dimdev.dimdoors.util.CodecUtils
import org.dimdev.dimdoors.world.ModDimensions
import org.dimdev.dimdoors.world.pocket.VirtualLocation.Companion.fromLocation
import org.dimdev.dimdoors.world.pocket.type.PocketColor
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddons
import org.dimdev.dimdoors.world.pocket.type.addon.environment.Environment
import org.dimdev.dimdoors.world.pocket.type.addon.environment.EnvironmentAddon
import org.dimdev.dimdoors.world.pocket.type.addon.environment.Environments
import java.util.concurrent.CompletableFuture
import kotlin.jvm.optionals.getOrNull

private val ServerPlayer.location: Location
    get() {
        return Location(serverLevel().dimension(), blockPosition())
    }

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

    private fun pocketOf(ctx: CommandContext<CommandSourceStack>) = ctx.source.player?.let { instance.getPocketAt(it.location) }

    private fun <B : Any> editEnvironment(
        name: String,
        registry: Registry<out Type<B>>,
        read: (EnvironmentAddon) -> B,
        write: (EnvironmentAddon, B) -> Environment
    ) = TypeCommands.branch(name, registry, current = { ctx -> pocketOf(ctx)?.getAddon(PocketAddons.ENVIRONMENT_ADDON)?.let(read) }) { ctx, value ->
        val pocket = pocketOf(ctx) ?: run {
            ctx.source.sendFailure("Not in a pocket.".literal())
            return@branch 0
        }

        val addon = pocket.createOrGet(PocketAddons.ENVIRONMENT_ADDON)
        addon.environment = write(addon, value)
        pocket.syncClientAddons()

        ctx.source.sendSuccess({ "Pocket $name updated.".literal() }, true)
        1
    }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>): LiteralCommandNode<CommandSourceStack> = dispatcher.register("pocket") {
            requires { source -> source.hasPermission(2) }

            literal("edit") {
                literal("dye") {
                    argument("color", StringArgumentType.string()) {
                        suggests { _, builder -> SharedSuggestionProvider.suggest(PocketColor.entries.map { it.serializedName }.toTypedArray(), builder) }
                        execute {
                            val player = source.playerOrException

                            val pocket = instance.getPocketAt(player.location) ?: run {
                                source.sendFailure("Not in a pocket.".literal())

                                return@execute 0
                            }

                            val color = StringArgumentType.getString(this, "color").let { PocketColor.from(it) } ?: run {
                                source.sendFailure("Invalid color.".literal())
                                return@execute 0
                            }



                            if(color.color == null) {
                                source.sendSuccess({ "Pocket forgot what color it had.".literal() }, false)
                                pocket.removeAddon(PocketAddons.DYEABLE_ADDON)

                                return@execute 1
                            }

                            pocket.createOrGet(PocketAddons.DYEABLE_ADDON).setColor(player, pocket, color.color)
                            pocket.syncClientAddons()

                            source.sendSuccess({ "Pocket color set to $color".literal() }, true)

                            return@execute 1
                        }
                    }
                }

                then(TypeCommands.branch("environment", Environments.registry, current = { ctx -> pocketOf(ctx)?.getAddon(PocketAddons.ENVIRONMENT_ADDON)?.environment }) { ctx, value ->
                    val pocket = pocketOf(ctx) ?: run {
                        ctx.source.sendFailure("Not in a pocket.".literal())
                        return@branch 0
                    }

                    val addon = pocket.createOrGet(PocketAddons.ENVIRONMENT_ADDON)
                    addon.environment = value
                    pocket.syncClientAddons()

                    ctx.source.sendSuccess({ "Pocket environment updated.".literal() }, true)
                    1
                })


                then(editEnvironment("environment", Environments.registry, { it.environment }) { _, environment -> environment })


                literal("music") {
                    argument("track", ResourceLocationArgument.id()) {
                        suggests { ctx, builder -> SharedSuggestionProvider.suggestResource(ctx.source.registryAccess().registryOrThrow(Registries.SOUND_EVENT).keySet(), builder) }
                        execute {
                            val player = source.playerOrException

                            val pocket = instance.getPocketAt(player.location) ?: run {
                                source.sendFailure("Not in a pocket.".literal())

                                return@execute 0
                            }

                            val id = ResourceLocationArgument.getId(this, "track")

                            val music: Holder<SoundEvent> = source.registryAccess().registry(Registries.SOUND_EVENT).flatMap { it.getHolder(id) }.getOrNull() ?: run {
                                source.sendFailure("Invalid music".literal())
                                return@execute 0
                            }

                            pocket.createOrGet(PocketAddons.MUSIC_ADDON)?.music = music.music()
                            pocket.syncClientAddons()

                            source.sendSuccess({ "Pocket music set to $id".literal() }, true)

                            return@execute 1
                        }
                    }
                }
            }

            literal("create") {

                placeOption("virtual_pocket", ModRegistryKeys.VIRTUAL_POCKET)
                placeOption("pocket_group", ModRegistryKeys.POCKET_GROUPS)
                placeOption("pocket_generator", ModRegistryKeys.POCKET_GENERATOR)
            }

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

private fun Holder<SoundEvent>.music(): Music = CodecUtils.createMusic(this)

private fun String.literal() = Component.literal(this)
