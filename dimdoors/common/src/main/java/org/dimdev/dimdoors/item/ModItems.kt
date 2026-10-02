package org.dimdev.dimdoors.item

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.*
import net.minecraft.world.level.material.Fluid
import org.dimdev.dimcore.api.CreativeTab
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.entity.ModEntityTypes
import org.dimdev.dimdoors.fluid.ModFluids

object ModItems : PlatformRegistry.ItemPlatformRegistry(DimensionalDoors.getSided()) {
    private val creativeTabs = CreativeTabPlatformRegistry(DimensionalDoors.getSided())

    val DIMENSIONAL_DOORS: CreativeTab = creativeTabs.create("dimensional_doors") {
        icon { RIFT_BLADE.defaultInstance }
        title("itemGroup.dimdoors.dimensional_doors".translate())
    }

    @JvmField
    val DECAY: CreativeTab = creativeTabs.create("decay") {
        icon { ModBlocks.UNRAVELED_SET.fence.asItem().defaultInstance }
        title("itemGroup.dimdoors.decay".translate())
    }

    @JvmField val INFRANGIBLE_FIBER = registerRegular("infrangible_fiber") { fireResistant() }
    @JvmField val WORLD_THREAD = registerRegular("world_thread")
    @JvmField val FRAYED_FILAMENT = registerRegular("frayed_filament")
    @JvmField val RIFT_CONFIGURATION_TOOL = registerRegular("rift_configuration_tool", ::RiftConfigurationToolItem)


    //TOOLS
    @JvmField val RIFT_BLADE = registerRegular("rift_blade", ::RiftBladeItem) {
        attributes(SwordItem.createAttributes(
            Tiers.IRON,
            3,
            -2.4f
        ))
    }

    @JvmField val FARSHOT = registerRegular("farshot", DimensionalDoors.getSided()::createFarShot) {
        stacksTo(1)
        durability(384)
    }

    @JvmField val RIFT_REMOVER = registerRegular("rift_remover", ::RiftRemoverItem) {
        stacksTo(1).durability(100)
    }

    @JvmField val RIFT_SIGNATURE = registerRegular("rift_signature", { RiftSignatureItem(it, true) }) {
        stacksTo(1)
        durability(1)
    }

    @JvmField
    val STABILIZED_RIFT_SIGNATURE = registerRegular("stabilized_rift_signature", ::StabilizedRiftSignatureItem) {
        stacksTo(1)
        durability(20)
    }

    @JvmField
    val RIFT_STABILIZER = registerRegular("rift_stabilizer", ::RiftStabilizerItem) {
        stacksTo(1)
        durability(6)
    }

    @JvmField val RIFT_KEY = registerRegular("rift_key", ::RiftKeyItem) {
        fireResistant()
        stacksTo(1)
    }

    @JvmField val DIMENSIONAL_ERASER = registerRegular("dimensional_eraser", ::DimensionalEraserItem) {
        durability(100)
    }

    @JvmField
    val MONOLITH_SPAWNER = registerRegular("monolith_spawner", { SpawnEggItem(ModEntityTypes.MONOLITH, 0xffffff, 0xffffff, it) })

    @JvmField val MASK_WAND = registerRegular("mask_wand", ::MaskWandItem) {
        stacksTo(1)
    }

    @JvmField val STABLE_FABRIC = registerRegular("stable_fabric")

    @JvmField val CREEPY_RECORD = registerRegular("creepy_record") {
        jukeboxPlayable(ModJukeboxSongs.CREEPY)
        stacksTo(1)
    }

    @JvmField
    val THEY_STARE_BACK_RECORD = registerRegular("they_stare_back_record") {
        jukeboxPlayable(ModJukeboxSongs.THEY_STARE_BACK).stacksTo(1)
    }

    @JvmField val WHITE_VOID_RECORD = registerRegular("white_void_record") {
        jukeboxPlayable(ModJukeboxSongs.WHITE_VOID).stacksTo(1)
    }

    @JvmField
    val ETERNAL_FLUID_BUCKET = registerBucket("eternal_fluid_bucket", { ModFluids.ETERNAL_FLUID })

    @JvmField
    val LEAK_BUCKET = registerBucket("leak_bucket", { ModFluids.LEAK })

    @JvmField val MASK_SHARD = registerRegular("mask_shard")
    @JvmField val FUZZY_FIREBALL = registerRegular("fuzzy_fireball")
    @JvmField val FABRIC_OF_FINALITY = registerRegular("fabric_of_finality")
    @JvmField val LIMINAL_LINT = registerRegular("liminal_lint")
    @JvmField val ENDURING_FIBERS = registerRegular("enduring_fibers")
    @JvmField val RIFT_PEARL = registerRegular("rift_pearl")
    @JvmField val AMALGAM_LUMP = registerDecay("amalgam_lump")
    @JvmField val CLOD = registerDecay("clod")
    @JvmField val GARMENT_OF_REALITY_ARMOR: ArmorSet = registerArmorSet("garment_of_reality", ModArmorMaterials.GARMENT_OF_REALITY) { stacksTo(1) }
    @JvmField val WORLD_THREAD_ARMOR: ArmorSet = registerArmorSet("world_thread", ModArmorMaterials.WORLD_THREAD) { stacksTo(1) }

    fun registerRegular(name: String, item: (Item.Properties) -> Item = ::Item, block: Item.Properties.() -> Unit = {}): Item = register(name, item, DIMENSIONAL_DOORS, block)
    fun registerDecay(name: String, item: (Item.Properties) -> Item = ::Item, block: Item.Properties.() -> Unit = {}): Item = register(name, item, DECAY, block)

    private fun registerBucket(name: String, fluid: () -> Fluid, block: Item.Properties.() -> Unit = {}): Item = registerRegular(name, { BucketItem(fluid.invoke(), it) }) {
        craftRemainder(Items.BUCKET)
        stacksTo(1)
        block(this)
    }

    private fun registerArmorSet(
        name: String,
        material: ArmorMaterial,
        block: Item.Properties.() -> Unit
    ): ArmorSet {
        val holder = BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(material)

        val function: (ArmorItem.Type, Item.Properties) -> ArmorItem = { type, properties -> ArmorItem(holder, type, properties) }

        val helmet = register("${name}_helmet", { function.invoke(ArmorItem.Type.HELMET, it) }, DIMENSIONAL_DOORS, block)
        val chestplate = register("${name}_chestplate", { function.invoke(ArmorItem.Type.CHESTPLATE, it) }, DIMENSIONAL_DOORS, block)
        val leggings = register("${name}_leggings", { function.invoke(ArmorItem.Type.LEGGINGS, it) }, DIMENSIONAL_DOORS, block)
        val boots = register("${name}_boots", { function.invoke(ArmorItem.Type.BOOTS, it) }, DIMENSIONAL_DOORS, block)

        return ArmorSet(helmet, chestplate, leggings, boots)
    }

    fun register(name: String, item: (Item.Properties) -> Item, tab: CreativeTab, block: Item.Properties.() -> Unit): Item = create(name) { item.invoke(Item.Properties().also(block)) }.also { tab.add { it } }

    override fun register() {
        super.register()
        creativeTabs.register()
    }
}

fun String.translate(vararg arg: Any): Component = Component.translatable(this, *arg)
