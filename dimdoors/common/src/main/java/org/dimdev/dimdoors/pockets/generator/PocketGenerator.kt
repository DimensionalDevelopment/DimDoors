package org.dimdev.dimdoors.pockets.generator

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.core.Vec3i
import net.minecraft.resources.RegistryFileCodec
import net.minecraft.world.Container
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity
import org.apache.logging.log4j.LogManager
import org.dimdev.dimcore.api.MapCodecHasHolder
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.pockets.PocketCreator
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.pockets.TemplateUtils
import org.dimdev.dimdoors.pockets.modifier.Modifier
import org.dimdev.dimdoors.pockets.modifier.RiftManager
import org.dimdev.dimdoors.pockets.type.Pocket
import org.dimdev.dimdoors.pockets.type.PocketImpl.Companion.builder
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.util.Weighted
import org.dimdev.dimdoors.util.ext.mutableList
import org.dimdev.dimdoors.util.ext.nullableForGetter
import org.dimdev.dimdoors.util.math.Equation

abstract class PocketGenerator<T : PocketGenerator<T>> protected constructor(
    protected val builder: Pocket.PocketBuilder<*, *>?,
    protected val weight: Equation,
    protected val setupLoot: Boolean?,
    protected val modifiers: MutableList<Holder<Modifier>>,
) : Weighted<PocketGenerationContext>, PocketCreator, MapCodecHasHolder<PocketGenerator<*>> {

    abstract fun prepareAndPlacePocket(parameters: PocketGenerationContext, builder: Pocket.PocketBuilder<*, *>): Pocket<*, *>

    override fun prepareAndPlacePocket(parameters: PocketGenerationContext) = prepareAndPlacePocket(parameters, setupLoot)

    override fun prepareAndPlacePocket(parameters: PocketGenerationContext, setupLoot: Boolean?): Pocket<*, *> {
        val builder: Pocket.PocketBuilder<*, *> = pocketBuilder(parameters)
            .virtualLocation(parameters.sourceVirtualLocation) // TODO: virtualLocation thing still makes little sense

        this.applyModifiers(parameters, builder)

        val pocket = prepareAndPlacePocket(parameters, builder)

        val manager = getRiftManager(pocket)

        this.applyModifiers(parameters, manager)

        setup(pocket, manager, parameters, setupLoot ?: false)

        return pocket
    }

    override fun getWeight(parameters: PocketGenerationContext): Double {
        return this.weight.apply(parameters.toVariableMap())
    }

    fun isSetupLoot(): Boolean {
        return setupLoot != null && setupLoot
    }


    fun applyModifiers(parameters: PocketGenerationContext, manager: RiftManager) {
        for (modifier in modifiers) {
            modifier.value().apply(parameters, manager)
        }
    }

    fun applyModifiers(parameters: PocketGenerationContext, builder: Pocket.PocketBuilder<*, *>) {
        for (modifier in modifiers) {
            modifier.value().apply(parameters, builder)
        }
    }

    fun setup(
        pocket: Pocket<*, *>,
        manager: RiftManager,
        parameters: PocketGenerationContext,
        setupLootTables: Boolean
    ) {
        val world = parameters.world

        if (setupLootTables)  // temp
            pocket.blockEntities.forEach { (_, blockEntity: BlockEntity) ->
                if (blockEntity is Container) { // comment in if needed
                    if (blockEntity is RandomizableContainerBlockEntity && blockEntity.lootTable == null && blockEntity.isEmpty) {
                        TemplateUtils.setupLootTable(world, blockEntity, LOGGER)
                    }
                }
            }

        manager.rifts.forEach { rift ->
            val destination = rift.data().destination
            if (destination === VirtualTarget.NoneTarget) {
                val pos = rift.self().blockPos

                LOGGER.warn(
                    "Pocket {} in {}: rift at {} (relative {}) has no destination. Its schematic block entity is missing rift data, or no modifier assigned it an id.",
                    pocket.id, world.dimension().location(), pos.toShortString(), pos.subtract(pocket.origin).toShortString()
                )
            }
        }
        TemplateUtils.registerRifts(manager.rifts, parameters.linkTo, parameters.linkProperties, pocket)
    }

    fun getRiftManager(pocket: Pocket<*, *>): RiftManager {
        return RiftManager(pocket)
    }

    fun pocketBuilder(parameters: PocketGenerationContext): Pocket.PocketBuilder<*, *> {
        return (builder?.copy() ?: builder()).expand(getSize(parameters))
    }

    abstract fun getSize(parameters: PocketGenerationContext): Vec3i

    companion object {
        private val LOGGER = LogManager.getLogger()
        val CODEC = PocketGenerators.codec
        val HOLDER_CODEC: Codec<Holder<PocketGenerator<*>>> = RegistryFileCodec.create(ModRegistryKeys.POCKET_GENERATOR, CODEC)

        fun <T : PocketGenerator<T>> commonFields(instance: RecordCodecBuilder.Instance<T>) = instance.group(
            Pocket.BUILDER_CODEC.optionalFieldOf("builder").nullableForGetter { it.builder },
            Equation.CODEC.optionalFieldOf("weight", Equation.FIVE).forGetter { it.weight },
            Codec.BOOL.optionalFieldOf("setup_loot").nullableForGetter { it.setupLoot },
            Modifier.HOLDER_CODEC.mutableList().fieldOf("modifiers").forGetter { it.modifiers },
        )
    }
}
