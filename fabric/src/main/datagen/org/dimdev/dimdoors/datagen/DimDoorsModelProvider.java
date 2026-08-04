package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.Direction;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.dimdev.dimdoors.item.ModItems;

import static net.minecraft.data.models.model.TextureMapping.getBlockTexture;
import static net.minecraft.data.models.model.TextureMapping.getItemTexture;

public class DimDoorsModelProvider extends FabricModelProvider {
    public DimDoorsModelProvider(FabricDataOutput dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        generator.createDoor(ModBlocks.GOLD_DOOR);
        generator.createDoor(ModBlocks.STONE_DOOR);
        generator.createDoor(ModBlocks.QUARTZ_DOOR);
        generator.createDoor(ModBlocks.DIALING_DOOR);

//        BuiltInRegistries.BLOCK.stream().filter(a -> a instanceof DimensionalDoorBlockRegistrar.AutoGenDimensionalDoorBlock).map(a -> (DimensionalDoorBlockRegistrar.AutoGenDimensionalDoorBlock) a).forEach(a -> registerAutoGenDoor(generator, a));

//        registerAutoGenDoor(generator, BuiltInRegistries.BLOCK.get(DimensionalDoors.id("block_ag_dim_minecraft_iron_door")), Blocks.IRON_DOOR);
//        registerAutoGenDoor(generator, BuiltInRegistries.BLOCK.get(DimensionalDoors.id("block_ag_dim_dimdoors_gold_door")), ModBlocks.GOLD_DOOR);
//        registerAutoGenDoor(generator, BuiltInRegistries.BLOCK.get(DimensionalDoors.id("block_ag_dim_dimdoors_quartz_door")), ModBlocks.QUARTZ_DOOR);
//        registerAutoGenDoor(generator, BuiltInRegistries.BLOCK.get(DimensionalDoors.id("block_ag_dim_minecraft_oak_door")), Blocks.OAK_DOOR);
//        registerAutoGenDoor(generator, BuiltInRegistries.BLOCK.get(DimensionalDoors.id("block_ag_dim_dimdoors_stone_door")), ModBlocks.STONE_DOOR);


        generator.woodProvider(ModBlocks.DRIFTWOOD_LOG).log(ModBlocks.DRIFTWOOD_LOG).wood(ModBlocks.DRIFTWOOD_WOOD);
        generator.family(ModBlocks.DRIFTWOOD_PLANKS)
                .fence(ModBlocks.DRIFTWOOD_FENCE)
                .fenceGate(ModBlocks.DRIFTWOOD_GATE)
                .button(ModBlocks.DRIFTWOOD_BUTTON)
                .slab(ModBlocks.DRIFTWOOD_SLAB)
                .stairs(ModBlocks.DRIFTWOOD_STAIRS);
        generator.createDoor(ModBlocks.DRIFTWOOD_DOOR);
        generator.createTrapdoor(ModBlocks.DRIFTWOOD_TRAPDOOR);

        generator.family(ModBlocks.AMALGAM_BLOCK)
                .slab(ModBlocks.AMALGAM_SLAB)
                .stairs(ModBlocks.AMALGAM_STAIRS);
        generator.createDoor(ModBlocks.AMALGAM_DOOR);
        generator.createTrapdoor(ModBlocks.AMALGAM_TRAPDOOR);
        generator.createTrivialCube(ModBlocks.AMALGAM_ORE);
        generator.createTrivialCube(ModBlocks.RUST);

        generator.createTrivialCube(ModBlocks.CLOD_ORE);
        generator.createTrivialCube(ModBlocks.CLOD_BLOCK);
        registerSingleTextureCube(generator, ModBlocks.PALE_SAND, ResourceLocation.parse("minecraft:block/white_concrete_powder"));
        registerCarpetLikeBlock(generator, ModBlocks.DARK_SAND_LAYER, TextureMapping.getBlockTexture(ModBlocks.DARK_SAND));
        registerCarpetLikeBlock(generator, ModBlocks.LINT_LAYER, TextureMapping.getBlockTexture(ModBlocks.UNRAVELLED_FABRIC));

        generateDecaySet(generator, Blocks.RED_SAND, ModBlocks.RED_SAND_SET);
        generateDecaySet(generator, Blocks.GRAVEL, ModBlocks.GRAVEL_SET);
        generator.createTrivialCube(ModBlocks.DARK_SAND);
        generateDecaySet(generator, ModBlocks.DARK_SAND, ModBlocks.DARK_SAND_SET);
        generateDecaySet(generator, Blocks.CLAY, ModBlocks.CLAY_SET);
        generateDecaySet(generator, Blocks.TERRACOTTA, ModBlocks.TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.WHITE_TERRACOTTA, ModBlocks.WHITE_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.WHITE_GLAZED_TERRACOTTA, ModBlocks.WHITE_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.ORANGE_TERRACOTTA, ModBlocks.ORANGE_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.ORANGE_GLAZED_TERRACOTTA, ModBlocks.ORANGE_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.MAGENTA_TERRACOTTA, ModBlocks.MAGENTA_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.MAGENTA_GLAZED_TERRACOTTA, ModBlocks.MAGENTA_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.LIGHT_BLUE_TERRACOTTA, ModBlocks.LIGHT_BLUE_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, ModBlocks.LIGHT_BLUE_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.YELLOW_TERRACOTTA, ModBlocks.YELLOW_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.YELLOW_GLAZED_TERRACOTTA, ModBlocks.YELLOW_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.LIME_TERRACOTTA, ModBlocks.LIME_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.LIME_GLAZED_TERRACOTTA, ModBlocks.LIME_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.PINK_TERRACOTTA, ModBlocks.PINK_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.PINK_GLAZED_TERRACOTTA, ModBlocks.PINK_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.GRAY_TERRACOTTA, ModBlocks.GRAY_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.GRAY_GLAZED_TERRACOTTA, ModBlocks.GRAY_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.LIGHT_GRAY_TERRACOTTA, ModBlocks.LIGHT_GRAY_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, ModBlocks.LIGHT_GRAY_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.CYAN_TERRACOTTA, ModBlocks.CYAN_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.CYAN_GLAZED_TERRACOTTA, ModBlocks.CYAN_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.PURPLE_TERRACOTTA, ModBlocks.PURPLE_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.PURPLE_GLAZED_TERRACOTTA, ModBlocks.PURPLE_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.BLUE_TERRACOTTA, ModBlocks.BLUE_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.BLUE_GLAZED_TERRACOTTA, ModBlocks.BLUE_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.BROWN_TERRACOTTA, ModBlocks.BROWN_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.BROWN_GLAZED_TERRACOTTA, ModBlocks.BROWN_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.GREEN_TERRACOTTA, ModBlocks.GREEN_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.GREEN_GLAZED_TERRACOTTA, ModBlocks.GREEN_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.RED_TERRACOTTA, ModBlocks.RED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.RED_GLAZED_TERRACOTTA, ModBlocks.RED_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.BLACK_TERRACOTTA, ModBlocks.BLACK_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.BLACK_GLAZED_TERRACOTTA, ModBlocks.BLACK_GLAZED_TERRACOTTA_SET);


        generateDecaySet(generator, Blocks.MUD, ModBlocks.MUD_SET);
        generator.createTrivialCube(ModBlocks.UNRAVELLED_FABRIC);
        generateDecaySet(generator, ModBlocks.UNRAVELLED_FABRIC, ModBlocks.UNRAVELED_SET);
        generateDecaySet(generator, Blocks.DEEPSLATE, ModBlocks.DEEPSLATE_SET);
        generateDecaySet(generator, Blocks.SAND, ModBlocks.SAND_SET);
        generateDecaySet(generator, Blocks.END_STONE, ModBlocks.END_STONE_SET);
        generateDecaySet(generator, Blocks.NETHERRACK, ModBlocks.NETHERRACK_SET);
        generateStoneSet(generator);

        generator.createTrivialCube(ModBlocks.DRIFTWOOD_LEAVES);
        generator.createCrossBlockWithDefaultItem(ModBlocks.DRIFTWOOD_SAPLING, BlockModelGenerators.TintState.NOT_TINTED); //TODO: Decide if we need potted version
        generator.createTrivialCube(ModBlocks.GRITTY_STONE);
        generator.family(ModBlocks.REALITY_SPONGE);

        registerUnraveledSpike(generator);


        generator.createAirLikeBlock(ModBlocks.LIMBO_AIR, Blocks.BARRIER.asItem());
    }

