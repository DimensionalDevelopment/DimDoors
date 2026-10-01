package org.dimdev.dimdoors.datagen

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.core.Direction
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.blockstates.MultiVariantGenerator
import net.minecraft.data.models.blockstates.PropertyDispatch
import net.minecraft.data.models.blockstates.Variant
import net.minecraft.data.models.blockstates.VariantProperties
import net.minecraft.data.models.model.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DripstoneThickness
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.block.ModBlocks.DecayGroupSet
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar.AutoGenDimensionalDoorBlock
import org.dimdev.dimdoors.item.ModItems

class DimDoorsModelProvider(dataGenerator: FabricDataOutput) : FabricModelProvider(dataGenerator) {
    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        generator.createDoor(ModBlocks.GOLD_DOOR)
        generator.createDoor(ModBlocks.STONE_DOOR)
        generator.createDoor(ModBlocks.QUARTZ_DOOR)

        generator.woodProvider(ModBlocks.DRIFTWOOD_LOG).log(ModBlocks.DRIFTWOOD_LOG).wood(ModBlocks.DRIFTWOOD_WOOD)
        generator.family(ModBlocks.DRIFTWOOD_PLANKS)
            .fence(ModBlocks.DRIFTWOOD_FENCE)
            .fenceGate(ModBlocks.DRIFTWOOD_GATE)
            .button(ModBlocks.DRIFTWOOD_BUTTON)
            .slab(ModBlocks.DRIFTWOOD_SLAB)
            .stairs(ModBlocks.DRIFTWOOD_STAIRS)
        generator.createDoor(ModBlocks.DRIFTWOOD_DOOR)
        generator.createTrapdoor(ModBlocks.DRIFTWOOD_TRAPDOOR)

        generator.family(ModBlocks.AMALGAM_BLOCK)
            .slab(ModBlocks.AMALGAM_SLAB)
            .stairs(ModBlocks.AMALGAM_STAIRS)
        generator.createDoor(ModBlocks.AMALGAM_DOOR)
        generator.createTrapdoor(ModBlocks.AMALGAM_TRAPDOOR)
        generator.createTrivialCube(ModBlocks.AMALGAM_ORE)
        generator.createTrivialCube(ModBlocks.RUST)

        generator.createTrivialCube(ModBlocks.CLOD_ORE)
        generator.createTrivialCube(ModBlocks.CLOD_BLOCK)
        registerSingleTextureCube(
            generator,
            ModBlocks.PALE_SAND,
            ResourceLocation.parse("minecraft:block/white_concrete_powder")
        )
        registerCarpetLikeBlock(
            generator,
            ModBlocks.DARK_SAND_LAYER,
            TextureMapping.getBlockTexture(ModBlocks.DARK_SAND)
        )
        registerCarpetLikeBlock(
            generator,
            ModBlocks.LINT_LAYER,
            TextureMapping.getBlockTexture(ModBlocks.UNRAVELLED_FABRIC)
        )

