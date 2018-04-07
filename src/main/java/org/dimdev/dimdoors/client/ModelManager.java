package org.dimdev.dimdoors.client;

import net.minecraft.block.BlockDoor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.items.ModItems;

@SideOnly(Side.CLIENT)
public final class ModelManager {

    //@SubscribeEvent
    public static void registerItemModels() {
        // Register item models
        registerColored(ModItems.FABRIC);
        registerColored(ModItems.ANCIENT_FABRIC);
        register(ModItems.UNRAVELLED_FABRIC);
        register(ModItems.ETERNAL_FABRIC);
        register(ModItems.WOOD_DIMENSIONAL_TRAPDOOR);
        register(ModItems.IRON_DIMENSIONAL_DOOR);
        register(ModItems.GOLD_DIMENSIONAL_DOOR);
        register(ModItems.GOLD_DOOR);
        register(ModItems.QUARTZ_DIMENSIONAL_DOOR);
        register(ModItems.QUARTZ_DOOR);
        register(ModItems.STABLE_FABRIC);
        register(ModItems.UNSTABLE_DIMENSIONAL_DOOR);
        register(ModItems.WOOD_DIMENSIONAL_DOOR);
        register(ModItems.WORLD_THREAD);
        register(ModItems.RIFT_CONFIGURATION_TOOL);
        register(ModItems.RIFT_BLADE);
        register(ModItems.RIFT_REMOVER);
        register(ModItems.RIFT_SIGNATURE);
        register(ModItems.RIFT_STABILIZER);
        register(ModItems.STABILIZED_RIFT_SIGNATURE);
        register(ModItems.WOVEN_WORLD_THREAD_BOOTS);
        register(ModItems.WOVEN_WORLD_THREAD_CHESTPLATE);
        register(ModItems.WOVEN_WORLD_THREAD_HELMET);
        register(ModItems.WOVEN_WORLD_THREAD_LEGGINGS);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        // Register item variants
        registerColoredVariants(ModItems.FABRIC);
        registerColoredVariants(ModItems.ANCIENT_FABRIC);

        // Register state mappers
        StateMap ignorePowered = new StateMap.Builder().ignore(BlockDoor.POWERED).build();
        ModelLoader.setCustomStateMapper(ModBlocks.GOLD_DOOR, ignorePowered);
        ModelLoader.setCustomStateMapper(ModBlocks.QUARTZ_DOOR, ignorePowered);
        ModelLoader.setCustomStateMapper(ModBlocks.GOLD_DIMENSIONAL_DOOR, ignorePowered);
        ModelLoader.setCustomStateMapper(ModBlocks.IRON_DIMENSIONAL_DOOR, ignorePowered);
        ModelLoader.setCustomStateMapper(ModBlocks.PERSONAL_DIMENSIONAL_DOOR, ignorePowered);
        ModelLoader.setCustomStateMapper(ModBlocks.WARP_DIMENSIONAL_DOOR, ignorePowered);

        ModelLoader.setCustomStateMapper(ModBlocks.DIMENSIONAL_PORTAL, new StateMap.Builder().ignore(BlockDoor.FACING, BlockDoor.HALF, BlockDoor.HINGE, BlockDoor.OPEN, BlockDoor.POWERED).build());
    }

    private static void register(Item item) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0,
                new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    private static void register(Item item, int meta, String name) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta,
                new ModelResourceLocation(item.getRegistryName() + "_" + name, "inventory"));
    }

    private static void registerColored(Item item) {
        for (EnumDyeColor color : EnumDyeColor.values()) {
            register(item, color.getMetadata(), color.getName());
        }
    }

    private static void registerColoredVariants(Item item) {
        ResourceLocation itemName = item.getRegistryName();
        ResourceLocation[] variants = new ResourceLocation[16];
        for (EnumDyeColor color : EnumDyeColor.values()) {
            variants[color.getMetadata()] = new ResourceLocation(itemName + "_" + color.getName());
        }

        ModelBakery.registerItemVariants(item, variants);
    }
}