    private void generateDecaySet(BlockModelGenerators generator, Block textureSource, ModBlocks.DecayGroupSet set) {
        TextureMapping mapping = getTextureMapping(generator, textureSource);
        ResourceLocation fullBlockModel = ModelLocationUtils.getModelLocation(textureSource);

        generateButton(generator, set.button(), mapping);
        generateSlab(generator, set.slab(), mapping, fullBlockModel);
        generateStairs(generator, set.stairs(), mapping);
        generateWall(generator, set.wall(), mapping);
        generateFence(generator, set.fence(), mapping);
        generateFenceGate(generator, set.gate(), mapping);
    }

    private void generateStoneSet(BlockModelGenerators generator) {
        TextureMapping mapping = getTextureMapping(generator, Blocks.STONE);
        ResourceLocation fullBlockModel = ModelLocationUtils.getModelLocation(Blocks.STONE);

        generateSlab(generator, ModBlocks.STONE_SLAB, mapping, fullBlockModel);
        generateStairs(generator, ModBlocks.STONE_STAIRS, mapping);
        generateWall(generator, ModBlocks.STONE_WALL, mapping);
    }

    private TextureMapping getTextureMapping(BlockModelGenerators generator, Block textureSource) {
        TexturedModel texturedModel = generator.texturedModels.getOrDefault(textureSource, TexturedModel.CUBE.get(textureSource));
        return texturedModel.getMapping();
    }