        generateDecaySet(generator, Blocks.RED_SAND, ModBlocks.RED_SAND_SET)
        generateDecaySet(generator, Blocks.GRAVEL, ModBlocks.GRAVEL_SET)
        generator.createTrivialCube(ModBlocks.DARK_SAND)
        generateDecaySet(generator, ModBlocks.DARK_SAND, ModBlocks.DARK_SAND_SET)
        generateDecaySet(generator, Blocks.CLAY, ModBlocks.CLAY_SET)
        generateDecaySet(generator, Blocks.TERRACOTTA, ModBlocks.TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.WHITE_TERRACOTTA, ModBlocks.WHITE_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.WHITE_GLAZED_TERRACOTTA, ModBlocks.WHITE_GLAZED_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.ORANGE_TERRACOTTA, ModBlocks.ORANGE_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.ORANGE_GLAZED_TERRACOTTA, ModBlocks.ORANGE_GLAZED_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.MAGENTA_TERRACOTTA, ModBlocks.MAGENTA_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.MAGENTA_GLAZED_TERRACOTTA, ModBlocks.MAGENTA_GLAZED_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.LIGHT_BLUE_TERRACOTTA, ModBlocks.LIGHT_BLUE_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, ModBlocks.LIGHT_BLUE_GLAZED_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.YELLOW_TERRACOTTA, ModBlocks.YELLOW_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.YELLOW_GLAZED_TERRACOTTA, ModBlocks.YELLOW_GLAZED_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.LIME_TERRACOTTA, ModBlocks.LIME_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.LIME_GLAZED_TERRACOTTA, ModBlocks.LIME_GLAZED_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.PINK_TERRACOTTA, ModBlocks.PINK_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.PINK_GLAZED_TERRACOTTA, ModBlocks.PINK_GLAZED_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.GRAY_TERRACOTTA, ModBlocks.GRAY_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.GRAY_GLAZED_TERRACOTTA, ModBlocks.GRAY_GLAZED_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.LIGHT_GRAY_TERRACOTTA, ModBlocks.LIGHT_GRAY_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, ModBlocks.LIGHT_GRAY_GLAZED_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.CYAN_TERRACOTTA, ModBlocks.CYAN_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.CYAN_GLAZED_TERRACOTTA, ModBlocks.CYAN_GLAZED_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.PURPLE_TERRACOTTA, ModBlocks.PURPLE_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.PURPLE_GLAZED_TERRACOTTA, ModBlocks.PURPLE_GLAZED_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.BLUE_TERRACOTTA, ModBlocks.BLUE_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.BLUE_GLAZED_TERRACOTTA, ModBlocks.BLUE_GLAZED_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.BROWN_TERRACOTTA, ModBlocks.BROWN_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.BROWN_GLAZED_TERRACOTTA, ModBlocks.BROWN_GLAZED_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.GREEN_TERRACOTTA, ModBlocks.GREEN_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.GREEN_GLAZED_TERRACOTTA, ModBlocks.GREEN_GLAZED_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.RED_TERRACOTTA, ModBlocks.RED_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.RED_GLAZED_TERRACOTTA, ModBlocks.RED_GLAZED_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.BLACK_TERRACOTTA, ModBlocks.BLACK_TERRACOTTA_SET)
        generateDecaySet(generator, Blocks.BLACK_GLAZED_TERRACOTTA, ModBlocks.BLACK_GLAZED_TERRACOTTA_SET)


        generateDecaySet(generator, Blocks.MUD, ModBlocks.MUD_SET)
        generator.createTrivialCube(ModBlocks.UNRAVELLED_FABRIC)
        generateDecaySet(generator, ModBlocks.UNRAVELLED_FABRIC, ModBlocks.UNRAVELED_SET)
        generateDecaySet(generator, Blocks.DEEPSLATE, ModBlocks.DEEPSLATE_SET)
        generateDecaySet(generator, Blocks.SAND, ModBlocks.SAND_SET)
        generateDecaySet(generator, Blocks.END_STONE, ModBlocks.END_STONE_SET)
        generateDecaySet(generator, Blocks.NETHERRACK, ModBlocks.NETHERRACK_SET)
        generateStoneSet(generator)

        generator.createTrivialCube(ModBlocks.DRIFTWOOD_LEAVES)
        generator.createCrossBlockWithDefaultItem(
            ModBlocks.DRIFTWOOD_SAPLING,
            BlockModelGenerators.TintState.NOT_TINTED
        ) //TODO: Decide if we need potted version
        generator.createTrivialCube(ModBlocks.GRITTY_STONE)
        generator.family(ModBlocks.REALITY_SPONGE)

        registerUnraveledSpike(generator)


