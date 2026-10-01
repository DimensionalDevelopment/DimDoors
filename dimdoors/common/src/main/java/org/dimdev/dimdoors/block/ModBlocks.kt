package org.dimdev.dimdoors.block

import net.minecraft.core.Holder
import net.minecraft.util.ColorRGBA
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.BlockSetType
import net.minecraft.world.level.block.state.properties.WoodType
import net.minecraft.world.level.material.MapColor
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided
import org.dimdev.dimdoors.block.door.DialingDoor
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes
import org.dimdev.dimdoors.item.ModItems.DECAY
import org.dimdev.dimdoors.item.ModItems.DIMENSIONAL_DOORS
import org.dimdev.dimdoors.item.PlaceOnlyOnRiftBlockItem
import org.dimdev.dimdoors.item.door.EntranceRiftBlockItem

object ModBlocks : PlatformRegistry.BlockItemPlatformRegistry(getSided()) {
    @JvmField val FABRIC_BLOCKS = mutableMapOf<DyeColor, Block>()
    private val ANCIENT_FABRIC_BLOCKS = mutableMapOf<DyeColor, Block>()

    @JvmField val STONE_PLAYER = create("stone_player") {
        blockProperties(Blocks.STONE) {
            strength(0.5f)
            noOcclusion()
        }
    }

    @JvmField val GOLD_DOOR = door("gold_door", Blocks.GOLD_BLOCK, BlockSetType.GOLD) {
        noOcclusion()
    }

    @JvmField val STONE_DOOR = door("stone_door", Blocks.STONE, BlockSetType.IRON) {
        mapColor(MapColor.WOOD)
    }

    @JvmField val QUARTZ_DOOR = door("quartz_door", Blocks.QUARTZ_BLOCK, BlockSetType.IRON) {
        noOcclusion()
    }

    @JvmField val DIMENSIONAL_PORTAL = create("dimensional_portal") {
        block(::DimensionalPortalBlock)
        blockProperties {
            noLootTable()
            strength(-1.0f, 3600000.0f)
            noOcclusion()
            dropsLike(Blocks.AIR)
            lightLevel { 10 }
        }
        item()
    }

    @JvmField val DETACHED_RIFT = create("detached_rift") {
        block(::DetachedRiftBlock)
        blockProperties {
            noCollission()
            noLootTable()
            mapColor(MapColor.COLOR_BLACK)
            strength(-1.0f, 3600000.0f)
        }
    }

    @JvmField val DIALING_DOOR = regular("dialing_door") {
        block { DialingDoor(it, BlockSetType.IRON) }
        blockProperties(Blocks.GOLD_BLOCK) { requiresCorrectToolForDrops() }
        item(::EntranceRiftBlockItem)
    }

    @JvmField val WHITE_FABRIC = DyeColor.WHITE.fabric()
    @JvmField val ORANGE_FABRIC = DyeColor.ORANGE.fabric()
    @JvmField val MAGENTA_FABRIC = DyeColor.MAGENTA.fabric()
    @JvmField val LIGHT_BLUE_FABRIC = DyeColor.LIGHT_BLUE.fabric()
    @JvmField val YELLOW_FABRIC = DyeColor.YELLOW.fabric()
    @JvmField val LIME_FABRIC = DyeColor.LIME.fabric()
    @JvmField val PINK_FABRIC = DyeColor.PINK.fabric()
    @JvmField val GRAY_FABRIC = DyeColor.GRAY.fabric()
    @JvmField val LIGHT_GRAY_FABRIC = DyeColor.LIGHT_GRAY.fabric()
    @JvmField val CYAN_FABRIC = DyeColor.CYAN.fabric()
    @JvmField val PURPLE_FABRIC = DyeColor.PURPLE.fabric()
    @JvmField val BLUE_FABRIC = DyeColor.BLUE.fabric()
    @JvmField val BROWN_FABRIC = DyeColor.BROWN.fabric()
    @JvmField val GREEN_FABRIC = DyeColor.GREEN.fabric()
    @JvmField val RED_FABRIC = DyeColor.RED.fabric()
    @JvmField val BLACK_FABRIC = DyeColor.BLACK.fabric()

