package org.dimdev.dimdoors.block;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

public final class ModBlocks {
    public static final Block GOLD_DOOR = register("dimdoors:gold_door", new DoorBlock(FabricBlockSettings.of(Material.METAL, MaterialColor.GOLD).nonOpaque()));
    public static final Block QUARTZ_DOOR = register("dimdoors:quartz_door", new DoorBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.QUARTZ).nonOpaque()));
    public static final Block OAK_DIMENSIONAL_DOOR = register("dimdoors:oak_dimensional_door", new DimensionalDoorBlock(FabricBlockSettings.of(Material.WOOD, MaterialColor.WOOD).nonOpaque()));
    public static final Block IRON_DIMENSIONAL_DOOR = register("dimdoors:iron_dimensional_door", new DimensionalDoorBlock(FabricBlockSettings.of(Material.METAL, MaterialColor.IRON).nonOpaque()));
    public static final Block GOLD_DIMENSIONAL_DOOR = register("dimdoors:gold_dimensional_door", new DimensionalDoorBlock(FabricBlockSettings.of(Material.METAL, MaterialColor.GOLD).nonOpaque()));
    public static final Block QUARTZ_DIMENSIONAL_DOOR = register("dimdoors:quartz_dimensional_door", new DimensionalDoorBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.QUARTZ).nonOpaque()));
    public static final Block OAK_DIMENSIONAL_TRAPDOOR = register("dimdoors:wood_dimensional_trapdoor", new DimensionalTrapdoorBlock(FabricBlockSettings.of(Material.WOOD, MaterialColor.WOOD).nonOpaque()));

    public static final Block DIMENSIONAL_PORTAL = register("dimdoors:dimensional_portal", new DimensionalPortalBlock(FabricBlockSettings.of(Material.AIR).collidable(false).dropsNothing()));
    public static final Block DETACHED_RIFT = register("dimdoors:detached_rift", new DetachedRiftBlock(FabricBlockSettings.of(Material.AIR).nonOpaque()));

    public static final Block WHITE_FABRIC = registerFabric("dimdoors:white_fabric", DyeColor.WHITE);
    public static final Block ORANGE_FABRIC = registerFabric("dimdoors:orange_fabric", DyeColor.ORANGE);
    public static final Block MAGENTA_FABRIC = registerFabric("dimdoors:magenta_fabric", DyeColor.MAGENTA);
    public static final Block LIGHT_BLUE_FABRIC = registerFabric("dimdoors:light_blue_fabric", DyeColor.LIGHT_BLUE);
    public static final Block YELLOW_FABRIC = registerFabric("dimdoors:yellow_fabric", DyeColor.YELLOW);
    public static final Block LIME_FABRIC = registerFabric("dimdoors:lime_fabric", DyeColor.LIME);
    public static final Block PINK_FABRIC = registerFabric("dimdoors:pink_fabric", DyeColor.PINK);
    public static final Block GRAY_FABRIC = registerFabric("dimdoors:gray_fabric", DyeColor.GRAY);
    public static final Block LIGHT_GRAY_FABRIC = registerFabric("dimdoors:light_gray_fabric", DyeColor.LIGHT_GRAY);
    public static final Block CYAN_FABRIC = registerFabric("dimdoors:cyan_fabric", DyeColor.CYAN);
    public static final Block PURPLE_FABRIC = registerFabric("dimdoors:purple_fabric", DyeColor.PURPLE);
    public static final Block BLUE_FABRIC = registerFabric("dimdoors:blue_fabric", DyeColor.BLUE);
    public static final Block BROWN_FABRIC = registerFabric("dimdoors:brown_fabric", DyeColor.BROWN);
    public static final Block GREEN_FABRIC = registerFabric("dimdoors:green_fabric", DyeColor.GREEN);
    public static final Block RED_FABRIC = registerFabric("dimdoors:red_fabric", DyeColor.RED);
    public static final Block BLACK_FABRIC = registerFabric("dimdoors:black_fabric", DyeColor.BLACK);

    public static final Block WHITE_ANCIENT_FABRIC = registerAncientFabric("dimdoors:white_ancient_fabric", DyeColor.WHITE);
    public static final Block ORANGE_ANCIENT_FABRIC = registerAncientFabric("dimdoors:orange_ancient_fabric", DyeColor.ORANGE);
    public static final Block MAGENTA_ANCIENT_FABRIC = registerAncientFabric("dimdoors:magenta_ancient_fabric", DyeColor.MAGENTA);
    public static final Block LIGHT_BLUE_ANCIENT_FABRIC = registerAncientFabric("dimdoors:light_blue_ancient_fabric", DyeColor.LIGHT_BLUE);
    public static final Block YELLOW_ANCIENT_FABRIC = registerAncientFabric("dimdoors:yellow_ancient_fabric", DyeColor.YELLOW);
    public static final Block LIME_ANCIENT_FABRIC = registerAncientFabric("dimdoors:lime_ancient_fabric", DyeColor.LIME);
    public static final Block PINK_ANCIENT_FABRIC = registerAncientFabric("dimdoors:pink_ancient_fabric", DyeColor.PINK);
    public static final Block GRAY_ANCIENT_FABRIC = registerAncientFabric("dimdoors:gray_ancient_fabric", DyeColor.GRAY);
    public static final Block LIGHT_GRAY_ANCIENT_FABRIC = registerAncientFabric("dimdoors:light_gray_ancient_fabric", DyeColor.LIGHT_GRAY);
    public static final Block CYAN_ANCIENT_FABRIC = registerAncientFabric("dimdoors:cyan_ancient_fabric", DyeColor.CYAN);
    public static final Block PURPLE_ANCIENT_FABRIC = registerAncientFabric("dimdoors:purple_ancient_fabric", DyeColor.PURPLE);
    public static final Block BLUE_ANCIENT_FABRIC = registerAncientFabric("dimdoors:blue_ancient_fabric", DyeColor.BLUE);
    public static final Block BROWN_ANCIENT_FABRIC = registerAncientFabric("dimdoors:brown_ancient_fabric", DyeColor.BROWN);
    public static final Block GREEN_ANCIENT_FABRIC = registerAncientFabric("dimdoors:green_ancient_fabric", DyeColor.GREEN);
    public static final Block RED_ANCIENT_FABRIC = registerAncientFabric("dimdoors:red_ancient_fabric", DyeColor.RED);
    public static final Block BLACK_ANCIENT_FABRIC = registerAncientFabric("dimdoors:black_ancient_fabric", DyeColor.BLACK);

    public static final Block ETERNAL_FLUID = register("dimdoors:eternal_fluid", new EternalFluidBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.RED).lightLevel(15)));
    public static final Block UNRAVELLED_FABRIC = register("dimdoors:unravelled_fabric", new UnravelledFabricBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.BLACK).lightLevel(15)));

    public static final Block MARKING_PLATE = register("dimdoors:marking_plate", new MarkingPlateBlock(FabricBlockSettings.of(Material.METAL, DyeColor.BLACK)));

    private static Block register(String string, Block block) {
        return Registry.register(Registry.BLOCK, string, block);
    }

    public static Block registerAncientFabric(String id, DyeColor color) {
        return register(id, new Block(FabricBlockSettings.of(Material.STONE, color).strength(-1.0F, 3600000.0F).dropsNothing()));
    }

    private static Block registerFabric(String id, DyeColor color) {
        return register(id, new FabricBlock(color));
    }

    public static void init() {
        // just loads the class
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        putCutout(ModBlocks.OAK_DIMENSIONAL_DOOR);
        putCutout(ModBlocks.GOLD_DIMENSIONAL_DOOR);
        putCutout(ModBlocks.IRON_DIMENSIONAL_DOOR);
        putCutout(ModBlocks.OAK_DIMENSIONAL_TRAPDOOR);
        putCutout(ModBlocks.QUARTZ_DIMENSIONAL_DOOR);
        putCutout(ModBlocks.QUARTZ_DOOR);
    }

    @Environment(EnvType.CLIENT)
    private static void putCutout(Block block) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
    }

    private static class DoorBlock extends net.minecraft.block.DoorBlock { // TODO: use access widener instead
        public DoorBlock(Settings settings) {
            super(settings);
        }
    }
}