        generator.createAirLikeBlock(ModBlocks.LIMBO_AIR, Blocks.BARRIER.asItem())
    }

    private fun generateDecaySet(generator: BlockModelGenerators, textureSource: Block, set: DecayGroupSet) {
        val mapping = getTextureMapping(generator, textureSource)
        val fullBlockModel = ModelLocationUtils.getModelLocation(textureSource)

        generateButton(generator, set.button, mapping)
        generateSlab(generator, set.slab, mapping, fullBlockModel)
        generateStairs(generator, set.stairs, mapping)
        generateWall(generator, set.wall, mapping)
        generateFence(generator, set.fence, mapping)
        generateFenceGate(generator, set.gate, mapping)
    }

    private fun generateStoneSet(generator: BlockModelGenerators) {
        val mapping = getTextureMapping(generator, Blocks.STONE)
        val fullBlockModel = ModelLocationUtils.getModelLocation(Blocks.STONE)

        generateSlab(generator, ModBlocks.STONE_SLAB, mapping, fullBlockModel)
        generateStairs(generator, ModBlocks.STONE_STAIRS, mapping)
        generateWall(generator, ModBlocks.STONE_WALL, mapping)
    }

    private fun getTextureMapping(generator: BlockModelGenerators, textureSource: Block): TextureMapping {
        val texturedModel = generator.texturedModels.getOrDefault(textureSource, TexturedModel.CUBE.get(textureSource))
        return texturedModel.mapping
    }

    private fun generateButton(generator: BlockModelGenerators, buttonBlock: Block, mapping: TextureMapping) {
        val buttonModel = ModelTemplates.BUTTON.create(buttonBlock, mapping, generator.modelOutput)
        val pressedModel = ModelTemplates.BUTTON_PRESSED.create(buttonBlock, mapping, generator.modelOutput)
        generator.blockStateOutput.accept(BlockModelGenerators.createButton(buttonBlock, buttonModel, pressedModel))
        val inventoryModel = ModelTemplates.BUTTON_INVENTORY.create(buttonBlock, mapping, generator.modelOutput)
        generator.delegateItemModel(buttonBlock, inventoryModel)
    }

    private fun generateSlab(
        generator: BlockModelGenerators,
        slabBlock: Block,
        mapping: TextureMapping,
        fullBlockModel: ResourceLocation
    ) {
        val bottomModel = ModelTemplates.SLAB_BOTTOM.create(slabBlock, mapping, generator.modelOutput)
        val topModel = ModelTemplates.SLAB_TOP.create(slabBlock, mapping, generator.modelOutput)
        generator.blockStateOutput.accept(
            BlockModelGenerators.createSlab(
                slabBlock,
                bottomModel,
                topModel,
                fullBlockModel
            )
        )
        generator.delegateItemModel(slabBlock, bottomModel)
    }

    private fun generateStairs(generator: BlockModelGenerators, stairsBlock: Block, mapping: TextureMapping) {
        val innerModel = ModelTemplates.STAIRS_INNER.create(stairsBlock, mapping, generator.modelOutput)
        val straightModel = ModelTemplates.STAIRS_STRAIGHT.create(stairsBlock, mapping, generator.modelOutput)
        val outerModel = ModelTemplates.STAIRS_OUTER.create(stairsBlock, mapping, generator.modelOutput)
        generator.blockStateOutput.accept(
            BlockModelGenerators.createStairs(
                stairsBlock,
                innerModel,
                straightModel,
                outerModel
            )
        )
        generator.delegateItemModel(stairsBlock, straightModel)
    }

    private fun generateWall(generator: BlockModelGenerators, wallBlock: Block, mapping: TextureMapping) {
        val postModel = ModelTemplates.WALL_POST.create(wallBlock, mapping, generator.modelOutput)
        val lowSideModel = ModelTemplates.WALL_LOW_SIDE.create(wallBlock, mapping, generator.modelOutput)
        val tallSideModel = ModelTemplates.WALL_TALL_SIDE.create(wallBlock, mapping, generator.modelOutput)
        generator.blockStateOutput.accept(
            BlockModelGenerators.createWall(
                wallBlock,
                postModel,
                lowSideModel,
                tallSideModel
            )
        )
        val inventoryModel = ModelTemplates.WALL_INVENTORY.create(wallBlock, mapping, generator.modelOutput)
        generator.delegateItemModel(wallBlock, inventoryModel)
    }

    private fun generateFence(generator: BlockModelGenerators, fenceBlock: Block, mapping: TextureMapping) {
        val postModel = ModelTemplates.FENCE_POST.create(fenceBlock, mapping, generator.modelOutput)
        val sideModel = ModelTemplates.FENCE_SIDE.create(fenceBlock, mapping, generator.modelOutput)
        generator.blockStateOutput.accept(BlockModelGenerators.createFence(fenceBlock, postModel, sideModel))
        val inventoryModel = ModelTemplates.FENCE_INVENTORY.create(fenceBlock, mapping, generator.modelOutput)
        generator.delegateItemModel(fenceBlock, inventoryModel)
    }

    private fun generateFenceGate(generator: BlockModelGenerators, fenceGateBlock: Block, mapping: TextureMapping) {
        val openModel = ModelTemplates.FENCE_GATE_OPEN.create(fenceGateBlock, mapping, generator.modelOutput)
        val closedModel = ModelTemplates.FENCE_GATE_CLOSED.create(fenceGateBlock, mapping, generator.modelOutput)
        val wallOpenModel = ModelTemplates.FENCE_GATE_WALL_OPEN.create(fenceGateBlock, mapping, generator.modelOutput)
        val wallClosedModel =
            ModelTemplates.FENCE_GATE_WALL_CLOSED.create(fenceGateBlock, mapping, generator.modelOutput)
        generator.blockStateOutput.accept(
            BlockModelGenerators.createFenceGate(
                fenceGateBlock,
                openModel,
                closedModel,
                wallOpenModel,
                wallClosedModel,
                true
            )
        )
        generator.delegateItemModel(fenceGateBlock, closedModel)
    }

    private fun registerSingleTextureCube(generator: BlockModelGenerators, block: Block, texture: ResourceLocation) {
        val model = ModelTemplates.CUBE_ALL.create(block, TextureMapping.cube(texture), generator.modelOutput)
        generator.blockStateOutput.accept(
            MultiVariantGenerator.multiVariant(
                block, Variant.variant().with(
                    VariantProperties.MODEL, model
                )
            )
        )
    }

    private fun registerCarpetLikeBlock(generator: BlockModelGenerators, block: Block, texture: ResourceLocation) {
        val model = ModelTemplates.CARPET.create(block, TextureMapping.wool(texture), generator.modelOutput)
        generator.blockStateOutput.accept(
            MultiVariantGenerator.multiVariant(
                block, Variant.variant().with(
                    VariantProperties.MODEL, model
                )
            )
        )
    }

    private fun registerUnraveledSpike(generators: BlockModelGenerators) {
        generators.skipAutoItemBlock(ModBlocks.UNRAVELED_SPIKE)
        val c2 = PropertyDispatch.properties(
            BlockStateProperties.VERTICAL_DIRECTION,
            BlockStateProperties.DRIPSTONE_THICKNESS
        )

        for (dripstoneThickness in DripstoneThickness.entries) {
            c2.select(
                Direction.UP,
                dripstoneThickness,
                createPointedDripstoneVariant(generators, Direction.UP, dripstoneThickness)
            )
        }

        for (dripstoneThickness in DripstoneThickness.entries) {
            c2.select(
                Direction.DOWN,
                dripstoneThickness,
                createPointedDripstoneVariant(generators, Direction.DOWN, dripstoneThickness)
            )
        }

        generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.UNRAVELED_SPIKE).with(c2))
    }

    fun createPointedDripstoneVariant(
        generators: BlockModelGenerators,
        direction: Direction,
        dripstoneThickness: DripstoneThickness
    ): Variant {
        val var10000 = direction.serializedName
        val string = "_" + var10000 + "_" + dripstoneThickness.serializedName
        val textureMapping = TextureMapping.cross(TextureMapping.getBlockTexture(ModBlocks.UNRAVELED_SPIKE, string))
        return Variant.variant().with(
            VariantProperties.MODEL,
            ModelTemplates.POINTED_DRIPSTONE.createWithSuffix(
                ModBlocks.UNRAVELLED_BLOCK,
                string,
                textureMapping,
                generators.modelOutput
            )
        )
    }

    fun registerDoor(generator: BlockModelGenerators, doorBlock: Block, textureSource: Block) {
        val textureMap = TextureMapping.door(textureSource)
        val identifier = ModelTemplates.DOOR_BOTTOM_LEFT.create(doorBlock, textureMap, generator.modelOutput)
        val identifier2 = ModelTemplates.DOOR_BOTTOM_LEFT_OPEN.create(doorBlock, textureMap, generator.modelOutput)
        val identifier3 = ModelTemplates.DOOR_BOTTOM_RIGHT.create(doorBlock, textureMap, generator.modelOutput)
        val identifier4 = ModelTemplates.DOOR_BOTTOM_RIGHT_OPEN.create(doorBlock, textureMap, generator.modelOutput)
        val identifier5 = ModelTemplates.DOOR_TOP_LEFT.create(doorBlock, textureMap, generator.modelOutput)
        val identifier6 = ModelTemplates.DOOR_TOP_LEFT_OPEN.create(doorBlock, textureMap, generator.modelOutput)
        val identifier7 = ModelTemplates.DOOR_TOP_RIGHT.create(doorBlock, textureMap, generator.modelOutput)
        val identifier8 = ModelTemplates.DOOR_TOP_RIGHT_OPEN.create(doorBlock, textureMap, generator.modelOutput)
        generator.createSimpleFlatItemModel(doorBlock.asItem())
        generator.blockStateOutput.accept(
            BlockModelGenerators.createDoor(
                doorBlock,
                identifier,
                identifier2,
                identifier3,
                identifier4,
                identifier5,
                identifier6,
                identifier7,
                identifier8
            )
        )
    }

    fun registerAutoGenDoor(generator: BlockModelGenerators, doorBlock: AutoGenDimensionalDoorBlock<*>) {
        val textureSource = doorBlock.originalBlock

        val identifier = TextureMapping.getBlockTexture(textureSource, "_bottom_left")
        val identifier2 = TextureMapping.getBlockTexture(textureSource, "_bottom_left_open")
        val identifier3 = TextureMapping.getBlockTexture(textureSource, "_bottom_right")
        val identifier4 = TextureMapping.getBlockTexture(textureSource, "_bottom_right_open")
        val identifier5 = TextureMapping.getBlockTexture(textureSource, "_top_left")
        val identifier6 = TextureMapping.getBlockTexture(textureSource, "_top_left_open")
        val identifier7 = TextureMapping.getBlockTexture(textureSource, "_top_right")
        val identifier8 = TextureMapping.getBlockTexture(textureSource, "_top_right_open")
        ModelTemplates.TWO_LAYERED_ITEM.create(
            ModelLocationUtils.getModelLocation(doorBlock),
            TextureMapping.layered(
                DimensionalDoors.id("item/dimdoor_back"),
                TextureMapping.getItemTexture(textureSource.asItem())
            ),
            generator.modelOutput
        )
        generator.blockStateOutput.accept(
            BlockModelGenerators.createDoor(
                doorBlock,
                identifier,
                identifier2,
                identifier3,
                identifier4,
                identifier5,
                identifier6,
                identifier7,
                identifier8
            )
        )
    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerators) {
        itemModelGenerator.generateFlatItem(ModItems.FUZZY_FIREBALL, ModelTemplates.FLAT_ITEM)
        itemModelGenerator.generateFlatItem(ModItems.FABRIC_OF_FINALITY, ModelTemplates.FLAT_ITEM)
        itemModelGenerator.generateFlatItem(ModItems.LIMINAL_LINT, ModelTemplates.FLAT_ITEM)
        itemModelGenerator.generateFlatItem(ModItems.ENDURING_FIBERS, ModelTemplates.FLAT_ITEM)
        itemModelGenerator.generateFlatItem(ModItems.RIFT_PEARL, ModelTemplates.FLAT_ITEM)
        itemModelGenerator.generateFlatItem(ModItems.AMALGAM_LUMP, ModelTemplates.FLAT_ITEM)
        itemModelGenerator.generateFlatItem(ModItems.CLOD, ModelTemplates.FLAT_ITEM)

        itemModelGenerator.generateFlatItem(ModItems.GARMENT_OF_REALITY_ARMOR.boots, ModelTemplates.FLAT_ITEM)
        itemModelGenerator.generateFlatItem(ModItems.GARMENT_OF_REALITY_ARMOR.chestplate, ModelTemplates.FLAT_ITEM)
        itemModelGenerator.generateFlatItem(ModItems.GARMENT_OF_REALITY_ARMOR.helmet, ModelTemplates.FLAT_ITEM)
        itemModelGenerator.generateFlatItem(ModItems.GARMENT_OF_REALITY_ARMOR.leggings, ModelTemplates.FLAT_ITEM)

        generateFarshot(itemModelGenerator)
    }

    companion object {
        private fun generateFarshot(itemModelGenerator: ItemModelGenerators) {
            itemModelGenerator.generateFlatItem(ModItems.FARSHOT, "_pulling_0", ModelTemplates.FLAT_ITEM)
            itemModelGenerator.generateFlatItem(ModItems.FARSHOT, "_pulling_1", ModelTemplates.FLAT_ITEM)
            itemModelGenerator.generateFlatItem(ModItems.FARSHOT, "_pulling_2", ModelTemplates.FLAT_ITEM)

            ModelTemplates.FLAT_ITEM.create(
                ModelLocationUtils.getModelLocation(ModItems.FARSHOT),
                TextureMapping.layer0(TextureMapping.getItemTexture(ModItems.FARSHOT)),
                itemModelGenerator.output
            ) { location: ResourceLocation, textures: MutableMap<TextureSlot, ResourceLocation> ->
                val json = ModelTemplates.FLAT_ITEM.createBaseTemplate(location, textures)
                val display = JsonObject()
                display.add(
                    "thirdperson_righthand", transform(
                        nums(-80, 260, -40),
                        nums(-1, -2, -2.5),
                        nums(0.9, 0.9, 0.9)
                    )
                )
                display.add(
                    "thirdperson_lefthand", transform(
                        nums(-80, -280, 40),
                        nums(-1, -2, -2.5),
                        nums(0.9, 0.9, 0.9)
                    )
                )
                display.add(
                    "firstperson_righthand", transform(
                        nums(0, -90, 25),
                        nums(1.13, 3.2, 1.13),
                        nums(0.68, 0.68, 0.68)
                    )
                )
                display.add(
                    "firstperson_lefthand", transform(
                        nums(0, 90, -25),
                        nums(1.13, 3.2, 1.13),
                        nums(0.68, 0.68, 0.68)
                    )
                )
                json.add("display", display)

                val overrides = JsonArray()
                overrides.add(
                    override(
                        ModelLocationUtils.getModelLocation(ModItems.FARSHOT, "_pulling_0"),
                        "pulling",
                        1
                    )
                )
                overrides.add(
                    override(
                        ModelLocationUtils.getModelLocation(ModItems.FARSHOT, "_pulling_1"),
                        "pulling",
                        1,
                        "pull",
                        0.65
                    )
                )
                overrides.add(
                    override(
                        ModelLocationUtils.getModelLocation(ModItems.FARSHOT, "_pulling_2"),
                        "pulling",
                        1,
                        "pull",
                        0.90
                    )
                )
                json.add("overrides", overrides)
                json
            }
        }

        private fun transform(rotation: JsonArray?, translation: JsonArray?, scale: JsonArray?): JsonObject {
            val json = JsonObject()
            json.add("rotation", rotation)
            json.add("translation", translation)
            json.add("scale", scale)
            return json
        }

        private fun nums(vararg values: Number?): JsonArray {
            val array = JsonArray()
            for (value in values) {
                array.add(value)
            }
            return array
        }

        private fun override(model: ResourceLocation, vararg predicate: Any?): JsonObject {
            val predicateJson = JsonObject()
            var i = 0
            while (i < predicate.size) {
                predicateJson.addProperty(predicate[i] as String?, predicate[i + 1] as Number?)
                i += 2
            }

            val json = JsonObject()
            json.add("predicate", predicateJson)
            json.addProperty("model", model.toString())
            return json
        }
    }
}
