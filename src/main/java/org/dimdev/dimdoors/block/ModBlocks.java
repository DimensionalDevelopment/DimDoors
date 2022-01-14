package org.dimdev.dimdoors.block;

import java.util.HashMap;
import java.util.Map;

import org.dimdev.dimdoors.block.door.DimensionalTrapdoorBlock;
import org.dimdev.dimdoors.block.door.data.DoorData;
import org.dimdev.dimdoors.block.door.data.DoorDataReader;
import org.dimdev.matrix.Matrix;
import org.dimdev.matrix.Registrar;
import org.dimdev.matrix.RegistryEntry;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;

@Registrar(element = Block.class, modid = "dimdoors")
public final class ModBlocks {
	public static final Map<DyeColor, Block> FABRIC_BLOCKS = new HashMap<>();
	private static final Map<DyeColor, Block> ANCIENT_FABRIC_BLOCKS = new HashMap<>();

	@RegistryEntry("stone_player")
	public static final Block STONE_PLAYER = register(new Block(FabricBlockSettings.of(Material.STONE).strength(0.5F).breakByHand(true).nonOpaque()));

	@RegistryEntry("gold_door")
	public static final Block GOLD_DOOR = register(new DoorBlock(FabricBlockSettings.of(Material.METAL, MapColor.GOLD).strength(5.0F).breakByHand(false).nonOpaque()));

	@RegistryEntry("quartz_door")
	public static final Block QUARTZ_DOOR = register(new DoorBlock(FabricBlockSettings.of(Material.STONE, MapColor.OFF_WHITE).strength(5.0F).breakByHand(false).nonOpaque()));

	@RegistryEntry("wood_dimensional_trapdoor")
	public static final Block OAK_DIMENSIONAL_TRAPDOOR = register(new DimensionalTrapdoorBlock(FabricBlockSettings.copyOf(Blocks.OAK_TRAPDOOR).luminance(state -> 10)));


	@RegistryEntry("dimensional_portal")
	public static final Block DIMENSIONAL_PORTAL = register(new DimensionalPortalBlock(FabricBlockSettings.of(Material.AIR).collidable(false).strength(-1.0F, 3600000.0F).nonOpaque().dropsNothing().luminance(10)));

	@RegistryEntry("detached_rift")
	public static final Block DETACHED_RIFT = register(new DetachedRiftBlock(FabricBlockSettings.of(Material.AIR).strength(-1.0F, 3600000.0F).noCollision().nonOpaque()));


	@RegistryEntry("white_fabric")
	public static final Block WHITE_FABRIC = registerFabric(DyeColor.WHITE);

	@RegistryEntry("orange_fabric")
	public static final Block ORANGE_FABRIC = registerFabric(DyeColor.ORANGE);

	@RegistryEntry("magenta_fabric")
	public static final Block MAGENTA_FABRIC = registerFabric(DyeColor.MAGENTA);

	@RegistryEntry("light_blue_fabric")
	public static final Block LIGHT_BLUE_FABRIC = registerFabric(DyeColor.LIGHT_BLUE);

	@RegistryEntry("yellow_fabric")
	public static final Block YELLOW_FABRIC = registerFabric(DyeColor.YELLOW);

	@RegistryEntry("lime_fabric")
	public static final Block LIME_FABRIC = registerFabric(DyeColor.LIME);

	@RegistryEntry("pink_fabric")
	public static final Block PINK_FABRIC = registerFabric(DyeColor.PINK);

	@RegistryEntry("gray_fabric")
	public static final Block GRAY_FABRIC = registerFabric(DyeColor.GRAY);

	@RegistryEntry("light_gray_fabric")
	public static final Block LIGHT_GRAY_FABRIC = registerFabric(DyeColor.LIGHT_GRAY);

	@RegistryEntry("cyan_fabric")
	public static final Block CYAN_FABRIC = registerFabric(DyeColor.CYAN);

	@RegistryEntry("purple_fabric")
	public static final Block PURPLE_FABRIC = registerFabric(DyeColor.PURPLE);

	@RegistryEntry("blue_fabric")
	public static final Block BLUE_FABRIC = registerFabric(DyeColor.BLUE);

	@RegistryEntry("brown_fabric")
	public static final Block BROWN_FABRIC = registerFabric(DyeColor.BROWN);

	@RegistryEntry("green_fabric")
	public static final Block GREEN_FABRIC = registerFabric(DyeColor.GREEN);

	@RegistryEntry("red_fabric")
	public static final Block RED_FABRIC = registerFabric(DyeColor.RED);

	@RegistryEntry("black_fabric")
	public static final Block BLACK_FABRIC = registerFabric(DyeColor.BLACK);