    @JvmField val WHITE_ANCIENT_FABRIC = DyeColor.WHITE.ancientFabric()
    @JvmField val ORANGE_ANCIENT_FABRIC = DyeColor.ORANGE.ancientFabric()
    @JvmField val MAGENTA_ANCIENT_FABRIC = DyeColor.MAGENTA.ancientFabric()
    @JvmField val LIGHT_BLUE_ANCIENT_FABRIC = DyeColor.LIGHT_BLUE.ancientFabric()
    @JvmField val YELLOW_ANCIENT_FABRIC = DyeColor.YELLOW.ancientFabric()
    @JvmField val LIME_ANCIENT_FABRIC = DyeColor.LIME.ancientFabric()
    @JvmField val PINK_ANCIENT_FABRIC = DyeColor.PINK.ancientFabric()
    @JvmField val GRAY_ANCIENT_FABRIC = DyeColor.GRAY.ancientFabric()
    @JvmField val LIGHT_GRAY_ANCIENT_FABRIC = DyeColor.LIGHT_GRAY.ancientFabric()
    @JvmField val CYAN_ANCIENT_FABRIC = DyeColor.CYAN.ancientFabric()
    @JvmField val PURPLE_ANCIENT_FABRIC = DyeColor.PURPLE.ancientFabric()
    @JvmField val BLUE_ANCIENT_FABRIC = DyeColor.BLUE.ancientFabric()
    @JvmField val BROWN_ANCIENT_FABRIC = DyeColor.BROWN.ancientFabric()
    @JvmField val GREEN_ANCIENT_FABRIC = DyeColor.GREEN.ancientFabric()
    @JvmField val RED_ANCIENT_FABRIC = DyeColor.RED.ancientFabric()
    @JvmField val BLACK_ANCIENT_FABRIC = DyeColor.BLACK.ancientFabric()

    private val UNRAVELLED_FABRIC_BLOCK_SETTINGS: BlockBehaviour.Properties.() -> Unit = {
        mapColor(MapColor.COLOR_BLACK)
        randomTicks()
        lightLevel { 15 }
        strength(0.3f, 0.3f)
    }

    @JvmField val ETERNAL_FLUID = create("eternal_fluid") {
        block(::EternalFluidBlock)
        blockProperties(Blocks.LAVA) {
            mapColor(MapColor.COLOR_RED)
            lightLevel { 15 }
        }
    }

    @JvmField val LEAK = create("leak") {
        block(::LeakLiquidBlock)
        blockProperties(Blocks.WATER)
    }

    @JvmField val DECAYED_BLOCK = unravelled("decayed_block")
    @JvmField val UNFOLDED_BLOCK = unravelled("unfolded_block")
    @JvmField val UNWARPED_BLOCK = unravelled("unwarped_block")
    @JvmField val UNRAVELLED_BLOCK = unravelled("unravelled_block")
    @JvmField val UNRAVELLED_FABRIC = unravelled("unravelled_fabric") { tab(DIMENSIONAL_DOORS) }

    @JvmField val MARKING_PLATE = create("marking_plate") {
        blockProperties(Blocks.IRON_BLOCK) {
            mapColor(DyeColor.BLACK)
            noOcclusion()
        }
    }

    @JvmField val SOLID_STATIC = regular("solid_static") {
        block(::UnravelledFabricBlock)
        blockProperties(Blocks.STONE) {
            strength(7f, 25f)
            randomTicks()
            requiresCorrectToolForDrops()
            sound(SoundType.SAND)
        }
    }

    @JvmField val TESSELATING_LOOM = regular("tesselating_loom") {
        block(::TesselatingLoomBlock)
        blockProperties(Blocks.LOOM)
    }

    @JvmField val LIMINAL_TRANSMITTER = regular("liminal_transmitter") {
        block(::LiminalTransmitterBlock)
        blockProperties(Blocks.IRON_BLOCK)
        item({ block, properties -> PlaceOnlyOnRiftBlockItem(block, properties) })
    }

    @JvmField val REALITY_SPONGE = regular("reality_sponge") {
        block(::RealitySpongeBlock)
        blockProperties(Blocks.STONE, UNRAVELLED_FABRIC_BLOCK_SETTINGS)
    }

    @JvmField val LIMBO_AIR = create("limbo_air") {
        block(::LimboAirBlock)
        blockProperties {
            randomTicks()
            replaceable()
            noCollission()
            noLootTable()
            isSuffocating { _, _, _ -> false }
            isViewBlocking { _, _, _ -> false }
        }
    }

