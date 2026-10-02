package org.dimdev.dimdoors.pockets.generator

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.Vec3i
import net.minecraft.world.Container
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity
import org.apache.logging.log4j.LogManager
import org.dimdev.dimcore.api.MapCodecHasHolder
import org.dimdev.dimdoors.api.util.Location.Companion.ofWorld
import org.dimdev.dimdoors.api.util.Weighted
import org.dimdev.dimdoors.api.util.math.Equation
import org.dimdev.dimdoors.pockets.PocketCreator
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.pockets.TemplateUtils
import org.dimdev.dimdoors.pockets.modifier.Modifier
import org.dimdev.dimdoors.pockets.modifier.RiftManager
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.util.CodecUtils.mutableList
import org.dimdev.dimdoors.util.CodecUtils.nullableForGetter
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket
import org.dimdev.dimdoors.world.pocket.type.Pocket
import org.dimdev.dimdoors.world.pocket.type.PocketImpl.Companion.builder

abstract class PocketGenerator<T : PocketGenerator<T>> protected constructor(
    protected val builder: AbstractPocket.AbstractPocketBuilder<*, *>?,
    protected val weight: Equation,
    protected val setupLoot: Boolean?,
    protected val modifiers: MutableList<Holder<Modifier>>,
    protected val tags: MutableList<String>
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
            pocket.blockEntities.forEach { (blockPos: BlockPos?, blockEntity: BlockEntity?) ->
                if (blockEntity is Container) { // comment in if needed
                    if (blockEntity.isEmpty) {
                        if (blockEntity is RandomizableContainerBlockEntity) {
                            TemplateUtils.setupLootTable(world, blockEntity, LOGGER)
                        }
                    }
                }
            }

        manager.rifts.forEach { rift ->
            val destination = rift.data.destination
            if (destination === VirtualTarget.NoneTarget) {
                LOGGER.warn(
                    "Pocket {} in {}: rift at {} (relative {}) has no destination. Its schematic block entity is missing rift data, or no modifier assigned it an id.",
                    pocket.id, world.dimension().location(), rift.riftBlockPos.toShortString(), rift.riftBlockPos.subtract(pocket.origin).toShortString()
                )
            } else {
                destination.location = ofWorld(world, rift.riftBlockPos)
            }
        }
        TemplateUtils.registerRifts(manager.rifts, parameters.linkTo!!, parameters.linkProperties, pocket)
    }

    fun getRiftManager(pocket: Pocket<*, *>): RiftManager {
        return RiftManager(pocket)
    }

    // why would you want to check for exact tags, but still need a blackList? Good question, but there is probably some use case for it.
    fun checkTags(required: List<String>?, blackList: List<String>?, exact: Boolean): Boolean {
        if (exact && required!!.size != tags.size) return false
        if (required != null) {
            for (req in required) {
                if (!tags.contains(req)) return false
            }
        }
        if (blackList != null) {
            for (black in blackList) {
                if (tags.contains(black)) return false
            }
        }
        return true
    }

    fun pocketBuilder(parameters: PocketGenerationContext): Pocket.PocketBuilder<*, *> { // TODO: PocketBuilder from json
        if (builder == null) {
            return builder().expand(getSize(parameters))
        }
        val abstractBuilder: AbstractPocket.AbstractPocketBuilder<*, *> = builder.copy()

        if (abstractBuilder is Pocket.PocketBuilder<*, *>) {
            return abstractBuilder.expand(getSize(parameters))
        }
        return builder().expand(getSize(parameters))
    }

    abstract fun getSize(parameters: PocketGenerationContext): Vec3i

    companion object {
        private val LOGGER = LogManager.getLogger()
        val CODEC = PocketGenerators.codec

        fun <T : PocketGenerator<T>> commonFields(instance: RecordCodecBuilder.Instance<T>) = instance.group(
            AbstractPocket.BUILDER_CODEC.optionalFieldOf("builder").nullableForGetter { it.builder },
            Equation.CODEC.optionalFieldOf("weight", Equation.FIVE).forGetter { it.weight },
            Codec.BOOL.optionalFieldOf("setup_loot").nullableForGetter { it.setupLoot },
            Modifier.HOLDER_CODEC.mutableList().fieldOf("modifiers").forGetter { it.modifiers },
            Codec.STRING.mutableList().optionalFieldOf("tags", mutableListOf()).forGetter { it.tags }
        )
    }
}