	@RegistryEntry("white_ancient_fabric")
	public static final Block WHITE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.WHITE);

	@RegistryEntry("orange_ancient_fabric")
	public static final Block ORANGE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.ORANGE);

	@RegistryEntry("magenta_ancient_fabric")
	public static final Block MAGENTA_ANCIENT_FABRIC = registerAncientFabric(DyeColor.MAGENTA);

	@RegistryEntry("light_blue_ancient_fabric")
	public static final Block LIGHT_BLUE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.LIGHT_BLUE);

	@RegistryEntry("yellow_ancient_fabric")
	public static final Block YELLOW_ANCIENT_FABRIC = registerAncientFabric(DyeColor.YELLOW);

	@RegistryEntry("lime_ancient_fabric")
	public static final Block LIME_ANCIENT_FABRIC = registerAncientFabric(DyeColor.LIME);

	@RegistryEntry("pink_ancient_fabric")
	public static final Block PINK_ANCIENT_FABRIC = registerAncientFabric(DyeColor.PINK);

	@RegistryEntry("gray_ancient_fabric")
	public static final Block GRAY_ANCIENT_FABRIC = registerAncientFabric(DyeColor.GRAY);

	@RegistryEntry("light_gray_ancient_fabric")
	public static final Block LIGHT_GRAY_ANCIENT_FABRIC = registerAncientFabric(DyeColor.LIGHT_GRAY);

	@RegistryEntry("cyan_ancient_fabric")
	public static final Block CYAN_ANCIENT_FABRIC = registerAncientFabric(DyeColor.CYAN);

	@RegistryEntry("purple_ancient_fabric")
	public static final Block PURPLE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.PURPLE);

	@RegistryEntry("blue_ancient_fabric")
	public static final Block BLUE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.BLUE);

	@RegistryEntry("brown_ancient_fabric")
	public static final Block BROWN_ANCIENT_FABRIC = registerAncientFabric(DyeColor.BROWN);

	@RegistryEntry("green_ancient_fabric")
	public static final Block GREEN_ANCIENT_FABRIC = registerAncientFabric(DyeColor.GREEN);

	@RegistryEntry("red_ancient_fabric")
	public static final Block RED_ANCIENT_FABRIC = registerAncientFabric(DyeColor.RED);

	@RegistryEntry("black_ancient_fabric")
	public static final Block BLACK_ANCIENT_FABRIC = registerAncientFabric(DyeColor.BLACK);

	private static final FabricBlockSettings UNRAVELLED_FABRIC_BLOCK_SETTINGS = FabricBlockSettings.of(Material.STONE, MapColor.BLACK).ticksRandomly().luminance(15).strength(0.3F, 0.3F);

	@RegistryEntry("eternal_fluid")
	public static final Block ETERNAL_FLUID = register(new EternalFluidBlock(FabricBlockSettings.of(Material.STONE, MapColor.RED).luminance(15)));

	@RegistryEntry("decayed_block")
	public static final Block DECAYED_BLOCK = register(new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

	@RegistryEntry("unfolded_block")
	public static final Block UNFOLDED_BLOCK = register(new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

	@RegistryEntry("unwarped_block")
	public static final Block UNWARPED_BLOCK = register(new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

	@RegistryEntry("unravelled_block")
	public static final Block UNRAVELLED_BLOCK = register(new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

	@RegistryEntry("unravelled_fabric")
	public static final Block UNRAVELLED_FABRIC = register(new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

	@RegistryEntry("marking_plate")
	public static final Block MARKING_PLATE = register(new MarkingPlateBlock(FabricBlockSettings.of(Material.METAL, DyeColor.BLACK).nonOpaque()));

	@RegistryEntry("solid_static")
	public static final Block SOLID_STATIC = register(new UnravelledFabricBlock(FabricBlockSettings.of(Material.STONE).strength(7, 25).ticksRandomly().breakByHand(false).sounds(BlockSoundGroup.SAND)));

	private static Block register(Block block) {
		return block;
	}

	private static Block registerAncientFabric(DyeColor color) {
		Block block = new AncientFabricBlock(color);
		ANCIENT_FABRIC_BLOCKS.put(color, block);
		return register(block);
	}

	private static Block registerFabric(DyeColor color) {
		Block block = new FabricBlock(color);
		FABRIC_BLOCKS.put(color, block);
		return register(block);
	}

	public static void init() {
		Matrix.register(ModBlocks.class, Registry.BLOCK);
		DoorDataReader.read();
	}

	@Environment(EnvType.CLIENT)
	public static void initClient() {
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ModBlocks.QUARTZ_DOOR, ModBlocks.GOLD_DOOR);
		DoorData.DOORS.forEach(door -> BlockRenderLayerMap.INSTANCE.putBlock(door, RenderLayer.getCutout()));
	}

	public static Block ancientFabricFromDye(DyeColor color) {
		return ANCIENT_FABRIC_BLOCKS.get(color);
	}

	public static Block fabricFromDye(DyeColor color) {
		return FABRIC_BLOCKS.get(color);
	}

	private static class DoorBlock extends net.minecraft.block.DoorBlock {
		public DoorBlock(Settings settings) {
			super(settings);
		}
	}
}