    //Decay graph filler.
    @JvmField val CLOD_ORE = decay("clod_ore") { blockProperties(Blocks.AMETHYST_BLOCK) }
    @JvmField val CLOD_BLOCK = decay("clod_block") { blockProperties(Blocks.AMETHYST_BLOCK) }

    @JvmField val AMALGAM_BLOCK = decay("amalgam_block") {
        blockProperties(Blocks.IRON_BLOCK) {
            mapColor(MapColor.COLOR_LIGHT_GRAY)
            requiresCorrectToolForDrops()
            strength(5.0f, 6.0f)
            sound(SoundType.METAL)
        }
    }

    @JvmField val AMALGAM_DOOR = decay("amalgam_door") {
        block { DoorBlock(BlockSetType.IRON, it) }
        blockProperties(Blocks.IRON_BLOCK) {
            mapColor(MapColor.COLOR_LIGHT_GRAY)
            requiresCorrectToolForDrops()
            strength(5.0f)
            sound(SoundType.METAL)
            noOcclusion()
        }
    }

    @JvmField val AMALGAM_TRAPDOOR = decay("amalgam_trapdoor") {
        block { TrapDoorBlock(BlockSetType.IRON, it) }
        blockProperties(Blocks.IRON_BLOCK) {
            requiresCorrectToolForDrops()
            strength(5.0f)
            sound(SoundType.METAL)
            isValidSpawn { _, _, _, _ -> false }
        }
    }

    @JvmField val AMALGAM_SLAB = decay("amalgam_slab") {
        block(::SlabBlock)
        blockProperties(AMALGAM_BLOCK)
    }

    @JvmField val AMALGAM_STAIRS = decay("amalgam_stairs") {
        block { StairBlock(AMALGAM_BLOCK.defaultBlockState(), it) }
        blockProperties(AMALGAM_BLOCK)
    }

    @JvmField val AMALGAM_ORE = decay("amalgam_ore") {
        block { DropExperienceBlock(ConstantInt.of(1), it) }
        blockProperties(Blocks.STONE) {
            requiresCorrectToolForDrops()
            strength(3.0f, 3.0f)
        }
    }

    @JvmField val RUST = decay("rust") { blockProperties(Blocks.OAK_WOOD) }

    @JvmField val DRIFTWOOD_WOOD = decay("driftwood_wood") {
        block(::RotatedPillarBlock)
        blockProperties(Blocks.OAK_WOOD) {
            mapColor(MapColor.COLOR_LIGHT_GRAY)
            strength(2.0f)
            sound(SoundType.WOOD)
        }
    }

    @JvmField val DRIFTWOOD_LOG = decay("driftwood_log") {
        block(::RotatedPillarBlock)
        blockProperties(Blocks.OAK_WOOD) {
            mapColor(MapColor.COLOR_LIGHT_GRAY)
            strength(2.0f)
            sound(SoundType.WOOD)
        }
    }

    @JvmField val DRIFTWOOD_PLANKS = decay("driftwood_planks") {
        blockProperties(Blocks.OAK_WOOD) {
            mapColor(MapColor.COLOR_LIGHT_GRAY)
            strength(2.0f, 3.0f)
            sound(SoundType.WOOD)
        }
    }

    @JvmField val DRIFTWOOD_LEAVES = decay("driftwood_leaves") {
        block(::LeavesBlock)
        blockProperties(Blocks.OAK_LEAVES)
    }

    @JvmField val DRIFTWOOD_SAPLING = decay("driftwood_sapling") {
        block(::DriftwoodSaplingBlock)
        blockProperties(Blocks.OAK_SAPLING)
    }

    @JvmField val DRIFTWOOD_FENCE = decay("driftwood_fence") {
        block(::FenceBlock)
        blockProperties(DRIFTWOOD_PLANKS)
    }

    @JvmField val DRIFTWOOD_GATE = decay("driftwood_gate") {
        block { FenceGateBlock(WoodType.OAK, it) }
        blockProperties(DRIFTWOOD_PLANKS)
    }

    @JvmField val DRIFTWOOD_BUTTON = decay("driftwood_button") {
        block { ButtonBlock(BlockSetType.STONE, 20, it) }
        blockProperties(DRIFTWOOD_PLANKS) {
            noCollission()
            strength(0.5f)
        }
    }

