package org.dimdev.dimdoors.shared.items;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemColored;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;

import static org.dimdev.dimdoors.shared.sound.ModSounds.CREEPY;

public final class ModItems {

    // Regular doors
    public static final ItemDoorGold GOLD_DOOR = new ItemDoorGold();
    public static final ItemDoorQuartz QUARTZ_DOOR = new ItemDoorQuartz();

    // Dimensional doors
    public static final ItemDimensionalDoorIron IRON_DIMENSIONAL_DOOR = new ItemDimensionalDoorIron();
    public static final ItemDimensionalDoorGold GOLD_DIMENSIONAL_DOOR = new ItemDimensionalDoorGold();
    public static final ItemDimensionalDoorQuartz QUARTZ_DIMENSIONAL_DOOR = new ItemDimensionalDoorQuartz();
    public static final ItemDimensionalDoorUnstable UNSTABLE_DIMENSIONAL_DOOR = new ItemDimensionalDoorUnstable();
    public static final ItemDimensionalDoorWood WOOD_DIMENSIONAL_DOOR = new ItemDimensionalDoorWood();

    // Crafting ingredients
    private static final String WORLD_THREAD_ID = "world_thread";
    private static final String STABLE_FABRIC_ID = "stable_fabric";
    public static final Item WORLD_THREAD = new Item().setUnlocalizedName(WORLD_THREAD_ID).setFull3D().setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB).setRegistryName(new ResourceLocation(DimDoors.MODID, WORLD_THREAD_ID));
    public static final Item STABLE_FABRIC = new Item().setUnlocalizedName(STABLE_FABRIC_ID).setFull3D().setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB).setRegistryName(new ResourceLocation(DimDoors.MODID, STABLE_FABRIC_ID));

    // Tools
    public static final ItemRiftConfigurationTool RIFT_CONFIGURATION_TOOL = new ItemRiftConfigurationTool();
    public static final ItemRiftBlade RIFT_BLADE = new ItemRiftBlade();
    public static final ItemRiftRemover RIFT_REMOVER = new ItemRiftRemover();
    public static final ItemRiftSignature RIFT_SIGNATURE = new ItemRiftSignature();
    public static final ItemStabilizedRiftSignature STABILIZED_RIFT_SIGNATURE = new ItemStabilizedRiftSignature();
    public static final ItemRiftStabilizer RIFT_STABILIZER = new ItemRiftStabilizer();

    // Armors
    public static final ItemWovenWorldThreadArmor WOVEN_WORLD_THREAD_HELMET = new ItemWovenWorldThreadArmor("woven_world_thread_helmet", 1, EntityEquipmentSlot.HEAD);
    public static final ItemWovenWorldThreadArmor WOVEN_WORLD_THREAD_CHESTPLATE = new ItemWovenWorldThreadArmor("woven_world_thread_chestplate", 1, EntityEquipmentSlot.CHEST);
    public static final ItemWovenWorldThreadArmor WOVEN_WORLD_THREAD_LEGGINGS = new ItemWovenWorldThreadArmor("woven_world_thread_leggings", 2, EntityEquipmentSlot.LEGS);
    public static final ItemWovenWorldThreadArmor WOVEN_WORLD_THREAD_BOOTS = new ItemWovenWorldThreadArmor("woven_world_thread_boots", 1, EntityEquipmentSlot.FEET);

    // ItemBlocks
    public static final ItemColored FABRIC = (ItemColored) new ItemColored(ModBlocks.FABRIC, true).setSubtypeNames(new String[]{"white", "orange", "magenta", "lightBlue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"}).setRegistryName(ModBlocks.FABRIC.getRegistryName());
    public static final ItemColored ANCIENT_FABRIC = (ItemColored) new ItemColored(ModBlocks.ANCIENT_FABRIC, true).setSubtypeNames(new String[]{"white", "orange", "magenta", "lightBlue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"}).setRegistryName(ModBlocks.ANCIENT_FABRIC.getRegistryName());
    public static final ItemBlock UNRAVELLED_FABRIC = (ItemBlock) new ItemBlock(ModBlocks.UNRAVELLED_FABRIC).setRegistryName(ModBlocks.UNRAVELLED_FABRIC.getRegistryName());
    public static final ItemBlock ETERNAL_FABRIC = (ItemBlock) new ItemBlock(ModBlocks.ETERNAL_FABRIC).setRegistryName(ModBlocks.ETERNAL_FABRIC.getRegistryName());
    public static final ItemDimensionalTrapdoorWood WOOD_DIMENSIONAL_TRAPDOOR = new ItemDimensionalTrapdoorWood();

    // Records
    public static final ItemModRecord CREEPY_RECORD = new ItemModRecord("creepy", CREEPY);

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                QUARTZ_DOOR,
                QUARTZ_DIMENSIONAL_DOOR,
                GOLD_DOOR,
                GOLD_DIMENSIONAL_DOOR,
                IRON_DIMENSIONAL_DOOR,
                WOOD_DIMENSIONAL_DOOR,
                STABLE_FABRIC,
                UNSTABLE_DIMENSIONAL_DOOR,
                WORLD_THREAD,
                RIFT_CONFIGURATION_TOOL,
                RIFT_BLADE,
                RIFT_REMOVER,
                RIFT_SIGNATURE,
                STABILIZED_RIFT_SIGNATURE,
                RIFT_STABILIZER,
                WOVEN_WORLD_THREAD_HELMET,
                WOVEN_WORLD_THREAD_CHESTPLATE,
                WOVEN_WORLD_THREAD_LEGGINGS,
                WOVEN_WORLD_THREAD_BOOTS,
                FABRIC,
                ANCIENT_FABRIC,
                UNRAVELLED_FABRIC,
                ETERNAL_FABRIC,
                WOOD_DIMENSIONAL_TRAPDOOR,
                CREEPY_RECORD);
    }
}
