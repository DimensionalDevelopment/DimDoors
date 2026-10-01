package org.dimdev.dimdoors.client

import net.minecraft.client.renderer.block.BlockModelShaper
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.TrapDoorBlock
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.DimensionalDoors.Companion.getDimensionalDoorBlockRegistrar
import org.dimdev.dimdoors.block.TraversableRiftBlock

object GeneratedDoorModelMappings {
    /**
     * Upright portal plane. Door items are flat `item/generated` sprites living in the same
     * plane, so the portal lines up with them as-is.
     */
    val PORTAL_ITEM_MODEL: ResourceLocation = DimensionalDoors.id("item/dimensional_portal")

    /**
     * Horizontal portal slab matching [TrapDoorBlock.BOTTOM_AABB], which is what the in-world
     * renderer draws for trapdoors. Trapdoor items use the 3d `block/ *_trapdoor_bottom` model
     * rather than a sprite, so the upright plane would stand on edge through them.
     */
    val PORTAL_FLAT_ITEM_MODEL: ResourceLocation = DimensionalDoors.id("item/dimensional_portal_flat")

    val PORTAL_ITEM_MODELS = listOf(PORTAL_ITEM_MODEL, PORTAL_FLAT_ITEM_MODEL)

    fun create(): Mappings {
        val blocks = getDimensionalDoorBlockRegistrar()
            .gennedIds
            .map { BuiltInRegistries.BLOCK.get(it) }
            .filter(TraversableRiftBlock::class.java::isInstance)
            .toList()

        val blockModels = mutableMapOf<ModelResourceLocation, ModelResourceLocation>()
        val itemModels = mutableMapOf<ModelResourceLocation, ItemMapping>()

        for (block in blocks) {
            val rift = block as TraversableRiftBlock<*>

            for (state in block.stateDefinition.possibleStates) {
                blockModels[BlockModelShaper.stateToModelLocation(state)] = BlockModelShaper.stateToModelLocation(rift.getVisualBlockState(state))
            }

            val original = rift.getVisualBlockState(block.defaultBlockState()).block

            itemModels[ModelResourceLocation.inventory(
                block.asItem().builtInRegistryHolder().key().location()
            )] = ItemMapping(
                ModelResourceLocation.inventory(
                    original.asItem().builtInRegistryHolder().key().location()
                ),
                portalModelFor(original)
            )
        }

        return Mappings(blocks, blockModels, itemModels)
    }

    /**
     * Picks the portal model matching the shape of `visualBlock`'s item model.
     */
    fun portalModelFor(visualBlock: Block?): ResourceLocation? {
        return if (visualBlock is TrapDoorBlock) PORTAL_FLAT_ITEM_MODEL else PORTAL_ITEM_MODEL
    }

    /**
     * @param source the vanilla item model the generated door item borrows
     * @param portal the portal model whose orientation matches `source`
     */
    @JvmRecord
    data class ItemMapping(
        val source: ModelResourceLocation?,
        val portal: ResourceLocation?
    )

    @JvmRecord
    data class Mappings(
        val blocks: List<Block>,
        val blockModels: MutableMap<ModelResourceLocation, ModelResourceLocation>,
        val itemModels: MutableMap<ModelResourceLocation, ItemMapping>
    )
}