    @JvmField val DRIFTWOOD_SLAB = decay("driftwood_slab") {
        block(::SlabBlock)
        blockProperties(DRIFTWOOD_PLANKS)
    }

    @JvmField val DRIFTWOOD_STAIRS = decay("driftwood_stairs") {
        block { StairBlock(DRIFTWOOD_PLANKS.defaultBlockState(), it) }
        blockProperties(DRIFTWOOD_PLANKS)
    }

    @JvmField val DRIFTWOOD_DOOR = decay("driftwood_door") {
        block { DoorBlock(BlockSetType.OAK, it) }
        blockProperties(Blocks.OAK_WOOD) {
            mapColor(MapColor.COLOR_GRAY)
            strength(3.0f)
            sound(SoundType.WOOD)
            noOcclusion()
        }
    }

    @JvmField val DRIFTWOOD_TRAPDOOR = decay("driftwood_trapdoor") {
        block { TrapDoorBlock(BlockSetType.OAK, it) }
        blockProperties(Blocks.OAK_WOOD) {
            mapColor(MapColor.COLOR_GRAY)
            strength(3.0f)
            sound(SoundType.WOOD)
            noOcclusion()
            isValidSpawn { _, _, _, _ -> false }
        }
    }

    @JvmField val DARK_SAND = decay("dark_sand") {
        blockProperties(Blocks.SAND) {
            mapColor(MapColor.COLOR_BLACK)
            strength(0.5f)
            sound(SoundType.SAND)
        }
    }

    @JvmField val PALE_SAND = decay("pale_sand") {
        block { ColoredFallingBlock(ColorRGBA(-0xd162f), it) }
        blockProperties(Blocks.SAND) {
            mapColor(MapColor.COLOR_LIGHT_GRAY)
            strength(0.5f)
            sound(SoundType.SAND)
        }
    }

    @JvmField val DARK_SAND_LAYER = decay("dark_sand_layer") {
        block(::CarpetBlock)
        blockProperties(Blocks.MOSS_CARPET) {
            mapColor(MapColor.COLOR_BLACK)
            sound(SoundType.SAND)
        }
    }

    @JvmField val LINT_LAYER = decay("lint_layer") {
        block(::CarpetBlock)
        blockProperties(Blocks.MOSS_CARPET) {
            mapColor(MapColor.COLOR_LIGHT_GRAY)
        }
    }

    @JvmField val STONE_SLAB = decay("stone_slab") {
        block(::SlabBlock)
        blockProperties(Blocks.STONE)
    }

    @JvmField val STONE_STAIRS = decay("stone_stairs") {
        block { StairBlock(Blocks.STONE.defaultBlockState(), it) }
        blockProperties(Blocks.STONE)
    }

    @JvmField val STONE_WALL = decay("stone_wall") {
        block(::WallBlock)
        blockProperties(Blocks.STONE)
    }