    private void generateButton(BlockModelGenerators generator, Block buttonBlock, TextureMapping mapping) {
        ResourceLocation buttonModel = ModelTemplates.BUTTON.create(buttonBlock, mapping, generator.modelOutput);
        ResourceLocation pressedModel = ModelTemplates.BUTTON_PRESSED.create(buttonBlock, mapping, generator.modelOutput);
        generator.blockStateOutput.accept(BlockModelGenerators.createButton(buttonBlock, buttonModel, pressedModel));
        ResourceLocation inventoryModel = ModelTemplates.BUTTON_INVENTORY.create(buttonBlock, mapping, generator.modelOutput);
        generator.delegateItemModel(buttonBlock, inventoryModel);
    }

    private void generateSlab(BlockModelGenerators generator, Block slabBlock, TextureMapping mapping, ResourceLocation fullBlockModel) {
        ResourceLocation bottomModel = ModelTemplates.SLAB_BOTTOM.create(slabBlock, mapping, generator.modelOutput);
        ResourceLocation topModel = ModelTemplates.SLAB_TOP.create(slabBlock, mapping, generator.modelOutput);
        generator.blockStateOutput.accept(BlockModelGenerators.createSlab(slabBlock, bottomModel, topModel, fullBlockModel));
        generator.delegateItemModel(slabBlock, bottomModel);
    }

    private void generateStairs(BlockModelGenerators generator, Block stairsBlock, TextureMapping mapping) {
        ResourceLocation innerModel = ModelTemplates.STAIRS_INNER.create(stairsBlock, mapping, generator.modelOutput);
        ResourceLocation straightModel = ModelTemplates.STAIRS_STRAIGHT.create(stairsBlock, mapping, generator.modelOutput);
        ResourceLocation outerModel = ModelTemplates.STAIRS_OUTER.create(stairsBlock, mapping, generator.modelOutput);
        generator.blockStateOutput.accept(BlockModelGenerators.createStairs(stairsBlock, innerModel, straightModel, outerModel));
        generator.delegateItemModel(stairsBlock, straightModel);
    }

