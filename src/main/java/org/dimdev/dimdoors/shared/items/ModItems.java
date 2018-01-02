package org.dimdev.dimdoors.shared.items;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;

public final class ModItems {

    // Regular doors
    public static final ItemDoorGold GOLD_DOOR = new ItemDoorGold();
    public static final ItemDoorQuartz QUARTZ_DOOR = new ItemDoorQuartz();

    // Dimensional doors
    public static final ItemDimensionalDoorIron DIMENSIONAL_DOOR = new ItemDimensionalDoorIron();
    public static final ItemDimensionalDoorGold GOLD_DIMENSIONAL_DOOR = new ItemDimensionalDoorGold();
    public static final ItemDimensionalDoorPersonal PERSONAL_DIMENSIONAL_DOOR = new ItemDimensionalDoorPersonal();
    public static final ItemDimensionalDoorUnstable UNSTABLE_DIMENSIONAL_DOOR = new ItemDimensionalDoorUnstable();
    public static final ItemDimensionalDoorWarp WARP_DIMENSIONAL_DOOR = new ItemDimensionalDoorWarp();

    // Fabric
    public static final ItemWorldThread WORLD_THREAD = new ItemWorldThread();
    public static final ItemStableFabric STABLE_FABRIC = new ItemStableFabric();

    // Tools
    public static final ItemRiftConnectionTool RIFT_CONNECTION_TOOL = new ItemRiftConnectionTool();
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
    public static final ItemFabric FABRIC = new ItemFabric();
    public static final ItemDimensionalTrapdoorWood WOOD_DIMENSIONAL_TRAPDOOR = new ItemDimensionalTrapdoorWood();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                QUARTZ_DOOR,
                PERSONAL_DIMENSIONAL_DOOR,
                GOLD_DOOR,
                GOLD_DIMENSIONAL_DOOR,
                DIMENSIONAL_DOOR,
                WARP_DIMENSIONAL_DOOR,
                STABLE_FABRIC,
                UNSTABLE_DIMENSIONAL_DOOR,
                WORLD_THREAD,
                RIFT_CONNECTION_TOOL,
                RIFT_BLADE,
                RIFT_REMOVER,
                RIFT_SIGNATURE,
                STABILIZED_RIFT_SIGNATURE,
                HELMET_WOVEN_WORLD_THREAD,
                CHESTPLATE_WOVEN_WORLD_THREAD,
                LEGGINGS_WOVEN_WORLD_THREAD,
                BOOTS_WOVEN_WORLD_THREAD);

        // ItemBlocks
        event.getRegistry().registerAll(
                FABRIC,
                WOOD_DIMENSIONAL_TRAPDOOR,
                new ItemBlock(ModBlocks.RIFT).setRegistryName(ModBlocks.RIFT.getRegistryName()));
    }
}