    @JvmField val GRAVEL_SET = DecayGroupSet.create("gravel", Blocks.GRAVEL)
    @JvmField val DARK_SAND_SET = DecayGroupSet.create("dark_sand", DARK_SAND)
    @JvmField val CLAY_SET = DecayGroupSet.create("clay", Blocks.CLAY)
    @JvmField val TERRACOTTA_SET = DecayGroupSet.create("terracotta", Blocks.TERRACOTTA)
    @JvmField val WHITE_TERRACOTTA_SET = DecayGroupSet.create("white_terracotta", Blocks.WHITE_TERRACOTTA)
    @JvmField val WHITE_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("white_glazed_terracotta", Blocks.WHITE_GLAZED_TERRACOTTA)
    @JvmField val ORANGE_TERRACOTTA_SET = DecayGroupSet.create("orange_terracotta", Blocks.ORANGE_TERRACOTTA)
    @JvmField val ORANGE_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("orange_glazed_terracotta", Blocks.ORANGE_GLAZED_TERRACOTTA)
    @JvmField val MAGENTA_TERRACOTTA_SET = DecayGroupSet.create("magenta_terracotta", Blocks.MAGENTA_TERRACOTTA)
    @JvmField val MAGENTA_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("magenta_glazed_terracotta", Blocks.MAGENTA_GLAZED_TERRACOTTA)
    @JvmField val LIGHT_BLUE_TERRACOTTA_SET = DecayGroupSet.create("light_blue_terracotta", Blocks.LIGHT_BLUE_TERRACOTTA)
    @JvmField val LIGHT_BLUE_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("light_blue_glazed_terracotta", Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA)
    @JvmField val YELLOW_TERRACOTTA_SET = DecayGroupSet.create("yellow_terracotta", Blocks.YELLOW_TERRACOTTA)
    @JvmField val YELLOW_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("yellow_glazed_terracotta", Blocks.YELLOW_GLAZED_TERRACOTTA)
    @JvmField val LIME_TERRACOTTA_SET = DecayGroupSet.create("lime_terracotta", Blocks.LIME_TERRACOTTA)
    @JvmField val LIME_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("lime_glazed_terracotta", Blocks.LIME_GLAZED_TERRACOTTA)
    @JvmField val PINK_TERRACOTTA_SET = DecayGroupSet.create("pink_terracotta", Blocks.PINK_TERRACOTTA)
    @JvmField val PINK_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("pink_glazed_terracotta", Blocks.PINK_GLAZED_TERRACOTTA)
    @JvmField val GRAY_TERRACOTTA_SET = DecayGroupSet.create("gray_terracotta", Blocks.GRAY_TERRACOTTA)
    @JvmField val GRAY_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("gray_glazed_terracotta", Blocks.GRAY_GLAZED_TERRACOTTA)
    @JvmField val LIGHT_GRAY_TERRACOTTA_SET = DecayGroupSet.create("light_gray_terracotta", Blocks.LIGHT_GRAY_TERRACOTTA)
    @JvmField val LIGHT_GRAY_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("light_gray_glazed_terracotta", Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA)
    @JvmField val CYAN_TERRACOTTA_SET = DecayGroupSet.create("cyan_terracotta", Blocks.CYAN_TERRACOTTA)
    @JvmField val CYAN_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("cyan_glazed_terracotta", Blocks.CYAN_GLAZED_TERRACOTTA)
    @JvmField val PURPLE_TERRACOTTA_SET = DecayGroupSet.create("purple_terracotta", Blocks.PURPLE_TERRACOTTA)
    @JvmField val PURPLE_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("purple_glazed_terracotta", Blocks.PURPLE_GLAZED_TERRACOTTA)
    @JvmField val BLUE_TERRACOTTA_SET = DecayGroupSet.create("blue_terracotta", Blocks.BLUE_TERRACOTTA)
    @JvmField val BLUE_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("blue_glazed_terracotta", Blocks.BLUE_GLAZED_TERRACOTTA)
    @JvmField val BROWN_TERRACOTTA_SET = DecayGroupSet.create("brown_terracotta", Blocks.BROWN_TERRACOTTA)
    @JvmField val BROWN_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("brown_glazed_terracotta", Blocks.BROWN_GLAZED_TERRACOTTA)
    @JvmField val GREEN_TERRACOTTA_SET = DecayGroupSet.create("green_terracotta", Blocks.GREEN_TERRACOTTA)
    @JvmField val GREEN_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("green_glazed_terracotta", Blocks.GREEN_GLAZED_TERRACOTTA)
    @JvmField val RED_TERRACOTTA_SET = DecayGroupSet.create("red_terracotta", Blocks.RED_TERRACOTTA)
    @JvmField val RED_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("red_glazed_terracotta", Blocks.RED_GLAZED_TERRACOTTA)
    @JvmField val BLACK_TERRACOTTA_SET = DecayGroupSet.create("black_terracotta", Blocks.BLACK_TERRACOTTA)
    @JvmField val BLACK_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("black_glazed_terracotta", Blocks.BLACK_GLAZED_TERRACOTTA)

    @JvmField val MUD_SET = DecayGroupSet.create("mud", Blocks.MUD) {
        isViewBlocking { _, _, _ -> false }
        isSuffocating { _, _, _ -> false }
    }
    @JvmField val UNRAVELED_SET = DecayGroupSet.create("unraveled", UNRAVELLED_FABRIC)
    @JvmField val DEEPSLATE_SET = DecayGroupSet.create("deepslate", Blocks.DEEPSLATE)
    @JvmField val RED_SAND_SET = DecayGroupSet.create("red_sand", Blocks.RED_SAND)
    @JvmField val SAND_SET = DecayGroupSet.create("sand", Blocks.SAND)
    @JvmField val END_STONE_SET = DecayGroupSet.create("end_stone", Blocks.END_STONE)
    @JvmField val NETHERRACK_SET = DecayGroupSet.create("netherrack", Blocks.NETHERRACK)