    private void generateWall(BlockModelGenerators generator, Block wallBlock, TextureMapping mapping) {
        ResourceLocation postModel = ModelTemplates.WALL_POST.create(wallBlock, mapping, generator.modelOutput);
        ResourceLocation lowSideModel = ModelTemplates.WALL_LOW_SIDE.create(wallBlock, mapping, generator.modelOutput);
        ResourceLocation tallSideModel = ModelTemplates.WALL_TALL_SIDE.create(wallBlock, mapping, generator.modelOutput);
        generator.blockStateOutput.accept(BlockModelGenerators.createWall(wallBlock, postModel, lowSideModel, tallSideModel));
        ResourceLocation inventoryModel = ModelTemplates.WALL_INVENTORY.create(wallBlock, mapping, generator.modelOutput);
        generator.delegateItemModel(wallBlock, inventoryModel);
    }

    private void generateFence(BlockModelGenerators generator, Block fenceBlock, TextureMapping mapping) {
        ResourceLocation postModel = ModelTemplates.FENCE_POST.create(fenceBlock, mapping, generator.modelOutput);
        ResourceLocation sideModel = ModelTemplates.FENCE_SIDE.create(fenceBlock, mapping, generator.modelOutput);
        generator.blockStateOutput.accept(BlockModelGenerators.createFence(fenceBlock, postModel, sideModel));
        ResourceLocation inventoryModel = ModelTemplates.FENCE_INVENTORY.create(fenceBlock, mapping, generator.modelOutput);
        generator.delegateItemModel(fenceBlock, inventoryModel);
    }

    private void generateFenceGate(BlockModelGenerators generator, Block fenceGateBlock, TextureMapping mapping) {
        ResourceLocation openModel = ModelTemplates.FENCE_GATE_OPEN.create(fenceGateBlock, mapping, generator.modelOutput);
        ResourceLocation closedModel = ModelTemplates.FENCE_GATE_CLOSED.create(fenceGateBlock, mapping, generator.modelOutput);
        ResourceLocation wallOpenModel = ModelTemplates.FENCE_GATE_WALL_OPEN.create(fenceGateBlock, mapping, generator.modelOutput);
        ResourceLocation wallClosedModel = ModelTemplates.FENCE_GATE_WALL_CLOSED.create(fenceGateBlock, mapping, generator.modelOutput);
        generator.blockStateOutput.accept(BlockModelGenerators.createFenceGate(fenceGateBlock, openModel, closedModel, wallOpenModel, wallClosedModel, true));
        generator.delegateItemModel(fenceGateBlock, closedModel);
    }

