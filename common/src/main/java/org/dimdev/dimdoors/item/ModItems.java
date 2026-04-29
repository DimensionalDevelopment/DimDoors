package org.dimdev.dimdoors.item;

import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.fluid.ModFluids;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ModItems {
//    public static final RegistrySupplier<Item> OAK_DIMENSIONAL_TRAPDOOR = register("wood_dimensional_trapdoor", properties -> new DimensionalTrapdoorItem(
//        ModBlocks.OAK_DIMENSIONAL_TRAPDOOR,
//        properties.stacksTo(1),
//        rift -> rift.setDestination(
//            RandomTarget.builder()
//                .acceptedGroups(Collections.singleton(0))
//                .coordFactor(1)
//                .negativeDepthFactor(80)
//                .positiveDepthFactor(Double.MAX_VALUE)
//                .weightMaximum(100)
//                .noLink(false)
//                .newRiftWeight(0)
//                .build())
//    ));

    public static final CreativeModeTab DIMENSIONAL_DOORS = DimensionalDoors.getSided().registerCreativeModeTab("dimensional_doors", builder -> builder.icon(() -> ModItems.RIFT_BLADE.getDefaultInstance()).title(Component.translatable("itemGroup.dimdoors.dimensional_doors")));
    public static final CreativeModeTab DECAY = DimensionalDoors.getSided().registerCreativeModeTab("decay", builder -> builder.icon(() -> ModBlocks.UNRAVELED_SET.fence().asItem().getDefaultInstance()).title(Component.translatable("itemGroup.dimdoors.decay")));

    public static final Item INFRANGIBLE_FIBER = registerRegular("infrangible_fiber", properties -> new Item(properties.fireResistant()));

    public static final Item WORLD_THREAD = registerRegular("world_thread", Item::new);

    public static final Item FRAYED_FILAMENTS = registerRegular("frayed_filament", Item::new);

    public static final Item RIFT_CONFIGURATION_TOOL = registerRegular("rift_configuration_tool", RiftConfigurationToolItem::new);

    public static final Item RIFT_BLADE = registerRegular("rift_blade", properties -> new RiftBladeItem(properties.durability(100)));

    public static final Item RIFT_REMOVER = registerRegular("rift_remover", properties -> new RiftRemoverItem(properties.stacksTo(1).durability(100)));

    public static final Item RIFT_SIGNATURE = registerRegular("rift_signature", properties -> new RiftSignatureItem(properties.stacksTo(1).durability(1), true));

    public static final Item STABILIZED_RIFT_SIGNATURE = registerRegular("stabilized_rift_signature", properties -> new StabilizedRiftSignatureItem(properties.stacksTo(1).durability(20)));

    public static final Item RIFT_STABILIZER = registerRegular("rift_stabilizer", properties -> new RiftStabilizerItem(properties.stacksTo(1).durability(6)));

    public static final Item RIFT_KEY = registerRegular("rift_key", properties -> new RiftKeyItem(properties.fireResistant().stacksTo(1)));

    public static final Item DIMENSIONAL_ERASER = registerRegular("dimensional_eraser", properties -> new DimensionalEraserItem(properties.durability(100)));

    public static final Item MONOLITH_SPAWNER = registerRegular("monolith_spawner", properties -> new SpawnEggItem(ModEntityTypes.MONOLITH, 0xffffff, 0xffffff, properties));

    public static final Item MASK_WAND = registerRegular("mask_wand", properties -> new MaskWandItem(properties.stacksTo(1)));

    public static final Item STABLE_FABRIC = registerRegular("stable_fabric", Item::new);

    public static final Item CREEPY_RECORD = registerRegular("creepy_record", properties -> new Item(properties.jukeboxPlayable(ModJukeboxSongs.CREEPY).stacksTo(1)));

    public static final Item THEY_STARE_BACK_RECORD = registerRegular("they_stare_back_record", properties -> new Item(properties.jukeboxPlayable(ModJukeboxSongs.THEY_STARE_BACK).stacksTo(1)));
    public static final Item WHITE_VOID_RECORD = registerRegular("white_void_record", properties -> new Item(properties.jukeboxPlayable(ModJukeboxSongs.WHITE_VOID).stacksTo(1)));

    public static final Item ETERNAL_FLUID_BUCKET = registerRegular("eternal_fluid_bucket", properties -> new ArchitecturyBucketItem(() -> ModFluids.ETERNAL_FLUID, properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final Item LEAK_BUCKET = registerDecay("leak_bucket", properties -> new ArchitecturyBucketItem(() -> ModFluids.LEAK, properties.craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final Item MASK_SHARD = registerRegular("mask_shard", Item::new);

    public static final Item FUZZY_FIREBALL = registerRegular("fuzzy_fireball", Item::new);

    public static final Item FABRIC_OF_FINALITY = registerRegular("fabric_of_finality", Item::new);

    public static final Item LIMINAL_LINT = registerRegular("liminal_lint", Item::new);

    public static final Item ENDURING_FIBERS = registerRegular("enduring_fibers", Item::new);

    public static final Item RIFT_PEARL = registerRegular("rift_pearl", Item::new);

    public static final Item AMALGAM_LUMP = registerDecay("amalgam_lump", Item::new);

    public static final Item CLOD = registerDecay("clod", Item::new);

    public static final ArmorSet GARMENT_OF_REALITY_ARMOR = registerArmorSet("garment_of_reality", ModArmorMaterials.GARMENT_OF_REALITY, properties -> properties.stacksTo(1));

    public static final ArmorSet WORLD_THREAD_ARMOR = registerArmorSet("world_thread", ModArmorMaterials.WORLD_THREAD, properties -> properties.stacksTo(1));

    public static final Set<Item> DOOR_ITEMS = new HashSet<>();

    public static Item registerRegular(String name, Function<Item.Properties, Item> item) {
        return register(name, item.apply(new Item.Properties()), DIMENSIONAL_DOORS);
    }

    public static Item registerDecay(String name, Function<Item.Properties, Item> item) {
        return register(name, item.apply(new Item.Properties()), DECAY);
    }

    private static ArmorSet registerArmorSet(String name, ArmorMaterial material, Consumer<Item.Properties> propertiesConsumer) {
        var holder = BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(material);
        var properties = new Item.Properties();

        var helmet = register(name + "_helmet", new ArmorItem(holder, ArmorItem.Type.HELMET, properties), DIMENSIONAL_DOORS);
        var chestplate = register(name + "_chestplate", new ArmorItem(holder, ArmorItem.Type.CHESTPLATE, properties), DIMENSIONAL_DOORS);
        var leggings = register(name + "_leggings", new ArmorItem(holder, ArmorItem.Type.LEGGINGS, properties), DIMENSIONAL_DOORS);
        var boots = register(name + "_boots", new ArmorItem(holder, ArmorItem.Type.BOOTS, properties), DIMENSIONAL_DOORS);

        return new ArmorSet(helmet, chestplate, leggings, boots);
    }

    public static Item register(String name, Item item, CreativeModeTab tab) {
        var itemVal = DimensionalDoors.getSided().registerItem(name, item);
        DimensionalDoors.getSided().appendStack(tab, item.getDefaultInstance());
        return itemVal;
    }

    public static void init() {
    }
}