    @JvmField val UNRAVELED_SPIKE = decay("unraveled_spike") { //TODO: make this proper class later
        block(::PointedDripstoneBlock)
        blockProperties(UNRAVELLED_FABRIC) { lightLevel { 0 } }
    }

    @JvmField val GRITTY_STONE = decay("gritty_stone") { blockProperties(Blocks.STONE) }

    fun init() {
        ModBlockEntityTypes.DETACHED_RIFT.addBlock(DETACHED_RIFT)
        ModBlockEntityTypes.TESSELATING_LOOM.addBlock(TESSELATING_LOOM)
        ModBlockEntityTypes.ENTRANCE_RIFT.addBlock(DIMENSIONAL_PORTAL)
        ModBlockEntityTypes.DIALING_DOOR.addBlock(DIALING_DOOR)
        ModBlockEntityTypes.GENERIC_RIFT.addBlock(LIMINAL_TRANSMITTER)
    }

    @JvmStatic
    fun ancientFabricFromDye(color: DyeColor): Block? = ANCIENT_FABRIC_BLOCKS[color]

    @JvmStatic
    fun fabricFromDye(color: DyeColor): Block? = FABRIC_BLOCKS[color]

    private fun regular(name: String, block: Builder.() -> Unit): Block = create(name) {
        tab(DIMENSIONAL_DOORS)
        block(this)
    }

    private fun decay(name: String, block: Builder.() -> Unit): Block = create(name) {
        tab(DECAY)
        block(this)
    }

    private fun door(name: String, from: Block, set: BlockSetType, properties: BlockBehaviour.Properties.() -> Unit = {}): Block = regular(name) {
        block { DoorBlock(set, it) }
        blockProperties(from) {
            strength(5.0f)
            requiresCorrectToolForDrops()
            properties(this)
        }
    }

    private fun unravelled(name: String, block: Builder.() -> Unit = {}): Block = create(name) {
        block(::UnravelledFabricBlock)
        blockProperties(Blocks.STONE, UNRAVELLED_FABRIC_BLOCK_SETTINGS)
        block(this)
    }

    fun DyeColor.fabric(): Block = regular("${serializedName}_fabric") {
        block { FabricBlock(this@fabric) }
    }.also { FABRIC_BLOCKS[this] = it }

    fun DyeColor.ancientFabric(): Block = regular("${serializedName}_ancient_fabric") {
        block { AncientFabricBlock(this@ancientFabric) }
    }.also { ANCIENT_FABRIC_BLOCKS[this] = it }

    @JvmRecord
    data class DecayGroupSet(
        @JvmField val fence: Block,
        @JvmField val gate: Block,
        @JvmField val button: Block,
        @JvmField val slab: Block,
        @JvmField val stairs: Block,
        @JvmField val wall: Block
    ) {
        companion object {
            @JvmField
            val SETS = mutableListOf<DecayGroupSet>()

            fun create(name: String, from: Block, properties: BlockBehaviour.Properties.() -> Unit = {}): DecayGroupSet =
                create(name, { from }, properties)

            fun create(name: String, from: Holder<Block>, properties: BlockBehaviour.Properties.() -> Unit = {}): DecayGroupSet =
                create(name, from::value, properties)

            private fun create(name: String, from: () -> Block, properties: BlockBehaviour.Properties.() -> Unit): DecayGroupSet {
                fun part(suffix: String, function: (BlockBehaviour.Properties) -> Block, extra: BlockBehaviour.Properties.() -> Unit = {}) = decay("${name}_$suffix") {
                    block(function)
                    blockProperties = { BlockBehaviour.Properties.ofFullCopy(from()).apply(properties).apply(extra) }
                }

                val set = DecayGroupSet(
                    part("fence", ::FenceBlock),
                    part("gate", { FenceGateBlock(WoodType.OAK, it) }),
                    part("button", { ButtonBlock(BlockSetType.STONE, 20, it) }) {
                        noCollission()
                        strength(0.5f)
                    },
                    part("slab", ::SlabBlock),
                    part("stairs", { StairBlock(from().defaultBlockState(), it) }),
                    part("wall", ::WallBlock)
                )

                SETS.add(set)

                return set
            }
        }
    }
}