    private void registerSingleTextureCube(BlockModelGenerators generator, Block block, ResourceLocation texture) {
        ResourceLocation model = ModelTemplates.CUBE_ALL.create(block, TextureMapping.cube(texture), generator.modelOutput);
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, model)));
    }

    private void registerCarpetLikeBlock(BlockModelGenerators generator, Block block, ResourceLocation texture) {
        ResourceLocation model = ModelTemplates.CARPET.create(block, TextureMapping.wool(texture), generator.modelOutput);
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, model)));
    }

    private void registerUnraveledSpike(BlockModelGenerators generators) {
        generators.skipAutoItemBlock(ModBlocks.UNRAVELED_SPIKE);
        PropertyDispatch.C2<Direction, DripstoneThickness> c2 = PropertyDispatch.properties(BlockStateProperties.VERTICAL_DIRECTION, BlockStateProperties.DRIPSTONE_THICKNESS);

        for(DripstoneThickness dripstoneThickness : DripstoneThickness.values()) {
            c2.select(Direction.UP, dripstoneThickness, createPointedDripstoneVariant(generators, Direction.UP, dripstoneThickness));
        }

        for(DripstoneThickness dripstoneThickness : DripstoneThickness.values()) {
            c2.select(Direction.DOWN, dripstoneThickness, createPointedDripstoneVariant(generators, Direction.DOWN, dripstoneThickness));
        }

        generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.UNRAVELED_SPIKE).with(c2));
    }

    final Variant createPointedDripstoneVariant(BlockModelGenerators generators, Direction direction, DripstoneThickness dripstoneThickness) {
        String var10000 = direction.getSerializedName();
        String string = "_" + var10000 + "_" + dripstoneThickness.getSerializedName();
        TextureMapping textureMapping = TextureMapping.cross(TextureMapping.getBlockTexture(ModBlocks.UNRAVELED_SPIKE, string));
        return Variant.variant().with(VariantProperties.MODEL, ModelTemplates.POINTED_DRIPSTONE.createWithSuffix(ModBlocks.UNRAVELLED_BLOCK, string, textureMapping, generators.modelOutput));
    }

    public void registerDoor(BlockModelGenerators generator, Block doorBlock, Block textureSource) {
        TextureMapping textureMap = TextureMapping.door(textureSource);
        ResourceLocation identifier = ModelTemplates.DOOR_BOTTOM_LEFT.create(doorBlock, textureMap, generator.modelOutput);
        ResourceLocation identifier2 = ModelTemplates.DOOR_BOTTOM_LEFT_OPEN.create(doorBlock, textureMap, generator.modelOutput);
        ResourceLocation identifier3 = ModelTemplates.DOOR_BOTTOM_RIGHT.create(doorBlock, textureMap, generator.modelOutput);
        ResourceLocation identifier4 = ModelTemplates.DOOR_BOTTOM_RIGHT_OPEN.create(doorBlock, textureMap, generator.modelOutput);
        ResourceLocation identifier5 = ModelTemplates.DOOR_TOP_LEFT.create(doorBlock, textureMap, generator.modelOutput);
        ResourceLocation identifier6 = ModelTemplates.DOOR_TOP_LEFT_OPEN.create(doorBlock, textureMap, generator.modelOutput);
        ResourceLocation identifier7 = ModelTemplates.DOOR_TOP_RIGHT.create(doorBlock, textureMap, generator.modelOutput);
        ResourceLocation identifier8 = ModelTemplates.DOOR_TOP_RIGHT_OPEN.create(doorBlock, textureMap, generator.modelOutput);
        generator.createSimpleFlatItemModel(doorBlock.asItem());
        generator.blockStateOutput.accept(BlockModelGenerators.createDoor(doorBlock, identifier, identifier2, identifier3, identifier4, identifier5, identifier6, identifier7, identifier8));
    }

    public void registerAutoGenDoor(BlockModelGenerators generator, DimensionalDoorBlockRegistrar.AutoGenDimensionalDoorBlock doorBlock) {
        Block textureSource = doorBlock.getOriginalBlock();

        ResourceLocation identifier = getBlockTexture(textureSource, "_bottom_left");
        ResourceLocation identifier2 = getBlockTexture(textureSource, "_bottom_left_open");
        ResourceLocation identifier3 = getBlockTexture(textureSource, "_bottom_right");
        ResourceLocation identifier4 = getBlockTexture(textureSource, "_bottom_right_open");
        ResourceLocation identifier5 = getBlockTexture(textureSource, "_top_left");
        ResourceLocation identifier6 = getBlockTexture(textureSource, "_top_left_open");
        ResourceLocation identifier7 = getBlockTexture(textureSource, "_top_right");
        ResourceLocation identifier8 = getBlockTexture(textureSource, "_top_right_open");
        ModelTemplates.TWO_LAYERED_ITEM.create(ModelLocationUtils.getModelLocation(doorBlock), TextureMapping.layered(DimensionalDoors.id("item/dimdoor_back"), getItemTexture(textureSource.asItem())), generator.modelOutput);
        generator.blockStateOutput.accept(BlockModelGenerators.createDoor(doorBlock, identifier, identifier2, identifier3, identifier4, identifier5, identifier6, identifier7, identifier8));
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(ModItems.FUZZY_FIREBALL, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.FABRIC_OF_FINALITY, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.LIMINAL_LINT, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.ENDURING_FIBERS, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.RIFT_PEARL, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.AMALGAM_LUMP, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.CLOD, ModelTemplates.FLAT_ITEM);

        itemModelGenerator.generateFlatItem(ModItems.GARMENT_OF_REALITY_ARMOR.boots(), ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.GARMENT_OF_REALITY_ARMOR.chestplate(), ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.GARMENT_OF_REALITY_ARMOR.helmet(), ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.GARMENT_OF_REALITY_ARMOR.leggings(), ModelTemplates.FLAT_ITEM);
    }
}
