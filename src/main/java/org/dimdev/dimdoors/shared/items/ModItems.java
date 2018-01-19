package org.dimdev.dimdoors.shared.items;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemColored;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;

public final class ModItems {

    // Regular doors
    public static final ItemDoorGold GOLD_DOOR = new ItemDoorGold();
    public static final ItemDoorQuartz QUARTZ_DOOR = new ItemDoorQuartz();

    // Dimensional doors
    public static final ItemDimensionalDoorIron DIMENSIONAL_DOOR = new ItemDimensionalDoorIron();
    public static final ItemDimensionalDoorGold GOLD_DIMENSIONAL_DOOR = new ItemDimensionalDoorGold();
    public static final ItemDimensionalDoorPersonal PERSONAL_DIMENSIONAL_DOOR = new ItemDimensionalDoorPersonal();
    public static final ItemDimensionalDoorChaos CHAOS_DOOR = new ItemDimensionalDoorChaos();
    public static final ItemDimensionalDoorWarp WARP_DIMENSIONAL_DOOR = new ItemDimensionalDoorWarp();

    // Fabric
    public static final ItemWorldThread WORLD_THREAD = new ItemWorldThread();
    public static final ItemStableFabric STABLE_FABRIC = new ItemStableFabric();

    // Tools
    public static final ItemRiftConfigurationTool RIFT_CONNECTION_TOOL = new ItemRiftConfigurationTool();
    public static final ItemRiftBlade RIFT_BLADE = new ItemRiftBlade();
    public static final ItemRiftRemover RIFT_REMOVER = new ItemRiftRemover();
    public static final ItemRiftSignature RIFT_SIGNATURE = new ItemRiftSignature();
    public static final ItemStabilizedRiftSignature STABILIZED_RIFT_SIGNATURE = new ItemStabilizedRiftSignature();

    // Armors
    public static final ItemWovenWorldThreadArmor HELMET_WOVEN_WORLD_THREAD = new ItemWovenWorldThreadArmor("helmet_woven_world_thread", 1, EntityEquipmentSlot.HEAD);
    public static final ItemWovenWorldThreadArmor CHESTPLATE_WOVEN_WORLD_THREAD = new ItemWovenWorldThreadArmor("chestplate_woven_world_thread", 1, EntityEquipmentSlot.CHEST);
    public static final ItemWovenWorldThreadArmor LEGGINGS_WOVEN_WORLD_THREAD = new ItemWovenWorldThreadArmor("leggings_woven_world_thread", 2, EntityEquipmentSlot.LEGS);
    public static final ItemWovenWorldThreadArmor BOOTS_WOVEN_WORLD_THREAD = new ItemWovenWorldThreadArmor("boots_woven_world_thread", 1, EntityEquipmentSlot.FEET);

    // ItemBlocks
    public static final Item FABRIC = new ItemColored(ModBlocks.FABRIC, true).setSubtypeNames(new String[] {"white", "orange", "magenta", "lightBlue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"}).setRegistryName(ModBlocks.FABRIC.getRegistryName());
    public static final Item ANCIENT_FABRIC = new ItemColored(ModBlocks.ANCIENT_FABRIC, true).setSubtypeNames(new String[] {"white", "orange", "magenta", "lightBlue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"}).setRegistryName(ModBlocks.ANCIENT_FABRIC.getRegistryName());
    public static final Item UNRAVELLED_FABRIC = new ItemBlock(ModBlocks.UNRAVELLED_FABRIC).setRegistryName(ModBlocks.UNRAVELLED_FABRIC.getRegistryName());
    public static final Item ETERNAL_FABRIC = new ItemBlock(ModBlocks.ETERNAL_FABRIC).setRegistryName(ModBlocks.ETERNAL_FABRIC.getRegistryName());
    public static final Item RIFT = new ItemBlock(ModBlocks.RIFT).setRegistryName(ModBlocks.RIFT.getRegistryName());
    public static final ItemDimensionalTrapdoorWood WOOD_DIMENSIONAL_TRAPDOOR = new ItemDimensionalTrapdoorWood();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(QUARTZ_DOOR,
                PERSONAL_DIMENSIONAL_DOOR,
                GOLD_DOOR,
                GOLD_DIMENSIONAL_DOOR,
                DIMENSIONAL_DOOR,
                WARP_DIMENSIONAL_DOOR,
                STABLE_FABRIC,
                CHAOS_DOOR,
                WORLD_THREAD,
                RIFT_CONNECTION_TOOL,
                RIFT_BLADE,
                RIFT_REMOVER,
                RIFT_SIGNATURE,
                STABILIZED_RIFT_SIGNATURE,
                HELMET_WOVEN_WORLD_THREAD,
                CHESTPLATE_WOVEN_WORLD_THREAD,
                LEGGINGS_WOVEN_WORLD_THREAD,
                BOOTS_WOVEN_WORLD_THREAD,
                FABRIC,
                ANCIENT_FABRIC,
                UNRAVELLED_FABRIC,
                ETERNAL_FABRIC,
                WOOD_DIMENSIONAL_TRAPDOOR,
                RIFT);

        DimDoors.proxy.afterItemsRegistered();
    }
}
